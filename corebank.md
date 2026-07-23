# CoreBank — Hibrit Mini Bankacılık Sistemi | Kapsamlı Geliştirme Planı

> Bu doküman bir AI kodlama ajanı (Antigravity) tarafından baştan sona uygulanmak üzere hazırlanmıştır.
> Amaç: Öğrenme odaklı ama **production-grade güvenlik standartlarında** çalışan bir mini core banking sistemi.
> Tüm kod, öğretici Türkçe yorum satırları içermelidir (ne yapıldığı VE neden yapıldığı açıklanmalı).

---

## 0. Proje Kimliği

- **Proje adı:** CoreBank
- **Amaç:** NoSQL + SQL hibrit veritabanı mimarisi, core banking domain mantığı ve enterprise-grade güvenlik pratiklerini öğrenmek.
- **Backend:** Java 21 + Spring Boot 3.x
- **Frontend:** Angular 18+ (standalone components, signals kullanılabilir)
- **Veritabanları:** PostgreSQL 16 (ilişkisel, çekirdek finansal veri) + MongoDB 7 (döküman tabanlı, işlem geçmişi/audit log) + Redis 7 (cache + distributed lock + refresh token store)
- **Konteynerizasyon:** Docker + Docker Compose
- **Auth modeli:** JWT (kısa ömürlü access token, memory'de) + Refresh Token (httpOnly, Secure, SameSite=Strict cookie'de)
- **Mimari stil:** Modüler monolith (mikroservis GÖRÜNÜMLÜ ama tek Spring Boot uygulaması içinde paket bazlı ayrım — öğrenme projesi için mikroservis altyapısı (service discovery, API gateway, ayrı deployment) gereksiz karmaşıklık yaratır. İleride gerçek mikroservise bölünebilecek şekilde paketler net sınırlarla ayrılmalı.)

---

## 1. Genel Mimari Diyagramı

```
┌─────────────────────────────────────────────────────────┐
│                    Angular 18 Frontend                    │
│  (Standalone Components, Route Guards, HTTP Interceptor)  │
└───────────────────────────┬─────────────────────────────┘
                             │ HTTPS (dev: localhost, CORS whitelist)
                             │ Access Token: Authorization header (Bearer)
                             │ Refresh Token: httpOnly Secure cookie (otomatik gider)
┌───────────────────────────▼─────────────────────────────┐
│                Spring Boot 3.x Backend (tek uygulama)      │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐         │
│  │ auth package │ │ account     │ │ transfer    │         │
│  │ (JWT, login) │ │ package     │ │ package     │         │
│  └─────────────┘ └─────────────┘ └─────────────┘         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐         │
│  │ loan package │ │ transaction │ │ common/     │         │
│  │              │ │ -history    │ │ security    │         │
│  └─────────────┘ └─────────────┘ └─────────────┘         │
└──────┬──────────────────┬──────────────────┬─────────────┘
       │                  │                  │
       ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ PostgreSQL  │    │  MongoDB    │    │   Redis     │
│ (users,     │    │ (transaction│    │ (cache,     │
│ accounts,   │    │  history,   │    │ refresh     │
│ transfers,  │    │  audit log) │    │ token       │
│ loans)      │    │             │    │ blacklist,  │
│             │    │             │    │ dist. lock) │
└─────────────┘    └─────────────┘    └─────────────┘
```

