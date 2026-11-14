SET @has_admin_reply := (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'support_ticket'
      AND column_name = 'admin_reply'
);
SET @add_admin_reply_sql := IF(
    @has_admin_reply = 0,
    'ALTER TABLE support_ticket ADD COLUMN admin_reply VARCHAR(2000) DEFAULT NULL AFTER admin_note',
    'SELECT 1'
);
PREPARE add_admin_reply_stmt FROM @add_admin_reply_sql;
EXECUTE add_admin_reply_stmt;
DEALLOCATE PREPARE add_admin_reply_stmt;
