# Stage 1: Dependencies
FROM eclipse-temurin:21-jdk-alpine AS dependencies
WORKDIR /app

# Copy only the files needed for dependency resolution
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make mvnw executable and download dependencies
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline

# Stage 2: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy dependencies and files from previous stage
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /app/mvnw /app/pom.xml ./
COPY --from=dependencies /app/.mvn .mvn

# Copy source code and build
COPY src src
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/BankApp-0.1.1-SNAPSHOT.jar app.jar
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]