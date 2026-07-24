-- V4__Create_Loans_Table.sql

-- TODO(Can): Create 'loans' table
-- It should have:
-- id UUID PRIMARY KEY
-- account_id UUID NOT NULL (Foreign key to accounts table)
-- amount NUMERIC(19, 4) NOT NULL
-- interest_rate NUMERIC(5, 2) NOT NULL
-- status VARCHAR(20) NOT NULL
-- created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

-- TODO(Can): Add foreign key constraint for account_id referencing accounts(id)
