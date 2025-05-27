# Build Stage
FROM maven:3.9.9-amazoncorretto-17 AS build

# Set working directory
WORKDIR /app

# Copy project files
COPY learn-bridge-back-end/pom.xml ./learn-bridge-back-end/
COPY learn-bridge-back-end/src ./learn-bridge-back-end/src

# Build the application
RUN mvn -f learn-bridge-back-end/pom.xml clean package -DskipTests

# Runtime Stage
FROM amazoncorretto:17

# Set working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/learn-bridge-back-end/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
