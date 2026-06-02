-- 手工皂生产管理系统 v2 升级脚本
-- 1. soap_production 表增加字段
ALTER TABLE soap_production
  ADD COLUMN total_weight decimal(10,2) DEFAULT NULL COMMENT '入模总重量(g)',
  ADD COLUMN actual_weight decimal(10,2) DEFAULT NULL COMMENT '成熟后实际重量(g)',
  ADD COLUMN maturation_date date DEFAULT NULL COMMENT '预计成熟日期',
  ADD COLUMN actual_maturation_date date DEFAULT NULL COMMENT '实际成熟日期',
  ADD COLUMN notes varchar(500) DEFAULT NULL COMMENT '备注',
  MODIFY COLUMN status tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态: 1=生产中, 2=成熟期, 3=已成熟, 4=已切块入库';

-- 2. 新建皂块分装表
DROP TABLE IF EXISTS `soap_piece`;
CREATE TABLE `soap_piece` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `production_id` int(11) NOT NULL COMMENT '关联生产记录ID',
  `piece_name` varchar(100) DEFAULT NULL COMMENT '皂块名称',
  `weight` decimal(10,2) NOT NULL COMMENT '重量(g)',
  `price` decimal(10,2) DEFAULT NULL COMMENT '售价',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1=在售, 2=已售, 3=赠送',
  `notes` varchar(200) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_piece_production` (`production_id`),
  CONSTRAINT `fk_piece_production` FOREIGN KEY (`production_id`) REFERENCES `soap_production` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='皂块分装表';

-- 3. soap_consumables 数量改为支持小数
ALTER TABLE soap_consumables MODIFY COLUMN quantity decimal(10,3) NOT NULL COMMENT '物料的数量(支持小数)';

-- 4. soap_consumables_usage 增加 unit 字段
ALTER TABLE soap_consumables_usage ADD COLUMN unit varchar(10) DEFAULT NULL COMMENT '使用时的单位';
