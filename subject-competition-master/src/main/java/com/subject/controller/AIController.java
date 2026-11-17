package com.subject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subject.commons.result.Result;
import com.subject.domain.dto.ChatMessageDTO;
import com.subject.domain.dto.ChatRequestDTO;
import com.subject.domain.dto.UserProfileDTO;
import com.subject.domain.entity.ChatHistory;
import com.subject.domain.entity.UserProfile;
import com.subject.service.AIService;
import com.subject.service.ChatHistoryService;
import com.subject.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * AI聊天控制器
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class);

    @Autowired
    private AIService aiService;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private UserProfileService userProfileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 设置CORS响应头（支持credentials）
     */
    private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            // 允许所有来源（也可以在这里限制特定域名）
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // 默认允许所有
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    /**
     * 流式聊天接口
     * 
     * @param request  聊天请求
     * @param response HTTP响应
     */
    @PostMapping("/chat/stream")
    public void streamChat(@RequestBody ChatRequestDTO request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        try {
            // 设置CORS头
            setCorsHeaders(httpRequest, response);
            // 将消息列表转换为JSON字符串
            String messagesJson = objectMapper.writeValueAsString(request.getMessages());
            // 传递用户的深度思考偏好
            aiService.streamChat(messagesJson, httpRequest, response, request.getEnableThinking());
        } catch (Exception e) {
            try {
                setCorsHeaders(httpRequest, response);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
                response.getWriter().flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 简化版聊天接口（直接传消息列表）
     * 
     * @param messages 消息列表
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @PostMapping("/chat")
    public void chat(@RequestBody List<ChatMessageDTO> messages,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            // 设置CORS头
            setCorsHeaders(request, response);

            // 将消息列表转换为JSON字符串
            String messagesJson = objectMapper.writeValueAsString(messages);

            // 使用默认配置（启用深度思考）
            aiService.streamChat(messagesJson, request, response, true);
        } catch (Exception e) {
            try {
                e.printStackTrace();
                setCorsHeaders(request, response);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorMsg = e.getMessage() != null ? e.getMessage() : "未知错误";
                response.getWriter().write("{\"error\":\"" + errorMsg.replace("\"", "\\\"") + "\"}");
                response.getWriter().flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 处理OPTIONS预检请求
     */
    @RequestMapping(value = "/chat", method = RequestMethod.OPTIONS)
    public void options(HttpServletRequest request, HttpServletResponse response) {
        setCorsHeaders(request, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result health() {
        return Result.ok().message("AI服务运行正常");
    }

    /**
     * 保存聊天记录
     * 
     * @param chatHistory 聊天记录对象
     * @return 保存结果
     */
    @PostMapping("/history/save")
    public Result saveChatHistory(@RequestBody ChatHistory chatHistory) {
        try {
            log.info("收到聊天记录保存请求 - userId: {}, userMessage: {}, assistantMessage: {}",
                    chatHistory.getUserId(),
                    chatHistory.getUserMessage() != null ? chatHistory.getUserMessage().substring(0,
                            Math.min(50, chatHistory.getUserMessage().length())) : "null",
                    chatHistory.getAssistantMessage() != null ? chatHistory.getAssistantMessage().substring(0,
                            Math.min(50, chatHistory.getAssistantMessage().length())) : "null");

            if (chatHistory.getUserId() == null) {
                log.error("保存失败：userId为空");
                return Result.error().message("用户ID为空");
            }

            int result = chatHistoryService.saveChatHistory(chatHistory);
            if (result > 0) {
                log.info("聊天记录保存成功");
                return Result.ok().message("聊天记录保存成功");
            } else {
                log.warn("聊天记录保存失败");
                return Result.error().message("聊天记录保存失败");
            }
        } catch (Exception e) {
            log.error("保存聊天记录异常", e);
            e.printStackTrace();
            return Result.error().message("保存聊天记录异常：" + e.getMessage());
        }
    }

    /**
     * 获取用户聊天历史
     * 
     * @param userId 用户ID
     * @param limit  获取条数（默认50）
     * @return 聊天历史列表
     */
    @GetMapping("/history/user/{userId}")
    public Result getUserChatHistory(@PathVariable Long userId,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<ChatHistory> histories = chatHistoryService.getUserChatHistory(userId, limit);
            return Result.ok().message("获取聊天历史成功").data("data", histories);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("获取聊天历史异常：" + e.getMessage());
        }
    }

    /**
     * 获取用户聊天记录数
     * 
     * @param userId 用户ID
     * @return 聊天记录数
     */
    @GetMapping("/history/count/{userId}")
    public Result countUserChatHistory(@PathVariable Long userId) {
        try {
            long count = chatHistoryService.countUserChatHistory(userId);
            return Result.ok().message("获取聊天记录数成功").data("count", count);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("获取聊天记录数异常：" + e.getMessage());
        }
    }

    /**
     * 删除聊天记录
     * 
     * @param id 聊天记录ID
     * @return 删除结果
     */
    @DeleteMapping("/history/{id}")
    public Result deleteChatHistory(@PathVariable Long id) {
        try {
            int result = chatHistoryService.deleteChatHistory(id);
            if (result > 0) {
                return Result.ok().message("聊天记录删除成功");
            } else {
                return Result.error().message("聊天记录删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("删除聊天记录异常：" + e.getMessage());
        }
    }

    // ============ 用户画像相关接口 ============

    /**
     * 检查用户是否需要填写问卷
     * 
     * @param userId 用户ID
     * @return 是否需要填写问卷
     */
    @GetMapping("/profile/need-fill/{userId}")
    public Result needFillProfile(@PathVariable Long userId) {
        try {
            boolean needFill = userProfileService.needFillProfile(userId);
            return Result.ok().message("检查成功").data("needFill", needFill);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("检查问卷填写状态异常：" + e.getMessage());
        }
    }

    /**
     * 获取用户画像
     * 
     * @param userId 用户ID
     * @return 用户画像信息
     */
    @GetMapping("/profile/{userId}")
    public Result getUserProfile(@PathVariable Long userId) {
        try {
            UserProfile profile = userProfileService.getUserProfile(userId);
            if (profile == null) {
                return Result.error().message("用户画像不存在");
            }
            return Result.ok().message("获取成功").data("profile", profile);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("获取用户画像异常：" + e.getMessage());
        }
    }

    /**
     * 保存用户问卷
     * 
     * @param profileDTO 用户问卷DTO
     * @return 保存结果
     */
    @PostMapping("/profile/save")
    public Result saveUserProfile(@RequestBody UserProfileDTO profileDTO) {
        try {
            boolean result = userProfileService.saveUserProfile(profileDTO);
            if (result) {
                // 异步生成AI内容
                new Thread(() -> {
                    try {
                        userProfileService.generateUserPortrait(profileDTO.getUserId());
                        userProfileService.generateLearningPlan(profileDTO.getUserId());
                        userProfileService.generateCompetitionRecommendation(profileDTO.getUserId());
                        userProfileService.generateEmploymentGuidance(profileDTO.getUserId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                return Result.ok().message("问卷保存成功，AI正在生成您的专属计划...");
            } else {
                return Result.error().message("问卷保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("保存问卷异常：" + e.getMessage());
        }
    }

    /**
     * 更新下次登录提醒状态（用户点击"下次再说"时调用）
     * 
     * @param userId 用户ID
     * @return 更新结果
     */
    @PutMapping("/profile/remind/{userId}")
    public Result updateRemindNextLogin(@PathVariable Long userId) {
        try {
            int result = userProfileService.updateRemindNextLogin(userId);
            if (result > 0) {
                return Result.ok().message("已记录，下次登录再提醒");
            } else {
                return Result.error().message("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("更新提醒状态异常：" + e.getMessage());
        }
    }

    /**
     * 获取用户的AI生成内容
     * 
     * @param userId 用户ID
     * @param type   内容类型（portrait|learning|competition|employment）
     * @return AI生成的内容
     */
    @GetMapping("/profile/content/{userId}")
    public Result getUserProfileContent(@PathVariable Long userId, @RequestParam String type) {
        try {
            UserProfile profile = userProfileService.getUserProfile(userId);
            if (profile == null) {
                return Result.error().message("用户画像不存在");
            }

            String content = null;
            String contentType = "";
            switch (type) {
                case "portrait":
                    content = profile.getUserPortrait();
                    contentType = "用户印象";
                    break;
                case "learning":
                    content = profile.getLearningPlan();
                    contentType = "学习计划";
                    break;
                case "competition":
                    content = profile.getCompetitionRecommendation();
                    contentType = "竞赛推荐";
                    break;
                case "employment":
                    content = profile.getEmploymentGuidance();
                    contentType = "就业指导";
                    break;
                default:
                    return Result.error().message("内容类型不支持");
            }

            if (content == null || content.isEmpty()) {
                return Result.error().message(contentType + "正在生成中，请稍候...");
            }

            return Result.ok().message("获取成功").data("content", content);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().message("获取内容异常：" + e.getMessage());
        }
    }
}
