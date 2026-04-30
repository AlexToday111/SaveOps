FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=services/interest-service/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
