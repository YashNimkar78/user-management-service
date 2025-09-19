FROM eclipse-temurin:21-jdk-alpine
ARG JAR_FILE=target/usermanagement-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} usermanagement-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/usermanagement-0.0.1-SNAPSHOT.jar"]
