-- 用户画像表
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `school` VARCHAR(255) COMMENT '学校名称',
  `college` VARCHAR(255) COMMENT '学院名称',
  `major` VARCHAR(255) COMMENT '专业名称',
  `grade` VARCHAR(50) COMMENT '年级',
  `gender` VARCHAR(20) COMMENT '性别',
  -- 证书资质（JSON格式存储）
  `certificates` LONGTEXT COMMENT '证书资质（JSON格式）',
  -- 获奖经历（JSON格式存储）
  `awards` LONGTEXT COMMENT '获奖经历（JSON格式）',
  -- 核心技能（JSON格式存储）
  `skills` LONGTEXT COMMENT '核心技能（JSON格式）',
  -- 兴趣爱好（JSON格式存储）
  `interests` LONGTEXT COMMENT '兴趣爱好（JSON格式）',
  -- 竞赛偏好（JSON格式存储）
  `competition_preference` LONGTEXT COMMENT '竞赛偏好（JSON格式）',
  -- AI生成的内容
  `user_portrait` LONGTEXT COMMENT 'AI生成的用户印象',
  `learning_plan` LONGTEXT COMMENT 'AI生成的学习计划',
  `competition_recommendation` LONGTEXT COMMENT 'AI生成的竞赛推荐',
  `employment_guidance` LONGTEXT COMMENT 'AI生成的就业指导',
  -- 问卷填写状态
  `profile_status` INT DEFAULT 0 COMMENT '画像状态：0未填写 1已填写',
  `remind_next_login` INT DEFAULT 1 COMMENT '下次登录是否提醒：0否 1是',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_profile_status` (`profile_status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户画像表';