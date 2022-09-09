FROM openjdk:17-alpine
EXPOSE 8080
COPY upload upload
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

