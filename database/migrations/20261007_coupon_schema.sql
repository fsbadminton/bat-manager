-- Incremental schema update for the coupon feature.
-- Safe to run repeatedly on MySQL 8 without dropping existing data.

CREATE TABLE IF NOT EXISTS `coupon` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `coupon_history` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET @schema_name = DATABASE();

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'order' AND COLUMN_NAME = 'coupon_amount'),
  'SELECT 1',
  'ALTER TABLE `order` ADD COLUMN `coupon_amount` decimal(10,2) NOT NULL DEFAULT ''0.00'' AFTER `total_amount`'
);
PREPARE schema_statement FROM @ddl;
EXECUTE schema_statement;
DEALLOCATE PREPARE schema_statement;

-- Seed system coupons and grant the existing-user welcome campaign once.
-- The note values are stable identifiers used by the registration flow.
INSERT INTO `coupon` (
  `type`, `name`, `platform`, `publish_count`, `amount`, `per_limit`, `min_point`,
  `enable_time`, `start_time`, `end_time`, `use_type`, `note`,
  `product_relation_json`, `product_category_relation_json`, `create_time`, `update_time`
)
SELECT 0, CONVERT(0xE6BBA131303030E5878F323030 USING utf8mb4), 0, NULL, 200.00, 1, 1000.00,
       NOW(), NOW(), NULL, 0, 'system_code:ALL_USERS_1000_200',
       '[]', '[]', NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM `coupon` WHERE `note` = 'system_code:ALL_USERS_1000_200'
);

SET @all_users_coupon_id = (
  SELECT `id` FROM `coupon` WHERE `note` = 'system_code:ALL_USERS_1000_200' LIMIT 1
);

INSERT INTO `coupon_history` (
  `coupon_id`, `coupon_code`, `member_nickname`, `member_username`,
  `get_type`, `create_time`, `use_status`
)
SELECT @all_users_coupon_id,
       CONCAT('G1000-', u.`id`),
       COALESCE(u.`nickname`, u.`username`),
       u.`username`,
       2, NOW(), 0
FROM `user` u
WHERE NOT EXISTS (
  SELECT 1
  FROM `coupon_history` ch
  WHERE ch.`coupon_id` = @all_users_coupon_id
    AND ch.`member_username` = u.`username`
);

INSERT INTO `coupon` (
  `type`, `name`, `platform`, `publish_count`, `amount`, `per_limit`, `min_point`,
  `enable_time`, `start_time`, `end_time`, `use_type`, `note`,
  `product_relation_json`, `product_category_relation_json`, `create_time`, `update_time`
)
SELECT 0, CONVERT(0xE696B0E4BABAE697A0E997A8E6A79B3530E58583 USING utf8mb4), 0, NULL, 50.00, 1, 0.00,
       NOW(), NOW(), NULL, 0, 'system_code:NEW_USER_50',
       '[]', '[]', NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM `coupon` WHERE `note` = 'system_code:NEW_USER_50'
);

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'order' AND COLUMN_NAME = 'coupon_history_id'),
  'SELECT 1',
  'ALTER TABLE `order` ADD COLUMN `coupon_history_id` bigint DEFAULT NULL AFTER `coupon_amount`'
);
PREPARE schema_statement FROM @ddl;
EXECUTE schema_statement;
DEALLOCATE PREPARE schema_statement;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'user' AND COLUMN_NAME = 'email'),
  'SELECT 1',
  'ALTER TABLE `user` ADD COLUMN `email` varchar(100) DEFAULT NULL AFTER `address`'
);
PREPARE schema_statement FROM @ddl;
EXECUTE schema_statement;
DEALLOCATE PREPARE schema_statement;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'user' AND COLUMN_NAME = 'status'),
  'SELECT 1',
  'ALTER TABLE `user` ADD COLUMN `status` tinyint NOT NULL DEFAULT ''1'' AFTER `email`'
);
PREPARE schema_statement FROM @ddl;
EXECUTE schema_statement;
DEALLOCATE PREPARE schema_statement;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'user' AND COLUMN_NAME = 'is_registered'),
  'SELECT 1',
  'ALTER TABLE `user` ADD COLUMN `is_registered` tinyint NOT NULL DEFAULT ''1'' AFTER `status`'
);
PREPARE schema_statement FROM @ddl;
EXECUTE schema_statement;
DEALLOCATE PREPARE schema_statement;

SET @ddl = IF(
  EXISTS(SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = 'user' AND INDEX_NAME = 'uk_user_email'),
  'SELECT 1',
  'ALTER TABLE `user` ADD UNIQUE KEY `uk_user_email` (`email`)'
);
PREPARE schema_statement FROM @ddl;
EXECUTE schema_statement;
DEALLOCATE PREPARE schema_statement;