**Neden bu ayrım?**
- **PostgreSQL** → Para ile ilgili HER ŞEY burada. ACID garantisi şart (bir transferin yarısı gerçekleşip yarısı gerçekleşmemesi asla olmamalı).
- **MongoDB** → İşlem geçmişi/audit log yüksek hacimli, şema esnekliği gerektiren (her event tipi farklı alanlara sahip olabilir), "bir kere yazılır çok okunur" veri. Burada eventual consistency kabul edilebilir çünkü zaten para hareketi PostgreSQL'de garanti altına alınmış oluyor, Mongo sadece "ne oldu"nun kaydı.
- **Redis** → (1) Bakiye sorgularını cache'lemek, (2) transfer sırasında distributed lock (aynı hesaba eşzamanlı iki transfer gelirse çakışmayı önlemek), (3) refresh token'ların ve logout olmuş (blacklist'e alınmış) access token'ların tutulması.

---

## 2. Klasör Yapısı

### 2.1 Backend (`corebank-backend/`)

```
corebank-backend/
├── src/main/java/com/corebank/
│   ├── CorebankApplication.java
│   │
│   ├── common/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java          # Spring Security zincirleme yapılandırması
│   │   │   ├── CorsConfig.java              # CORS whitelist (sadece frontend origin'i)
│   │   │   ├── RedisConfig.java
│   │   │   ├── MongoConfig.java
│   │   │   ├── OpenApiConfig.java           # Swagger/OpenAPI dökümantasyonu
│   │   │   └── WebConfig.java               # Rate limiting interceptor kaydı
│   │   ├── security/
│   │   │   ├── jwt/
│   │   │   │   ├── JwtTokenProvider.java    # Token üretme/doğrulama
│   │   │   │   ├── JwtAuthenticationFilter.java  # Her request'te token kontrolü
│   │   │   │   └── JwtProperties.java       # application.yml'den secret/expiry okuma
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── RefreshTokenService.java     # Redis'te refresh token yönetimi
│   │   │   └── RateLimitInterceptor.java    # IP bazlı brute-force koruması
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java  # @ControllerAdvice, tüm hataları merkezi yönetme
│   │   │   ├── InsufficientBalanceException.java
│   │   │   ├── AccountNotFoundException.java
│   │   │   └── ErrorResponse.java           # Standart hata formatı (stack trace ASLA dönmez!)
│   │   ├── audit/
│   │   │   └── AuditEventPublisher.java     # Mongo'ya event yazan merkezi servis
│   │   └── util/
│   │       ├── IbanGenerator.java
│   │       └── MoneyUtil.java               # BigDecimal işlemleri için yardımcı sınıf
│   │
│   ├── auth/
│   │   ├── controller/AuthController.java   # /api/auth/login, /register, /refresh, /logout
│   │   ├── dto/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── service/AuthService.java
│   │   ├── entity/User.java
│   │   └── repository/UserRepository.java
│   │
│   ├── account/
│   │   ├── controller/AccountController.java
│   │   ├── dto/
│   │   │   ├── AccountResponse.java
│   │   │   ├── CreateAccountRequest.java
│   │   │   └── BalanceResponse.java
│   │   ├── service/AccountService.java
│   │   ├── entity/Account.java
│   │   └── repository/AccountRepository.java
│   │
│   ├── transfer/
│   │   ├── controller/TransferController.java
│   │   ├── dto/
│   │   │   ├── TransferRequest.java
│   │   │   └── TransferResponse.java
│   │   ├── service/
│   │   │   ├── TransferService.java         # @Transactional, locking, idempotency
│   │   │   └── DistributedLockService.java  # Redisson tabanlı kilit
│   │   ├── entity/Transfer.java
│   │   └── repository/TransferRepository.java
│   │
│   ├── loan/
│   │   ├── controller/LoanController.java
│   │   ├── dto/
│   │   │   ├── LoanApplicationRequest.java
│   │   │   └── LoanResponse.java
│   │   ├── service/
│   │   │   ├── LoanService.java
│   │   │   └── CreditScoreCalculator.java   # Basit risk skorlama algoritması
│   │   ├── entity/
│   │   │   ├── LoanApplication.java
│   │   │   └── LoanStatus.java              # Enum: state machine
│   │   └── repository/LoanRepository.java
│   │
│   └── transactionhistory/
│       ├── controller/TransactionHistoryController.java
│       ├── document/TransactionEvent.java   # MongoDB @Document
│       ├── repository/TransactionEventRepository.java  # MongoRepository
│       ├── listener/
│       │   └── TransferEventListener.java   # @EventListener, PostgreSQL işlemi sonrası Mongo'ya yazar
│       └── event/TransferCompletedEvent.java # Spring ApplicationEvent
│
├── src/main/resources/
│   ├── application.yml                      # Ortak ayarlar
│   ├── application-dev.yml                  # Local Docker DB bağlantıları
│   ├── application-prod.yml                 # Prod (env variable referansları, gerçek secret YOK)
│   └── db/migration/                        # Flyway migration dosyaları
│       ├── V1__create_users_table.sql
│       ├── V2__create_accounts_table.sql
│       ├── V3__create_transfers_table.sql
│       └── V4__create_loan_applications_table.sql
│
├── src/test/java/com/corebank/             # Unit + integration testler
├── Dockerfile
├── pom.xml
└── .env.example                             # Gerçek .env ASLA git'e eklenmez!
```

### 2.2 Frontend (`corebank-frontend/`)

```
corebank-frontend/
├── src/app/
│   ├── app.config.ts                        # Standalone bootstrap config
│   ├── app.routes.ts                        # Lazy-loaded route tanımları + guard'lar
│   │
│   ├── core/
│   │   ├── guards/
│   │   │   ├── auth.guard.ts                # Login olmayan kullanıcıyı /login'e atar
│   │   │   └── role.guard.ts                # RBAC — admin/user rolü kontrolü
│   │   ├── interceptors/
│   │   │   ├── auth.interceptor.ts          # Her isteğe Authorization header ekler
│   │   │   ├── error.interceptor.ts         # 401 geldiğinde otomatik refresh-token dener
│   │   │   └── csrf.interceptor.ts          # CSRF token header'a ekler
│   │   ├── services/
│   │   │   ├── auth.service.ts              # login/logout/refresh, access token'ı SADECE memory'de tutar
│   │   │   ├── token-storage.service.ts     # Access token in-memory (localStorage KULLANILMAZ!)
│   │   │   └── api-config.service.ts
│   │   └── models/
│   │       ├── user.model.ts
│   │       └── auth-response.model.ts
│   │
│   ├── features/
│   │   ├── auth/
│   │   │   ├── login/login.component.ts(.html/.scss)
│   │   │   └── register/register.component.ts
│   │   ├── dashboard/
│   │   │   └── dashboard.component.ts       # Genel bakış: bakiye, son işlemler özeti
│   │   ├── accounts/
│   │   │   ├── account-list/account-list.component.ts
│   │   │   └── account-detail/account-detail.component.ts
│   │   ├── transfer/
│   │   │   └── transfer-form/transfer-form.component.ts
│   │   ├── loans/
│   │   │   ├── loan-application/loan-application.component.ts
│   │   │   └── loan-status/loan-status.component.ts
│   │   └── transaction-history/
│   │       └── transaction-list/transaction-list.component.ts
│   │
│   ├── shared/
│   │   ├── components/                      # Buton, input, modal gibi ortak UI parçaları
│   │   └── pipes/currency-format.pipe.ts
│   │
│   └── app.component.ts
│
├── src/environments/
│   ├── environment.ts
│   └── environment.prod.ts
├── angular.json
├── package.json
└── Dockerfile
```

---

## 3. Docker Compose (Tüm Ortam)

`docker-compose.yml` proje kök dizininde olmalı:

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: corebank-postgres
    environment:
      POSTGRES_DB: corebank
      POSTGRES_USER: corebank_user
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}   # .env dosyasından okunur, hardcode YOK
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U corebank_user -d corebank"]
      interval: 5s
      timeout: 5s
      retries: 5

  mongodb:
    image: mongo:7
    container_name: corebank-mongo
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_ROOT_USER}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_ROOT_PASSWORD}
      MONGO_INITDB_DATABASE: corebank_history
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  redis:
    image: redis:7-alpine
    container_name: corebank-redis
    command: redis-server --requirepass ${REDIS_PASSWORD}
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  postgres_data:
  mongo_data:
  redis_data:
