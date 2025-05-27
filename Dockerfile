# Stage 1: Build
FROM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /app

# adjust these paths if your backend is in a different folder
COPY backend/pom.xml ./
RUN mvn dependency:go-offline

COPY backend/src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
