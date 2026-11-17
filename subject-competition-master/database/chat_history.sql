-- 聊天历史记录表
CREATE TABLE IF NOT EXISTS chat_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '聊天记录ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  user_message LONGTEXT NOT NULL COMMENT '用户消息内容',
  assistant_message LONGTEXT NOT NULL COMMENT 'AI回复消息内容（最终输出）',
  thinking_process LONGTEXT COMMENT 'AI思考过程（用于展示思考过程）',
  topic VARCHAR(255) COMMENT '对话主题/标签',
  chat_type INT DEFAULT 0 COMMENT '对话类型（0=问答, 1=讨论, 2=解释, 3=其他）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag INT DEFAULT 0 COMMENT '删除标记（0=未删除, 1=已删除）',
  KEY idx_user_id (user_id) COMMENT '用户ID索引',
  KEY idx_topic (topic) COMMENT '主题索引',
  KEY idx_chat_type (chat_type) COMMENT '聊天类型索引',
  KEY idx_create_time (create_time) COMMENT '创建时间索引',
  KEY idx_del_flag (del_flag) COMMENT '删除标记索引'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI聊天历史记录表';