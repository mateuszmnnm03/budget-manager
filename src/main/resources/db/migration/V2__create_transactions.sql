CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(15,2) NOT NULL CHECK ( amount > 0 ),
    type VARCHAR(20) NOT NULL CHECK ( type IN ('INCOME', 'EXPENSE') ),
    category VARCHAR(100) NOT NULL,
    description TEXT,
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
    account_id BIGINT NOT NULL,

    CONSTRAINT fk_transaction_account
      FOREIGN KEY(account_id) REFERENCES accounts(id)
);