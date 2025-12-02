# --- build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download all dependencies
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copy source and build server
COPY src ./src
RUN mvn -q clean package -DskipTests

# --- runtime stage ---
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy built server
COPY --from=build /app/target/*.jar app.jar

# Expose server port
EXPOSE 3000

# Run server with `docker` profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]
