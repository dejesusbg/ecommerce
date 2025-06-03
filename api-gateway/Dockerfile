# Use a lightweight base image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file into the image
ARG JAR_FILE=target/api-gateway-*.jar
COPY ${JAR_FILE} app.jar

# Expose port 8080
EXPOSE 8080

# Set the entrypoint to run the Java application
ENTRYPOINT ["java","-jar","/app/app.jar"]
