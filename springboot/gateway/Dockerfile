# Build stage
FROM openjdk:21-jdk-slim AS build
LABEL author="nexxserve"

WORKDIR /app

# Copy gradle wrapper and config first (infrequent changes)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle* ./

RUN chmod +x ./gradlew

# Download dependencies separately
RUN ./gradlew build --no-daemon -x test || return 0

# Now copy the actual source code
COPY src ./src

# Then build the application
RUN ./gradlew build --no-daemon -x test

# --- Runtime image ---
FROM openjdk:21-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the application port
EXPOSE 8080
#EXPOSE 9090

# Set the startup command
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]