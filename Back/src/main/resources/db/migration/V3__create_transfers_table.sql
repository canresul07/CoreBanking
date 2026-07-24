-- TODO(Can): transfers tablosunu oluşturacak SQL kodunu buraya yaz!
-- Gerekli sütunlar: id (UUID), from_account_id (UUID), to_account_id (UUID), amount (NUMERIC/DECIMAL), status (VARCHAR), idempotency_key (VARCHAR), created_at
-- idempotency_key benzersiz (UNIQUE) olmalı.
-- from_account_id ve to_account_id, accounts tablosundaki id ile ilişkilendirilmiş (FOREIGN KEY) olmalı.

-- BURAYA SQL KODUNU YAZ:

CREATE TABLE transfers (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    "from_account_id" UUID NOT NULL,
    "to_account_id" UUID NOT NULL,
    "amount" NUMERIC(19, 4) NOT NULL,
    "status" VARCHAR(255) NOT NULL,
    "idempotency_key" VARCHAR(255) UNIQUE NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY ("from_account_id") REFERENCES "accounts" ("id"),
    FOREIGN KEY ("to_account_id") REFERENCES "accounts" ("id")
);