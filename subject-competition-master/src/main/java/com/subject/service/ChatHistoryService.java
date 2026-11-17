package com.subject.service;

import com.subject.domain.entity.ChatHistory;
import java.util.List;

/**
 * 聊天历史记录业务层接口
 */
public interface ChatHistoryService {

  /**
   * 保存聊天记录
   */
  int saveChatHistory(ChatHistory chatHistory);

  /**
   * 获取用户的聊天历史
   */
  List<ChatHistory> getUserChatHistory(Long userId, int limit);

  /**
   * 获取用户聊天记录数
   */
  long countUserChatHistory(Long userId);

  /**
   * 删除聊天记录
   */
  int deleteChatHistory(Long id);
}