```

`.env` dosyası (git'e eklenmeyecek, `.env.example` şablonu commit edilecek):

```
POSTGRES_PASSWORD=change_me_local_dev_only
MONGO_ROOT_USER=corebank_admin
MONGO_ROOT_PASSWORD=change_me_local_dev_only
REDIS_PASSWORD=change_me_local_dev_only
JWT_SECRET=<en az 256 bit rastgele üretilmiş, base64 encoded>
```

**Ajan için not:** `.gitignore` içine mutlaka `.env`, `*.log`, `target/`, `node_modules/`, `dist/` eklenmeli.

---

## 4. Backend Detayları

### 4.1 PostgreSQL Şeması (Flyway Migration Örneği)

`V1__create_users_table.sql`:
```sql
-- Kullanıcı tablosu: kimlik doğrulama için kullanılır.
-- Şifre ASLA plaintext tutulmaz, BCrypt hash'i saklanır (60 karakter sabit uzunluk).
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username        VARCHAR(50) UNIQUE NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(60) NOT NULL,       -- BCrypt her zaman 60 karakter üretir
    role            VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- CUSTOMER / ADMIN
    failed_login_attempts INT NOT NULL DEFAULT 0,  -- Brute-force koruması için sayaç
    locked_until    TIMESTAMP NULL,              -- Hesap kilitleme (5 başarısız denemeden sonra)
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
```

`V2__create_accounts_table.sql`:
```sql
-- Banka hesabı tablosu.
-- balance NUMERIC(19,4) kullanılır — asla FLOAT/DOUBLE değil! Ondalık hassasiyet finansal
-- işlemlerde kritik, float yuvarlama hatası milyonlarca işlemde büyük tutarsızlık yaratabilir.
CREATE TABLE accounts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    iban            VARCHAR(34) UNIQUE NOT NULL,
    account_type    VARCHAR(20) NOT NULL,   -- CHECKING / SAVINGS
    balance         NUMERIC(19,4) NOT NULL DEFAULT 0.00,
    currency        VARCHAR(3) NOT NULL DEFAULT 'TRY',
    version         BIGINT NOT NULL DEFAULT 0,  -- Optimistic locking için (@Version alanı JPA'da)
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / FROZEN / CLOSED
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_accounts_iban ON accounts(iban);
```

`V3__create_transfers_table.sql`:
```sql
-- Transfer kayıtları. idempotency_key: aynı isteğin yanlışlıkla iki kez gönderilmesini önler
-- (örneğin kullanıcı "Gönder" butonuna iki kez tıklarsa, ikinci istek aynı key ile geldiğinde
-- backend ilk sonucu döner, parayı ikinci kez çekmez).
CREATE TABLE transfers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key     VARCHAR(100) UNIQUE NOT NULL,
    from_account_id     UUID NOT NULL REFERENCES accounts(id),
    to_account_id       UUID NOT NULL REFERENCES accounts(id),
    amount              NUMERIC(19,4) NOT NULL CHECK (amount > 0),
    status              VARCHAR(20) NOT NULL, -- PENDING / COMPLETED / FAILED
    description         VARCHAR(255),
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);
```

`V4__create_loan_applications_table.sql`:
```sql
CREATE TABLE loan_applications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    amount          NUMERIC(19,4) NOT NULL CHECK (amount > 0),
    term_months     INT NOT NULL,
    monthly_income  NUMERIC(19,4) NOT NULL,
    existing_debt   NUMERIC(19,4) NOT NULL DEFAULT 0,
    credit_score    INT,
    status          VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
    -- SUBMITTED -> UNDER_REVIEW -> APPROVED/REJECTED -> ACTIVE -> CLOSED
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    decided_at      TIMESTAMP NULL
);
```

### 4.2 MongoDB Şeması (Öğretici Yorumlarla)

```java
// TransactionEvent.java
// MongoDB'de şema esnektir — farklı işlem tipleri farklı alanlara sahip olabilir,
// bu yüzden burada "metadata" gibi esnek bir Map alanı da tutuyoruz.
@Document(collection = "transaction_events")
public class TransactionEvent {

    @Id
    private String id;

    private String eventType;       // TRANSFER_COMPLETED, LOGIN_ATTEMPT, LOAN_STATUS_CHANGED vb.
    private String accountId;       // İlgili hesap (varsa)
    private String userId;
    private BigDecimal amount;
    private String description;

    @Indexed                        // Sorgu performansı için index (partition key mantığına benzer)
    private Instant timestamp;

