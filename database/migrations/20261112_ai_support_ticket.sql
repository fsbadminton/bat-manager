CREATE TABLE IF NOT EXISTS support_ticket (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ticket_no VARCHAR(24) NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    priority VARCHAR(16) NOT NULL DEFAULT 'medium',
    status VARCHAR(24) NOT NULL DEFAULT 'open',
    idempotency_key VARCHAR(128) NOT NULL,
    admin_note VARCHAR(2000) DEFAULT NULL,
    admin_reply VARCHAR(2000) DEFAULT NULL,
    handled_by VARCHAR(64) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_support_ticket_no (ticket_no),
    UNIQUE KEY uk_support_ticket_user_idempotency (user_id, idempotency_key),
    KEY idx_support_ticket_user_created (user_id, id DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
