FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

ARG APP_VERSION=0.1.9-SNAPSHOT
COPY --from=builder /app/target/BankApp-${APP_VERSION}.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

LABEL maintainer="pawel@mackiewicz.info"
LABEL version="${APP_VERSION}"
LABEL description="BankApp"