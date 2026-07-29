## Customer Panel

<img width="1919" height="865" alt="Ekran görüntüsü 2026-07-28 174800" src="https://github.com/user-attachments/assets/9d2aa5a2-7c45-4071-95f2-c6729d3d8ce8" />

<img width="1918" height="865" alt="Ekran görüntüsü 2026-07-28 174904" src="https://github.com/user-attachments/assets/d0e6205b-1e5a-4293-b544-4a8b62e54e21" />

<img width="1915" height="864" alt="Ekran görüntüsü 2026-07-28 174813" src="https://github.com/user-attachments/assets/44bc9528-e7be-444f-9953-4618f6830351" />

<img width="1916" height="862" alt="Ekran görüntüsü 2026-07-28 174920" src="https://github.com/user-attachments/assets/145da9ba-f563-4373-be97-86931c77e7fc" />

<img width="1916" height="861" alt="Ekran görüntüsü 2026-07-28 174929" src="https://github.com/user-attachments/assets/949f0bf1-5632-4ea6-91c4-a5884f7256f3" />

<img width="1916" height="862" alt="Ekran görüntüsü 2026-07-28 174434" src="https://github.com/user-attachments/assets/d83d5164-4522-405e-81d8-6513127b0b8c" />

---

## Admin Panel

<img width="1919" height="842" alt="Ekran görüntüsü 2026-07-28 175133" src="https://github.com/user-attachments/assets/fdcd0a6f-1162-4de3-a9fc-b65af87a0338" />

<img width="1914" height="863" alt="Ekran görüntüsü 2026-07-28 175147" src="https://github.com/user-attachments/assets/1a0e8dad-1522-4f39-a288-5a2ef33cc4ce" />



---

## ATM Simulator in website to deposit and withdraw  

<img width="1277" height="863" alt="Ekran görüntüsü 2026-07-28 174944" src="https://github.com/user-attachments/assets/5f2295d0-69fb-40ac-a227-546d7ebcbdf0" />
<img width="1276" height="865" alt="Ekran görüntüsü 2026-07-28 175015" src="https://github.com/user-attachments/assets/c35faa48-0e0b-4521-b70e-60ee5b887eed" />
<img width="1275" height="865" alt="Ekran görüntüsü 2026-07-28 175026" src="https://github.com/user-attachments/assets/73582741-b80c-44b7-affc-eff528683c17" />





---

<div align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular"/>
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis"/>
  <img src="https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
</div>

# 🏛️ NexBank - Enterprise Core Banking System

NexBank is a full-stack, enterprise-grade Core Banking application engineered with **Domain-Driven Design (DDD)** principles and **Event-Driven Architecture**. It simulates a real-world digital banking environment, focusing heavily on **Data Integrity**, **Concurrency Management**, and **High Security**.

This project isn't just a simple CRUD application; it addresses complex financial engineering problems such as **Double Spending prevention**, **Idempotent transactions**, and **Distributed Locking**.

---

## 🚀 Key Technical Highlights & "Wow" Factors

### 1. Bulletproof Financial Transactions (Core Engine)
- **Distributed Locking with Redis:** Prevents "Double Spending" and race conditions. If two rapid requests try to transfer money from the same account at the exact same millisecond, Redis `SETNX` (Set if Not Exists) atomically locks the account, rejecting the second request instantly.
- **Idempotency (Eşetkisellik):** Network glitched and the user clicked "Send" twice? No problem. Every transfer requires a unique UUID `idempotencyKey`. The backend ensures the same transaction is never executed twice, even if the request is duplicated.
- **ACID Transactions (`@Transactional`):** If money is deducted from Account A, but an error occurs before adding it to Account B (e.g., database failure), the entire transaction is rolled back. No money is ever lost in the void.

