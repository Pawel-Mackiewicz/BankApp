FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/BankApp-0.4.8-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]