-- =====================================================
-- v3 升级：user 表加 workshop_name + bio，用于"个人中心"页
-- =====================================================

ALTER TABLE `user`
  ADD COLUMN workshop_name varchar(100) DEFAULT NULL COMMENT '皂坊名称（默认: 用户名+的皂坊）' AFTER email,
  ADD COLUMN bio           varchar(500) DEFAULT NULL COMMENT '个人简介' AFTER workshop_name;

-- 历史用户初始化默认 workshop_name
UPDATE `user`
SET    workshop_name = CONCAT(username, '的皂坊')
WHERE  workshop_name IS NULL;
