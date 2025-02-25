FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/BankApp-0.1.8.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]