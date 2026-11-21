DROP TABLE IF EXISTS failed_transaction;
DROP TABLE IF EXISTS transaction_audit;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS customer;

 CREATE TABLE customer(
    customer_id VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT customer_pkey PRIMARY KEY(customer_id),
    CONSTRAINT customer_email UNIQUE(email));

  CREATE TABLE account(
    account_id VARCHAR(20) NOT NULL,
    customer_id VARCHAR(20) NOT NULL,
    account_type VARCHAR(20) NOT NULL DEFAULT 'SAVINGS',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     CONSTRAINT account_pkey PRIMARY KEY(account_id),
     CONSTRAINT account_customer FOREIGN KEY(customer_id) REFERENCES customer(customer_id),
     CONSTRAINT chk_account_balance CHECK(balance>= 0.00),
     CONSTRAINT chk_account_status CHECK(status IN('ACTIVE','INACTIVE','CLOSED')),
     CONSTRAINT chk_account_type CHECK(account_type IN('SAVINGS','CURRENT','FIXED')));

 CREATE INDEX ind_account_customer ON account(customer_id);
 CREATE INDEX ind_account_status ON account(status);

CREATE TABLE transaction(
    id BIGINT NOT NULL AUTO_INCREMENT,
    transaction_id VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    source_account_id VARCHAR(20),
    dest_account_id VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    CONSTRAINT transaction PRIMARY KEY(id),
    CONSTRAINT transaction_id UNIQUE(transaction_id),
    CONSTRAINT chk_trs_amount CHECK(amount > 0),
    CONSTRAINT chk_trs_type CHECK(transaction_type IN('DEPOSIT','WITHDRAWAL','TRANSFER')),
    CONSTRAINT chk_trs_status CHECK(status IN('PENDING','SUCCESS','FAILED','REJECTED','DUPLICATE'))   );

 CREATE INDEX ind_trs_transaction_id ON transaction(transaction_id);
 CREATE INDEX ind_trs_source_account ON transaction(source_account_id);
 CREATE INDEX ind_trs_dest_account ON transaction(dest_account_id);
 CREATE INDEX ind_trs_status ON transaction(status);
 CREATE INDEX ind_trs_created_type ON transaction(created_at, transaction_type);

CREATE TABLE transaction_audit (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transaction_id VARCHAR(50) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thread_name VARCHAR(100) NOT NULL,
    duration_ms BIGINT NOT NULL,
    operation VARCHAR(50) NOT NULL,
    result VARCHAR(20) NOT NULL,
    CONSTRAINT transaction_audit_pkey PRIMARY KEY(id),
     CONSTRAINT audit_transaction_fkey FOREIGN KEY(transaction_id) REFERENCES transaction(transaction_id) ON DELETE CASCADE);

 CREATE INDEX ind_audit_trs_id ON transaction_audit(transaction_id);

 CREATE TABLE failed_transaction(
    id BIGINT NOT NULL AUTO_INCREMENT,
    transaction_id VARCHAR(50) NOT NULL,
    error_category VARCHAR(50) NOT NULL,
    error_message VARCHAR(500) NOT NULL,
    failed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT failed_transaction_pkey PRIMARY KEY (id),
    CONSTRAINT failed_transaction_fkey FOREIGN KEY (transaction_id) REFERENCES transaction(transaction_id) ON DELETE CASCADE,
    CONSTRAINT chk_error_category CHECK (error_category IN ('INSUFFICIENT_BALANCE','ACCOUNT_NOT_FOUND','ACCOUNT_INACTIVE','DUPLICATE_TRANSACTION','INVALID_AMOUNT','INVALID_TRANSACTION','SYSTEM_ERROR')));

 CREATE INDEX ind_failed_category ON failed_transaction(error_category);
 CREATE INDEX ind_failed_trs_id ON failed_transaction(transaction_id);