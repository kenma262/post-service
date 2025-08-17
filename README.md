# Post Service

A Spring Boot microservice for managing posts and comments with distributed tracing capabilities.

## Features

- **Post Management**: Create, read, update posts
- **Comment Management**: Add and manage comments on posts
- **Authentication**: JWT-based authentication with Keycloak integration
- **Distributed Tracing**: Micrometer Tracing with Zipkin integration
- **Database**: MongoDB for data persistence
- **API Documentation**: OpenAPI/Swagger integration

## Tech Stack

- **Framework**: Spring Boot 3.3.0
- **Database**: MongoDB
- **Security**: Spring Security with OAuth2 Resource Server
- **Tracing**: Micrometer Tracing + Zipkin
- **Documentation**: SpringDoc OpenAPI
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- MongoDB
- Keycloak Server
- Zipkin (for distributed tracing)

## Quick Start

1. **Start MongoDB**:
   ```bash
   ./start-mongodb.sh
   ```

2. **Start Zipkin (for tracing)**:
   ```bash
   ./start-zipkin.sh
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**:
   - API Base URL: `http://localhost:8082`
   - Swagger UI: `http://localhost:8082/swagger-ui/index.html`
   - Zipkin UI: `http://localhost:9411`

## API Endpoints

### Posts
- `GET /api/posts` - Get all posts
- `GET /api/posts/{id}` - Get post by ID
- `POST /api/posts` - Create a new post (requires authentication)
- `PUT /api/posts/{id}` - Update a post (requires authentication)

### Comments
- `GET /api/comments/post/{postId}` - Get comments for a post
- `POST /api/comments` - Create a new comment (requires authentication)
- `PUT /api/comments/{id}` - Update a comment (requires authentication)

### Test Endpoints
- `GET /api/test/health` - Health check
- `GET /api/test/info` - Service information
- `POST /api/test/echo` - Echo test endpoint

## Configuration

The application can be configured via `application.yml`:

- **MongoDB**: Configure connection string and database name
- **Security**: Configure JWT issuer URI for Keycloak
- **Tracing**: Configure Zipkin endpoint and sampling rate

## Distributed Tracing

The service automatically:
- Adds trace and span IDs to all log messages
- Sends trace data to Zipkin for visualization
- Propagates trace context across microservice calls

## Authentication

The service uses JWT tokens from Keycloak. Users need either `ROLE_USER` or `ROLE_ADMIN` to access protected endpoints.

## Development

### Running Tests
```bash
mvn test
```

### Building
```bash
mvn clean package
```

### Docker Support
(Docker configuration can be added as needed)

## Monitoring

- **Health Checks**: Available at `/actuator/health`
- **Metrics**: Available at `/actuator/metrics`
- **Tracing**: View traces at Zipkin UI (http://localhost:9411)

## Related Services

This service is part of a microservices architecture:
- **Auth Service**: Handles user authentication and registration
- **Post Service**: This service - manages posts and comments

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
