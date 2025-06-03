FROM openjdk:17-slim
WORKDIR /app
ARG JAR_FILE=target/order-service-*.jar
COPY ${JAR_FILE} app.jar
# EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
