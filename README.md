# SIGESS - Incident Management System

A full-stack web application designed for managing, tracking, and documenting industrial or operational incidents.

## 🏗️ Project Architecture

The project is structured as a monorepo containing both the frontend and backend components:

- **/fronted-angular**: The client-side application built with Angular.
- **/backend**: The server-side application built with Java Servlets.
- **/db**: Database initialization scripts (PostgreSQL).
- **/postman**: API documentation and test collections.

---

## 🚀 Tech Stack

### Frontend
- **Angular 18**: Using standalone components and modern routing.
- **RxJS**: For reactive state management and API communication.
- **JWT Authentication**: Secure session management via local storage.
- **CSS3**: Custom styling without heavy UI frameworks for maximum performance.

### Backend
- **Java (J2EE)**: Built using standard Servlets.
- **Maven**: Dependency and build management.
- **Tomcat 7**: Servlet container (via `tomcat7-maven-plugin`).
- **PostgreSQL**: Relational database for persistence.
- **JJWT**: JSON Web Token implementation for Java.
- **Gson**: JSON serialization and deserialization.

---

## ✨ Key Features

- **Authentication & Authorization**: Role-based access control (RBAC) with `SUPERVISOR` and `ANALISTA` roles.
- **Incident Management**:
    - Full CRUD (Create, Read, Update, Delete/Close) operations.
    - Advanced filtering by status, level, area, and date range.
    - Sorting and pagination.
- **History Tracking**: Complete audit trail of all changes made to an incident.
- **Attachment System**:
    - Support for multiple file formats (PDF, Images, MS Office, etc.).
    - Robust multipart upload handling compatible with Servlet 3.0/3.1.
    - File size and type validation.
- **Reporting**: Dashboard and statistical reports for incident analysis.

---

## 🛠️ Setup and Installation

### Prerequisites
- Node.js (v18+) and npm.
- Java JDK 17.
- Maven 3.x.
- PostgreSQL database.

### Backend Setup
The backend APIs run on **Apache Tomcat 7**.

1. Navigate to the `backend` directory.
2. Configure your database connection and JWT secret in `src/main/resources/application.properties`.
3. Build the project:
   ```bash
   mvn clean compile
   ```
4. Run the project with Tomcat using the following command:
   ```bash
   mvn tomcat7:run
   ```

### Frontend Setup
1. Navigate to the `fronted-angular` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm start
   ```
4. Access the app at `http://localhost:4200`.

---

## 🔧 Recent Improvements and Bug Fixes

- **Robust File Upload**: Fixed `NoSuchMethodError` by implementing a custom filename parser compatible with Tomcat 7 environments (Servlet 3.0).
- **Security Interceptor**: Enhanced `authInterceptor` to handle 401 Unauthorized errors gracefully, preventing accidental logouts during complex operations like file uploads.
- **Diagnostic Logging**: Added comprehensive backend and frontend logging for easier troubleshooting of authentication and multipart data issues.
- **Resilient Routing**: Added proper fallback routes and login redirection to prevent "white screen" errors on session expiration.
