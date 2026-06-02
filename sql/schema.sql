-- ============================================================
-- 手工皂管理系统 - 完整数据库初始化脚本
-- 使用说明：
--   1. 在本地 MySQL 创建数据库：CREATE DATABASE durian CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
--   2. 先执行 schema.sql（创建表结构）
--   3. 再执行 data.sql（从远端 mysqldump 导出的数据）
-- ============================================================

-- --------------------------------------------------------
-- Part 1: 表结构（对应 all.sql + alter 脚本合并整理）
-- --------------------------------------------------------

DROP TABLE IF EXISTS `oil`;
DROP TABLE IF EXISTS `soap_consumables`;
DROP TABLE IF EXISTS `soap_consumables_usage`;
DROP TABLE IF EXISTS `soap_production`;
DROP TABLE IF EXISTS `soap_tools`;
DROP TABLE IF EXISTS `soap_piece`;
DROP TABLE IF EXISTS `soap_package`;
DROP TABLE IF EXISTS `soap_package_piece`;
DROP TABLE IF EXISTS `soap_package_material`;

-- --------------------------------------------------------
-- 油脂表（配方灵感）
-- --------------------------------------------------------
CREATE TABLE `oil` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oil_chinese_name` varchar(50) NOT NULL,
  `oil_english_name` varchar(50) DEFAULT NULL,
  `sodium_hydroxide_saponification_value` decimal(10,2) NOT NULL,
  `potassium_hydroxide_saponification_value` decimal(10,2) NOT NULL,
  `ins_value` decimal(10,2) DEFAULT NULL,
  `creator` varchar(50) NOT NULL,
  `modifier` varchar(50) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'active',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 物料表（油脂、精油、添加剂、水相、包装材料、赠品）
-- --------------------------------------------------------
CREATE TABLE `soap_consumables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL COMMENT '物料名称',
  `brand` varchar(50) DEFAULT '' COMMENT '品牌',
  `quantity` decimal(10,3) NOT NULL DEFAULT 0 COMMENT '库存数量(支持小数)',
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '总价',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '0=已删除 1=可用 2=已用完 3=已过期',
  `expiry_date` date DEFAULT NULL COMMENT '过期日期',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `unit` ENUM('g','ml','mg','kg','ug','ng','l','ul','cl','dl','tsp','tbsp','cup','drop','oz','lb','ge','zhang','bao','kun') NOT NULL DEFAULT 'g' COMMENT '单位',
  `consumable_type` int(11) NOT NULL DEFAULT 0 COMMENT '类型: 0=其他 1=油脂 2=精油 3=添加剂 4=水相 5=包装材料 6=赠品',
  `purchase_channel` varchar(100) DEFAULT NULL COMMENT '采购渠道',
  `purchase_time` datetime DEFAULT NULL COMMENT '采购时间',
  `weight_in_grams` decimal(10,3) DEFAULT NULL COMMENT '克重(换算用)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 物料使用明细（快照价格）
-- --------------------------------------------------------
CREATE TABLE `soap_consumables_usage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `soap_id` int(11) NOT NULL COMMENT '关联生产记录ID',
  `consumable_id` int(11) NOT NULL COMMENT '关联物料ID',
  `quantity_used` decimal(10,3) NOT NULL COMMENT '使用量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '使用时的单价(快照)',
  `unit` varchar(10) DEFAULT NULL COMMENT '使用时的单位',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_soap` (`soap_id`),
  KEY `fk_consumable` (`consumable_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 手工皂生产记录
-- --------------------------------------------------------
CREATE TABLE `soap_production` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL COMMENT '批次名称',
  `batch_number` varchar(50) NOT NULL COMMENT '批次号',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1=生产中 2=晾皂中 3=成熟中 4=已切块 5=已成熟',
  `total_cost` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '总物料成本',
  `total_weight` decimal(10,2) DEFAULT NULL COMMENT '入模总重量(g)',
  `cut_weight` decimal(10,2) DEFAULT NULL COMMENT '切块重量(g)',
  `actual_weight` decimal(10,2) DEFAULT NULL COMMENT '成熟后实际重量(g)',
  `maturation_date` date DEFAULT NULL COMMENT '预计成熟日期',
  `actual_maturation_date` date DEFAULT NULL COMMENT '实际成熟日期',
  `notes` varchar(500) DEFAULT NULL COMMENT '备注',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_batch` (`batch_number`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 皂块分装表
-- --------------------------------------------------------
CREATE TABLE `soap_piece` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `production_id` int(11) NOT NULL COMMENT '关联生产记录ID',
  `piece_name` varchar(100) DEFAULT NULL COMMENT '皂块名称',
  `piece_batch_number` varchar(50) DEFAULT NULL COMMENT '皂块批次号',
  `weight` decimal(10,2) NOT NULL COMMENT '重量(g)',
  `price` decimal(10,2) DEFAULT NULL COMMENT '售价',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1=在售 2=已售 3=赠送 4=打包中',
  `notes` varchar(200) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_piece_production` (`production_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 工具表
-- --------------------------------------------------------
CREATE TABLE `soap_tools` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL COMMENT '工具名称',
  `brand` varchar(50) DEFAULT '' COMMENT '品牌',
  `quantity` decimal(10,3) NOT NULL DEFAULT 0 COMMENT '数量',
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '总价',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 打包发货记录
-- --------------------------------------------------------
CREATE TABLE `soap_package` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_number` varchar(50) NOT NULL COMMENT '打包批次号',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '1=待内包装 2=待外包装 3=已完成',
  `total_material_cost` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '打包物料总成本',
  `notes` varchar(500) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 打包关联皂块
-- --------------------------------------------------------
CREATE TABLE `soap_package_piece` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package_id` int(11) NOT NULL,
  `piece_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pkg_piece_pkg` (`package_id`),
  KEY `fk_pkg_piece_piece` (`piece_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- 打包物料明细
-- --------------------------------------------------------
CREATE TABLE `soap_package_material` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package_id` int(11) NOT NULL,
  `consumable_id` int(11) DEFAULT NULL COMMENT '关联物料ID（赠品可为空）',
  `material_type` varchar(20) NOT NULL COMMENT 'inner/outer/gift',
  `quantity_used` decimal(10,3) NOT NULL DEFAULT 0 COMMENT '用量',
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '单价(快照)',
  `unit` varchar(10) DEFAULT NULL COMMENT '单位',
  PRIMARY KEY (`id`),
  KEY `fk_mat_pkg` (`package_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;