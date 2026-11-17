package com.subject.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subject.commons.constant.AIConstants;
import com.subject.commons.util.AIUtil;
import com.subject.service.AIService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * AI服务实现类
 * 所有AI功能都在后端实现，包括系统提示词、消息处理等
 */
@Service
public class AIServiceImpl implements AIService {

    @Value("${ai.dashscope.api-key:}")
    private String apiKey;

    @Value("${ai.dashscope.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${ai.dashscope.model:qwen-plus-latest}")
    private String defaultModel;

    @Value("${ai.dashscope.enable-thinking:true}")
    private boolean enableThinking;

    @Value("${ai.dashscope.thinking-budget:4000}")
    private int thinkingBudget;

    private static final String CHAT_ENDPOINT = "/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AIServiceImpl() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(AIConstants.Timeout.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(AIConstants.Timeout.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(AIConstants.Timeout.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void streamChat(String messagesJson, HttpServletRequest request, HttpServletResponse response,
                           Boolean userEnableThinking) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"" + AIConstants.ErrorMessage.API_KEY_NOT_CONFIGURED + "\"}");
            response.getWriter().flush();
            return;
        }

        // 验证消息格式
        if (!AIUtil.isValidMessages(messagesJson)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"" + AIConstants.ErrorMessage.INVALID_REQUEST + "\"}");
            response.getWriter().flush();
            return;
        }

        try {
            // 解析消息
            String enrichedMessages = AIUtil.enrichMessagesWithSystemPrompt(messagesJson);
            JsonNode messagesNode = objectMapper.readTree(enrichedMessages);

            boolean shouldEnableThinking = enableThinking &&
                    (userEnableThinking != null ? userEnableThinking : true);

            String requestBody = buildRequestBody(messagesNode, true, shouldEnableThinking);

            // HTTP请求
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + CHAT_ENDPOINT)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            //  SSE 响应头
            response.setContentType(AIConstants.SSE.CONTENT_TYPE); // text/event-stream
            response.setCharacterEncoding(AIConstants.SSE.CHARSET); // UTF-8
            response.setHeader("Cache-Control", "no-cache,no-store");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("X-Accel-Buffering", "no"); // 禁用所有缓存

            // 执行请求
            try (Response httpResponse = httpClient.newCall(httpRequest).execute()) {
                if (!httpResponse.isSuccessful()) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\":\"AI服务调用失败: " + httpResponse.code() + "\"}");
                    response.getWriter().flush();
                    return;
                }

                ResponseBody body = httpResponse.body();
                if (body == null) return;

                try (okio.BufferedSource source = body.source()) {
                    while (!source.exhausted()) {
                        // 逐行读取第三方AI的SSE流
                        String line = source.readUtf8Line();
                        if (line == null || line.isEmpty()) continue;

                        if (line.startsWith("data: ")) {
                            String data = line.substring(6).trim();
                            if (data.isEmpty() || "[DONE]".equals(data)) {
                                response.getWriter().write("data: {\"type\":\"done\"}\n\n");
                                response.getWriter().flush();
                                break;
                            }

                            try {
                                JsonNode jsonNode = objectMapper.readTree(data);
                                JsonNode choices = jsonNode.get("choices");
                                if (choices == null || !choices.isArray() || choices.isEmpty()) continue;

                                JsonNode choice = choices.get(0);
                                JsonNode delta = choice.get("delta");
                                if (delta == null) continue;

                                //  1. 输出思考过程
                                JsonNode reasoning = delta.get("reasoning_content");
                                if (reasoning != null && !reasoning.isNull()) {
                                    String text = reasoning.asText();
                                    if (!text.isEmpty()) {
                                        // 严格匹配前端解析格式：type:thinking
                                        response.getWriter().write("data: {\"type\":\"thinking\",\"content\":\"" + escapeJson(text) + "\"}\n\n");
                                        response.getWriter().flush(); // 强制推送
                                    }
                                }

                                // 2. 输出回答内容
                                JsonNode content = delta.get("content");
                                if (content != null && !content.isNull()) {
                                    String text = content.asText();
                                    if (!text.isEmpty()) {
                                        // 严格匹配前端解析格式：type:content
                                        response.getWriter().write("data: {\"type\":\"content\",\"content\":\"" + escapeJson(text) + "\"}\n\n");
                                        response.getWriter().flush(); // 逐字推送
                                    }
                                }

                                // 3. 结束标记
                                JsonNode finish = choice.get("finish_reason");
                                if (finish != null && !finish.isNull()) {
                                    response.getWriter().write("data: {\"type\":\"done\"}\n\n");
                                    response.getWriter().flush();
                                }

                            } catch (Exception e) {
                                // 忽略解析错误，不中断流
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"服务异常\"}");
            response.getWriter().flush();
        }
    }

    @Override
    public String chat(String messagesJson) {
        // 非流式聊天实现（可选）
        return "非流式聊天功能待实现";
    }

    @Override
    public String generateContent(String prompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }

        try {
            // 构建消息
            String messagesJson = "[{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}]";
            String enrichedMessages = AIUtil.enrichMessagesWithSystemPrompt(messagesJson);
            JsonNode messagesNode = objectMapper.readTree(enrichedMessages);

            // 构建请求体（非流式）
            String requestBody = buildRequestBody(messagesNode, false, enableThinking);

            // 创建HTTP请求
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + CHAT_ENDPOINT)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            // 执行请求
            try (Response httpResponse = httpClient.newCall(httpRequest).execute()) {
                if (!httpResponse.isSuccessful()) {
                    return null;
                }

                ResponseBody body = httpResponse.body();
                if (body == null) {
                    return null;
                }

                // 解析响应
                JsonNode jsonNode = objectMapper.readTree(body.string());
                JsonNode choices = jsonNode.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode choice = choices.get(0);
                    JsonNode message = choice.get("message");
                    if (message != null) {
                        JsonNode content = message.get("content");
                        if (content != null) {
                            return content.asText();
                        }
                    }
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建请求体
     */
    private String buildRequestBody(JsonNode messagesNode, boolean stream, boolean useThinking) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"model\":\"").append(defaultModel).append("\",");
            sb.append("\"messages\":").append(messagesNode.toString()).append(",");
            sb.append("\"stream\":").append(stream).append(",");
            sb.append("\"temperature\":0.7,");
            sb.append("\"top_p\":0.8,");
            sb.append("\"enable_search\":false,");
            sb.append("\"enable_thinking\":").append(useThinking);
            if (useThinking) {
                sb.append(",\"thinking_budget\":").append(thinkingBudget);
            }
            sb.append("}");
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("构建请求体失败", e);
        }
    }

    /**
     * 转义JSON字符串
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
