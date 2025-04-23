CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `balance` decimal(38,2) DEFAULT NULL,
  `creation_date` datetime(6) DEFAULT NULL,
  `iban` varchar(255) NOT NULL,
  `user_account_number` int NOT NULL,
  `owner_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnpdpfltolx9ei49uw6lxol877` (`iban`),
  KEY `FK46cu6sew5blmbse68d6wn9pdv` (`owner_id`),
  CONSTRAINT `FK46cu6sew5blmbse68d6wn9pdv` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;