### 2. High-Performance Architecture
- **Event-Driven Modularity:** Modules are completely decoupled using Spring's `ApplicationEventPublisher`. For example, the `AtmController` doesn't know the `NotificationService` exists. It simply shouts `"ATM_DEPOSIT_EVENT"`, and the Notification system listens asynchronously. (Open/Closed Principle).
- **Redis In-Memory Caching:** Account balances—the most frequently accessed data in banking—are cached in Redis to dramatically reduce PostgreSQL database load and improve response times to sub-milliseconds.
- **MongoDB NoSQL Document Storage:** Financial transaction histories and high-volume logs are offloaded to MongoDB. This ensures the primary PostgreSQL database remains highly optimized and focused strictly on core relational operations.

### 3. Fortified Security
- **Stateless JWT Architecture:** No sessions are stored on the server. Every request is verified via a cryptographically signed JSON Web Token (JWT), intercepted by a custom `JwtAuthenticationFilter`.
- **Rate Limiting (Anti-Brute-Force):** Implemented using `Bucket4j`. Prevents automated password guessing or API spamming by limiting requests (e.g., max 5 login attempts per minute per IP).
- **BCrypt Hashing:** Passwords are never stored in plain text. A strength-12 BCrypt algorithm ensures mathematically unbreakable password hashes.

### 4. Modern & Premium Frontend (Angular)
- **Glassmorphism & Micro-animations:** A highly premium, TailwindCSS-powered user interface that feels alive.
- **Real-time Analytics:** Integrated `Chart.js` for dynamic pie charts and bar charts on the Dashboard, giving administrators and users instant insights into their finances.
- **RxJS Reactive Programming:** Advanced state management and HTTP request handling using `BehaviorSubject` and `Observables` for a seamless, SPA (Single Page Application) experience.

---

## 🛠️ Technology Stack

**Backend:**
- Java 21, Spring Boot 3 (Web, Data JPA, Security)
- PostgreSQL (Primary Relational Database)
- MongoDB (NoSQL Storage for Transaction History & Logs)
- Redis (Caching & Distributed Locking)
- Bucket4j (Rate Limiting)
- JWT (io.jsonwebtoken)

**Frontend:**
- Angular 18 (Standalone Components, RxJS)
- Tailwind CSS (Styling & Animations)
- Chart.js (Data Visualization)

**DevOps & Infrastructure:**
- Docker & Docker Compose (Containerization of DBs and Backend)
- Maven & NPM

---

## ⚙️ How to Run Locally

Running the entire banking infrastructure is incredibly simple thanks to Docker.

### Prerequisites
- Docker & Docker Compose installed.
- Node.js (v18+) and Angular CLI installed.

### Step 1: Start the Backend Infrastructure
Navigate to the root directory and start the databases (PostgreSQL, Redis, MongoDB) and the Spring Boot backend container.
```bash
docker-compose up --build -d
```
*The backend will be available at `http://localhost:8080`.*

### Step 2: Start the Frontend App
Open a new terminal, navigate to the frontend folder, install dependencies, and run the development server.
```bash
cd Front
npm install
npm start
```
*The web application will be available at `http://localhost:4200`.*

### Step 3: Access the System
- **Admin Account (Auto-seeded):**
  - Username: `admin`
  - Password: `admin`
- Or simply register a new customer account from the login screen!

---

## 📚 Core Annotations Used (For Technical Reviewers)
- **`@RestController`, `@RequestMapping`**: Exposes our domain endpoints to the internet.
- **`@Transactional`**: The guardian of our financial logic. Ensures operations either fully complete or fully rollback.
- **`@RequiredArgsConstructor`**: Clean Dependency Injection via constructors (Lombok).
- **`@EventListener`**: Powers our loosely coupled, event-driven architecture.
- **`@RestControllerAdvice`**: Intercepts unhandled `RuntimeExceptions` globally and formats them into clean JSON `400 Bad Request` responses for the frontend.

---
> *Developed with an emphasis on Enterprise Software Engineering best practices.*
