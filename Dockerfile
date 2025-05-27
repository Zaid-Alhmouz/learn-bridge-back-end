# Stage 1: Build the application using Maven and Amazon Corretto 17
FROM maven:3.9.6-amazoncorretto-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the application, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Amazon Corretto 17
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application's port (adjust if necessary)
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
