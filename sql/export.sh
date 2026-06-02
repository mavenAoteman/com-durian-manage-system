#!/bin/bash
# ============================================================
# 手工皂管理系统 - 数据库迁移脚本
#
# 使用步骤：
#   1. 在远端环境可连接时，运行本脚本（需要 mysqldump）：
#      ./sql/export.sh
#      执行后会生成 sql/data.sql
#
#   2. 在本地 MySQL 初始化数据库：
#      mysql -u root -p -e "CREATE DATABASE durian CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
#      mysql -u root -p durian < sql/schema.sql
#      mysql -u root -p durian < sql/data.sql
#
#   3. 修改 application.properties 指向本地数据库：
#      spring.datasource.druid.url=jdbc:mysql://localhost:3306/durian
#      spring.datasource.username=root
#      spring.datasource.password=你的本地密码
# ============================================================

set -e

# 远端数据库配置（从环境变量读取，避免硬编码凭据）
REMOTE_HOST="${REMOTE_HOST:-127.0.0.1}"
REMOTE_PORT="${REMOTE_PORT:-3306}"
REMOTE_USER="${REMOTE_USER:-root}"
REMOTE_PASS="${REMOTE_PASS:-}"
DATABASE="${DATABASE:-durian}"
OUTPUT_FILE="sql/data.sql"

echo "==> 导出远端数据库 $DATABASE 的数据（不含表结构）..."
mysqldump -h "$REMOTE_HOST" -P "$REMOTE_PORT" -u "$REMOTE_USER" -p"$REMOTE_PASS" \
  --single-transaction --quick --routines --triggers \
  --no-create-info \
  "$DATABASE" > "$OUTPUT_FILE"

echo "==> 导出完成：$OUTPUT_FILE"
echo "==> 行数：$(wc -l < "$OUTPUT_FILE")"
echo ""
echo "==> 下一步："
echo "   1. mysql -u root -p -e \"CREATE DATABASE durian ...\""
echo "   2. mysql -u root -p durian < sql/schema.sql"
echo "   3. mysql -u root -p durian < sql/data.sql"
echo "   4. 修改 com-durian-manage-system-web/src/main/resources/application.properties"