    private Map<String, Object> metadata;  // Esnek ek alanlar (IP adresi, cihaz bilgisi vb.)
}
```

**Ajan için önemli not:** `TransactionEvent`, PostgreSQL'deki `Transfer` kaydından **sonra**, `@TransactionalEventListener(phase = AFTER_COMMIT)` ile yazılmalı. Yani önce PostgreSQL transaction'ı başarıyla commit olur, ancak ondan sonra Mongo'ya event yazılır. Bu sıralama önemli: Mongo yazma işlemi başarısız olsa bile gerçek para transferi zaten güvenle tamamlanmış olur (Mongo burada "nice to have" audit katmanı, kritik iş mantığı değil).

### 4.3 Transfer Servisi — Güvenlik Kritik Kod (Ajanın Uygulaması Gereken Mantık)

```java
// TransferService.java — pseudocode + açıklama, ajan bunu tam kod olarak yazacak

@Service
public class TransferService {

    @Transactional  // Ya tamamı başarılı olur ya da tamamı geri alınır (rollback)
    public TransferResponse executeTransfer(TransferRequest request, String idempotencyKey) {

        // 1) IDEMPOTENCY KONTROLÜ
        // Aynı idempotency_key ile daha önce bir kayıt var mı? Varsa mevcut sonucu dön,
        // yeniden işlem yapma. Bu, çift tıklama veya network retry'lerinde çift para
        // çekilmesini engeller.
        Optional<Transfer> existing = transferRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        // 2) DISTRIBUTED LOCK ALMA (Redisson)
        // İki hesap arasında transfer yapılırken, ID'leri alfabetik sıraya göre kilitle.
        // NEDEN sıralama? A hesabından B'ye ve aynı anda B'den A'ya transfer varsa,
        // sıralama olmadan "deadlock" (çıkmaz sokak) oluşabilir — iki işlem birbirini
        // sonsuza kadar bekler. Sıralı kilitleme bunu önler.
        String lockKeyFrom = "account-lock:" + min(fromId, toId);
        String lockKeyTo = "account-lock:" + max(fromId, toId);
        RLock lock1 = redissonClient.getLock(lockKeyFrom);
        RLock lock2 = redissonClient.getLock(lockKeyTo);

        try {
            lock1.tryLock(5, 10, TimeUnit.SECONDS);
            lock2.tryLock(5, 10, TimeUnit.SECONDS);

            // 3) HESAPLARI VERİTABANI SEVİYESİNDE DE KİLİTLE (pessimistic lock)
            // Redis lock uygulama seviyesinde koruma sağlar, ama DB seviyesinde de
            // ekstra güvenlik için SELECT ... FOR UPDATE kullanılır (iki katmanlı savunma).
            Account fromAccount = accountRepository.findByIdForUpdate(fromId);
            Account toAccount = accountRepository.findByIdForUpdate(toId);

            // 4) İŞ KURALI KONTROLLERİ
            if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new AccountFrozenException();
            }
            if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException();
            }

            // 5) BAKİYE GÜNCELLEME (BigDecimal ile, asla double/float değil)
            fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            // 6) TRANSFER KAYDINI OLUŞTUR
            Transfer transfer = new Transfer(...);
            transfer.setStatus(TransferStatus.COMPLETED);
            transferRepository.save(transfer);

            // 7) EVENT YAYINLA (Mongo'ya audit log için, AFTER_COMMIT ile dinlenecek)
            eventPublisher.publishEvent(new TransferCompletedEvent(transfer));

