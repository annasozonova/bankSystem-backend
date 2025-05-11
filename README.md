# README.md

## Bank Card Management System

A secure backend system for managing bank cards, built with **Java 17**, **Spring Boot**, **PostgreSQL**, **Liquibase**, and **Docker**.

---

### Features
- JWT-based authentication and role-based access (ADMIN / USER)
- User registration and card creation
- Secure storage of encrypted card numbers
- Card listing, filtering, and pagination
- Transfer funds between user-owned cards
- Request card blocking (USER)
- Admin-only card activation, blocking, deletion
- Swagger/OpenAPI documentation

---

### Technologies Used
- Java 17, Spring Boot, Spring Security, Spring Data JPA
- PostgreSQL
- Liquibase (for DB migrations)
- Docker + Docker Compose
- OpenAPI (springdoc-openapi-ui)

---

### Running Locally (Docker)

#### 1. Clone the repository
```bash
git clone https://github.com/your-username/bank-card-system.git
cd bank-card-system
```

#### 2. Build the project
```bash
./mvnw clean package
```

#### 3. Start with Docker Compose
```bash
docker-compose up --build
```

App will be available at: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

### Default Roles
- `ADMIN`: Can manage users and cards
- `USER`: Can view and manage their own cards

---

### API Documentation
Accessible via Swagger: `http://localhost:8080/swagger-ui.html`

---

### Project Structure
```
├── controller       # REST controllers
├── service          # Business logic
├── repository       # Spring Data JPA
├── model            # JPA entities
├── dto              # Data Transfer Objects
├── security         # JWT and auth
├── util             # Utility classes
├── exception        # Centralized error handling
```

---

### 📎 Attached Documents
- `Lite Разработка Системы Управления Банковскими Картами.pdf` — original project specification (in Russian)

---

### 🔐 Included Configuration Files (for testing purposes)

The repository includes a file with sensitive configuration (e.g., JWT secret and database credentials) **only for the purpose of evaluating this test assignment**.  
In a real-world project, this file would be excluded via `.gitignore` and managed through environment variables or secrets vaults.

**Provided file:** `.env`

---

### Contact
Author: Anna Sozonova  
Email: annasozonova@example.com

---