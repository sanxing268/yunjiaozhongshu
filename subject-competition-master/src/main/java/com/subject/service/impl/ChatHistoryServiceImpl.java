package com.subject.service.impl;

import com.subject.domain.entity.ChatHistory;
import com.subject.mapper.ChatHistoryMapper;
import com.subject.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 聊天历史记录业务实现
 */
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

  private static final Logger log = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);

  @Autowired
  private ChatHistoryMapper chatHistoryMapper;

  @Override
  public int saveChatHistory(ChatHistory chatHistory) {
    // 补充默认值
    if (chatHistory.getCreateTime() == null) {
      chatHistory.setCreateTime(new Date());
    }
    if (chatHistory.getDelFlag() == null) {
      chatHistory.setDelFlag(0);
    }

    log.info("保存聊天记录 - userId: {}, userMessage长度: {}, assistantMessage长度: {}, thinkingProcess长度: {}",
        chatHistory.getUserId(),
        chatHistory.getUserMessage() != null ? chatHistory.getUserMessage().length() : 0,
        chatHistory.getAssistantMessage() != null ? chatHistory.getAssistantMessage().length() : 0,
        chatHistory.getThinkingProcess() != null ? chatHistory.getThinkingProcess().length() : 0);

    int result = chatHistoryMapper.insert(chatHistory);

    if (result > 0) {
      log.info("聊天记录保存成功 - id: {}", chatHistory.getId());
    } else {
      log.warn("聊天记录保存失败");
    }

    return result;
  }

  @Override
  public List<ChatHistory> getUserChatHistory(Long userId, int limit) {
    return chatHistoryMapper.selectByUserId(userId, limit);
  }

  @Override
  public long countUserChatHistory(Long userId) {
    return chatHistoryMapper.countByUserId(userId);
  }

  @Override
  public int deleteChatHistory(Long id) {
    return chatHistoryMapper.deleteById(id);
  }
}
