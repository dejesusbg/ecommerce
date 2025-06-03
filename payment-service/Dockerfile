FROM openjdk:17-slim
WORKDIR /app
ARG JAR_FILE=target/payment-service-*.jar
COPY ${JAR_FILE} app.jar
# EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
