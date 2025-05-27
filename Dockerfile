# Use Maven image to build the project
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files into the container
COPY learn-bridge-back-end/pom.xml ./learn-bridge-back-end/
COPY learn-bridge-back-end/src ./learn-bridge-back-end/src

# Navigate to the project directory and build the project
RUN mvn -f learn-bridge-back-end/pom.xml clean package -DskipTests

# Use a lightweight Java image to run the application
FROM eclipse-temurin:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the previous stage
COPY --from=build /app/learn-bridge-back-end/target/*.jar app.jar

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
