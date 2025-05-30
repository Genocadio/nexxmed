FROM openjdk:21-jdk-slim AS builder
LABEL authors="nexxserve"

# Set the working directory
WORKDIR /app
# Copy the Gradle wrapper and the project files into the container
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
# Run Gradle build to create the application .jar
RUN ./gradlew build -x test
# Use the OpenJDK image to run the application
FROM openjdk:21-jdk-slim
# Set the working directory in the container

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
# Copy the built .jar file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar
# Expose the port the app will run on
EXPOSE 8761
# Command to run the app
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
