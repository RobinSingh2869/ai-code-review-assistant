FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY target/ai-worker.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]