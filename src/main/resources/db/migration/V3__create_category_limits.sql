CREATE TABLE category_limits (
     id BIGSERIAL PRIMARY KEY,
     account_id  BIGINT NOT NULL REFERENCES accounts(id),
     category VARCHAR(100) NOT NULL,
     limit_amount NUMERIC(15,2) NOT NULL CHECK (limit_amount > 0),
     CONSTRAINT uq_account_category UNIQUE (account_id, category)
);