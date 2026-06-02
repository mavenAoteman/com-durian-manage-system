#!/bin/bash
# ============================================================
# 手工皂管理系统 - 本地数据库初始化脚本
#
# 使用前提：
#   本地已安装 MySQL（命令行 mysql 可用）
#
# 使用步骤：
#   1. chmod +x sql/init.sh
#   2. mysql -u root -p -e "CREATE DATABASE durian ..."
#   3. ./sql/init.sh
# ============================================================

set -e

echo "请确保已通过 mysql 命令创建数据库："
echo "  CREATE DATABASE durian CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo ""
read -p "按 Enter 开始导入表结构..." key

echo "==> 导入表结构..."
mysql -u root -p durian < sql/schema.sql
echo "==> 表结构导入完成"

if [ -s sql/data.sql ]; then
  echo "==> 导入数据..."
  mysql -u root -p durian < sql/data.sql
  echo "==> 数据导入完成"
else
  echo "==> data.sql 为空或不存在，跳过"
fi

echo ""
echo "==> 导入完成！现在修改 application.properties 指向本地："
echo "  spring.datasource.druid.url=jdbc:mysql://localhost:3306/durian"
echo "  spring.datasource.username=root"
echo "  spring.datasource.password=你的本地密码"