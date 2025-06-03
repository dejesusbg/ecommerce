FROM openjdk:17-slim
WORKDIR /app
ARG JAR_FILE=target/discovery-service-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
