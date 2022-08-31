FROM openjdk:17-alpine

EXPOSE 8080

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]
