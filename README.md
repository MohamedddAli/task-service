# Quarkus Task Management Service

A robust, event-driven task management REST API built with Quarkus, MySQL, RabbitMQ, Kafka / Redpanda, and Keycloak.

## Architecture Overview

- Quarkus (Java 23) – core framework
- MySQL 8.0 – database
- RabbitMQ – publishes TaskCreated events
- Kafka / Redpanda – publishes TaskCompleted events
- Keycloak – authentication & authorization (OIDC)
- Docker Compose – container infrastructure

## Prerequisites

- Java 23 (Required to compile code locally)
- Maven (Required to build artifacts)
- Docker Desktop (with WSL enabled)

Verify installation:
java -version
mvn -version

## Running the Application

### 1. Build the Quarkus application
**Important:** Since the `target/` folder is ignored by git, you must run this command locally to generate the compiled artifacts. The Dockerfile copies the resulting JARs from this step.

mvn clean package -DskipTests

This produces the build artifacts inside `target/quarkus-app`.

### 2. Start the Docker infrastructure
docker compose up -d --build

Wait ~30–60 seconds for MySQL and Keycloak to initialize.

## First-Time Keycloak Setup

Open http://localhost:8081 and log in using admin / admin.

Create a Realm:
- Name: task-realm

Create a Client:
- Client ID: task-service
- Enable Standard Flow and Direct Access Grants
- **Important:** Ensure "Client authentication" is OFF (Public Client)

Create Roles:
- user
- admin

Create Users:
User: alice
Password: alice
Role: user

User: bob
Password: bob
Roles: user, admin

## Testing the API

Application runs on port 8080  
Keycloak runs on port 8081

### 1. Get User Token (Alice - Role: user)
curl -X POST http://localhost:8081/realms/task-realm/protocol/openid-connect/token \
  -d "client_id=task-service" \
  -d "username=alice" \
  -d "password=alice" \
  -d "grant_type=password"

*Copy the `access_token` from the response and replace `<TOKEN>` below.*

### 2. Create a task (POST)
*Requires Role: user*
curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title": "Docker Setup", "description": "Running in containers", "status": "TODO", "assignee": "Alice"}'

### 3. Get All Tasks (GET)
*Requires Role: user*
curl -X GET http://localhost:8080/tasks \
  -H "Authorization: Bearer <TOKEN>"

### 4. Get Task by ID (GET)
*Requires Role: user*
curl -X GET http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <TOKEN>"

### 5. Update a task (PUT)
*Requires Role: user*
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"status": "DONE"}'

### 6. Get Admin Token (Bob - Role: admin)
*We need Bob's token because only admins can delete.*
curl -X POST http://localhost:8081/realms/task-realm/protocol/openid-connect/token \
  -d "client_id=task-service" \
  -d "username=bob" \
  -d "password=bob" \
  -d "grant_type=password"

*Copy the `access_token` and use it as `<BOB_TOKEN>` below.*

### 7. Delete a task (DELETE)
*Requires Role: admin*
curl -X DELETE http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <BOB_TOKEN>"

## Viewing Logs

docker logs -f task-service-app

Expected logs:
- RabbitMQ received task event
- Kafka received completed event

## Stopping the System

docker compose down

Data for MySQL and Keycloak persists in Docker volumes (mysql_data, keycloak_data), so restarting preserves your users and tasks.