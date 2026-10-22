# Build the Spring Boot application with Java 21 and Maven.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

# Run the packaged application on a lightweight Java 21 runtime.
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/sgt-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

# Render provides PORT at runtime; fall back to 10000 for local/container use.
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar app.jar"]
