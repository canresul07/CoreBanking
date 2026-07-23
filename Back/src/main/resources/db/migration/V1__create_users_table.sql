-- TODO(Can): Kullanıcılar (users) tablosunu oluştur.
-- Aşağıdaki PostgreSQL söz dizimini (syntax) örnek alarak kendi tablonu oluşturabilirsin.
--
-- ÖRNEK ŞABLON (Bunu kopyalama, sadece yapısal olarak incele):
-- CREATE TABLE ornek_tablo (
--     id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
--     ad_soyad        VARCHAR(100) NOT NULL,
--     yas             INT DEFAULT 18,
--     aktif_mi        BOOLEAN DEFAULT true,
--     kayit_tarihi    TIMESTAMP NOT NULL DEFAULT now()
-- );
--
-- GÖREVİN: Yukarıdaki şablonu inceleyerek kendi 'users' tablonu aşağıdaki kurallara göre yaz:
-- 1. Tablo adını 'users' olarak belirle.
-- 2. 'id' kolonu: UUID tipinde, PRIMARY KEY, varsayılan değeri gen_random_uuid()
-- 3. 'username' kolonu: VARCHAR(50), UNIQUE, NOT NULL
-- 4. 'email' kolonu: VARCHAR(255), UNIQUE, NOT NULL
-- 5. 'password_hash' kolonu: VARCHAR(60), NOT NULL
-- 6. 'role' kolonu: VARCHAR(20), NOT NULL, varsayılan 'CUSTOMER'
-- 7. 'failed_login_attempts' kolonu: INT, NOT NULL, varsayılan 0
-- 8. 'locked_until' kolonu: TIMESTAMP, NULL olabilir
-- 9. 'created_at' kolonu: TIMESTAMP, NOT NULL, varsayılan now()
-- 10. 'updated_at' kolonu: TIMESTAMP, NOT NULL, varsayılan now()

-- KODUNU BURANIN ALTINA YAZ:

CREATE TABLE "users" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    "username" VARCHAR(50) UNIQUE NOT NULL,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "password_hash" VARCHAR(60) NOT NULL,
    "role" VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    "failed_login_attempts" INT NOT NULL DEFAULT 0,
    "locked_until" TIMESTAMP,
    "created_at" TIMESTAMP NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMP NOT NULL DEFAULT now()
);