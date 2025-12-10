Quarkus Task Management Service
A robust, event-driven REST API built with Quarkus, MySQL, RabbitMQ, Kafka, and Keycloak.
🏗 Architecture
Core Framework: Quarkus (Java 23)
Database: MySQL 8.0 (Persistence)
Message Broker 1: RabbitMQ (Handles TaskCreated events)
Message Broker 2: Kafka / Redpanda (Handles TaskCompleted events)
Security: Keycloak (OIDC Authentication & Authorization)
Containerization: Docker & Docker Compose
🚀 Prerequisites
Java 23 (Verify with java -version)
Maven (Verify with mvn -version)
Docker Desktop (Running with WSL 2 integration)
🛠️ Setup & Run Instructions
1. Build the Application (Locally)
Since we are using the local Java 23 environment, you must compile the code first.
Run this in your terminal:
code
Bash
mvn clean package -DskipTests
Success: You should see a BUILD SUCCESS message.
Result: This creates a target/quarkus-app folder containing your compiled code.
2. Start the Infrastructure (Docker)
Now, package that compiled code into a container and start the database/brokers.
code
Bash
docker compose up -d --build
--build: Tells Docker to grab your newly compiled JAR files and create the image.
Wait about 30-60 seconds for Keycloak and MySQL to fully initialize.
3. Configure Keycloak (First Time Only)
Since this is a fresh Keycloak instance, you need to set up the Realm and Users manually.
Open the Keycloak Admin Console: http://localhost:8081
Log in with admin / admin.
A. Create Realm
Hover over "Master" (top-left) -> Create Realm.
Name: task-realm.
Click Create.
B. Create Client
Go to Clients -> Create Client.
Client ID: task-service.
Click Next, ensure Standard Flow and Direct Access Grants are enabled.
Click Save.
C. Create Roles
Go to Realm roles -> Create role.
Create two roles: user and admin.
D. Create Users
User: alice
Users -> Add user -> Username: alice. Create.
Credentials Tab: Set password to alice (Uncheck "Temporary").
Role Mapping Tab: Assign role user.
User: bob
Users -> Add user -> Username: bob. Create.
Credentials Tab: Set password to bob (Uncheck "Temporary").
Role Mapping Tab: Assign roles user AND admin.
🧪 How to Test (API Usage)
The Application runs on Port 8080.
Keycloak runs on Port 8081.
1. Get an Access Token
You must get a token to talk to the API.
Get Token for Alice (User):
code
Bash
curl -X POST http://localhost:8081/realms/task-realm/protocol/openid-connect/token \
  -d "client_id=task-service" \
  -d "username=alice" \
  -d "password=alice" \
  -d "grant_type=password"
Copy the access_token from the response.
2. Create a Task (Triggers RabbitMQ)
Role Required: user
Effect: Saves to MySQL -> Sends event to RabbitMQ.
code
Bash
curl -X POST http://localhost:8080/tasks \
  -H "Authorization: Bearer <PASTE_TOKEN_HERE>" \
  -H "Content-Type: application/json" \
  -d '{"title": "Docker Setup", "description": "Running in containers", "status": "TODO", "assignee": "Alice"}'
3. Complete a Task (Triggers Kafka)
Role Required: user
Effect: Updates MySQL -> Status becomes DONE -> Sends event to Kafka.
code
Bash
# Replace '1' with actual Task ID
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <PASTE_TOKEN_HERE>" \
  -H "Content-Type: application/json" \
  -d '{"title": "Docker Setup", "description": "Finished", "status": "DONE", "assignee": "Alice"}'
4. Delete a Task (Admin Only)
Role Required: admin (You must use Bob's token).
code
Bash
curl -X DELETE http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <PASTE_BOBS_TOKEN_HERE>"
🔍 Verification & Logs
To see the application processing messages (RabbitMQ/Kafka logs):
code
Bash
docker logs -f task-service-app
What to look for:
✅ [CONSUMER] RabbitMQ Received: Task ID ...
✅ [CONSUMER] Kafka Received: Task ... is DONE
🛑 Stopping the System
To stop all containers and remove networks:
code
Bash
docker compose down
Note: Data for MySQL and Keycloak is persisted in Docker Volumes (mysql_data and keycloak_data). If you restart, your users and tasks will still be there.