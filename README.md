# Conjunto Residencial API

API for managing a residential complex. This project is built using Java and Spring Boot.

## Technologies Used

*   Java 17
*   Spring Boot
*   Spring Security
*   Spring Data JPA
*   PostgreSQL
*   Maven
*   Docker

## Getting Started

This section will guide you through setting up and running the project locally.

### Prerequisites

*   Java 17 Development Kit (JDK)
*   Apache Maven
*   Docker Desktop (or Docker engine and Docker Compose)

### Database Setup

The project uses PostgreSQL as its database. A Docker Compose configuration is provided for convenience.

1.  **Start the PostgreSQL container:**
    Open a terminal in the project root and run:
    ```bash
    docker-compose up -d db_postgres
    ```
    This will start a PostgreSQL instance in the background, using the credentials and database name specified in `docker-compose.yml` (username: `puniversidad`, password: `Admin123`, database: `conjunto_residencial_db`).

### Application Configuration

The main application configuration is located in `src/main/resources/application.properties`.

*   **Database Connection:** The properties `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` are pre-configured to connect to the PostgreSQL instance launched via Docker Compose.
*   **JWT Secret:** The application uses JSON Web Tokens (JWT) for security. The JWT secret key is defined by `app.jwt.secret`. For production environments, ensure this is a strong, unique Base64 encoded key.

### Running the Application

1.  **Using Maven:**
    You can run the application directly using the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```
    The application will start, and by default, it will be accessible at `http://localhost:8080`.

2.  **Building a JAR file (Optional):**
    To package the application as a JAR file, run:
    ```bash
    mvn clean package
    ```
    The executable JAR will be created in the `target/` directory. You can then run it using:
    ```bash
    java -jar target/conjunto-residencial-api-0.0.1-SNAPSHOT.jar
    ```

## API Endpoints

Currently, the API primarily provides authentication endpoints. More endpoints will be documented as they are developed.

The base path for all V1 APIs is `/api/v1`.

### Authentication

#### `POST /api/v1/auth/register`

Registers a new user in the system.

**Request Body:**

```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "1234567890",
  "roles": ["RESIDENTE"]
}
```
*   `username` (String, required): Unique username (3-50 characters).
*   `email` (String, required): Unique email address (max 100 characters).
*   `password` (String, required): User's password (6-100 characters).
*   `firstName` (String, required): User's first name (max 100 characters).
*   `lastName` (String, required): User's last name (max 100 characters).
*   `phone` (String, optional): User's phone number.
*   `roles` (Set<String>, optional): Set of roles to assign (e.g., `["RESIDENTE", "ADMIN"]`). If not provided, a default role might be assigned.

**Responses:**

*   `201 Created`: If the user is successfully registered.
    ```json
    {
      "message": "Usuario registrado exitosamente!"
    }
    ```
*   `400 Bad Request`: If there's a validation error (e.g., username/email already exists, invalid input).
    ```json
    {
      "message": "Error: El nombre de usuario ya está en uso."
    }
    ```
*   `500 Internal Server Error`: For other server-side errors.

#### `POST /api/v1/auth/login`

Authenticates an existing user and returns a JWT token.

**Request Body:**

```json
{
  "usernameOrEmail": "user@example.com",
  "password": "password123"
}
```
*   `usernameOrEmail` (String, required): User's username or email.
*   `password` (String, required): User's password.

**Responses:**

*   `200 OK`: If authentication is successful.
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNj...",
      "tokenType": "Bearer",
      "userId": 1,
      "username": "testuser",
      "email": "user@example.com",
      "roles": ["RESIDENTE"]
    }
    ```
*   `401 Unauthorized`: If credentials are invalid.
    ```json
    {
      "message": "Error de autenticación: Credenciales inválidas."
    }
    ```
*   `500 Internal Server Error`: For other server-side errors.

---
*Note: This section will be updated as more features and endpoints are developed.*

## Project Structure

The project follows a layered architecture inspired by Domain-Driven Design principles:

*   **`com.conjuntoresidencial.api.conjuntoresidencialapi`**: Main application class.
*   **`com.conjuntoresidencial.api.domain`**: Contains the core business logic, entities, value objects, domain services, and ports (interfaces for repositories or external services).
    *   `model`: Domain entities and value objects.
    *   `port.in`: Use case interfaces (what the application can do).
    *   `port.out`: Repository interfaces (how data is persisted or fetched).
    *   `service`: Domain services containing business logic not fitting within a single entity.
*   **`com.conjuntoresidencial.api.application`**: Implements the use cases defined in the domain layer. These are the application services that orchestrate domain objects.
    *   `service`: Concrete implementations of use case interfaces.
*   **`com.conjuntoresidencial.api.infrastructure`**: Contains components that interact with external concerns like databases, web frameworks, security, etc.
    *   `config`: Configuration classes (e.g., Spring Boot configurations, data initializers).
    *   `persistence`: Implementations of repository ports, specific to a database technology (e.g., PostgreSQL with Spring Data JPA).
    *   `security`: Security-related configurations, JWT utilities, and user details services.
    *   `web`: Handles web layer concerns.
        *   `controller`: Spring MVC controllers that expose API endpoints.
        *   `dto`: Data Transfer Objects for requests and responses.
        *   `exception`: Global exception handling.
        *   `mapper`: Mappers between DTOs and domain models (though often DTOs are directly used or mapped within application services).

This structure aims for a separation of concerns, making the application more maintainable and testable.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please follow these general guidelines:

1.  **Fork the repository.**
2.  **Create a new branch** for your feature or bug fix:
    ```bash
    git checkout -b feature/your-feature-name
    ```
    or
    ```bash
    git checkout -b fix/issue-number
    ```
3.  **Make your changes.** Ensure your code adheres to the existing style and that all tests pass (if applicable).
4.  **Commit your changes** with a clear and descriptive commit message.
5.  **Push your changes** to your forked repository.
6.  **Submit a Pull Request (PR)** to the main repository's `main` (or `develop`) branch.
    *   Clearly describe the changes you've made and why.
    *   Reference any related issues.

We appreciate your contributions to make this project better!

## License

This project is currently unlicensed. Please add a license file (e.g., MIT, Apache 2.0) if you intend to distribute or share this code.
