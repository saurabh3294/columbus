-- MySQL dump 10.13  Distrib 5.5.32, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: proptiger
-- ------------------------------------------------------
-- Server version	5.5.32-0ubuntu0.13.04.1

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
-- Table structure for table `ObjectType`
--

DROP TABLE IF EXISTS `ObjectType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ObjectType` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_UNIQUE` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ObjectType`
--

LOCK TABLES `ObjectType` WRITE;
/*!40000 ALTER TABLE `ObjectType` DISABLE KEYS */;
INSERT INTO `ObjectType` VALUES (5,'bank'),(3,'builder'),(4,'locality'),(1,'project'),(2,'property');
/*!40000 ALTER TABLE `ObjectType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ImageType`
--

DROP TABLE IF EXISTS `ImageType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ImageType` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ObjectType_id` int(11) NOT NULL,
  `type` varchar(45) NOT NULL,
  `status` enum('','Done','Hold','Partial') NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_object_type` (`ObjectType_id`,`type`),
  KEY `fk_ImageType_ObjectType_idx` (`ObjectType_id`),
  CONSTRAINT `fk_ImageType_ObjectType` FOREIGN KEY (`ObjectType_id`) REFERENCES `ObjectType` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ImageType`
--

LOCK TABLES `ImageType` WRITE;
/*!40000 ALTER TABLE `ImageType` DISABLE KEYS */;
INSERT INTO `ImageType` VALUES (1,1,'applicationForm',''),(2,1,'clusterPlan',''),(3,1,'constructionStatus',''),(4,1,'layoutPlan',''),(5,1,'locationPlan',''),(6,1,'main',''),(7,1,'masterPlan',''),(8,1,'paymentPlan',''),(9,1,'priceList',''),(10,1,'sitePlan',''),(11,1,'specification',''),(12,2,'floorPlan',''),(13,3,'logo','Done'),(14,4,'mall','Done'),(15,4,'road','Done'),(16,4,'school','Done'),(17,4,'hospital','Done'),(18,4,'other','Done'),(19,5,'logo','Done');
/*!40000 ALTER TABLE `ImageType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Image`
--

DROP TABLE IF EXISTS `Image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Image` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ImageType_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `path` varchar(2048) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `taken_at` timestamp NULL DEFAULT NULL,
  `size_in_bytes` bigint(20) unsigned NOT NULL,
  `width` int(10) unsigned NOT NULL,
  `height` int(10) unsigned NOT NULL,
  `latitude` decimal(18,15) DEFAULT NULL,
  `longitude` decimal(18,15) DEFAULT NULL,
  `alt_text` varchar(1024) DEFAULT NULL,
  `title` varchar(1024) DEFAULT NULL,
  `description` text,
  `json_dump` text,
  `priority` int(11) DEFAULT NULL,
  `original_hash` varchar(255) NOT NULL,
  `watermark_hash` varchar(255) NOT NULL,
  `original_name` varchar(255) NOT NULL,
  `watermark_name` varchar(255) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_Image_ImageType_idx` (`ImageType_id`),
  CONSTRAINT `fk_Image_ImageType` FOREIGN KEY (`ImageType_id`) REFERENCES `ImageType` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Image`
--

LOCK TABLES `Image` WRITE;
/*!40000 ALTER TABLE `Image` DISABLE KEYS */;
/*!40000 ALTER TABLE `Image` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-10-16 12:38:33
