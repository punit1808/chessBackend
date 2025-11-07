# ChessMaster Backend

ChessMaster Backend is a real‑time multiplayer chess server built using **Spring Boot**, featuring:

* JWT authentication using Bearer Token
* PostgreSQL database persistence
* Move validation & game state updates
* Multiplayer + vs Bot gameplay
* Spectator mode
* Stockfish microservice communication
* WebSocket‑based live gameplay

---

## ✅ Features

* Secure login & authentication (JWT)
* Fully validated chess move handling
* Real‑time WebSocket gameplay
* Join via unique Game ID
* Bot opponent support
* Spectator feature
* Persistent storage via PostgreSQL
* Seamless play experience

---

## 🏗 Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring WebSockets
* Spring Security (JWT)
* JPA / Hibernate
* PostgreSQL

### External

* Stockfish engine (external microservice)

---

## 📁 Project Structure

```
backend/
 ├─ src/
 │  ├─ main/
 │  │   ├─ java/... (application source)
 │  │   └─ resources/
 │  │       └─ application.properties
 ├─ pom.xml
```

---

## ⚙️ Environment Variables

Create a `.env` file or export env variables.

```
DB_URL=jdbc:postgresql://<host>:5432/<db_name>
DB_OWNER=<username>
DB_PASS=<password>

JWT_SECRET=<your_jwt_secret>
JWT_EXPIRATION=86400000

STOCKFISH_URL=http://<stockfish-url>/bestmove
ENV=dev
```

---

## 🔧 application.properties

```
spring.application.name=chessmaster
server.port=8080
spring.jackson.date-format=dd-mm-yyyy
logging.level.org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration=OFF

# JWT
jwt_secret=${JWT_SECRET}
jwt_expiration=${JWT_EXPIRATION}

spring.profiles.active=${ENV:dev}

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_OWNER}
spring.datasource.password=${DB_PASS}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Stockfish Microservice
stockfish.url=${STOCKFISH_URL}
```

---

## 🚀 Running Locally

### 1️⃣ Clone Repo

```
git clone <repo-url>
cd backend
```

### 2️⃣ Configure environment variables

Create a `.env` file or set shell env variables.

### 3️⃣ Build

```
mvn clean package -DskipTests
```

### 4️⃣ Run Server

```
java -jar target/*.jar
```

Server runs at:

```
http://localhost:8080
```

---

## 🔌 WebSockets

Main WebSocket Endpoint:

```
/ws/game/{gameId}/{playerId}
```

* Two players → active game
* Extra clients → Spectators

Bot WebSocket endpoint:

```
/ws/bot/{gameId}/{playerId}
```

---

## ♟ Game Flow

1. Create/Join game via Game ID
2. Authenticate using JWT
3. WebSockets sync game state
4. Moves validated server‑side
5. Updates broadcast to all clients
6. Bot (optional) fetches bestmove from Stockfish

---

## ✅ Move Validation

All moves are validated before updating game state.
Invalid moves are rejected, ensuring fair gameplay.

---

## 🗃 Database

Stores:

* Users
* Games & states
* Player stats
* Move history

---

## 🧠 Stockfish Microservice

Request:

```
POST /bestmove
{
  "fen": "<FEN_STRING>",
  "depth": 8
}
```

Response:

```
{
  "bestmove": "e2e4"
}
```

---

## 🐳 Docker Support

Example Dockerfile:

```
### ---------- Build Stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only descriptor first so Maven dependencies get cached
COPY pom.xml .
RUN mvn -q -e dependency:go-offline

# Now copy source & build
COPY src ./src
RUN mvn clean package -DskipTests


### ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8800

ENTRYPOINT ["java", "-jar", "app.jar"]

```

---

## 🔒 Authentication

* Login → JWT generated
* Pass token in `Authorization: Bearer <token>`

---


## 🔗 Frontend URL

Set your frontend base URL so backend can allow CORS & communication.

Example (React deployment):

```
FRONTEND_URL=https://chess-frontend-xi.vercel.app
```

If using local development:

```
FRONTEND_URL=http://localhost:3000
```

Configure in Spring Boot (example):

```
@CrossOrigin(origins = "${FRONTEND_URL}")
```

---

## 📌 Future Enhancements

* Leaderboard
* Tournament mode
* Time‑controlled chess
* Social login

---

