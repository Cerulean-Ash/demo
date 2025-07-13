# Bank API

## Overview

This project implements a REST API for a fictional bank.
The API is designed to allow users to manage their bank accounts.
The API is secured using JWT (JSON Web Token) based authentication.

## Technologies Used

* **Spring Boot:** 3.5.3

* **Java:** 21

* **Spring Security:** For authentication and authorization (JWT, BCrypt Password Encoding).

* **Spring Data JPA:** For database interaction and persistence.

* **H2 Database:** In-memory database for development.

* **Lombok:** Reduces boilerplate code.

* **Springdoc OpenAPI:** For OpenAPI 3 specification generation and Swagger UI.

* **Nimbus JOSE + JWT:** For RSA key generation and JWT handling (integrated with Spring Security's OAuth2 Resource Server).

* **Maven:** For build automation and dependency management.

## Getting Started

### Prerequisites

* Java 21 or higher

* Maven 3.x

### Installation

1. **Clone the repository:**
```
git clone <repository-url>
cd eagle-bank-api
```
2. **Build the project:**
```
mvn clean install
```

### Running the Application

You can run the Spring Boot application using Maven:
```
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## Running Tests and Generating Coverage Report

### Running Tests
To execute all unit and integration tests:
```
mvn test
```

### Generating Test Coverage Report
To run tests and generate a detailed HTML test coverage report using Jacoco:
```
mvn clean test jacoco:report
```
After the test run is successful, open the following file in your web browser:
`target/site/jacoco/index.html`

## API Documentation (Swagger UI)

Once the application is running, you can access the interactive API documentation (Swagger UI) at:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

This interface allows you to view all available endpoints, their request/response schemas, and even test them directly.

## Database Access (H2 Console)

For development purposes, an H2 database console is available at:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

Use the following credentials:

* **JDBC URL:** `jdbc:h2:mem:testdb`

* **Username:** `sa`

* **Password:** `<leave blank>`

The database is seeded with an `admin` user (`admin@bank.com`/`adminpass`) and a `testuser` (`testuser@email.com`/`password123`) and their respective bank accounts on application startup.

## Authentication

The API uses JWT (JSON Web Token) for authentication.

1. **Login:** Send a `POST` request to `/v1/auth/login` with `email` and `password` to obtain a JWT.

    * **Endpoint:** `POST /v1/auth/login`

    * **Request Body Example:**

      ```
      {
        "email": "testuser@email.com",
        "password": "password123"
      }
      
      ```

    * **Response Example (200 OK):**

      ```
      {
        "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
      
      ```

2. **Access Secured Endpoints:** Include the obtained JWT in the `Authorization` header of subsequent requests as a Bearer token.

    * **Header Example:** `Authorization: Bearer <YOUR_JWT_TOKEN>`
