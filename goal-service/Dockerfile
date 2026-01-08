FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=goal-service/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

