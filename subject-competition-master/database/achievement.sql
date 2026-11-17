-- 竞赛成果表
CREATE TABLE IF NOT EXISTS `sys_achievement` (
  `achievement_id` INT NOT NULL AUTO_INCREMENT COMMENT '成果ID',
  `title` VARCHAR(255) NOT NULL COMMENT '获奖项目名称',
  `level` VARCHAR(50) DEFAULT NULL COMMENT '级别：国家级/省级/市级/校级',
  `team` VARCHAR(255) DEFAULT NULL COMMENT '获奖团队/个人名称',
  `award_date` DATE DEFAULT NULL COMMENT '获奖日期',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '成果描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` TINYINT DEFAULT 0 COMMENT '软删除标志 0-正常 1-删除',
  PRIMARY KEY (`achievement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛成果表';

-- 测试数据
INSERT INTO `sys_achievement` (`title`, `level`, `team`, `award_date`, `description`) VALUES
('全国大学生数学建模竞赛一等奖', '国家级', '张三、李四、王五', '2025-11-15', '在全国大学生数学建模竞赛中荣获一等奖'),
('省级"互联网+"创新创业大赛金奖', '省级', '创新团队A组', '2025-10-20', '省级"互联网+"创新创业大赛金奖项目'),
('全国大学生电子设计竞赛二等奖', '国家级', '电子创新小组', '2025-09-10', '全国大学生电子设计竞赛获得二等奖'),
('市级程序设计竞赛一等奖', '市级', '编程之星队', '2025-08-05', '市级ACM程序设计竞赛一等奖'),
('校级创新创业大赛特等奖', '校级', '未来科技团队', '2025-06-18', '校级创新创业大赛特等奖作品'),
('全国大学生机器人大赛三等奖', '国家级', '智能机器人团队', '2025-05-22', '全国大学生机器人大赛三等奖');
