-- =====================================================
-- 用户模块升级脚本 v1
-- 1. 新建 user 表
-- 2. 给 6 张业务表加 owner_user_id + 索引
-- 3. 现有数据 owner_user_id 默认 0（升级后由 super_admin 手动认领或重新建账号）
-- 执行方式：mysql -uroot -p durian < alter_user_module_v1.sql
-- =====================================================

-- ---------- 1. 用户表 ----------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名（登录名，唯一）',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt 哈希）',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱（选填）',
  `role` tinyint(4) NOT NULL DEFAULT 2 COMMENT '角色：1=超级管理员, 2=普通用户',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 2=禁用',
  `failed_login_count` int(11) NOT NULL DEFAULT 0 COMMENT '连续登录失败次数',
  `locked_until` timestamp NULL DEFAULT NULL COMMENT '锁定截止时间（超过该时间自动解锁）',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT '上次成功登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '上次成功登录 IP',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------- 2. 业务表加 owner_user_id ----------
-- owner_user_id = 0 表示历史遗留数据；新数据必填实际用户 id
-- super_admin (role=1) 在查询时不带 owner_user_id 过滤，可看到全部

ALTER TABLE `oil`
  ADD COLUMN `owner_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据归属用户ID（0=历史数据，super_admin可见全部）' AFTER `id`,
  ADD INDEX `idx_owner_status` (`owner_user_id`, `status`);

ALTER TABLE `soap_consumables`
  ADD COLUMN `owner_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据归属用户ID' AFTER `id`,
  ADD INDEX `idx_owner_status` (`owner_user_id`, `status`);

ALTER TABLE `soap_tools`
  ADD COLUMN `owner_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据归属用户ID' AFTER `id`,
  ADD INDEX `idx_owner_status` (`owner_user_id`, `status`);

ALTER TABLE `soap_production`
  ADD COLUMN `owner_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据归属用户ID' AFTER `id`,
  ADD INDEX `idx_owner_status` (`owner_user_id`, `status`);

ALTER TABLE `soap_piece`
  ADD COLUMN `owner_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据归属用户ID' AFTER `id`,
  ADD INDEX `idx_owner_status` (`owner_user_id`, `status`);

ALTER TABLE `soap_package`
  ADD COLUMN `owner_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '数据归属用户ID' AFTER `id`,
  ADD INDEX `idx_owner_status` (`owner_user_id`, `status`);

-- 子表（soap_consumables_usage / soap_package_piece / soap_package_material）通过父表关联，
-- 不单独加 owner_user_id，查询时连接父表过滤即可。

-- ---------- 3. 初始化超级管理员（可选）----------
-- 推荐做法：先通过前端"注册"界面创建一个普通账号（例如用户名 admin），
-- 然后在数据库执行一条 UPDATE 把它提升为超级管理员：
--   UPDATE `user` SET `role` = 1 WHERE `username` = 'admin';
-- 这样可以避免在 SQL 里手写 BCrypt 哈希。
