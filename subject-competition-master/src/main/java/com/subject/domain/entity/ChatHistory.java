package com.subject.domain.entity;

import java.util.Date;

/**
 * 聊天历史记录实体
 */
public class ChatHistory {
  private Long id;
  private Long userId;
  private String userMessage;
  private String assistantMessage;
  private String thinkingProcess;
  private String topic;
  private Integer chatType;
  private Date createTime;
  private Date updateTime;
  private Integer delFlag;

  public ChatHistory() {
  }

  public ChatHistory(Long userId, String userMessage, String assistantMessage) {
    this.userId = userId;
    this.userMessage = userMessage;
    this.assistantMessage = assistantMessage;
    this.createTime = new Date();
    this.delFlag = 0;
  }

  public ChatHistory(Long userId, String userMessage, String assistantMessage, String thinkingProcess) {
    this(userId, userMessage, assistantMessage);
    this.thinkingProcess = thinkingProcess;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  public String getAssistantMessage() {
    return assistantMessage;
  }

  public void setAssistantMessage(String assistantMessage) {
    this.assistantMessage = assistantMessage;
  }

  public String getThinkingProcess() {
    return thinkingProcess;
  }

  public void setThinkingProcess(String thinkingProcess) {
    this.thinkingProcess = thinkingProcess;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public Integer getChatType() {
    return chatType;
  }

  public void setChatType(Integer chatType) {
    this.chatType = chatType;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public Integer getDelFlag() {
    return delFlag;
  }

  public void setDelFlag(Integer delFlag) {
    this.delFlag = delFlag;
  }
}
