# Use an official Maven image as the base image
FROM maven:3.9.9-amazoncorretto-21 AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
# Build the application using Maven
RUN mvn clean package spring-boot:repackage -DskipTests
# Use an official OpenJDK image as the base image
FROM openjdk:21-jdk
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/examen-BE-1.0-SNAPSHOT.jar .
# Set the command to run the application
CMD ["java", "-jar", "examen-BE-1.0-SNAPSHOT.jar"]