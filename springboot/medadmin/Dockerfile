# Use Eclipse Temurin JRE 21 (official OpenJDK build)
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the already built JAR from host
COPY build/libs/medadmin-*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run with docker profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
