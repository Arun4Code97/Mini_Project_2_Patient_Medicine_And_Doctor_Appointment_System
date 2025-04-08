# ----------- STAGE 1: Build with Maven ----------- #
FROM maven:3.9.4-eclipse-temurin-17 as builder

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the app (skip tests for speed)
RUN mvn clean package -DskipTests

# ----------- STAGE 2: Run the built JAR ----------- #
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
