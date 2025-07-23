CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    
    user_id BIGINT UNIQUE,  -- 계정당 계좌 1개
    bank_code VARCHAR(20),
    account_number VARCHAR(50) NOT NULL UNIQUE,  
    account_owner VARCHAR(100),

    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);