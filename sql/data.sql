-- MySQL dump 10.13  Distrib 5.7.24, for osx11.1 (x86_64)
--
-- Host: localhost    Database: durian
-- ------------------------------------------------------
-- Server version	5.7.14-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `oil`
--

DROP TABLE IF EXISTS `oil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oil` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增的主键，用于唯一标识每种油脂',
  `oil_chinese_name` varchar(50) NOT NULL COMMENT '油脂的中文名称',
  `oil_english_name` varchar(50) NOT NULL COMMENT '油脂的英文名称',
  `sodium_hydroxide_saponification_value` decimal(10,4) NOT NULL COMMENT '氢氧化钠的皂化值',
  `potassium_hydroxide_saponification_value` decimal(10,4) DEFAULT NULL COMMENT '氢氧化钾皂化价',
  `ins_value` decimal(10,2) NOT NULL COMMENT 'INS值',
  `creator` varchar(50) NOT NULL COMMENT '创建者',
  `modifier` varchar(50) NOT NULL COMMENT '修改者',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8mb4 COMMENT='油脂表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oil`
--

LOCK TABLES `oil` WRITE;
/*!40000 ALTER TABLE `oil` DISABLE KEYS */;
INSERT INTO `oil` VALUES (1,'椰子油','Coconut',0.1900,0.2660,258.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(2,'橄榄油','Olive',0.1340,0.1876,109.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(3,'棕榈油','Palm',0.1410,0.1974,145.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(4,'大豆油','Soybean',0.1350,0.1890,61.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(5,'葵花籽油','Sunflower',0.1340,0.1876,63.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(6,'蓖麻油','Castor',0.1286,0.1800,95.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(7,'芥花籽油','Canola',0.1241,0.1736,56.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(8,'白油','Shortening (veg.)',0.1350,0.1904,115.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(9,'花生油','Peanut',0.1360,0.1904,99.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(10,'葡萄籽油','Grapeseed',0.1265,0.1785,66.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(11,'芝麻油','Sesame Seed',0.1330,0.1862,81.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(12,'米糠油','Rice Bran',0.1280,0.1792,70.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(13,'甜杏仁油','Almond, Sweet',0.1360,0.1904,97.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(14,'杏核油','Apricot Kernel',0.1350,0.1890,91.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(15,'蜂蜡','Beeswax, White',0.0690,0.0966,84.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(16,'乳木果油','Shea Butter',0.1280,0.1792,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(17,'酪梨油','Avocado',0.1330,0.1862,99.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(18,'可可脂','Cocoa Butter',0.1370,0.1918,157.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(19,'榛果油','Hazelnut',0.1356,0.1898,94.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(20,'月见草油','Evening Primrose',0.1357,0.1900,30.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(21,'大麻籽油','Hemp Seed',0.1345,0.1883,39.00,'admin','admin','ACTIVE','2026-05-02 10:33:55','2026-05-02 10:33:55'),(22,'荷荷芭油','Jojoba',0.0690,0.0966,11.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(23,'澳洲胡桃油','Macadamia',0.1390,0.1946,119.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(25,'芒果脂','Mango Butter',0.1371,0.1919,146.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(26,'棕榈核油','Palm Kernel',0.1560,0.2184,227.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(27,'小麦胚芽油','Wheatgerm',0.1310,0.1834,58.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(28,'玫瑰果油','Rose Hip Seed',0.1378,0.1930,16.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(29,'猪油','Lard',0.1380,0.1932,139.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(30,'牛油','Tallow, beef',0.1410,0.1974,147.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(32,'巴西核果油','Brazil Nut',0.1750,0.2450,230.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(33,'玉米油','Corn',0.1360,0.1904,69.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(34,'鸸鹋油','Emu Oil',0.1359,0.1903,128.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(35,'棉籽油','Cottonseed',0.1386,0.1940,89.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(36,'夏威夷核果油','Kukui Nut',0.1350,0.1890,24.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(37,'池花籽油','Meadowfoam Seed',0.1207,0.1690,77.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(38,'羊毛脂','Lanolin',0.0741,0.1037,83.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(39,'核桃仁油','Walnut Kernel',0.1370,0.1918,87.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(40,'开心果油','Pistachio',0.1328,0.1860,92.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(41,'南瓜籽油','Pumpkinseed',0.1331,0.1863,77.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(42,'苧麻油','Ramic',0.1240,0.1736,56.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(43,'油菜花油','Rape',0.1240,0.1736,56.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(44,'松香','Rosin',0.1130,0.1582,108.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(45,'红花籽油','Safflower',0.1360,0.1904,47.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(47,'核桃油','Walnut',0.1353,0.1894,45.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(48,'亚麻籽油','Flaxseed (Linseed)',0.1357,0.1900,-6.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(49,'马油','Horse Fat',0.1390,0.1946,153.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(50,'山茶花油','Camellia Seed Oil',0.1362,0.1910,108.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(51,'鸡油','Chicken Fat',0.1389,0.1945,130.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(52,'鹅油','Goose Fat',0.1369,0.1917,130.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(53,'绵羊油','Tallow, sheep',0.1383,0.1936,156.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(54,'亚麻仁油','Linseed',0.1357,0.1900,-6.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(55,'牛油(Butterfat)','Butterfat',0.1619,0.2267,191.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(56,'羚羊油','Antelope Tallow',0.1378,0.1929,168.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(57,'黄豆油','Soybean Oil (Alt)',0.1350,0.1890,61.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(58,'番瓜子油','Pumpkin (Alt)',0.1331,0.1863,77.00,'admin','admin','ACTIVE','2026-05-02 10:33:56','2026-05-02 10:33:56'),(59,'咖啡豆油','Coffee Bean Oil',0.1300,0.1820,100.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(60,'巴巴苏油','Babassu Oil',0.1750,0.2450,230.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(62,'琉璃苣油','Borage Oil',0.1357,0.1904,30.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(63,'棕榈脂','Palm Butter',0.1560,0.2189,183.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(65,'水蜜桃核仁油','Peach Kernel Oil',0.1370,0.1922,96.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(66,'芥子油','Mustard Oil',0.1241,0.1741,56.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(67,'牛蹄油','Neatsfoot Oil',0.1410,0.1978,124.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(68,'豆蔻脂','Nutmeg Butter (True)',0.1160,0.1627,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(69,'罂粟籽油','Poppy Seed Oil',0.1380,0.1936,54.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(70,'婆罗双树油','Sal (Shorea robusta)',0.1310,0.1838,145.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(71,'熊油','Bear Fat',0.1390,0.1950,150.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(72,'鹿油','Deer Tallow',0.1380,0.1936,166.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(73,'桐油','Tung Oil',0.1380,0.1936,25.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(74,'印度苦楝油','Neem Oil',0.1390,0.1950,124.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(75,'紫苏籽油','Perilla Seed Oil',0.1370,0.1922,-5.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(76,'琼崖海棠油','Tamanu Oil',0.1360,0.1908,85.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(77,'亚麻荠油','Camelina Oil',0.1340,0.1880,58.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(78,'貂油','Mink Oil',0.1400,0.1964,140.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(79,'驼鸟油','Ostrich Oil',0.1390,0.1950,115.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(80,'芥花油','Canola I (org)',0.1320,0.1852,56.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(81,'花生（Arachis）','Arachis',0.1360,0.1908,99.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(82,'非洲核果油','Karite Butter (Shea)',0.1280,0.1796,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(83,'乳油木果脂（雪亚脂）','Shea Butter (Alt)',0.1280,0.1796,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(84,'棉 I','Cotton I',0.1390,0.1950,89.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(85,'瓜哇棉花I','Java Cotton I',0.1326,0.1860,89.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(86,'瓜哇棉花II','Java Cotton II',0.1397,0.1960,89.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(87,'牛脂','Tallow (Alt)',0.1405,0.1971,147.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(88,'棉籽','Cottonseed (Alt)',0.1386,0.1945,89.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(89,'牛足油','Neatsfoot (Alt)',0.1410,0.1978,124.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(90,'羚羊','Antelope (Alt)',0.1378,0.1933,168.00,'admin','admin','ACTIVE','2026-05-02 10:33:57','2026-05-02 10:33:57'),(93,'牡丹籽油','Peony Seed Oil',0.1340,0.1881,186.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(94,'摩洛哥坚果油（阿甘油）','Argan Oil',0.1360,0.1908,97.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(95,'芦荟油','Aloe Oil',0.1390,0.1950,97.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(96,'芦荟脂','Aloe Butter',0.1390,0.1950,97.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(97,'鳄梨油(牛油果)','Avocado',0.1330,0.1866,99.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(98,'酪梨脂','Avocado Butter',0.1340,0.1880,120.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(100,'辣木籽油','Moringa Oil',0.1360,0.1908,100.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(101,'高油酸芥花油','Canola Oil (High Oleic)',0.1330,0.1866,90.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(102,'堪地里拉蜡','Candelilla Wax',0.0380,0.0533,32.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(103,'樱桃核仁油','Cherry Kernel Oil',0.1350,0.1894,62.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(104,'鸭油','Duck Fat',0.1380,0.1936,122.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(105,'大麻籽脂','Hemp Seed Butter',0.1360,0.1908,115.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(106,'金黄乳油木果脂','Kpangnan (Golden Shea)',0.1360,0.1908,149.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(107,'罗勒籽脂','Mowrah Butter',0.1380,0.1936,132.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(109,'橄榄脂','Olive Butter',0.1340,0.1880,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(110,'番西莲种子油','Passion Fruit Seed Oil',0.1300,0.1824,47.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(111,'桃仁油','Peach Kernel (Alt)',0.1360,0.1908,87.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(112,'开心果脂','Pistachio Butter',0.1420,0.1992,120.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(113,'红棕榈油','Red Palm Oil',0.1410,0.1978,110.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(114,'茶籽油（苦茶油）','Oiltea Camellia Oil',0.1390,0.1950,110.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(115,'分馏椰子油','Coconut Oil, fractionated',0.2320,0.3250,324.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(116,'月桂酸','Lauric Acid',0.2000,0.2800,280.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(117,'慕尤慕脂','Murumuru Butter',0.1960,0.2750,203.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(118,'大溪地茉莉油','Monoi de Tahiti Oil',0.1820,0.2550,246.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(119,'肉豆蔻酸','Myristic Acid',0.1760,0.2470,246.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(121,'土库马脂','Tucuma Seed Butter',0.1700,0.2380,175.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(123,'酥油（牛乳脂）','Ghee (any bovine)',0.1620,0.2270,191.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(124,'乳脂','Milk Fat (any bovine)',0.1620,0.2270,191.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(125,'布里蒂油','Buriti Oil',0.1590,0.2230,153.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(126,'锯棕榈油','Saw Palmetto Oil',0.1570,0.2200,176.00,'admin','admin','ACTIVE','2026-05-02 10:33:58','2026-05-02 10:33:58'),(127,'日本蜡','Japan Wax',0.1530,0.2150,204.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(129,'科胡内油','Cohune Oil',0.1460,0.2050,175.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(130,'乌库巴脂','Ucuuba Butter',0.1460,0.2050,167.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(131,'油酸','Oleic Acid',0.1440,0.2020,110.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(132,'兔脂','Rabbit Fat',0.1430,0.2010,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(133,'猴面包树油','Baobab Oil',0.1430,0.2000,125.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(134,'棕榈液油','Palmolein',0.1430,0.2000,142.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(135,'帕塔瓦油','Pataua (Patawa) Oil',0.1430,0.2000,123.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(136,'月桂果油','Laurel Fruit Oil',0.1410,0.1980,124.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(137,'马富拉脂','Mafura Butter',0.1410,0.1980,132.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(138,'水飞蓟油','Milk Thistle Oil',0.1400,0.1960,81.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(139,'马脂','Horse Oil',0.1400,0.1960,117.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(140,'黑籽油','Black Cumin Seed Oil (nigella sativa)',0.1390,0.1950,62.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(141,'沙棘籽油','Sea Buckthorn Oil (seed)',0.1390,0.1950,30.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(142,'李子核油','Plum Kernel Oil',0.1380,0.1940,96.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(143,'麻风树油','Jatropha Oil',0.1380,0.1930,91.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(144,'欧洲酸樱桃核仁油','Cherry Kern Oil (p. cerasus)',0.1370,0.1920,74.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(145,'古布阿苏脂','Cupuacu Butter',0.1370,0.1920,153.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(146,'马鲁拉油','Marula Oil',0.1370,0.1920,119.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(147,'杨古油','Yangu (Cape Chestnut)',0.1370,0.1920,97.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(148,'山羊脂','Tallow, goat',0.1370,0.1920,152.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(149,'黑加仑籽油','Black Currant Seed Oil',0.1350,0.1900,12.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(150,'蔓越莓籽油','Cranberry Seed Oil',0.1350,0.1900,40.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(151,'藤黄果脂','Kokum Butter',0.1350,0.1900,155.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(152,'燕麦油','Oat Oil',0.1350,0.1900,86.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(153,'山核桃油','Pecan Oil',0.1350,0.1900,77.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(154,'石榴籽油','Pomegranate Seed Oil',0.1350,0.1900,168.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(155,'高油酸红花油','Safflower Oil (high oleic)',0.1350,0.1900,97.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(156,'西瓜籽油','Watermelon Seed Oil',0.1350,0.1900,71.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(157,'杏仁脂','Almond Butter',0.1340,0.1880,118.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(158,'安迪罗巴油','Andiroba Oil',0.1340,0.1880,120.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(159,'澳洲胡桃脂','Macadamia Nut Butter',0.1340,0.1880,118.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(160,'印加果油','Sacha Inchi',0.1340,0.1880,47.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(161,'萨普塔籽油','Zapote Seed Oil',0.1340,0.1880,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(162,'丝瓜籽油','Loofa Seed Oil',0.1330,0.1870,79.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(163,'覆盆子籽油','Raspberry Seed Oil',0.1330,0.1870,24.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(164,'绿咖啡油','Coffee Bean Oil (green)',0.1320,0.1850,100.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(165,'婆罗脂','Illipe Butter',0.1320,0.1850,152.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(166,'柳安脂','Sal Butter',0.1320,0.1850,146.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(167,'鲑鱼油','Salmon Oil',0.1320,0.1850,16.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(169,'卡兰贾油','Karanja Oil',0.1300,0.1830,98.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(170,'普拉卡西种子油','Pracaxi Seed Oil',0.1250,0.1750,107.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(171,'西兰花籽油','Broccoli Seed Oil',0.1230,0.1720,67.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(172,'阿比西尼亚油','Abyssinian Oil',0.1200,0.1680,70.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(173,'肉豆蔻脂','Nutmeg Butter',0.1160,0.1620,116.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(174,'木瓜籽油','Papaya Seed Oil',0.1130,0.1580,91.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(175,'胡萝卜籽油','Carrot Seed Oil (cold pressed)',0.1030,0.1440,0.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(176,'羊毛脂蜡','Lanolin (liquid Wax)',0.0760,0.1060,83.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(177,'松焦油','Pine Tar',0.0430,0.0600,0.00,'admin','admin','ACTIVE','2026-05-02 10:33:59','2026-05-02 10:33:59'),(178,'大溪地栀子油','Tiare flower',0.1900,NULL,258.00,'admin','admin','1','2026-05-02 11:29:21','2026-05-02 11:29:21');
/*!40000 ALTER TABLE `oil` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_consumables`
--

DROP TABLE IF EXISTS `soap_consumables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_consumables` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增的主键，用于唯一标识每个耗材',
  `name` varchar(500) NOT NULL COMMENT '耗材名称',
  `brand` varchar(50) NOT NULL COMMENT '耗材的品牌名称',
  `quantity` decimal(10,3) NOT NULL COMMENT '耗材的数量(支持小数)',
  `unit_price` decimal(10,2) NOT NULL COMMENT '耗材的单价',
  `total_price` decimal(10,2) NOT NULL COMMENT '耗材的总价',
  `status` int(11) NOT NULL COMMENT '状态（1: 可用, 2: 已用完, 3: 待补货, 4: 已过期等）',
  `consumable_type` int(11) NOT NULL DEFAULT '0' COMMENT '耗材类型',
  `purchase_channel` varchar(255) DEFAULT NULL COMMENT '采购渠道',
  `purchase_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '采购时间',
  `unit` varchar(50) NOT NULL DEFAULT 'g/ml' COMMENT '单位类型',
  `expiry_date` date DEFAULT NULL COMMENT '耗材的过期日期',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '耗材记录的创建时间',
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '耗材记录的更新时间',
  `created_by` varchar(50) DEFAULT NULL COMMENT '添加该耗材的用户姓名',
  `updated_by` varchar(50) DEFAULT NULL COMMENT '最后更新该耗材的用户姓名',
  `weight_in_grams` decimal(12,3) DEFAULT NULL COMMENT '换算克重(数量×单位换算系数)，个/张/包/捆等计数单位为NULL',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='耗材表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_consumables`
--

LOCK TABLES `soap_consumables` WRITE;
/*!40000 ALTER TABLE `soap_consumables` DISABLE KEYS */;
INSERT INTO `soap_consumables` VALUES (1,'碱','电商',1200.000,0.02,25.26,0,0,'电商','2025-12-12 16:00:00','g/ml','2026-01-01','2025-05-26 12:59:56','2026-05-02 09:57:22','admin','admin',NULL),(3,'芝麻油','电商1',1.000,12.00,12.00,0,1,'电商1','2025-12-13 07:06:28','妥','2025-05-26','2025-05-26 13:13:38','2026-05-01 12:42:53','admin','admin',NULL),(4,'橄榄油','电商',500.000,0.20,100.00,0,4,'啦啦啦啦','2025-12-13 07:06:28','g/ml','2025-12-31','2025-12-13 03:45:37','2026-05-01 12:42:56','admin','admin',NULL),(7,'dd','dd',2.000,6.67,20.00,0,1,'dd','2026-05-01 12:45:00','kg','2026-05-01','2026-05-01 12:45:45','2026-05-01 14:20:43','admin','admin',NULL),(8,'菜籽油','电商',0.500,200.00,200.00,2,1,'电商','2026-05-01 14:20:53','kg','2026-05-01','2026-05-01 14:21:25','2026-05-03 02:52:48','admin','admin',500.000),(9,'橄榄油','杂牌',1136.000,0.25,300.00,1,1,'电商','2026-05-01 14:21:26','g','2026-05-01','2026-05-01 14:21:57','2026-05-03 03:48:50','admin','admin',1135.986),(10,'dd','dd',1.000,0.00,0.00,0,4,'dd','2026-05-02 03:32:43','g','2026-05-02','2026-05-02 03:32:56','2026-05-02 08:07:50','admin','admin',NULL),(11,'橄榄油','',100.000,2.00,200.00,0,1,'','2026-05-02 08:16:36','kg','2026-05-02','2026-05-02 08:17:15','2026-05-02 08:17:37','admin','admin',NULL),(12,'棕榈油','',22.000,2.00,200.00,2,1,'','2026-05-02 08:17:37','kg','2026-05-02','2026-05-02 08:17:54','2026-05-03 02:52:48','admin','admin',22000.000),(13,'牛奶','',197.078,0.10,30.00,1,4,'','2026-05-02 08:17:54','ml','2026-05-02','2026-05-02 08:18:19','2026-05-25 05:36:14','admin','admin',192.454),(14,'玫瑰精油','拉拉',375.800,1.00,500.00,1,2,'电商','2026-05-02 08:18:28','ml','2026-05-02','2026-05-02 08:19:21','2026-05-25 05:36:14','admin','admin',375.585),(15,'椰子油','',177.492,1.00,200.00,1,1,'','2026-05-02 10:23:49','kg',NULL,'2026-05-02 10:24:16','2026-05-25 05:36:14','admin','admin',177490.591),(16,'添加物','添加物',0.765,10.00,10.00,1,3,'添加物','2026-05-02 11:38:30','kg',NULL,'2026-05-02 11:38:47','2026-05-25 05:36:14','admin','admin',764.083),(17,'纸箱子','电商',9.000,2.00,20.00,1,5,'电商','2026-05-02 12:19:49','ge',NULL,'2026-05-02 12:20:21','2026-05-30 07:01:22','admin','admin',NULL),(18,'白油','品牌',0.997,200.00,200.00,1,1,'电商','2026-05-03 03:07:41','kg',NULL,'2026-05-03 03:08:30','2026-05-03 03:48:50','admin','admin',996.991),(19,'干燥剂','3M',9.000,2.00,20.00,1,5,'电商','2026-05-03 03:37:08','bao',NULL,'2026-05-03 03:37:36','2026-05-30 07:01:22','admin','admin',NULL),(20,'泡泡网','电商',99.000,0.10,10.00,1,6,NULL,'2026-05-29 09:33:55','ge',NULL,'2026-05-29 09:33:55','2026-05-30 07:01:22','admin','admin',NULL);
/*!40000 ALTER TABLE `soap_consumables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_consumables_usage`
--

DROP TABLE IF EXISTS `soap_consumables_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_consumables_usage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `soap_id` int(11) NOT NULL COMMENT '关联皂的生产记录ID',
  `consumable_id` int(11) NOT NULL COMMENT '关联耗材ID',
  `quantity_used` decimal(10,3) NOT NULL COMMENT '耗材使用量（支持小数，如0.5升）',
  `unit_price` decimal(10,2) NOT NULL COMMENT '使用时的耗材单价（快照）',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `unit` varchar(10) DEFAULT NULL COMMENT '使用时的单位',
  PRIMARY KEY (`id`),
  KEY `fk_soap` (`soap_id`),
  KEY `fk_consumable` (`consumable_id`),
  CONSTRAINT `fk_consumable` FOREIGN KEY (`consumable_id`) REFERENCES `soap_consumables` (`id`),
  CONSTRAINT `fk_soap` FOREIGN KEY (`soap_id`) REFERENCES `soap_production` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COMMENT='耗材使用明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_consumables_usage`
--

LOCK TABLES `soap_consumables_usage` WRITE;
/*!40000 ALTER TABLE `soap_consumables_usage` DISABLE KEYS */;
INSERT INTO `soap_consumables_usage` VALUES (2,4,9,12.000,0.25,'2026-05-01 14:23:16','g'),(3,4,8,0.500,200.00,'2026-05-01 14:23:16','kg'),(5,7,12,78.000,2.00,'2026-05-02 09:13:28','g'),(6,7,14,1.000,1.00,'2026-05-02 09:13:28','ml'),(7,8,15,11.000,1.00,'2026-05-02 11:44:44','g'),(8,8,16,0.100,10.00,'2026-05-02 11:44:44','kg'),(9,8,14,23.000,1.00,'2026-05-02 11:44:44','ml'),(10,9,15,11.000,1.00,'2026-05-02 12:04:50','g'),(11,9,9,2.000,0.25,'2026-05-02 12:04:50','g'),(12,9,14,1.000,1.00,'2026-05-02 12:04:50','ml'),(13,9,16,0.010,10.00,'2026-05-02 12:04:50','kg'),(19,11,15,3.000,1.00,'2026-05-03 03:48:49','g'),(20,11,9,4.000,0.25,'2026-05-03 03:48:49','g'),(21,11,18,3.000,200.00,'2026-05-03 03:48:49','g'),(22,11,14,9.000,1.00,'2026-05-03 03:48:49','g'),(23,11,16,25.000,10.00,'2026-05-03 03:48:49','g'),(24,11,13,3.927,0.10,'2026-05-03 03:48:49','g'),(25,12,15,500.000,0.00,'2026-05-25 05:36:14','g'),(26,12,16,0.100,0.01,'2026-05-25 05:36:14','g'),(27,12,14,0.200,1.00,'2026-05-25 05:36:14','g'),(28,12,13,30.000,0.10,'2026-05-25 05:36:14','g');
/*!40000 ALTER TABLE `soap_consumables_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_package`
--

DROP TABLE IF EXISTS `soap_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_package` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_number` varchar(60) NOT NULL COMMENT '打包批次号',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1=待内包装, 2=待外包装, 3=已完成',
  `total_material_cost` decimal(10,2) DEFAULT '0.00' COMMENT '包装物料总成本',
  `notes` varchar(500) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch` (`batch_number`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='打包发货记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_package`
--

LOCK TABLES `soap_package` WRITE;
/*!40000 ALTER TABLE `soap_package` DISABLE KEYS */;
INSERT INTO `soap_package` VALUES (1,'PKG20260525142755',3,0.00,'','2026-05-25 06:27:55','2026-05-29 09:32:58'),(2,'PKG20260530150122',3,4.10,NULL,'2026-05-30 07:01:22','2026-05-30 07:08:05');
/*!40000 ALTER TABLE `soap_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_package_material`
--

DROP TABLE IF EXISTS `soap_package_material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_package_material` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package_id` int(11) NOT NULL,
  `consumable_id` int(11) NOT NULL,
  `material_type` varchar(10) NOT NULL COMMENT 'inner=内包装, outer=外包装',
  `quantity_used` decimal(10,3) NOT NULL,
  `unit_price` decimal(10,2) DEFAULT '0.00',
  `unit` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pm_pkg` (`package_id`),
  CONSTRAINT `fk_pm_pkg` FOREIGN KEY (`package_id`) REFERENCES `soap_package` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='打包物料使用明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_package_material`
--

LOCK TABLES `soap_package_material` WRITE;
/*!40000 ALTER TABLE `soap_package_material` DISABLE KEYS */;
INSERT INTO `soap_package_material` VALUES (4,2,19,'inner',1.000,2.00,'bao'),(5,2,17,'outer',1.000,2.00,'ge'),(6,2,20,'gift',1.000,0.10,'ge');
/*!40000 ALTER TABLE `soap_package_material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_package_piece`
--

DROP TABLE IF EXISTS `soap_package_piece`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_package_piece` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package_id` int(11) NOT NULL,
  `piece_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pp_pkg` (`package_id`),
  KEY `fk_pp_piece` (`piece_id`),
  CONSTRAINT `fk_pp_piece` FOREIGN KEY (`piece_id`) REFERENCES `soap_piece` (`id`),
  CONSTRAINT `fk_pp_pkg` FOREIGN KEY (`package_id`) REFERENCES `soap_package` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COMMENT='打包-皂块关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_package_piece`
--

LOCK TABLES `soap_package_piece` WRITE;
/*!40000 ALTER TABLE `soap_package_piece` DISABLE KEYS */;
INSERT INTO `soap_package_piece` VALUES (38,2,96),(39,2,95),(40,2,94),(41,2,93),(42,2,92),(43,2,91);
/*!40000 ALTER TABLE `soap_package_piece` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_piece`
--

DROP TABLE IF EXISTS `soap_piece`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_piece` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `production_id` int(11) NOT NULL COMMENT '关联生产记录ID',
  `piece_name` varchar(100) DEFAULT NULL COMMENT '皂块名称',
  `weight` decimal(10,2) NOT NULL COMMENT '重量(g)',
  `price` decimal(10,2) DEFAULT NULL COMMENT '售价',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1=在售, 2=已售, 3=赠送',
  `notes` varchar(200) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `piece_batch_number` varchar(80) DEFAULT NULL COMMENT '皂块唯一批次号，可追溯到生产批次',
  PRIMARY KEY (`id`),
  KEY `fk_piece_production` (`production_id`),
  CONSTRAINT `fk_piece_production` FOREIGN KEY (`production_id`) REFERENCES `soap_production` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COMMENT='皂块分装表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_piece`
--

LOCK TABLES `soap_piece` WRITE;
/*!40000 ALTER TABLE `soap_piece` DISABLE KEYS */;
INSERT INTO `soap_piece` VALUES (28,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P01'),(29,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P02'),(30,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P03'),(31,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P04'),(32,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P05'),(33,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P06'),(34,6,'hhh',20.00,20.00,1,NULL,'2026-05-02 08:01:16','2026-05-29 09:32:58','20260502125658-P07'),(42,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P01'),(43,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P02'),(44,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P03'),(45,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P04'),(46,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P05'),(47,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P06'),(48,7,'ikkk',61.00,70.00,2,NULL,'2026-05-02 10:26:22','2026-05-02 10:26:22','20260502171326-P07'),(61,9,'淡淡的啊',2.00,35.00,1,NULL,'2026-05-02 12:16:02','2026-05-29 09:32:58','20260502200447-P01'),(62,9,'淡淡的啊',2.00,35.00,1,NULL,'2026-05-02 12:16:02','2026-05-29 09:32:58','20260502200447-P02'),(63,9,'淡淡的啊',2.00,35.00,1,NULL,'2026-05-02 12:16:02','2026-05-29 09:32:58','20260502200447-P03'),(64,9,'淡淡的啊',2.00,35.00,1,NULL,'2026-05-02 12:16:02','2026-05-29 09:32:57','20260502200447-P04'),(65,9,'淡淡的啊',2.00,35.00,1,NULL,'2026-05-02 12:16:02','2026-05-29 09:32:57','20260502200447-P05'),(66,9,'淡淡的啊',2.00,35.00,1,NULL,'2026-05-02 12:16:02','2026-05-29 09:32:57','20260502200447-P06'),(67,8,'dd',3.26,33.00,1,NULL,'2026-05-02 12:16:11','2026-05-29 09:32:57','20260502194442-P01'),(68,8,'dd',3.26,33.00,1,NULL,'2026-05-02 12:16:11','2026-05-29 09:32:57','20260502194442-P02'),(69,8,'dd',3.26,33.00,1,NULL,'2026-05-02 12:16:11','2026-05-29 09:32:57','20260502194442-P03'),(70,8,'dd',3.26,33.00,1,NULL,'2026-05-02 12:16:11','2026-05-29 09:32:57','20260502194442-P04'),(71,8,'dd',3.26,33.00,1,NULL,'2026-05-02 12:16:11','2026-05-29 09:32:57','20260502194442-P05'),(72,8,'dd',3.26,33.00,1,NULL,'2026-05-02 12:16:11','2026-05-29 09:32:57','20260502194442-P06'),(73,4,'二狗的第一批皂',108.33,135.00,1,NULL,'2026-05-02 12:16:43','2026-05-29 09:32:57','20260501222314-P01'),(74,4,'二狗的第一批皂',108.33,135.00,1,NULL,'2026-05-02 12:16:43','2026-05-29 09:32:57','20260501222314-P02'),(75,4,'二狗的第一批皂',108.33,135.00,1,NULL,'2026-05-02 12:16:43','2026-05-29 09:32:57','20260501222314-P03'),(76,4,'二狗的第一批皂',108.33,135.00,1,NULL,'2026-05-02 12:16:43','2026-05-29 09:32:57','20260501222314-P04'),(77,4,'二狗的第一批皂',108.33,135.00,1,NULL,'2026-05-02 12:16:43','2026-05-29 09:32:57','20260501222314-P05'),(78,4,'二狗的第一批皂',108.33,135.00,1,NULL,'2026-05-02 12:16:43','2026-05-29 09:32:57','20260501222314-P06'),(85,12,'dd',148.27,69.00,1,NULL,'2026-05-25 05:37:03','2026-05-29 09:32:58','20260525133613-P01'),(86,12,'dd',148.27,69.00,1,NULL,'2026-05-25 05:37:03','2026-05-29 09:32:58','20260525133613-P02'),(87,12,'dd',148.27,69.00,1,NULL,'2026-05-25 05:37:03','2026-05-29 09:32:58','20260525133613-P03'),(88,12,'dd',148.27,69.00,1,NULL,'2026-05-25 05:37:03','2026-05-29 09:32:58','20260525133613-P04'),(89,12,'dd',148.27,69.00,1,NULL,'2026-05-25 05:37:03','2026-05-29 09:32:58','20260525133613-P05'),(90,12,'dd',148.27,69.00,1,NULL,'2026-05-25 05:37:03','2026-05-29 09:32:58','20260525133613-P06'),(91,11,'在测试一下',2.58,69.00,2,NULL,'2026-05-25 05:50:45','2026-05-30 07:08:05','20260503114847-P01'),(92,11,'在测试一下',2.58,69.00,2,NULL,'2026-05-25 05:50:45','2026-05-30 07:08:05','20260503114847-P02'),(93,11,'在测试一下',2.58,69.00,2,NULL,'2026-05-25 05:50:45','2026-05-30 07:08:05','20260503114847-P03'),(94,11,'在测试一下',2.58,69.00,2,NULL,'2026-05-25 05:50:45','2026-05-30 07:08:05','20260503114847-P04'),(95,11,'在测试一下',2.58,69.00,2,NULL,'2026-05-25 05:50:45','2026-05-30 07:08:05','20260503114847-P05'),(96,11,'在测试一下',2.58,69.00,2,NULL,'2026-05-25 05:50:45','2026-05-30 07:08:05','20260503114847-P06');
/*!40000 ALTER TABLE `soap_piece` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_production`
--

DROP TABLE IF EXISTS `soap_production`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `soap_production` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(200) NOT NULL COMMENT '皂的名称',
  `batch_number` varchar(50) NOT NULL COMMENT '生产批次号（唯一）',
  `production_date` date NOT NULL COMMENT '生产日期',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态: 1=生产中, 2=成熟期, 3=已成熟, 4=已切块入库',
  `total_cost` decimal(10,2) NOT NULL COMMENT '总成本（自动计算）',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) DEFAULT NULL,
  `updated_by` varchar(50) DEFAULT NULL,
  `total_weight` decimal(10,2) DEFAULT NULL COMMENT '入模总重量(g)',
  `actual_weight` decimal(10,2) DEFAULT NULL COMMENT '成熟后实际重量(g)',
  `maturation_date` date DEFAULT NULL COMMENT '预计成熟日期',
  `actual_maturation_date` date DEFAULT NULL COMMENT '实际成熟日期',
  `notes` varchar(500) DEFAULT NULL COMMENT '备注',
  `cut_weight` decimal(10,2) DEFAULT NULL COMMENT '切块后总重量(g)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_batch` (`batch_number`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='手工皂生产记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `soap_production`
--

LOCK TABLES `soap_production` WRITE;
/*!40000 ALTER TABLE `soap_production` DISABLE KEYS */;
INSERT INTO `soap_production` VALUES (4,'二狗的第一批皂','20260501222314','2026-05-01',5,103.00,'2026-05-01 14:23:16','2026-05-02 11:58:50','admin','admin',700.00,650.00,'2026-06-15','2026-05-01','搞定',NULL),(6,'hhh','20260502125658','2026-05-02',5,0.00,'2026-05-02 04:57:00','2026-05-02 11:58:50','admin','admin',46948.48,46948.48,'2026-06-16','2026-05-02','',NULL),(7,'ikkk','20260502171326','2026-05-02',5,157.00,'2026-05-02 09:13:28','2026-05-02 11:58:50','admin','admin',112.58,500.00,'2026-06-16','2026-05-02','',NULL),(8,'dd','20260502194442','2026-05-02',5,35.00,'2026-05-02 11:44:44','2026-05-02 11:58:50','admin','admin',19.57,19.57,'2026-06-16','2026-05-02','',NULL),(9,'淡淡的啊','20260502200447','2026-05-02',5,12.60,'2026-05-02 12:04:50','2026-05-02 12:05:37','admin','admin',22.47,21.00,'2026-06-16','2026-05-02','阿斯蒂芬',12.00),(11,'在测试一下','20260503114847','2026-05-03',5,11.25,'2026-05-03 03:48:49','2026-05-25 06:25:01','admin','admin',15.50,900.00,'2026-06-17','2026-05-25','',15.48),(12,'dd','20260525133613','2026-05-25',5,3.70,'2026-05-25 05:36:14','2026-05-25 05:36:43','admin','admin',889.61,600.00,'2026-07-09','2026-05-25','',889.62);
/*!40000 ALTER TABLE `soap_production` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `soap_tools`
--

DROP TABLE IF EXISTS `soap_tools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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

--
-- Dumping data for table `soap_tools`
--

LOCK TABLES `soap_tools` WRITE;
/*!40000 ALTER TABLE `soap_tools` DISABLE KEYS */;
INSERT INTO `soap_tools` VALUES (5,'勺子1','电商',1,3.00,3.00,1,'2024-11-19 12:29:57','2024-11-19 12:29:57','admin','admin'),(6,'ddd','ddd',1,1.00,1.00,1,'2024-11-19 12:49:26','2024-11-19 12:49:26','admin','admin'),(7,'dsafasdf','asdfasd',12,23.00,276.00,1,'2024-11-19 12:51:27','2024-11-19 12:51:27','admin','admin'),(8,'东方闪电发生的','发斯蒂芬',1,23.00,23.00,1,'2024-11-19 12:57:54','2024-11-19 12:57:54','admin','admin'),(9,'阿斯顿发生','',1,2.00,2.00,1,'2024-11-19 12:58:21','2024-11-19 12:58:21','admin','admin'),(10,'水电费撒旦法','阿斯顿发斯蒂芬',1,2.00,2.00,1,'2024-11-19 12:58:42','2024-11-19 12:58:42','admin','admin'),(11,'发斯蒂芬','无收到发斯蒂芬',1,3.00,3.00,1,'2024-11-19 12:59:27','2024-11-19 12:59:27','admin','admin'),(12,'发斯蒂芬','发斯蒂芬',14,42.00,588.00,1,'2024-11-19 12:59:57','2024-11-19 12:59:57','admin','admin'),(13,'发斯蒂芬','无发斯蒂芬',1,34.00,34.00,1,'2024-11-19 13:01:19','2024-11-19 13:01:19','admin','admin'),(14,'发斯蒂芬','无胜多负少',1,2.00,2.00,1,'2024-11-19 13:01:58','2024-11-19 13:01:58','admin','admin'),(15,'发斯蒂芬','无发斯蒂芬',13,1.00,13.00,1,'2024-11-19 13:11:32','2024-11-19 13:11:32','admin','admin'),(16,'漏勺','1688',11,12.00,132.00,1,'2024-11-20 01:47:09','2024-11-20 01:47:09','admin','admin'),(17,'添置','淡淡的',12,2.00,24.00,1,'2024-11-20 01:47:59','2024-11-20 05:42:03','admin','admin'),(20,'的士速递','',1,2.00,2.00,1,'2024-11-20 02:04:29','2024-11-20 02:04:29','admin','admin'),(21,'是的是的','',1,2.00,2.00,1,'2024-11-20 02:06:39','2024-11-20 02:06:39','admin','admin'),(23,'sdfasdf','sdfsdf',14,3.00,42.00,1,'2024-11-20 05:12:33','2024-11-20 05:12:33','admin','admin'),(24,'sdfasdf','sdfsdf',14,3.00,42.00,1,'2024-11-20 05:32:29','2024-11-20 05:32:29','admin','admin'),(25,'sdfasdf','厉害',14,3.00,42.00,1,'2024-11-20 05:33:18','2024-11-20 05:39:57','admin','admin'),(26,'老来乐','拉拉',122,3.00,366.00,1,'2024-11-20 05:41:08','2024-11-20 05:41:14','admin','admin');
/*!40000 ALTER TABLE `soap_tools` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-30 20:50:04
