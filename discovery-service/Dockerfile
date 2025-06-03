# Use a lightweight base image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR file into the image
ARG JAR_FILE=target/discovery-service-*.jar
COPY ${JAR_FILE} app.jar

# Expose port 8761
EXPOSE 8761

# Set the entrypoint to run the Java application
ENTRYPOINT ["java","-jar","/app/app.jar"]
