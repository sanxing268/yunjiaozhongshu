package com.subject.commons.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.subject.commons.constant.AIConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AI工具类
 * 提供AI相关的辅助方法
 */
public class AIUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 获取系统提示词
   * 
   * @return 系统提示词
   */
  public static String getSystemPrompt() {
    return AIConstants.SYSTEM_PROMPT;
  }

  /**
   * 创建系统消息
   * 
   * @return 系统消息JSON对象
   */
  public static ObjectNode createSystemMessage() {
    ObjectNode message = objectMapper.createObjectNode();
    message.put("role", AIConstants.Role.SYSTEM);
    message.put("content", getSystemPrompt());
    return message;
  }

  /**
   * 创建用户消息
   * 
   * @param content 消息内容
   * @return 用户消息JSON对象
   */
  public static ObjectNode createUserMessage(String content) {
    ObjectNode message = objectMapper.createObjectNode();
    message.put("role", AIConstants.Role.USER);
    message.put("content", content);
    return message;
  }

  /**
   * 创建助手消息
   * 
   * @param content 消息内容
   * @return 助手消息JSON对象
   */
  public static ObjectNode createAssistantMessage(String content) {
    ObjectNode message = objectMapper.createObjectNode();
    message.put("role", AIConstants.Role.ASSISTANT);
    message.put("content", content);
    return message;
  }

  /**
   * 构建包含系统提示词的消息列表
   * 
   * @param originalMessages 原始消息列表
   * @return 包含系统提示词的消息列表
   */
  public static String enrichMessagesWithSystemPrompt(String originalMessages) throws IOException {
    JsonNode messagesNode = objectMapper.readTree(originalMessages);
    ArrayNode enrichedMessages = objectMapper.createArrayNode();

    // 首先添加系统提示词
    enrichedMessages.add(createSystemMessage());

    // 检查原始消息是否已经包含系统提示词
    boolean hasSystemPrompt = false;
    if (messagesNode.isArray()) {
      for (JsonNode msg : messagesNode) {
        JsonNode role = msg.get("role");
        if (role != null && AIConstants.Role.SYSTEM.equals(role.asText())) {
          hasSystemPrompt = true;
          break;
        }
      }
    }

    // 如果原始消息中没有系统提示词，或者已经有系统提示词，直接添加所有消息
    if (messagesNode.isArray()) {
      for (JsonNode msg : messagesNode) {
        if (!AIConstants.Role.SYSTEM.equals(msg.get("role").asText()) || !hasSystemPrompt) {
          enrichedMessages.add(msg);
        } else if (hasSystemPrompt) {
          // 如果原始消息中已经有系统提示词，跳过它（避免重复）
          continue;
        }
      }
    }

    return objectMapper.writeValueAsString(enrichedMessages);
  }

  /**
   * 验证消息列表格式
   * 
   * @param messagesJson 消息JSON字符串
   * @return 是否有效
   */
  public static boolean isValidMessages(String messagesJson) {
    try {
      JsonNode messagesNode = objectMapper.readTree(messagesJson);
      if (!messagesNode.isArray()) {
        return false;
      }

      // 检查每条消息的必要字段
      for (JsonNode msg : messagesNode) {
        if (!msg.has("role") || !msg.has("content")) {
          return false;
        }
      }

      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * 创建SSE事件
   * 
   * @param eventType 事件类型
   * @param content   事件内容
   * @return SSE格式的事件字符串
   */
  public static String createSSEEvent(String eventType, String content) throws IOException {
    ObjectNode event = objectMapper.createObjectNode();
    event.put("type", eventType);
    if (content != null) {
      event.put("content", content);
    }
    return "data: " + objectMapper.writeValueAsString(event) + "\n\n";
  }

  /**
   * 创建错误事件
   * 
   * @param errorMessage 错误消息
   * @return SSE格式的错误事件字符串
   */
  public static String createErrorEvent(String errorMessage) throws IOException {
    ObjectNode error = objectMapper.createObjectNode();
    error.put("type", AIConstants.EventType.ERROR);
    error.put("error", errorMessage);
    return "data: " + objectMapper.writeValueAsString(error) + "\n\n";
  }

  /**
   * 解析流式响应中的内容
   * 
   * @param line SSE格式的行
   * @return 解析后的JSON对象，如果不是data行则返回null
   */
  public static JsonNode parseSSELine(String line) throws IOException {
    if (line != null && line.startsWith("data: ")) {
      String data = line.substring(6).trim();
      if (!data.isEmpty() && !"[DONE]".equals(data)) {
        return objectMapper.readTree(data);
      }
    }
    return null;
  }

  /**
   * 获取ObjectMapper实例
   * 
   * @return ObjectMapper
   */
  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

}
