-- TODO(Can): accounts tablosunu oluşturacak SQL kodunu buraya yaz!
-- Gerekli sütunlar: id (UUID), user_id (UUID), account_number (VARCHAR), currency (VARCHAR), balance (NUMERIC/DECIMAL), created_at, updated_at
-- accountNumber benzersiz (UNIQUE) olmalı.
-- user_id, users tablosundaki id ile ilişkilendirilmiş (FOREIGN KEY) olmalı.

-- BURAYA SQL KODUNU YAZ:

CREATE TABLE accounts (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    "user_id" UUID NOT NULL,
    "account_number" VARCHAR(255) UNIQUE NOT NULL,
    "currency" VARCHAR(255) NOT NULL,
    "balance" NUMERIC(19, 4) NOT NULL DEFAULT 0.0,
    "created_at" TIMESTAMP NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMP NOT NULL DEFAULT now(),
    FOREIGN KEY ("user_id") REFERENCES "users" ("id")
);