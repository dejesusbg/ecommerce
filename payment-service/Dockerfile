# Use a lightweight base image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file into the image
ARG JAR_FILE=target/payment-service-*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port (Spring Boot will use a random port due to server.port=0)
# However, for container linking or specific port mapping, you might need to define one.
# For this example, let's assume we'll map a specific port during 'docker run' or in Docker Compose.
# EXPOSE 8084

# Set the entrypoint to run the Java application
ENTRYPOINT ["java","-jar","/app/app.jar"]
