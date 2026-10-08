-- webflux.product definition

CREATE TABLE IF NOT EXISTS `product` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `name` varchar(10) NOT NULL,
                           `price` float DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `product_unique` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;