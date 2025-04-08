# Use a Java base image
FROM eclipse-temurin:17-jdk-alpine

# Create a directory for the app
WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Expose the port (default is 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
