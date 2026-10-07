-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: batnew
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) COLLATE utf8mb3_bin NOT NULL,
  `username` varchar(32) COLLATE utf8mb3_bin NOT NULL,
  `password` varchar(64) COLLATE utf8mb3_bin NOT NULL,
  `role` varchar(10) COLLATE utf8mb3_bin DEFAULT 'admin',
  `phone` varchar(11) COLLATE utf8mb3_bin NOT NULL,
  `sex` varchar(2) COLLATE utf8mb3_bin NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` (`id`, `name`, `username`, `password`, `role`, `phone`, `sex`, `create_time`, `update_time`) VALUES (1,'adminn','admin','$2a$10$q9ymKSbpri9fbTCz6MbO4.NCtN6Zyl2p2TJMHh2US42Kkqq42IHha','admin','12345678911','1','2025-11-04 20:07:09','2026-10-06 23:02:56');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` (`id`, `name`, `create_time`, `update_time`) VALUES (1,'Yonex',NULL,NULL);
INSERT INTO `brand` (`id`, `name`, `create_time`, `update_time`) VALUES (2,'Wilson',NULL,NULL);
INSERT INTO `brand` (`id`, `name`, `create_time`, `update_time`) VALUES (3,'Butterfly',NULL,NULL);
INSERT INTO `brand` (`id`, `name`, `create_time`, `update_time`) VALUES (4,'Victor',NULL,NULL);
INSERT INTO `brand` (`id`, `name`, `create_time`, `update_time`) VALUES (5,'Li-Ning',NULL,NULL);
INSERT INTO `brand` (`id`, `name`, `create_time`, `update_time`) VALUES (7,'642d434343','2025-11-16 15:59:29','2025-11-19 00:04:43');
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` (`id`, `name`, `create_time`, `update_time`) VALUES (1,'羽毛球拍',NULL,NULL);
INSERT INTO `category` (`id`, `name`, `create_time`, `update_time`) VALUES (2,'网球拍',NULL,NULL);
INSERT INTO `category` (`id`, `name`, `create_time`, `update_time`) VALUES (3,'乒乓球拍',NULL,NULL);
INSERT INTO `category` (`id`, `name`, `create_time`, `update_time`) VALUES (7,'223','2025-11-18 01:03:27','2025-11-19 00:03:18');
INSERT INTO `category` (`id`, `name`, `create_time`, `update_time`) VALUES (8,'1345','2025-11-24 15:23:47',NULL);
INSERT INTO `category` (`id`, `name`, `create_time`, `update_time`) VALUES (9,'22','2025-11-24 18:44:05',NULL);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` int DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `platform` int DEFAULT NULL,
  `publish_count` int DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `per_limit` int NOT NULL DEFAULT '1',
  `min_point` decimal(10,2) NOT NULL DEFAULT '0.00',
  `enable_time` datetime DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `use_type` int NOT NULL DEFAULT '0',
  `note` varchar(500) DEFAULT NULL,
  `product_relation_json` text,
  `product_category_relation_json` text,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_coupon_available` (`enable_time`,`end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` (`id`, `type`, `name`, `platform`, `publish_count`, `amount`, `per_limit`, `min_point`, `enable_time`, `start_time`, `end_time`, `use_type`, `note`, `product_relation_json`, `product_category_relation_json`, `create_time`, `update_time`) VALUES (2,0,'满1000减200',0,NULL,200.00,1,1000.00,'2026-10-07 14:48:21','2026-10-07 14:48:21',NULL,0,'system_code:ALL_USERS_1000_200','[]','[]','2026-10-07 14:48:21','2026-10-07 15:06:35');
INSERT INTO `coupon` (`id`, `type`, `name`, `platform`, `publish_count`, `amount`, `per_limit`, `min_point`, `enable_time`, `start_time`, `end_time`, `use_type`, `note`, `product_relation_json`, `product_category_relation_json`, `create_time`, `update_time`) VALUES (3,0,'新人无门槛50元',0,NULL,50.00,1,0.00,'2026-10-07 14:49:15','2026-10-07 14:49:15',NULL,0,'system_code:NEW_USER_50','[]','[]','2026-10-07 14:49:15','2026-10-07 15:06:35');
INSERT INTO `coupon` (`id`, `type`, `name`, `platform`, `publish_count`, `amount`, `per_limit`, `min_point`, `enable_time`, `start_time`, `end_time`, `use_type`, `note`, `product_relation_json`, `product_category_relation_json`, `create_time`, `update_time`) VALUES (4,3,'新用户立减30元',0,10,30.00,1,30.01,'2026-10-06 16:00:00','2026-10-06 16:00:00','2026-10-13 16:00:00',0,NULL,'[]','[]','2026-10-07 23:18:37','2026-10-07 23:18:52');
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupon_history`
--

DROP TABLE IF EXISTS `coupon_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint NOT NULL,
  `coupon_code` varchar(32) NOT NULL,
  `member_nickname` varchar(100) DEFAULT NULL,
  `member_username` varchar(50) NOT NULL,
  `get_type` int NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `use_status` int NOT NULL DEFAULT '0',
  `use_time` datetime DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `order_sn` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coupon_history_code` (`coupon_code`),
  KEY `idx_coupon_history_coupon` (`coupon_id`),
  KEY `idx_coupon_history_member_status` (`member_username`,`use_status`),
  KEY `idx_coupon_history_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon_history`
--

