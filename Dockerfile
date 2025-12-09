# syntax=docker/dockerfile:1

## Build stage: compiles the project and collects runtime dependencies
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy the Maven descriptor first to leverage layer caching for dependencies
COPY pom.xml ./
RUN mvn -q dependency:go-offline

# Copy the application sources and build
COPY src ./src
RUN mvn -q -DskipTests package dependency:copy-dependencies

## Runtime stage: lightweight JRE image to execute the compiled application
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy compiled classes and third-party jars from the build stage
COPY --from=build /workspace/target/classes ./classes
COPY --from=build /workspace/target/dependency ./libs

# Default command runs the CLI application. Use -it when running to provide input.
ENTRYPOINT ["java", "-cp", "/app/classes:/app/libs/*", "SimplexUberAnalysis"]