            return mapToResponse(transfer);

        } finally {
            // Kilitleri MUTLAKA serbest bırak, exception olsa bile (finally bloğu garantiler)
            lock1.unlock();
            lock2.unlock();
        }
    }
}
```

---

## 5. Güvenlik Mimarisi (Bankacılık Seviyesi — Kritik Bölüm)

Bu bölüm ajanın **atlamaması gereken** en kritik kısımdır. Her madde açık ve net uygulanmalı.

### 5.1 Kimlik Doğrulama (Authentication)

- **Access Token:** JWT, kısa ömürlü (**15 dakika**). Frontend'de **localStorage/sessionStorage'da ASLA saklanmaz** — sadece Angular servisinin bellek (in-memory) değişkeninde tutulur. Sayfa yenilendiğinde kaybolur, bu normaldir; refresh token ile otomatik yeni access token alınır.
- **Refresh Token:** Uzun ömürlü (**7 gün**), rastgele UUID/opaque token olarak üretilir (JWT olması ZORUNLU değil), Redis'te `refresh:{userId}:{tokenId}` formatında saklanır. Frontend'e **httpOnly + Secure + SameSite=Strict** cookie olarak gönderilir.
  - `httpOnly` → JavaScript bu cookie'yi okuyamaz, bu yüzden **XSS saldırısı olsa bile** saldırgan token'ı çalıp `document.cookie` ile okuyamaz.
  - `Secure` → Sadece HTTPS üzerinden gönderilir (prod ortamda zorunlu).
  - `SameSite=Strict` → Cross-site request forgery (CSRF) saldırılarına karşı tarayıcı seviyesinde ekstra koruma; cookie sadece aynı origin'den gelen isteklerde gönderilir.
- **Refresh token rotasyonu:** Her refresh isteğinde eski token geçersiz kılınır, yeni bir token üretilir (token replay saldırılarını zorlaştırır — biri eski bir refresh token'ı çalıp kullanmaya çalışırsa, token zaten geçersiz kılınmış olur ve sistem tüm oturumları sonlandırabilir — "refresh token reuse detection").
- **Logout:** Access token'ın `jti` (JWT ID) claim'i Redis blacklist'e eklenir, süresi dolana kadar orada tutulur — böylece logout sonrası çalınmış bir access token bile geçersiz sayılır (senin bahsettiğin "açık unutulan oturum" senaryosu tam olarak burada çözülüyor).

### 5.2 Şifre Güvenliği

- BCrypt (`strength = 12`) ile hash'lenir. Asla MD5/SHA1 kullanılmaz.
- **Brute-force koruması:** 5 başarısız girişten sonra hesap 15 dakika kilitlenir (`failed_login_attempts`, `locked_until` alanları).
- **Rate limiting:** `/api/auth/login` endpoint'i IP bazlı rate limit'e tabi (örn. dakikada 5 istek — Bucket4j veya basit Redis counter ile).

### 5.3 Yetkilendirme (Authorization) — Broken Access Control Önleme

Senin bahsettiğin **"URL değiştirip dashboard'a erişme"** sorunu tam olarak "Broken Access Control" (OWASP Top 10 #1) kategorisine giriyor. Önlemler:

- **Backend'de HER endpoint** `@PreAuthorize` ile korunmalı, sadece frontend route guard'a güvenilmez (frontend guard sadece UX içindir, gerçek güvenlik backend'dedir).
- **IDOR (Insecure Direct Object Reference) önleme:** `/api/accounts/{accountId}` gibi bir endpoint çağrıldığında, backend mutlaka "bu accountId, token'daki kullanıcıya mı ait?" kontrolü yapmalı. Sadece ID'nin var olması yetmez, **sahiplik (ownership) kontrolü** şart.
  ```java
  // ÖRNEK — HER account/transfer/loan endpoint'inde bu pattern olmalı:
  if (!account.getUserId().equals(currentUser.getId()) && !currentUser.hasRole("ADMIN")) {
      throw new AccessDeniedException("Bu hesaba erişim yetkiniz yok");
  }
  ```
- **Rol bazlı erişim (RBAC):** `CUSTOMER` sadece kendi verilerini görür, `ADMIN` kredi onay/red işlemlerini yapabilir. Spring Security `@PreAuthorize("hasRole('ADMIN')")` ile.

### 5.4 XSS (Cross-Site Scripting) Önleme

- Angular varsayılan olarak template'lerde otomatik HTML escaping yapar — `[innerHTML]` binding'i **kesinlikle kullanılmamalı** (kullanıcıdan gelen veri asla raw HTML olarak render edilmemeli).
- Backend'den dönen tüm response'larda `Content-Type: application/json` net belirtilmeli (tarayıcının içeriği yanlış yorumlamasını önler).
- **Content-Security-Policy (CSP) header'ı** eklenmeli (Spring Security `headers().contentSecurityPolicy(...)`), sadece güvenilir script kaynaklarına izin verir.

### 5.5 CSRF Önleme

- SameSite=Strict cookie zaten önemli bir katman, ama ek olarak state-changing (POST/PUT/DELETE) isteklerde **CSRF token** de kullanılmalı (Spring Security'nin `CookieCsrfTokenRepository`'si; Angular'ın `HttpClientXsrfModule`'ü ile otomatik entegre olur — Angular bunu cookie'den okuyup header'a ekler).

### 5.6 Diğer Zorunlu Güvenlik Katmanları

- **HTTPS zorunluluğu (prod):** `Strict-Transport-Security` header'ı.
- **Input validation:** Her DTO'da `@NotNull`, `@Positive`, `@Size`, `@Pattern` gibi Bean Validation anotasyonları — backend, frontend validasyonuna asla güvenmemeli, her zaman kendi validasyonunu da yapmalı.
- **SQL Injection önleme:** JPA/Hibernate parametrized query kullandığı için doğal koruma var — asla native query'de string concatenation yapılmamalı.
- **Hata mesajlarında bilgi sızdırmama:** `GlobalExceptionHandler`, stack trace'i **asla** client'a döndürmez, sadece generic + loglanabilir hata kodu döner (örn. "Geçersiz kullanıcı adı veya şifre" — "kullanıcı bulunamadı" VS "şifre yanlış" ayrımı yapılmaz, çünkü bu ayrım saldırgana hangi kullanıcı adlarının sistemde var olduğunu sızdırır — **user enumeration** saldırısı).
- **Şifre/hassas veri loglanmaması:** Loglarda asla şifre, token, tam kart/IBAN numarası plaintext görünmemeli (IBAN gösterilecekse maskeli: `TR33 **** **** **** 1234`).
- **HTTP Security Headers:** `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY` (clickjacking önleme), `Referrer-Policy: strict-origin-when-cross-origin`.
- **Oturum zaman aşımı:** Frontend'de belirli bir inaktivite süresinden (örn. 10 dakika) sonra kullanıcı otomatik logout edilmeli (idle timer servisi).

---

## 6. Frontend — Kritik Güvenlik Uygulama Detayları

```typescript
// auth.interceptor.ts — pseudocode + açıklama

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const accessToken = tokenStorage.getAccessToken(); // Bellekten okunur, cookie/localStorage'dan DEĞİL

  // withCredentials: true → refresh token cookie'sinin backend'e otomatik gitmesi için ZORUNLU
  const authReq = req.clone({
    setHeaders: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
    withCredentials: true
  });

  return next(authReq);
};
```

```typescript
// error.interceptor.ts — 401 geldiğinde otomatik refresh deneme mantığı

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/refresh')) {
        // Refresh token cookie otomatik gönderilir (httpOnly, JS erişemez ama tarayıcı
        // otomatik ekler), backend yeni access token döner.
        return authService.refreshToken().pipe(
          switchMap(() => next(req.clone({ /* yeni token ile tekrar dene */ }))),
          catchError(() => {
            authService.forceLogout(); // Refresh de başarısızsa kullanıcıyı login'e at
            return throwError(() => error);
          })
        );
      }
      return throwError(() => error);
    })
  );
};
```

```typescript
// auth.guard.ts — route seviyesinde koruma (UX katmanı, gerçek güvenlik backend'de olduğunu unutma)

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};
```

**Önemli öğretici not:** Frontend guard'lar sadece kullanıcı deneyimi içindir — "yetkisiz kullanıcıyı login sayfasına yönlendir" gibi. Gerçek güvenlik **her zaman backend'dedir**. Biri tarayıcı console'undan frontend kodunu bypass edip direkt API'ye istek atsa bile, backend'deki `@PreAuthorize` ve ownership kontrolleri onu durdurmalı. Bu projede bu ilkeyi ajan hem yorumlarla hem de kodla göstermeli.

---

## 7. Uygulama Adımları (Ajanın İzleyeceği Sıra)

1. **Altyapı:** `docker-compose.yml`, `.env.example`, Postgres/Mongo/Redis servislerini ayağa kaldır, bağlantıları test et.
2. **Backend iskeleti:** Spring Boot projesini oluştur (Spring Initializr mantığıyla: Web, Security, JPA, Validation, MongoDB, Redis, Flyway bağımlılıkları).
3. **Auth modülü:** Register/Login/Refresh/Logout uçtan uca çalışır hale getir, Postman/Bruno ile test et.
4. **Account modülü:** Hesap oluşturma, bakiye sorgulama, Redis cache entegrasyonu.
5. **Transfer modülü:** Transaction güvenliği, locking, idempotency — bu projenin en kritik parçası, en dikkatli yazılmalı.
6. **Transaction history modülü:** Event listener + MongoDB entegrasyonu.
7. **Loan modülü:** Başvuru, state machine, basit skorlama.
8. **Frontend:** Auth ekranları → Dashboard → Account → Transfer → Loan → Transaction history sırasıyla.
9. **Güvenlik taraması:** Bölüm 5'teki her maddeyi tek tek kontrol listesi gibi gözden geçir.
10. **Dokümantasyon:** Swagger/OpenAPI otomatik dökümantasyonu aktif et, README'ye "nasıl çalıştırılır" talimatları ekle.

---

## 8. Kod Yorumlama Standardı (Ajan İçin Zorunlu Kural)

Her dosyada:
- Sınıf/metot üstünde **ne işe yaradığı** açıklanmalı.
- Güvenlik ile ilgili her satırda **neden bu şekilde yazıldığı** açıklanmalı (örn. neden `BigDecimal`, neden `httpOnly`, neden `@Transactional`).
- Karmaşık iş mantığı (özellikle transfer/lock/idempotency) adım adım numaralı yorumlarla anlatılmalı (yukarıdaki `TransferService` örneğindeki gibi).
- Türkçe yorum satırları kullanılmalı, kod (değişken/metot isimleri) İngilizce kalmalı (sektör standardı budur).

---

## 9. Kapsam Dışı Bırakılanlar (Bilinçli Basitleştirmeler)

Öğrenme projesinin kapsamını şişirmemek için şunlar bilinçli olarak dahil edilmiyor, ajan bunları eklemeye çalışmamalı:
- Gerçek ödeme ağı entegrasyonu (SWIFT, EFT/Havale gerçek altyapısı)
- Kafka/RabbitMQ gibi mesaj kuyruğu (Spring'in kendi `ApplicationEventPublisher`'ı yeterli, ileride istenirse eklenir)
- Mikroservis olarak ayrı deployment/service discovery (Eureka, Consul)
- Gerçek KYC/kimlik doğrulama entegrasyonu

---

## 10. Görev Dağılımı: Ajan vs. Ben (Can) — Öğrenme Odaklı Çalışma Modeli

> **Neden bu bölüm var?** AI Fluency eğitiminde vurgulanan ilke şu: bir konuyu yapay zeka olmadan biri başkasına anlatamıyorsan ya da tekrar üretemiyorsan, kontrol sende değildir, öğrenme gerçekleşmemiştir. Bu proje bilinçli olarak **tam otomatik ajan implementasyonu DEĞİL**, bir "iskelet + boşluk doldurma + rehberli yazım" modeliyle ilerleyecek.

### 10.1 Çalışma Modeli — Ajana Talimat

Ajan, aşağıdaki tabloda **"BEN"** olarak işaretlenen her dosya için:
1. Dosyayı **TAMAMEN doldurup implement ETMEYECEK.**
2. Bunun yerine dosyanın **iskeletini** (class/method imzaları, import'lar, gerekli alanlar) oluşturacak.
3. Her boş bırakılan metot/blok içine **satır satır, ne yazılması gerektiğini anlatan** yorum satırları (`// TODO(Can): ...`) bırakacak. Bu yorumlar sadece "buraya kod yaz" demeyecek, **hangi mantığı, hangi sırayla, hangi Java/TypeScript yapılarını kullanarak** yazmam gerektiğini adım adım açıklayacak (bir öğretmenin ödev üstüne bıraktığı ipucu notları gibi).
4. Örnek hint formatı şu şekilde olmalı:

