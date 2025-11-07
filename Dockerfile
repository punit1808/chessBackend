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
