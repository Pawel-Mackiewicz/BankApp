FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/BankApp-0.4.6.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]