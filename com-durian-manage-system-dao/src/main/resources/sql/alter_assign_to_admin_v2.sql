-- =====================================================
-- v2 升级脚本：把现有业务数据全部归属给 admin，并提升为超级管理员
--
-- 前置条件：admin 账号必须已经存在（通过前端注册）
--   如不存在，请先去 /register 用 admin 注册一个，再跑本脚本
--
-- 执行：mysql -uroot -p durian < alter_assign_to_admin_v2.sql
-- =====================================================

-- 1. 查找 admin 的 id
SET @adminid = (SELECT id FROM `user` WHERE username = 'admin' LIMIT 1);

-- 2. 检查 & 打印状态
SELECT IF(
    @adminid IS NULL,
    '❌ 错误：admin 不存在！请先在前端 /register 注册（用户名 admin，密码至少 8 位含字母+数字），然后重新执行本脚本。',
    CONCAT('✅ 找到用户 admin, id = ', @adminid, '，开始迁移数据 ...')
) AS status;

-- 3. 提升角色到超级管理员（仅当用户存在时）
UPDATE `user`
SET    role = 1
WHERE  id = @adminid
   AND @adminid IS NOT NULL;

-- 4. 迁移所有业务表数据归属
--    只迁 owner_user_id = 0 的历史数据；其他用户后续注册添加的不动
UPDATE oil
SET    owner_user_id = @adminid
WHERE  owner_user_id = 0 AND @adminid IS NOT NULL;

UPDATE soap_consumables
SET    owner_user_id = @adminid
WHERE  owner_user_id = 0 AND @adminid IS NOT NULL;

UPDATE soap_tools
SET    owner_user_id = @adminid
WHERE  owner_user_id = 0 AND @adminid IS NOT NULL;

UPDATE soap_production
SET    owner_user_id = @adminid
WHERE  owner_user_id = 0 AND @adminid IS NOT NULL;

UPDATE soap_piece
SET    owner_user_id = @adminid
WHERE  owner_user_id = 0 AND @adminid IS NOT NULL;

UPDATE soap_package
SET    owner_user_id = @adminid
WHERE  owner_user_id = 0 AND @adminid IS NOT NULL;

-- 5. 完成提示 & 各表迁移行数
SELECT '迁移完成，下面是各表归属于 admin 的数据条数：' AS done;

SELECT 'oil'              AS table_name, COUNT(*) AS rows_owned FROM oil              WHERE owner_user_id = @adminid
UNION ALL
SELECT 'soap_consumables' AS table_name, COUNT(*) AS rows_owned FROM soap_consumables WHERE owner_user_id = @adminid
UNION ALL
SELECT 'soap_tools'       AS table_name, COUNT(*) AS rows_owned FROM soap_tools       WHERE owner_user_id = @adminid
UNION ALL
SELECT 'soap_production'  AS table_name, COUNT(*) AS rows_owned FROM soap_production  WHERE owner_user_id = @adminid
UNION ALL
SELECT 'soap_piece'       AS table_name, COUNT(*) AS rows_owned FROM soap_piece       WHERE owner_user_id = @adminid
UNION ALL
SELECT 'soap_package'     AS table_name, COUNT(*) AS rows_owned FROM soap_package     WHERE owner_user_id = @adminid;
