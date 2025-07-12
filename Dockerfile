# Use a lightweight OpenJDK 21 image based on Alpine Linux
# https://hub.docker.com/_/maven
FROM maven:3.9.10-eclipse-temurin-21-alpine

# Set the working directory in the container
WORKDIR /app

# Only copy pom.xml and download dependencies first to cache better
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Set the PORT environment variable (default to 8080, can be overridden by Render)
ENV PORT=8080

# Expose the default Spring Boot port
EXPOSE $PORT

# Run the JAR file (any *.jar in target/), using the PORT env variable
CMD ["sh", "-c", "java -jar $(find target -maxdepth 1 -type f -name '*.jar' | head -n 1) --server.port=${PORT}"]