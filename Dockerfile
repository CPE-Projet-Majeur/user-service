# Build stage
FROM maven:3.9 AS build
WORKDIR /usr/app
COPY . .
RUN mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:23-alpine
WORKDIR /usr/app
COPY --from=build /usr/app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT java -jar app.jar