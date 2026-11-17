-- =============================================
-- 后台菜单新增：竞赛成果管理 + 文件下载管理
-- =============================================

-- 1. 新增菜单项
-- 竞赛成果管理（放在"竞赛管理"栏目下，parentId=12）
INSERT INTO `admin_menu` (`path`, `name`, `name_zh`, `icon_cls`, `component`, `parent_id`)
VALUES ('/admin/team/achievement', 'AchievementManagement', '竞赛成果管理', '', 'team/AchievementManagement', 12);

-- 文件下载管理（放在"内容管理"栏目下，parentId=6）
INSERT INTO `admin_menu` (`path`, `name`, `name_zh`, `icon_cls`, `component`, `parent_id`)
VALUES ('/admin/content/download', 'DownloadManagement', '文件下载管理', '', 'content/DownloadManagement', 6);

-- 2. 为各角色分配新菜单权限
-- 获取刚插入的菜单ID（竞赛成果管理）
SET @achievement_menu_id = (SELECT id FROM admin_menu WHERE name = 'AchievementManagement' AND component = 'team/AchievementManagement' LIMIT 1);
-- 获取刚插入的菜单ID（文件下载管理）
SET @download_menu_id = (SELECT id FROM admin_menu WHERE name = 'DownloadManagement' AND component = 'content/DownloadManagement' LIMIT 1);

-- 管理员(rid=1)：拥有全部菜单
INSERT INTO `admin_role_menu` (`rid`, `mid`) VALUES (1, @achievement_menu_id);
INSERT INTO `admin_role_menu` (`rid`, `mid`) VALUES (1, @download_menu_id);

-- 学生(rid=4)：可查看竞赛成果管理（已有竞赛管理权限parent=12）
INSERT INTO `admin_role_menu` (`rid`, `mid`) VALUES (4, @achievement_menu_id);

-- 教师(rid=5)：可查看竞赛成果管理和文件下载管理
INSERT INTO `admin_role_menu` (`rid`, `mid`) VALUES (5, @achievement_menu_id);
INSERT INTO `admin_role_menu` (`rid`, `mid`) VALUES (5, @download_menu_id);
