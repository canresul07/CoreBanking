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