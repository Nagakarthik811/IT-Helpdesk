# IT Helpdesk Ticket Management System

This project is a full-stack IT helpdesk application built with a Spring Boot backend and an Angular frontend.

## Project Overview
The application allows employees to raise support tickets, support staff to manage and resolve them, and administrators to manage users and system-wide operations. The backend exposes JWT-protected REST APIs, and the Angular frontend provides role-based dashboards and ticket workflows.

## Features
- Authentication and registration
- JWT-based authorization
- Ticket creation and management
- Ticket assignment and workflow
- Comments and resolution notes
- Attachment support
- Search, filtering, and pagination
- Employee, support, and admin dashboards
- Ticket history tracking
- OpenAPI documentation

## Technologies
- Angular
- TypeScript
- Java 17
- Spring Boot 3
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- H2 for local development (MySQL-ready)
- Maven
- Bootstrap

## Prerequisites
- Java 17+
- Maven
- Node.js 18+
- npm
- MySQL (optional for production-style DB setup)

## Database Setup
The project is configured to run locally with the embedded H2 database by default. For MySQL, set these environment variables before running the backend:

```bash
set DB_URL=jdbc:mysql://localhost:3306/it_helpdesk?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set DB_USERNAME=root
set DB_PASSWORD=your_password
set DB_DRIVER=com.mysql.cj.jdbc.Driver
set JWT_SECRET=your_secret_key
```

Then create the database in MySQL manually:

```sql
CREATE DATABASE it_helpdesk;
```

## Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend runs at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Frontend Setup
```bash
cd frontend
npm install
npm start
```

The frontend runs at:

```text
http://localhost:4200
```

## Demo Accounts
These accounts are created at startup for local testing:

- Admin: username `admin`, password `Admin@123`
- IT Support: username `support`, password `Support@123`
- Employee: username `employee`, password `Employee@123`

> These are demo credentials for local development only.

## API Summary
- POST /api/auth/login
- POST /api/auth/register
- GET /api/tickets
- POST /api/tickets
- PATCH /api/tickets/{id}/assign
- PATCH /api/tickets/{id}/status
- POST /api/tickets/{ticketId}/comments

## Notes
This is a working full-stack implementation scaffolded with a realistic structure, but in a production deployment you should configure a real MySQL database, secure JWT secrets, and adjust the CORS, environment, and file storage settings as needed.
