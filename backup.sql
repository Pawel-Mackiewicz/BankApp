-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: bankapp
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Current Database: `bankapp`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `bankapp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `bankapp`;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `balance` decimal(38,2) DEFAULT NULL,
  `owner_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjln86358moqf5k5pw89oiq8ur` (`owner_id`),
  CONSTRAINT `FKjln86358moqf5k5pw89oiq8ur` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (-1,999999999.00,-1),(1,3400.00,2),(3,6100.00,4),(4,0.00,6),(5,0.00,7),(6,0.00,8),(7,0.00,9),(8,0.00,10),(9,0.00,11),(10,0.00,12),(11,0.00,13);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'bankappuser','2025-02-10 21:54:30',0,1),(2,'2','Add user columns','SQL','V2__Add_user_columns.sql',-522240274,'bankappuser','2025-02-12 18:18:07',172,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `status` enum('DONE','FAULTY','IN_PROGRESS','NEW') DEFAULT NULL,
  `type` enum('DEPOSIT','FEE','TRANSFER','WITHDRAWAL') DEFAULT NULL,
  `destination_id` int DEFAULT NULL,
  `source_id` int DEFAULT NULL,
  `transaction_title` varchar(100) DEFAULT NULL,
  `transaction_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcsb0kdvsdj55hikqj3unk926t` (`destination_id`),
  KEY `FKmfw23pm7d5mhjufcjh85vgt59` (`source_id`),
  CONSTRAINT `FKcsb0kdvsdj55hikqj3unk926t` FOREIGN KEY (`destination_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `FKmfw23pm7d5mhjufcjh85vgt59` FOREIGN KEY (`source_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `transactions_chk_1` CHECK ((`status` in (_utf8mb4'NEW',_utf8mb4'IN_PROGRESS',_utf8mb4'FAULTY',_utf8mb4'DONE'))),
  CONSTRAINT `transactions_chk_2` CHECK ((`type` in (_utf8mb4'TRANSFER',_utf8mb4'DEPOSIT',_utf8mb4'WITHDRAWAL',_utf8mb4'FEE')))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (5,2000.00,'DONE','DEPOSIT',1,NULL,NULL,NULL),(6,2000.00,'DONE','DEPOSIT',1,NULL,'przelewik',NULL),(7,2000.00,'DONE','WITHDRAWAL',NULL,1,'wyplata',NULL),(8,2000.00,'NEW','DEPOSIT',1,NULL,'przelewik',NULL),(9,2000.00,'NEW','DEPOSIT',1,NULL,'przelewik',NULL),(10,2000.00,'NEW','DEPOSIT',1,NULL,'przelewik','2025-02-10 23:02:04.482785');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` int NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pesel` varchar(255) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `expired` tinyint(1) NOT NULL DEFAULT '0',
  `credentialsExpired` tinyint(1) NOT NULL DEFAULT '0',
  `locked` tinyint(1) NOT NULL DEFAULT '0',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `credentials_expired` bit(1) NOT NULL,
  `firstname` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKitp610ijy6ku9afo2it8wajii` (`pesel`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (-1,NULL,'2025-02-08','Rich','Richie',NULL,NULL,0,0,0,1,_binary '\0','',''),(1,'12345678901','1995-05-20','Smith','Alice',NULL,NULL,0,0,0,1,_binary '\0','',''),(2,'90020512345','1990-02-05','Johnson','John',NULL,NULL,0,0,0,1,_binary '\0','',''),(4,'00050504811','2000-05-05','Smith','John',NULL,NULL,0,0,0,1,_binary '\0','',''),(5,'01234567890','2025-02-04','Picasso','Pablo','zizu4851@gmail.com','$2a$10$3lTG0YQtMidvreZC4GKWG.RRa/rXaCM.uZH4LcfuQcsiU6PBHGjJa',0,0,0,1,_binary '\0','',''),(6,'01234567892','2003-01-28','Macierewicz','Antoni','dasda@wad.pll','$2a$10$tWN21R5AzhOX0rfFbTxJdunW.e09FJSAFq4lsrKbrBCz98mB5yPZO',0,0,0,1,_binary '\0','',''),(7,'01061405573','1999-10-03','Paul','Jon','jon@paul.ok','$2a$10$RYKZ5e0p1jzOUKKA/Au30OV4Da4Ws6vdXV4q4XVXsf1.xAsGF9Cxi',0,0,0,1,_binary '\0','',''),(8,'01234567811','2002-01-30','JOVI','BON','EQQ@WE.PL','$2a$10$mUzXGhSWd7./SKAVenTqzOmj3eiVffhsNCzVw0QXuJm8GJ2h9K7JC',0,0,0,1,_binary '\0','',''),(9,'01061405574','2000-02-03','adas','dsada','zizu4851@gmail.com','$2a$10$l9E89k3NYPFGi6.P5veUxOY10Y0WLVuZindSUvnU7kTWTZXBCYN.i',0,0,0,1,_binary '\0','',''),(10,'99010101011','1999-01-01','Jackowiak','Anna','anna.jackowiak@wp.pl','$2a$10$mKh4K5n8y8BGCtCI0UfAk.VYNnGXsb1R4BQ97Fap6O6JMrffTjLy2',0,0,0,1,_binary '\0','',''),(11,'99010101012','1999-01-01','Leśniak','Anna','anna.lesniak@wp.pl','$2a$10$OQZ/jIKcZDa6zBwN4hvnEO8r7B7o8i1vRba8e1t5Jx7HTacZs1tuW',0,0,0,1,_binary '\0','',''),(12,'91010112345','1991-01-01','Walczak','Piotr','piotr.walczak@wp.pl','$2a$10$3lGpx55mGPVFnjSAg615IuymIIEkgnml0BSxOqddfEmqbBKZlzzqC',0,0,0,1,_binary '\0','',''),(13,'91010112344','1991-01-01','Waleczny','Piotr','piotr.waleczny@wp.pl','$2a$10$2VI39201LkmYhXdo3SKUVuV6IV4qHdNQ5KQFbRarNtsTZQIvO6/NO',0,0,0,1,_binary '\0','','');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-12 19:42:08
