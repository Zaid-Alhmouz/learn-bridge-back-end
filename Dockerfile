# Stage 1: Build the application with Maven on Corretto 17
FROM maven:3.9.6-amazoncorretto-17 AS build

# set working dir in container
WORKDIR /app

# copy only the pom first (for dependency caching)
COPY learn-bridge-back-end/pom.xml ./

# go fetch dependencies
RUN mvn dependency:go-offline

# now copy your Java source and resources
COPY learn-bridge-back-end/src ./src

# package the app (skip tests to speed up)
RUN mvn clean package -DskipTests

# Stage 2: Run the application on Corretto 17
FROM amazoncorretto:17

# again set working dir
WORKDIR /app

# pull the fat JAR out of the build stage
COPY --from=build /app/target/*.jar app.jar

# expose your app port
EXPOSE 8080

# launch the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
