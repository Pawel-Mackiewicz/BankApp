# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Maven wrapper and project configuration files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Ensure the Maven wrapper has execution permissions
RUN chmod +x mvnw

# Download dependencies to speed up future builds
RUN ./mvnw dependency:go-offline

# Copy the source code into the container
COPY src src

# Build the application, skipping tests for faster execution
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a lightweight final image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/BankApp-0.1.0-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]