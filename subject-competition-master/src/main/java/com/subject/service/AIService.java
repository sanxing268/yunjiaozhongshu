package com.subject.service;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AI服务接口
 */
public interface AIService {
    /**
     * 流式聊天
     * 
     * @param messages 消息列表（JSON字符串）
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param userEnableThinking 用户是否启用深度思考（可为null，使用默认值）
     * @throws IOException IO异常
     */
    void streamChat(String messages, HttpServletRequest request, HttpServletResponse response, Boolean userEnableThinking) throws IOException;

    /**
     * 非流式聊天
     * 
     * @param messages 消息列表（JSON字符串）
     * @return 响应内容
     */
    String chat(String messages);

    /**
     * 生成内容（同步调用）
     * 
     * @param prompt 提示词
     * @return AI生成的内容
     */
    String generateContent(String prompt);
}
