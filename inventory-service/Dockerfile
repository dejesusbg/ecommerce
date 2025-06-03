FROM openjdk:17-slim
WORKDIR /app
ARG JAR_FILE=target/inventory-service-*.jar
COPY ${JAR_FILE} app.jar
# EXPOSE 8082 
ENTRYPOINT ["java","-jar","/app/app.jar"]