```java
public TransferResponse executeTransfer(TransferRequest request, String idempotencyKey) {
    // TODO(Can): 1. transferRepository.findByIdempotencyKey(idempotencyKey) ile
    //    bu key daha önce işlenmiş mi kontrol et. Optional<Transfer> döner.
    //    Eğer değer varsa (isPresent()), mapToResponse(...) ile direkt onu dön,
    //    fonksiyondan çık. (Neden? Aynı isteğin iki kez işlenmesini engellemek için.)

    // TODO(Can): 2. redissonClient.getLock(...) ile from/to hesap ID'lerini
    //    alfabetik/UUID sırasına göre kilitle (küçük olan ID önce kilitlenmeli,
    //    deadlock'u önlemek için). tryLock(waitTime, leaseTime, TimeUnit) kullan.

    // TODO(Can): 3. try bloğu içinde: accountRepository.findByIdForUpdate(...) ile
    //    her iki hesabı da pessimistic lock ile çek.

    // TODO(Can): 4. İş kuralı kontrolleri: hesap ACTIVE mi? bakiye yeterli mi?
    //    Değilse ilgili custom exception'ı fırlat (AccountFrozenException,
    //    InsufficientBalanceException — bunlar common/exception paketinde tanımlı).

    // TODO(Can): 5. BigDecimal.subtract() / .add() ile bakiyeleri güncelle,
    //    accountRepository.save() ile kaydet.

    // TODO(Can): 6. Yeni bir Transfer entity'si oluştur, status=COMPLETED yap,
    //    transferRepository.save() ile kaydet.

    // TODO(Can): 7. eventPublisher.publishEvent(new TransferCompletedEvent(transfer))
    //    ile event yayınla (Mongo'ya yazılacak audit log tetiklenir).

    // TODO(Can): 8. finally bloğunda lock1.unlock() ve lock2.unlock() çağır —
    //    exception fırlasa bile kilitler MUTLAKA serbest kalmalı.

    return null; // Can burayı dolduracak
}
```