LOCK TABLES `coupon_history` WRITE;
/*!40000 ALTER TABLE `coupon_history` DISABLE KEYS */;
INSERT INTO `coupon_history` (`id`, `coupon_id`, `coupon_code`, `member_nickname`, `member_username`, `get_type`, `create_time`, `use_status`, `use_time`, `order_id`, `order_sn`) VALUES (2,2,'G1000-1','张三','zhangsan',2,'2026-10-07 14:48:21',0,NULL,NULL,NULL);
INSERT INTO `coupon_history` (`id`, `coupon_id`, `coupon_code`, `member_nickname`, `member_username`, `get_type`, `create_time`, `use_status`, `use_time`, `order_id`, `order_sn`) VALUES (3,2,'G1000-2','tianxuan','tianxuan',2,'2026-10-07 14:48:21',1,'2026-10-07 23:34:59',34,'ORDER4468F659F2924A1FB830E66CC9C484DC');
INSERT INTO `coupon_history` (`id`, `coupon_id`, `coupon_code`, `member_nickname`, `member_username`, `get_type`, `create_time`, `use_status`, `use_time`, `order_id`, `order_sn`) VALUES (4,4,'CP644AEAD8E975','tianxuan','tianxuan',1,'2026-10-07 23:19:27',1,'2026-10-07 23:47:50',35,'ORDER1DC56E86CBDD4200B985E12CF79C4E14');
/*!40000 ALTER TABLE `coupon_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `NAME` varchar(50) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `Address` text,
  `RegisterDate` date DEFAULT NULL,
  `PASSWORD` varchar(20) DEFAULT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`CustomerID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (1,'Alice','1234567890','123 Main St','2024-01-01','alice',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (2,'Bob','0987654321','456 Elm St','2024-02-15','bob',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (3,'zhangsan',NULL,NULL,NULL,'zhangsan',1);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (4,'wo',NULL,NULL,NULL,'wo',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (5,'wowo',NULL,NULL,NULL,'wowo',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (6,'lisi','123654','c1-211','2025-06-17','888888',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (7,'zhangsan','123654456','c1-111','2025-06-17','1258',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (8,'胖猫','1595154678','重庆长江大桥',NULL,'mdlhanbao',0);
INSERT INTO `customer` (`CustomerID`, `NAME`, `Phone`, `Address`, `RegisterDate`, `PASSWORD`, `isDeleted`) VALUES (9,'马画藤',NULL,NULL,'2025-06-19','10001',0);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `InventoryID` int NOT NULL AUTO_INCREMENT,
  `RacketID` int DEFAULT NULL,
  `Stock` int DEFAULT NULL,
  `WarehouseLocation` varchar(100) DEFAULT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`InventoryID`),
  KEY `RacketID` (`RacketID`),
  CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`RacketID`) REFERENCES `racket` (`RacketID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` (`InventoryID`, `RacketID`, `Stock`, `WarehouseLocation`, `isDeleted`) VALUES (1,1,6,'Warehouse A',0);
INSERT INTO `inventory` (`InventoryID`, `RacketID`, `Stock`, `WarehouseLocation`, `isDeleted`) VALUES (2,2,30,'bbb',0);
INSERT INTO `inventory` (`InventoryID`, `RacketID`, `Stock`, `WarehouseLocation`, `isDeleted`) VALUES (3,3,100,'c1-211',1);
INSERT INTO `inventory` (`InventoryID`, `RacketID`, `Stock`, `WarehouseLocation`, `isDeleted`) VALUES (4,3,30,'c1-211',0);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oooooooorderitem`
--

DROP TABLE IF EXISTS `oooooooorderitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oooooooorderitem` (
  `OrderID` int NOT NULL,
  `RacketID` int NOT NULL,
  `Quantity` int DEFAULT NULL,
  `Price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`OrderID`,`RacketID`),
  KEY `RacketID` (`RacketID`),
  CONSTRAINT `oooooooorderitem_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `ooooorders` (`OrderID`),
  CONSTRAINT `oooooooorderitem_ibfk_2` FOREIGN KEY (`RacketID`) REFERENCES `racket` (`RacketID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oooooooorderitem`
--

LOCK TABLES `oooooooorderitem` WRITE;
/*!40000 ALTER TABLE `oooooooorderitem` DISABLE KEYS */;
INSERT INTO `oooooooorderitem` (`OrderID`, `RacketID`, `Quantity`, `Price`) VALUES (1,1,1,199.99);
INSERT INTO `oooooooorderitem` (`OrderID`, `RacketID`, `Quantity`, `Price`) VALUES (2,2,1,249.99);
INSERT INTO `oooooooorderitem` (`OrderID`, `RacketID`, `Quantity`, `Price`) VALUES (3,1,2,6000.00);
INSERT INTO `oooooooorderitem` (`OrderID`, `RacketID`, `Quantity`, `Price`) VALUES (4,1,2,6000.00);
/*!40000 ALTER TABLE `oooooooorderitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ooooorders`
--

DROP TABLE IF EXISTS `ooooorders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ooooorders` (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `OrderDate` date DEFAULT NULL,
  `Total` decimal(10,2) DEFAULT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`OrderID`),
  KEY `CustomerID` (`CustomerID`),
  CONSTRAINT `ooooorders_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`CustomerID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ooooorders`
--

LOCK TABLES `ooooorders` WRITE;
/*!40000 ALTER TABLE `ooooorders` DISABLE KEYS */;
INSERT INTO `ooooorders` (`OrderID`, `CustomerID`, `OrderDate`, `Total`, `isDeleted`) VALUES (1,1,'2024-03-10',199.99,0);
INSERT INTO `ooooorders` (`OrderID`, `CustomerID`, `OrderDate`, `Total`, `isDeleted`) VALUES (2,2,'2024-04-05',249.99,0);
INSERT INTO `ooooorders` (`OrderID`, `CustomerID`, `OrderDate`, `Total`, `isDeleted`) VALUES (3,2,'2025-06-17',12000.00,0);
INSERT INTO `ooooorders` (`OrderID`, `CustomerID`, `OrderDate`, `Total`, `isDeleted`) VALUES (4,1,'2025-06-19',12000.00,0);
/*!40000 ALTER TABLE `ooooorders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `member_username` varchar(64) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `coupon_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `coupon_history_id` bigint DEFAULT NULL,
  `pay_type` tinyint NOT NULL DEFAULT '0',
  `source_type` tinyint NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '0',
  `order_type` tinyint NOT NULL DEFAULT '0',
  `receiver_name` varchar(64) DEFAULT NULL,
  `receiver_phone` varchar(20) DEFAULT NULL,
  `receiver_post_code` varchar(10) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `receiver_province` varchar(32) DEFAULT NULL,
  `receiver_city` varchar(32) DEFAULT NULL,
  `receiver_region` varchar(32) DEFAULT NULL,
  `receiver_detail_address` varchar(128) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `admin_note` varchar(255) DEFAULT NULL,
  `delivery_company` varchar(64) DEFAULT NULL,
  `delivery_sn` varchar(64) DEFAULT NULL,
  `delivery_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (1,'479479795','zhangsan',1000.00,0.00,NULL,0,2,4,0,'张三','13800000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-18 19:45:24','2025-11-25 14:59:02');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (2,'462844222','lisi',400.00,0.00,NULL,0,0,0,0,'李四','15800000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-18 19:54:31','2025-11-19 12:00:22');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (10,'ORDER63C20EC42C774A689A77D25E067E89D6','zhangsan',242.00,0.00,NULL,1,1,2,1,'章三','1008611',NULL,'福建师法大学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-24 17:55:40','2025-11-25 15:09:58');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (11,'ORDER6A8EF3BC60494D00B259DF362F571DC1','zhangsan',500.00,0.00,NULL,1,1,2,1,'涨三','19552146246',NULL,'福建农林大学',NULL,NULL,NULL,NULL,NULL,NULL,'圆通快递','YT61469823315','2025-11-25 15:19:21','2025-11-24 18:51:44','2025-11-25 15:16:48');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (12,'ORDER90B389C341FA45F58C9F7FEE075C3672','zhangsan',1332.00,0.00,NULL,1,1,2,1,'帐散','12549763154',NULL,'福建闽江大学',NULL,NULL,NULL,NULL,NULL,NULL,'中通快递',NULL,'2025-11-28 14:16:31','2025-11-24 18:54:06','2025-11-27 17:38:50');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (13,'ORDER30EFF1E70164467BBF3778A9B5B901E6','zhangsan',500.00,0.00,NULL,1,1,1,1,'张五','15963294784',NULL,'福建理工小学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-25 09:23:33','2025-11-25 11:39:11');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (14,'ORDER79CBE01D459B466185AB3A3CDEA8F401','zhangsan',500.00,0.00,NULL,1,1,1,1,'张叁','15421659756',NULL,'福建理工中学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-25 09:28:11','2025-11-25 09:58:12');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (15,'ORDER859F085A1AC146D1BAF8CC7015805705','zhangsan',1998.00,0.00,NULL,1,1,1,1,'弓长三','13697513546',NULL,'福州大学旗山校区',NULL,NULL,NULL,NULL,'快快快',NULL,NULL,NULL,NULL,'2025-11-25 09:36:53','2025-11-25 13:38:04');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (16,'ORDERFFEE7C14C8064FCB83D59BA741E33A2B','zhangsan',1110.00,0.00,NULL,1,1,1,1,'方师','13860891574',NULL,'福州一中',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-26 22:16:53','2025-11-26 22:16:53');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (17,'ORDERFB0DDE9255D242308FDED59D9E491501','zhangsan',1110.00,0.00,NULL,1,1,1,1,'方帅','1008611',NULL,'福建医科大学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-26 22:18:56','2025-11-26 22:18:56');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (18,'ORDER5593CA11AF5441C3A8791FAD7687EDB9','zhangsan',555.00,0.00,NULL,1,1,1,1,'方芳','17469231534',NULL,'福建中医药大学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-26 22:19:53','2025-11-26 22:19:53');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (19,'ORDERE3FDE829717148549F965A164DF27624','zhangsan',0.00,0.00,NULL,1,1,1,1,'章三','16841566241',NULL,'福建江夏大学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-27 11:57:45','2025-11-27 11:57:45');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (20,'ORDER1745B479BE024922920D9D3337D97989','zhangsan',0.00,0.00,NULL,1,1,1,1,'方师','13814698975',NULL,'福建工程大学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-28 00:22:31','2025-11-28 00:22:31');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (21,'ORDERD426985362A94426BC738D36FDB3A077','zhangsan',177776.00,0.00,NULL,1,1,1,1,'丈叁','17931258617',NULL,'福州一中',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-28 00:38:59','2025-11-28 00:38:59');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (22,'ORDER3A8187BB9D034CBBAEC32D95D2C03083','zhangsan',0.00,0.00,NULL,1,1,1,1,'216','18359058722',NULL,'211',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-28 16:09:50','2025-11-28 16:14:06');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (23,'ORDER36E22294CEAD431B9547EFD14F7B42F7','zhangsan',242.00,0.00,NULL,1,1,1,1,'216','15759867855',NULL,'211',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-28 16:10:29','2025-11-28 16:14:48');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (24,'ORDERBEF21BDACE9A4C53975F48E8AF6F03B8','zhangsan',1000.00,0.00,NULL,1,1,1,1,'bh','13456467346',NULL,'宝城路',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-29 21:03:49','2025-11-29 21:03:49');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (25,'ORDER785A0A3EFB534522BB416D72F1FFE520','zhangsan',2000.00,0.00,NULL,1,1,1,1,'bh','17496457957',NULL,'宝城路',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-11-29 21:11:48','2025-11-29 21:11:48');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (26,'ORDERF9D3FA6827BF47CD937E2E17B4480F50','zhangsan',1899.00,0.00,NULL,1,1,1,1,'hyq','13467674671',NULL,'旗山大学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-01 16:39:53','2025-12-01 16:39:53');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (27,'ORDERBF33301AF92C4A31BB715ABBE0F68D3A','zhangsan',0.00,0.00,NULL,1,1,1,1,'lsl','13655465251',NULL,'福建工程小学C1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-02 09:31:26','2025-12-02 09:31:26');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (28,'ORDERDC0A60D6894E4CDEA2EAF0C25646F8EE','zhangsan',3768.00,0.00,NULL,1,1,1,1,'lsl','14597521452',NULL,'福建工程幼儿园',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-02 09:31:59','2025-12-02 09:31:59');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (29,'ORDERCA13A2CF6F7B41DE85D4EB586380E53B','zhangsan',0.00,0.00,NULL,1,1,1,1,'lsl','13594962151',NULL,'福建岐山胡幼儿园',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-02 09:37:01','2025-12-02 09:37:01');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (30,'ORDERE47D98A6819B4A2A9C42C53874E84EA6','zhangsan',0.00,0.00,NULL,1,1,1,1,'lsl','16984846210',NULL,'福建理工小学',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-02 09:38:41','2025-12-02 09:38:41');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (31,'ORDER8D4FD632ABB14725BD1C0EBF3AAFC445','zhangsan',0.00,0.00,NULL,1,1,1,1,'雷','15987635121',NULL,'福州一中',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-02 09:51:47','2025-12-02 09:51:47');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (32,'ORDERB83A806FDCA84E93A2800003236B38F7','zhangsan',0.00,0.00,NULL,1,1,2,1,'zhang三','19559481265',NULL,'福州',NULL,NULL,NULL,NULL,NULL,NULL,'顺丰快递','111949','2026-04-12 14:59:39','2026-04-12 14:58:21','2026-04-12 14:58:21');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (33,'ORDERF3007B279ED6476092460B24F56E1CA3','tianxuan',1000.00,0.00,NULL,1,1,3,1,'天选','14726954582',NULL,'福建省福州市福建理工大学南校区',NULL,NULL,NULL,NULL,'快快快',NULL,'顺丰快递','SF-15390000002','2026-10-07 15:39:32','2026-10-07 14:39:21','2026-10-07 15:40:07');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (34,'ORDER4468F659F2924A1FB830E66CC9C484DC','tianxuan',1333.00,200.00,3,1,1,3,1,'天选','14735491254',NULL,'福建理工大学北校区',NULL,NULL,NULL,NULL,'',NULL,'圆通快递',NULL,'2026-10-07 23:35:36','2026-10-07 23:34:59','2026-10-07 23:35:47');
INSERT INTO `order` (`id`, `order_sn`, `member_username`, `total_amount`, `coupon_amount`, `coupon_history_id`, `pay_type`, `source_type`, `status`, `order_type`, `receiver_name`, `receiver_phone`, `receiver_post_code`, `address`, `receiver_province`, `receiver_city`, `receiver_region`, `receiver_detail_address`, `note`, `admin_note`, `delivery_company`, `delivery_sn`, `delivery_time`, `create_time`, `update_time`) VALUES (35,'ORDER1DC56E86CBDD4200B985E12CF79C4E14','tianxuan',1503.00,30.00,4,1,1,1,1,'tianxuan','13645129568',NULL,'福建理工学校',NULL,NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,'2026-10-07 23:47:50','2026-10-07 23:47:50');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `order_sn` varchar(64) NOT NULL COMMENT '订单编号（冗余字段）',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `product_pic` varchar(500) DEFAULT NULL COMMENT '商品主图',
  `product_sn` varchar(255) DEFAULT NULL,
  `brand_name` varchar(255) DEFAULT NULL COMMENT '品牌名称',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `product_quantity` int NOT NULL COMMENT '商品数量',
  `product_total` decimal(10,2) NOT NULL COMMENT '商品总价（单价×数量）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100028 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100001,1,'202511180001',1,'天斧88Pro',NULL,'V42428451','尤尼克斯',2000.00,2,4000.00,'2025-11-18 21:07:55','2025-11-18 21:36:05');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100002,10,'ORDER63C20EC42C774A689A77D25E067E89D6',13,'ded',NULL,NULL,NULL,242.00,1,242.00,'2025-11-24 17:55:40','2025-11-25 08:39:47');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100003,11,'ORDER6A8EF3BC60494D00B259DF362F571DC1',11,'樊振东6666',NULL,NULL,NULL,500.00,1,500.00,'2025-11-24 18:51:44','2025-11-25 08:39:57');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100004,12,'ORDER90B389C341FA45F58C9F7FEE075C3672',3,'ikun','https://bat-manager.oss-cn-beijing.aliyuncs.com/81cf2ccc-1e44-4411-9db4-1048de6a7114.jpg','KUN19980802','尤尼克斯',666.00,2,1332.00,'2025-11-24 18:54:06','2025-12-02 09:29:54');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100005,13,'ORDER30EFF1E70164467BBF3778A9B5B901E6',11,'樊振东6666',NULL,NULL,NULL,500.00,1,500.00,'2025-11-25 09:23:33','2025-11-25 09:32:59');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100006,14,'ORDER79CBE01D459B466185AB3A3CDEA8F401',11,'樊振东6666',NULL,NULL,NULL,500.00,1,500.00,'2025-11-25 09:28:11','2025-11-25 09:33:03');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100007,15,'ORDER859F085A1AC146D1BAF8CC7015805705',3,'ikun',NULL,NULL,NULL,666.00,3,1998.00,'2025-11-25 09:36:53','2025-11-25 09:36:53');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100008,16,'ORDERFFEE7C14C8064FCB83D59BA741E33A2B',15,'5555',NULL,NULL,NULL,555.00,2,1110.00,'2025-11-26 22:16:53','2025-11-26 22:16:53');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100009,17,'ORDERFB0DDE9255D242308FDED59D9E491501',15,'5555',NULL,NULL,NULL,555.00,2,1110.00,'2025-11-26 22:18:57','2025-11-26 22:18:57');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100010,18,'ORDER5593CA11AF5441C3A8791FAD7687EDB9',15,'5555',NULL,NULL,NULL,555.00,1,555.00,'2025-11-26 22:19:53','2025-11-26 22:19:53');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100011,19,'ORDERE3FDE829717148549F965A164DF27624',14,'qqq',NULL,NULL,NULL,0.00,1,0.00,'2025-11-27 11:57:45','2025-11-27 11:57:45');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100012,20,'ORDER1745B479BE024922920D9D3337D97989',4,'驭12',NULL,NULL,NULL,0.00,3,0.00,'2025-11-28 00:22:31','2025-11-28 00:22:31');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100013,21,'ORDERD426985362A94426BC738D36FDB3A077',12,'888888',NULL,NULL,NULL,88888.00,2,177776.00,'2025-11-28 00:38:59','2025-11-28 00:38:59');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100014,22,'ORDER3A8187BB9D034CBBAEC32D95D2C03083',14,'qqq',NULL,NULL,NULL,0.00,1,0.00,'2025-11-28 16:09:50','2025-11-28 16:09:50');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100015,23,'ORDER36E22294CEAD431B9547EFD14F7B42F7',13,'ded',NULL,NULL,NULL,242.00,1,242.00,'2025-11-28 16:10:29','2025-11-28 16:10:29');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100016,24,'ORDERBEF21BDACE9A4C53975F48E8AF6F03B8',11,'樊振东6666',NULL,NULL,NULL,1000.00,1,1000.00,'2025-11-29 21:03:50','2025-11-29 21:03:50');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100017,25,'ORDER785A0A3EFB534522BB416D72F1FFE520',11,'樊振东6666',NULL,NULL,NULL,1000.00,2,2000.00,'2025-11-29 21:11:48','2025-11-29 21:11:48');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100018,26,'ORDERF9D3FA6827BF47CD937E2E17B4480F50',1,'天斧77Pro',NULL,NULL,NULL,1899.00,1,1899.00,'2025-12-01 16:39:53','2025-12-01 16:39:53');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100019,27,'ORDERBF33301AF92C4A31BB715ABBE0F68D3A',18,'雷霆80',NULL,NULL,NULL,0.00,2,0.00,'2025-12-02 09:31:26','2025-12-02 09:31:26');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100020,28,'ORDERDC0A60D6894E4CDEA2EAF0C25646F8EE',2,'BLADE V9',NULL,NULL,NULL,1884.00,2,3768.00,'2025-12-02 09:31:59','2025-12-02 09:31:59');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100021,29,'ORDERCA13A2CF6F7B41DE85D4EB586380E53B',6,'战戟8000',NULL,NULL,NULL,0.00,2,0.00,'2025-12-02 09:37:01','2025-12-02 09:37:01');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100022,30,'ORDERE47D98A6819B4A2A9C42C53874E84EA6',6,'战戟8000',NULL,NULL,NULL,0.00,1,0.00,'2025-12-02 09:38:41','2025-12-02 09:38:41');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100023,31,'ORDER8D4FD632ABB14725BD1C0EBF3AAFC445',6,'战戟8000','https://bat-manager.oss-cn-beijing.aliyuncs.com/53ad0b81-e8b4-42a1-8430-bb5ec04bfa84.jpg',NULL,'Li-Ning',0.00,1,0.00,'2025-12-02 09:51:47','2025-12-02 09:51:47');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100024,32,'ORDERB83A806FDCA84E93A2800003236B38F7',18,'雷霆80',NULL,NULL,NULL,0.00,1,0.00,'2026-04-12 14:58:21','2026-04-12 14:58:21');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100025,33,'ORDERF3007B279ED6476092460B24F56E1CA3',11,'樊振东6666',NULL,NULL,NULL,1000.00,1,1000.00,'2026-10-07 14:39:21','2026-10-07 14:42:24');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100026,34,'ORDER4468F659F2924A1FB830E66CC9C484DC',20,'test153',NULL,NULL,NULL,1533.00,1,1533.00,'2026-10-07 23:34:59','2026-10-07 23:34:59');
INSERT INTO `order_item` (`id`, `order_id`, `order_sn`, `product_id`, `product_name`, `product_pic`, `product_sn`, `brand_name`, `product_price`, `product_quantity`, `product_total`, `create_time`, `update_time`) VALUES (100027,35,'ORDER1DC56E86CBDD4200B985E12CF79C4E14',20,'test153',NULL,NULL,NULL,1533.00,1,1533.00,'2026-10-07 23:47:50','2026-10-07 23:47:50');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `country` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `category` tinyint NOT NULL DEFAULT '0' COMMENT '分类：1羽毛球拍 2网球拍 3乒乓球拍等',
  `image_url` varchar(255) DEFAULT NULL COMMENT '主图片url',
  `image_urls` varchar(255) DEFAULT NULL COMMENT '多图片url',
  `website` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `stock` bigint DEFAULT NULL COMMENT '库存',
  `sale` int DEFAULT '0',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `audit_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '审核状态：0未审核 1审核通过',
  `logo_url` varchar(500) DEFAULT NULL,
  `description` text,
  `price` decimal(10,2) DEFAULT NULL,
  `product_sn` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '货号',
  `recommend_status` tinyint(1) DEFAULT '0' COMMENT '推荐状态',
  `new_status` tinyint(1) DEFAULT '0' COMMENT '新品状态',
  `publish_status` tinyint(1) DEFAULT '0' COMMENT '审核状态',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (1,'Yonex','天斧77Pro','日本',1,'https://bat-manager.oss-cn-beijing.aliyuncs.com/1d34bef6-b3ce-49c6-a2ba-f04ae7fb273d.jpg',NULL,'https://www.yonex.com',29,11,0,1,NULL,NULL,1899.00,'Y221830384',1,1,1,'2025-11-06 16:07:05','2025-11-28 22:18:39');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (2,'Wilson','BLADE V9','美国',2,'https://bat-manager.oss-cn-beijing.aliyuncs.com/9ce30f39-adde-472c-a77a-a547e2c3b7ee.jpg',NULL,'https://www.wilson.com',8,14,0,0,NULL,'萨巴仑卡同款网球拍',1884.00,'W64810481',0,1,1,'2025-11-13 16:07:13','2025-11-28 22:43:58');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (3,'Butterfly','ikun','日本',3,'https://bat-manager.oss-cn-beijing.aliyuncs.com/81cf2ccc-1e44-4411-9db4-1048de6a7114.jpg',NULL,'https://www.butterfly.co.jp/',666,59,0,1,NULL,'string',666.00,'KUN19980802',0,1,1,'2025-11-10 16:07:16','2025-11-28 22:45:40');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (4,'Victor','驭12','中国台湾',1,'https://bat-manager.oss-cn-beijing.aliyuncs.com/661ed358-b58b-42e4-9c99-3384189f9ec1.jpg',NULL,'https://www.victorsport.com/',17,80,0,1,NULL,NULL,0.00,'V164975216',0,1,1,'2025-11-05 16:07:18','2025-11-28 22:48:20');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (5,'Li-Ning',NULL,'中国',1,NULL,NULL,'https://www.li‑ning.com/ ',NULL,13,1,0,NULL,NULL,NULL,NULL,1,0,1,'2025-11-01 16:07:21',NULL);
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (6,'Li-Ning','战戟8000','中国',1,'https://bat-manager.oss-cn-beijing.aliyuncs.com/53ad0b81-e8b4-42a1-8430-bb5ec04bfa84.jpg',NULL,'https://www.li‑ning.com/',8,4,0,1,NULL,'方诗博同款球拍',0.00,'L7489274',0,0,1,'2025-11-03 16:07:24','2025-11-28 22:48:18');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (9,'Wilson','哈哈哈',NULL,2,NULL,NULL,NULL,NULL,0,1,0,NULL,'222222',2942.00,'H37414',0,0,0,'2025-11-18 11:51:27','2025-11-18 14:54:37');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (10,'Yonex','1508',NULL,3,NULL,NULL,NULL,NULL,0,1,0,NULL,'111',222.00,'111',0,0,0,'2025-11-18 15:07:36','2025-11-18 15:07:53');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (11,'Butterfly','樊振东6666',NULL,3,'https://bat-manager.oss-cn-beijing.aliyuncs.com/efc2a322-123f-4e9e-b23d-3be2d62e709b.png',NULL,NULL,296,4,0,1,NULL,'樊振东',1000.00,'F3920344',0,0,1,'2025-11-18 15:26:43','2025-11-29 21:03:22');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (12,'Yonex','888888',NULL,1,NULL,NULL,NULL,886,2,0,0,NULL,'888888888',88888.00,'Y88888888888',0,0,0,'2025-11-23 23:16:10','2025-11-23 23:16:10');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (13,'Yonex','ded',NULL,1,NULL,NULL,NULL,21,1,0,0,NULL,'ded',242.00,'Y35356265',0,0,0,'2025-11-23 23:18:19','2025-11-23 23:18:19');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (14,'Yonex','qqq',NULL,1,NULL,NULL,NULL,31,2,0,1,NULL,'qqq',0.00,'Y4234242',0,0,1,'2025-11-23 23:20:40','2025-11-27 00:34:49');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (15,'Yonex','5555',NULL,1,NULL,NULL,NULL,50,1,0,1,NULL,'555',0.00,'Y55555',0,0,0,'2025-11-24 18:44:00','2025-11-27 00:34:13');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (16,'Li-Ning','雷霆80',NULL,1,NULL,NULL,NULL,10,0,1,0,NULL,'雷霆80',1399.00,'L6491313',0,0,0,'2025-11-28 15:45:26','2025-11-28 15:45:26');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (17,'Li-Ning','2109',NULL,9,'https://bat-manager.oss-cn-beijing.aliyuncs.com/c80f0e3c-3fdf-4df1-9129-ea0da8256d94.jpg',NULL,NULL,11,0,0,0,NULL,'2109',1399.00,'41471024701',0,0,0,'2025-11-28 21:15:43','2025-12-01 19:14:50');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (18,'Li-Ning','雷霆80',NULL,1,'https://bat-manager.oss-cn-beijing.aliyuncs.com/71907be1-2851-4fee-b170-7030f8d13f2a.jpg',NULL,NULL,7,3,0,0,NULL,'80',0.00,'L42342248',0,0,1,'2025-11-28 21:39:16','2025-11-28 22:48:43');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (19,'642d434343','223',NULL,7,'https://bat-manager.oss-cn-beijing.aliyuncs.com/2ed1e749-007b-4dca-90da-61d9e2006ffd.jpg',NULL,NULL,0,0,0,0,NULL,'223',0.00,'223',0,0,0,'2025-11-28 23:25:07','2026-04-12 15:16:43');
INSERT INTO `product` (`product_id`, `brand_name`, `name`, `country`, `category`, `image_url`, `image_urls`, `website`, `stock`, `sale`, `is_deleted`, `audit_status`, `logo_url`, `description`, `price`, `product_sn`, `recommend_status`, `new_status`, `publish_status`, `create_time`, `update_time`) VALUES (20,'Li-Ning','test153',NULL,1,NULL,NULL,NULL,3,2,0,1,NULL,'153',1533.00,'LN-153153',0,0,1,'2026-10-07 01:54:44','2026-10-07 23:34:11');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racket`
--

DROP TABLE IF EXISTS `racket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `racket` (
  `RacketID` int NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `Model` varchar(50) DEFAULT NULL,
  `TYPE` varchar(30) DEFAULT NULL,
  `Material` varchar(50) DEFAULT NULL,
  `Price` decimal(10,2) DEFAULT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`RacketID`),
  KEY `racket_ibfk_1` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racket`
--

LOCK TABLES `racket` WRITE;
/*!40000 ALTER TABLE `racket` DISABLE KEYS */;
INSERT INTO `racket` (`RacketID`, `product_id`, `Model`, `TYPE`, `Material`, `Price`, `isDeleted`) VALUES (1,1,'天斧99','Badminton','碳纤维',6000.00,0);
INSERT INTO `racket` (`RacketID`, `product_id`, `Model`, `TYPE`, `Material`, `Price`, `isDeleted`) VALUES (2,2,'Pro Staff 97','Tennis','碳纤维',1099.00,0);
INSERT INTO `racket` (`RacketID`, `product_id`, `Model`, `TYPE`, `Material`, `Price`, `isDeleted`) VALUES (3,1,'天斧77Pro','Badminton','碳纤维',1899.00,0);
INSERT INTO `racket` (`RacketID`, `product_id`, `Model`, `TYPE`, `Material`, `Price`, `isDeleted`) VALUES (4,1,'100ZZ','Badminton','碳纤维',3000.00,1);
INSERT INTO `racket` (`RacketID`, `product_id`, `Model`, `TYPE`, `Material`, `Price`, `isDeleted`) VALUES (5,3,'樊振东alc','Table Tennis','碳素',1036.00,0);
INSERT INTO `racket` (`RacketID`, `product_id`, `Model`, `TYPE`, `Material`, `Price`, `isDeleted`) VALUES (6,4,'龙牙之刃','Badminton','碳纤维',1699.00,0);
/*!40000 ALTER TABLE `racket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racketsupplier`
--

DROP TABLE IF EXISTS `racketsupplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `racketsupplier` (
  `RacketID` int NOT NULL,
  `SupplierID` int NOT NULL,
  PRIMARY KEY (`RacketID`,`SupplierID`),
  KEY `SupplierID` (`SupplierID`),
  CONSTRAINT `racketsupplier_ibfk_1` FOREIGN KEY (`RacketID`) REFERENCES `racket` (`RacketID`),
  CONSTRAINT `racketsupplier_ibfk_2` FOREIGN KEY (`SupplierID`) REFERENCES `supplier` (`SupplierID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racketsupplier`
--

LOCK TABLES `racketsupplier` WRITE;
/*!40000 ALTER TABLE `racketsupplier` DISABLE KEYS */;
INSERT INTO `racketsupplier` (`RacketID`, `SupplierID`) VALUES (1,1);
INSERT INTO `racketsupplier` (`RacketID`, `SupplierID`) VALUES (2,2);
/*!40000 ALTER TABLE `racketsupplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_apply`
--

DROP TABLE IF EXISTS `return_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL COMMENT '退货原因',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `company_address` varchar(50) DEFAULT NULL COMMENT '商家收获地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `handle_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `handle_man` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '连戍权',
  `receive_man` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '连戍权',
  `receive_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退货申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_apply`
--

LOCK TABLES `return_apply` WRITE;
/*!40000 ALTER TABLE `return_apply` DISABLE KEYS */;
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (1,202511180001,1,'test','商品质量问题',0,'福州商家退货中心','2025-11-18 22:42:39','2025-11-28 00:45:58','连戍权','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (2,1002,2,NULL,'不喜欢',1,'福州商家退货中心','2025-11-18 22:42:40','2025-11-28 00:45:55','连戍权','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (3,1003,3,NULL,'与描述不符',2,'福州商家退货中心','2025-11-18 23:42:44','2025-11-28 00:45:54','连戍权','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (4,1004,4,NULL,'hhhhhhhhh',1,'福州商家退货中心','2025-11-19 00:28:07','2025-11-28 00:45:54','连戍权','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (5,12,1,'zhangsan','不需要了',3,'福州商家退货中心','2025-11-27 00:00:00','2025-11-28 00:45:53','连戍权','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (6,19,1,'zhangsan','不要了',2,'福州商家退货中心','2025-11-27 00:00:00','2025-11-28 00:45:52','admin','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (7,12,1,'zhangsan','价格太贵',2,'福州商家退货中心','2025-11-27 00:00:00','2025-11-28 00:45:51','连戍权','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (8,20,1,'zhangsan','买错了',2,'福州商家退货中心','2025-11-28 00:00:00','2025-11-28 00:02:00','admin','连戍权','2025-11-28 00:27:16');
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (9,21,1,'zhangsan','不需要了',2,'福州商家退货中心','2025-11-28 00:00:00','2025-11-28 00:45:50','连戍权','连戍权','2025-11-28 00:43:27');
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (10,13,1,'zhangsan','不需要了',2,'福州商家退货中心','2025-11-29 21:04:37','2025-11-29 21:06:20','连戍权','连戍权','2025-11-29 21:06:20');
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (11,25,1,'zhangsan','没钱吃饭了',1,'福州商家退货中心','2025-11-29 21:12:55','2026-10-07 14:41:18','管理员','连戍权',NULL);
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (12,24,1,'zhangsan','价格太贵',2,'福州商家退货中心','2025-11-29 21:13:13','2025-11-29 21:25:41','连戍权','连戍权','2025-11-29 21:25:41');
INSERT INTO `return_apply` (`id`, `order_id`, `user_id`, `username`, `reason`, `status`, `company_address`, `create_time`, `handle_time`, `handle_man`, `receive_man`, `receive_time`) VALUES (13,33,2,'tianxuan','七天无理由',3,'福州商家退货中心','2026-10-07 15:41:50','2026-10-07 15:42:07','admin','连戍权',NULL);
/*!40000 ALTER TABLE `return_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_reason`
--

DROP TABLE IF EXISTS `return_reason`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_reason` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '退货原因名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退货原因表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_reason`
--

LOCK TABLES `return_reason` WRITE;
/*!40000 ALTER TABLE `return_reason` DISABLE KEYS */;
INSERT INTO `return_reason` (`id`, `name`, `create_time`) VALUES (1,'价格太贵','2025-11-19 01:14:58');
INSERT INTO `return_reason` (`id`, `name`, `create_time`) VALUES (2,'不需要了','2025-11-19 11:08:29');
INSERT INTO `return_reason` (`id`, `name`, `create_time`) VALUES (7,'44444','2025-11-19 11:52:54');
INSERT INTO `return_reason` (`id`, `name`, `create_time`) VALUES (8,'七天无理由','2026-10-07 14:41:46');
/*!40000 ALTER TABLE `return_reason` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint DEFAULT NULL COMMENT '关联订单ID',
  `product_id` bigint NOT NULL COMMENT '关联商品ID',
  `member_username` varchar(50) NOT NULL COMMENT '用户用户名',
  `content` text COMMENT '评论内容',
  `star` tinyint NOT NULL COMMENT '评分，1-5星',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除，0=未删除，1=已删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_member_username` (`member_username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (1,10,13,'zhangsan','哈哈哈哈',5,'2025-11-25 00:51:57','2025-11-25 00:51:57',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (2,10,13,'zhangsan','哈哈哈哈',5,'2025-11-25 00:52:10','2025-11-25 00:52:10',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (3,10,13,'zhangsan','哈哈哈',5,'2025-11-25 01:04:25','2025-11-25 01:04:25',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (4,10,13,'zhangsan','好评返现500刀',5,'2025-11-25 01:08:04','2025-11-28 16:25:05',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (5,11,11,'zhangsan','666',5,'2025-11-25 01:08:29','2025-11-25 22:08:21',1);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (6,10,13,'zhangsan','ded真好用',5,'2025-11-25 15:30:02','2025-11-25 15:30:02',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (7,11,11,'zhangsan','我的樊振东6666真好用',5,'2025-11-25 21:35:09','2025-11-25 22:08:14',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (8,11,11,'zhangsan','樊振东66666666',5,'2025-11-28 22:58:46','2025-11-28 22:58:46',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (9,11,11,'zhangsan','111',5,'2025-11-28 22:59:04','2025-11-28 22:59:04',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (10,1,1,'zhangsan','把88p价格打下来',5,'2025-11-28 23:06:45','2025-11-28 23:06:45',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (11,15,3,'zhangsan','我家哥哥',4,'2025-11-28 23:12:53','2025-11-28 23:12:53',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (12,24,11,'zhangsan','真好用',5,'2025-11-29 21:04:29','2025-11-29 21:04:29',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (13,32,18,'zhangsan','牛逼6666',5,'2026-04-12 15:00:57','2026-04-12 15:00:57',0);
INSERT INTO `review` (`id`, `order_id`, `product_id`, `member_username`, `content`, `star`, `create_time`, `update_time`, `is_deleted`) VALUES (14,34,20,'tianxuan','1333直接拿下O(∩_∩)O',5,'2026-10-07 23:36:11','2026-10-07 23:36:52',0);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rrrrrrrrreview`
--

DROP TABLE IF EXISTS `rrrrrrrrreview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rrrrrrrrreview` (
  `ReviewID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `RacketID` int DEFAULT NULL,
  `Rating` int DEFAULT NULL,
  `COMMENT` text,
  `ReviewDate` date DEFAULT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ReviewID`),
  KEY `CustomerID` (`CustomerID`),
  KEY `RacketID` (`RacketID`),
  CONSTRAINT `rrrrrrrrreview_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`CustomerID`),
  CONSTRAINT `rrrrrrrrreview_ibfk_2` FOREIGN KEY (`RacketID`) REFERENCES `racket` (`RacketID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rrrrrrrrreview`
--

LOCK TABLES `rrrrrrrrreview` WRITE;
/*!40000 ALTER TABLE `rrrrrrrrreview` DISABLE KEYS */;
INSERT INTO `rrrrrrrrreview` (`ReviewID`, `CustomerID`, `RacketID`, `Rating`, `COMMENT`, `ReviewDate`, `isDeleted`) VALUES (1,1,1,5,'很好用，会复购','2025-06-17',0);
INSERT INTO `rrrrrrrrreview` (`ReviewID`, `CustomerID`, `RacketID`, `Rating`, `COMMENT`, `ReviewDate`, `isDeleted`) VALUES (2,2,2,5,'gooooood','2024-04-10',0);
INSERT INTO `rrrrrrrrreview` (`ReviewID`, `CustomerID`, `RacketID`, `Rating`, `COMMENT`, `ReviewDate`, `isDeleted`) VALUES (3,6,1,5,'林丹的手拍就是好用','2025-06-17',0);
INSERT INTO `rrrrrrrrreview` (`ReviewID`, `CustomerID`, `RacketID`, `Rating`, `COMMENT`, `ReviewDate`, `isDeleted`) VALUES (4,2,2,4,'很好用','2025-06-17',0);
INSERT INTO `rrrrrrrrreview` (`ReviewID`, `CustomerID`, `RacketID`, `Rating`, `COMMENT`, `ReviewDate`, `isDeleted`) VALUES (5,1,1,5,'goooooooooooooooooooooooooooooood','2025-06-17',0);
INSERT INTO `rrrrrrrrreview` (`ReviewID`, `CustomerID`, `RacketID`, `Rating`, `COMMENT`, `ReviewDate`, `isDeleted`) VALUES (6,1,1,5,'球拍很好用，很轻，会回购','2025-06-19',0);
/*!40000 ALTER TABLE `rrrrrrrrreview` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `SupplierID` int NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `SupplyCategory` varchar(100) DEFAULT NULL,
  `isDeleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`SupplierID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier`
--

LOCK TABLES `supplier` WRITE;
/*!40000 ALTER TABLE `supplier` DISABLE KEYS */;
INSERT INTO `supplier` (`SupplierID`, `NAME`, `Phone`, `SupplyCategory`, `isDeleted`) VALUES (1,'Global Sports Co.','1112223333','Badminton Rackets',0);
INSERT INTO `supplier` (`SupplierID`, `NAME`, `Phone`, `SupplyCategory`, `isDeleted`) VALUES (2,'Tennis Pro Supplies','4445556666','Tennis Rackets',0);
INSERT INTO `supplier` (`SupplierID`, `NAME`, `Phone`, `SupplyCategory`, `isDeleted`) VALUES (3,'天下体育','123456654','Badminton Rackets',1);
INSERT INTO `supplier` (`SupplierID`, `NAME`, `Phone`, `SupplyCategory`, `isDeleted`) VALUES (4,'天下体育','888999123','Badminton Rackets',0);
/*!40000 ALTER TABLE `supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
  `phone` varchar(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `is_registered` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `role` varchar(10) DEFAULT 'user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `address`, `email`, `status`, `is_registered`, `create_time`, `update_time`, `role`) VALUES (1,'zhangsan','$2a$10$0U.9mROLn/Sl2PC5p7.ua.f3CdK2bgaHCXiGOyRt3lNvj.05cSQWW','张三','13800000000',NULL,NULL,1,1,NULL,'2026-10-07 01:57:14','user');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `phone`, `address`, `email`, `status`, `is_registered`, `create_time`, `update_time`, `role`) VALUES (2,'tianxuan','$2a$10$cuZwg2jQLdtd9/MTHbJKO.WF2jPI1jRzyAw6i8YUoS2Ez8PhDDzvC',NULL,NULL,NULL,'tianxuan@qq.com',1,1,'2026-10-07 14:38:13',NULL,'user');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'batnew'
--

--
-- Dumping routines for database 'batnew'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-07 23:52:07
