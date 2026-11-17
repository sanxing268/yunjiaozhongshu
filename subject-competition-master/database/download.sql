-- 文件下载表
CREATE TABLE IF NOT EXISTS `sys_download` (
  `download_id` INT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `title` VARCHAR(255) NOT NULL COMMENT '文件标题',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '文件描述',
  `file_url` VARCHAR(500) NOT NULL COMMENT '文件下载链接/路径',
  `file_size` VARCHAR(50) DEFAULT NULL COMMENT '文件大小',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '文件分类：通知文件/报名表/比赛规则/其他',
  `download_count` INT DEFAULT 0 COMMENT '下载次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` TINYINT DEFAULT 0 COMMENT '软删除标志 0-正常 1-删除',
  PRIMARY KEY (`download_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件下载表';

-- 测试数据
INSERT INTO `sys_download` (`title`, `description`, `file_url`, `file_size`, `category`) VALUES
('2025年学科竞赛报名表模板', '学科竞赛统一报名表模板，请按要求填写后提交', '#', '25KB', '报名表'),
('全国大学生数学建模竞赛参赛规则', '2025年数学建模竞赛参赛规则及注意事项', '#', '180KB', '比赛规则'),
('互联网+创新创业大赛申报书模板', '校内选拔赛申报书模板及填写说明', '#', '45KB', '报名表'),
('学科竞赛管理办法（2025年修订版）', '学校学科竞赛管理办法最新修订版', '#', '320KB', '通知文件'),
('竞赛经费报销流程说明', '学科竞赛相关经费报销流程及所需材料清单', '#', '56KB', '其他');