Bu format, hem "ne yazacağımı bilmiyorum, boş klasör karşımda donup kalıyorum" sorununu çözer (adımlar net), hem de kopyala-yapıştır değil, **anlayarak yazmamı** zorunlu kılar (mantığı anlamadan bu adımları koda dökemem).

### 10.2 Dosya Bazlı Görev Dağılımı

| Katman | Dosya | Kim Yazacak | Neden |
|---|---|---|---|
| **Altyapı** | `docker-compose.yml`, `.env.example` | AJAN | Tekrarlayan, öğretici değeri düşük konfigürasyon |
| **Backend — Auth** | `JwtTokenProvider.java` | **BEN** | JWT'nin nasıl üretildiğini/doğrulandığını anlamak proje genelinde en kritik güvenlik konusu |
| | `JwtAuthenticationFilter.java` | AJAN (iskelet) + **BEN** (filterInternal metodunun içi) | Filter zincirinin genel yapısı standart, ama "her istekte token nasıl kontrol edilir" mantığı öğretici |
| | `AuthService.java` (login/register/refresh/logout) | **BEN** | Şifre hash'leme, token üretme akışının uçtan uca kurgusu — auth'un kalbi |
| | `RefreshTokenService.java` (Redis) | AJAN (iskelet) + **BEN** (rotasyon mantığı) | Redis ile çalışmayı ilk kez göreceğin yer |
| | `RateLimitInterceptor.java` | AJAN | Standart, tekrar eden bir pattern, öğretici değeri sınırlı |
| **Backend — Account** | `Account.java` (entity) | **BEN** | JPA annotation'larını (`@Version`, `@Column`, `NUMERIC` eşlemesi) elle yazarak öğren |
| | `AccountService.java` | **BEN** (özellikle `getBalance` — cache-aside pattern) | Redis cache mantığını (önce cache'e bak, yoksa DB'ye git, cache'e yaz) elinle kurman gerekiyor |
| | `AccountController.java`, DTO'lar | AJAN | Standart CRUD/mapping, düşük öğretici değer |
| **Backend — Transfer** | `TransferService.java` | **BEN** (yukarıdaki hint formatıyla) | Projenin en kritik öğrenme noktası: locking, idempotency, transaction güvenliği |
| | `DistributedLockService.java` | AJAN (iskelet) + **BEN** (lock alma/bırakma sırası) | |
| | `Transfer.java` (entity), `TransferController.java` | AJAN | Standart yapı |
| **Backend — Loan** | `CreditScoreCalculator.java` | **BEN** | Basit bir algoritma yazma pratiği (if/else + puanlama mantığı), CSE358'de gördüğün mantığı uygulama fırsatı |
| | `LoanService.java` (state machine geçişleri) | AJAN (iskelet) + **BEN** (durum geçiş metotları: `approve()`, `reject()`) | Enum + state machine mantığını anlama |
| | `LoanController.java`, DTO'lar, `LoanApplication.java` | AJAN | Standart |
| **Backend — Transaction History** | `TransferEventListener.java` | **BEN** | `@TransactionalEventListener(phase = AFTER_COMMIT)` mantığını anlamak — neden bu sırayla çalıştığını kavramak önemli |
| | `TransactionEvent.java` (Mongo document), repository | AJAN | Standart mapping |
| **Backend — Security Config** | `SecurityConfig.java` | AJAN (iskelet) + **BEN** (filter chain sırası, hangi endpoint public/protected — `authorizeHttpRequests` bloğu) | Hangi endpoint'in neden public/protected olduğuna karar vermek güvenlik mantığını öğretir |
| | `GlobalExceptionHandler.java` | AJAN | Tekrarlayan boilerplate |
| **Backend — Migration** | Flyway SQL dosyaları (`V1`–`V4`) | **BEN** (planı burada zaten var, sen bunu satır satır kendi elinle yazarak SQL'i pekiştir) | SQL yazma pratiği, `NUMERIC` vs `FLOAT` farkını elle deneyimlemek |
| **Frontend — Core** | `auth.interceptor.ts` | **BEN** | HTTP interceptor mantığı — her backend projesinde karşına çıkacak temel bir kavram |
| | `error.interceptor.ts` (401 → refresh akışı) | **BEN** (hint formatıyla) | RxJS `catchError` + `switchMap` kullanımını pratik yaparak öğrenmen için ideal |
| | `auth.guard.ts`, `role.guard.ts` | AJAN (iskelet) + **BEN** (kontrol koşulu) | Kısa ama kavramsal önemi yüksek |
| | `token-storage.service.ts` | **BEN** | "Neden localStorage değil, neden memory" kararını kendi elinle kodlaman gerekiyor |
| **Frontend — Features** | `login.component.ts`, `register.component.ts` (form + validasyon) | **BEN** | Reactive Forms mantığını (`FormGroup`, `Validators`) pratik yapman için |
| | `transfer-form.component.ts` (form + submit + hata gösterimi) | **BEN** | Backend'deki idempotency key'i frontend'de nasıl ürettiğini/gönderdiğini anlaman gerekiyor |
| | `dashboard.component.ts`, `account-list.component.ts`, `account-detail.component.ts` | AJAN | Görsel ağırlıklı, tekrar eden CRUD listeleme, öğretici değeri düşük |
| | `loan-application.component.ts`, `loan-status.component.ts` | AJAN | |
| | `transaction-list.component.ts` | AJAN | |
| **Frontend — Shared** | Ortak UI bileşenleri, pipe'lar, styling | AJAN | Tasarım/tekrar odaklı, domain mantığı yok |

**Özet oran:** Backend'de güvenlik ve iş mantığı içeren ~9 dosyayı sen yazacaksın, geri kalan CRUD/boilerplate ajana kalıyor. Frontend'de auth altyapısı (interceptor/guard/token servisi) ve transfer formu sende, geri kalan görsel bileşenler ajanda. Bu oran, "her şeyi anlıyorum ama her satırı ben yazmadım" ile "hiçbir şey öğrenmeden proje bitti" arasındaki dengeyi kurmak için tasarlandı.

### 10.3 Revize Uygulama Sırası

Adım 2 (Uygulama Adımları, Bölüm 7) şu şekilde işleyecek — her adımda ajan önce iskeleti + hint'leri yazacak, sen kendi payına düşen dosyaları dolduracaksın, sonra bir sonraki adıma geçilecek (paralel değil, sıralı — çünkü her katman bir öncekine bağımlı):

1. Altyapı (Docker) → AJAN tek başına kurar, sen `docker-compose up` ile test edip ayakta olduğunu doğrularsın.
2. Auth modülü → AJAN iskelet+hint yazar → **SEN** `JwtTokenProvider`, `AuthService`, migration V1'i doldurursun → Postman ile test.
3. Account modülü → AJAN iskelet+hint → **SEN** `Account.java` entity ve `AccountService.getBalance` cache mantığını, migration V2'yi doldurursun.
4. Transfer modülü → AJAN iskelet+hint (Bölüm 10.1'deki örnek gibi) → **SEN** `TransferService`'i adım adım doldurursun (en kritik ve en uzun süreceğin kısım burası, acele etme).
5. Transaction history → AJAN çoğunu yazar → **SEN** sadece `TransferEventListener`'ı doldurursun.
6. Loan modülü → AJAN iskelet → **SEN** `CreditScoreCalculator` ve state geçiş metotlarını doldurursun.
7. Frontend auth altyapısı → AJAN iskelet+hint → **SEN** interceptor'lar, guard'lar, token servisini doldurursun.
8. Frontend feature ekranları → Çoğunlukla AJAN, sen `login/register` formu ve `transfer-form`'u doldurursun.
9. Uçtan uca test + Bölüm 5 güvenlik kontrol listesini birlikte gözden geçirme.

### 10.4 Ajana Ekstra Talimat: Kod Review Modu

Her "BEN" olarak işaretlenmiş dosyayı doldurduktan sonra, ajana o dosyayı yapıştırıp şunu sorman öneriliyor: *"Bu dosyayı ben doldurdum, mantık hatası, güvenlik açığı ya da eksik olan bir kısım var mı, açıklayarak söyler misin?"* — Bu, ajanın senin yerine yazması değil, senin yazdığını **denetlemesi** anlamına gelir; öğrenme döngüsünü tamamlayan son adım budur.

---

Bu dosya, ajana verilecek tek ve eksiksiz kaynak olacak şekilde tasarlanmıştır. Ajan bu planı uygularken belirsizlik yaşarsa, bu dokümandaki güvenlik ilkelerini (Bölüm 5) ve görev dağılımı ilkelerini (Bölüm 10) her zaman en yüksek öncelik olarak kabul etmelidir. Ajan, "BEN" olarak işaretlenmiş hiçbir dosyayı kendisi tam olarak doldurmamalı — sadece iskelet ve satır satır hint bırakmalıdır.
