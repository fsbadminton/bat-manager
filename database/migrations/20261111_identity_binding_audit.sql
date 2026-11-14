CREATE TABLE IF NOT EXISTS `identity_binding_audit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `operator_name` VARCHAR(100) NOT NULL,
  `action` VARCHAR(40) NOT NULL,
  `customer_id` BIGINT NOT NULL,
  `platform` VARCHAR(32) NOT NULL,
  `external_user_id_hash` VARCHAR(64) NOT NULL,
  `reason` VARCHAR(500) NOT NULL,
  `outcome` VARCHAR(20) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_identity_binding_audit_customer_created` (`customer_id`, `created_at`),
  KEY `idx_identity_binding_audit_operator_created` (`operator_name`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
