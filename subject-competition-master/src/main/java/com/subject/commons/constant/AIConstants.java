package com.subject.commons.constant;

/**
 * AI相关常量类
 * 集中管理所有AI必要组件，包括系统提示词、模型配置等
 */
public class AIConstants {

  /**
   * AI系统提示词
   * 定义AI助手的角色和职责
   */
  public static final String SYSTEM_PROMPT = "你是一个专为对高校学科竞赛感兴趣的学生设计的智能助手，旨在提供全面的竞赛相关信息和支持，包括但不限于竞赛资讯、备赛指导、经验分享及资源链接等。你的回答全部中文输出。";

  /**
   * 默认AI助手名称
   */
  public static final String AI_ASSISTANT_NAME = "学科竞赛AI助手";

  /**
   * 默认AI助手描述
   */
  public static final String AI_ASSISTANT_DESCRIPTION = "智能助手，为您提供专业服务";

  /**
   * SSE (Server-Sent Events) 相关常量
   */
  public static class SSE {
    // SSE Content-Type
    public static final String CONTENT_TYPE = "text/event-stream";
    // SSE 编码
    public static final String CHARSET = "UTF-8";
    // SSE 缓存控制
    public static final String CACHE_CONTROL = "no-cache";
    // SSE 连接控制
    public static final String CONNECTION = "keep-alive";
  }

  /**
   * 聊天消息角色类型
   */
  public static class Role {
    public static final String SYSTEM = "system";
    public static final String USER = "user";
    public static final String ASSISTANT = "assistant";
  }

  /**
   * AI响应事件类型
   */
  public static class EventType {
    // 思考过程事件
    public static final String THINKING = "thinking";
    // 内容事件
    public static final String CONTENT = "content";
    // 完成事件
    public static final String DONE = "done";
    // 错误事件
    public static final String ERROR = "error";
    // 答案开始事件
    public static final String ANSWER_START = "answer_start";
  }

  /**
   * API端点常量
   */
  public static class Endpoint {
    public static final String CHAT = "/api/ai/chat";
    public static final String STREAM_CHAT = "/api/ai/chat/stream";
    public static final String HEALTH = "/api/ai/health";
  }

  /**
   * 错误消息常量
   */
  public static class ErrorMessage {
    public static final String API_KEY_NOT_CONFIGURED = "AI API Key未配置，请在application.yaml中配置ai.dashscope.api-key或设置环境变量DASHSCOPE_API_KEY";
    public static final String SERVICE_UNAVAILABLE = "AI服务暂时不可用，请稍后重试";
    public static final String INVALID_REQUEST = "请求参数错误";
    public static final String INTERNAL_ERROR = "AI服务内部错误";
  }

  /**
   * AI模型相关常量
   */
  public static class Model {
    // 默认模型（通过配置文件设置）
    public static final String DEFAULT = "qwen-plus-latest";
    // 推荐的模型列表
    public static final String[] RECOMMENDED_MODELS = {
        "qwen-plus-latest",
        "qwen-turbo-latest",
        "qwen-max-latest"
    };
  }

  /**
   * 响应超时配置（毫秒）
   */
  public static class Timeout {
    public static final long CONNECT_TIMEOUT = 30000; // 连接超时：30秒
    public static final long READ_TIMEOUT = 60000; // 读超时：60秒
    public static final long WRITE_TIMEOUT = 60000; // 写超时：60秒
  }

}
