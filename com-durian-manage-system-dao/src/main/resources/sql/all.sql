-- MySQL dump 10.13  Distrib 8.0.34, for macos13 (arm64)
--
-- Host: localhost    Database: durian
-- ------------------------------------------------------
-- Server version	5.7.14-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
--
--
-- Table structure for table `oil`
--

DROP TABLE IF EXISTS `oil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oil` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增的主键，用于唯一标识每种油脂',
  `oil_chinese_name` varchar(50) NOT NULL COMMENT '油脂的中文名称',
  `oil_english_name` varchar(50) NOT NULL COMMENT '油脂的英文名称',
  `sodium_hydroxide_saponification_value` decimal(10,2) NOT NULL COMMENT '氢氧化钠的皂化值',
  `potassium_hydroxide_saponification_value` decimal(10,2) NOT NULL COMMENT '氢氧化钾的皂化值',
  `ins_value` decimal(10,2) NOT NULL COMMENT 'INS值',
  `creator` varchar(50) NOT NULL COMMENT '创建者',
  `modifier` varchar(50) NOT NULL COMMENT '修改者',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='油脂表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `soap_consumables`
--

DROP TABLE IF EXISTS `soap_consumables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `soap_consumables` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增的主键，用于唯一标识每个物料',
  `name` varchar(500) NOT NULL COMMENT '物料名称',
  `brand` varchar(50) NOT NULL COMMENT '物料的品牌名称',
  `quantity` int(11) NOT NULL COMMENT '物料的数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '物料的单价',
  `total_price` decimal(10,2) NOT NULL COMMENT '物料的总价',
  `status` int(11) NOT NULL COMMENT '状态（0：已删除 1: 可用, 2: 已用完, 3: 已过期）',
  `expiry_date` date DEFAULT NULL COMMENT '物料的过期日期',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '物料记录的创建时间',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '物料记录的更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '添加该物料的用户姓名',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '最后更新该物料的用户姓名',
  `unit` enum('g','ml','mg','kg','ug','ng','l','ul','cl','dl','tsp','tbsp','cup','drop','oz','lb') NOT NULL DEFAULT 'g' COMMENT '单位类型',
  `consumable_type` int(11) NOT NULL DEFAULT 0 COMMENT '物料类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='物料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `soap_consumables_usage`
--

DROP TABLE IF EXISTS `soap_consumables_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `soap_consumables_usage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `soap_id` int(11) NOT NULL COMMENT '关联皂的生产记录ID',
  `consumable_id` int(11) NOT NULL COMMENT '关联物料ID',
  `quantity_used` decimal(10,3) NOT NULL COMMENT '物料使用量（支持小数，如0.5升）',
  `unit_price` decimal(10,2) NOT NULL COMMENT '使用时的物料单价（快照）',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_soap` (`soap_id`),
  KEY `fk_consumable` (`consumable_id`),
  CONSTRAINT `fk_consumable` FOREIGN KEY (`consumable_id`) REFERENCES `soap_consumables` (`id`),
  CONSTRAINT `fk_soap` FOREIGN KEY (`soap_id`) REFERENCES `soap_production` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料使用明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `soap_production`
--

DROP TABLE IF EXISTS `soap_production`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `soap_production` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(200) NOT NULL COMMENT '皂的名称',
  `batch_number` varchar(50) NOT NULL COMMENT '生产批次号（唯一）',
  `production_date` date NOT NULL COMMENT '生产日期',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态（1: 生产中, 2: 已入库, 3: 已售罄）',
  `total_cost` decimal(10,2) NOT NULL COMMENT '总成本（自动计算）',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_batch` (`batch_number`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='手工皂生产记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `soap_tools`
--

DROP TABLE IF EXISTS `soap_tools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `soap_tools` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增的主键，用于唯一标识每个工具',
  `name` varchar(500) NOT NULL COMMENT '工具名称',
  `brand` varchar(50) NOT NULL COMMENT '工具的品牌名称',
  `quantity` int(11) NOT NULL COMMENT '工具的数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '工具的单价',
  `total_price` decimal(10,2) NOT NULL,
  `status` int(11) NOT NULL COMMENT '状态',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '工具记录的创建时间',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '工具记录的更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '添加该工具的用户姓名',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '最后更新该工具的用户姓名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COMMENT='工具表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-13 13:57:43
