FROM ubuntu:latest
LABEL authors="sozon"

# Use OpenJDK as base image
FROM openjdk:17-jdk-slim

# Add a volume to store logs
VOLUME /tmp

# Copy the built .jar file into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
