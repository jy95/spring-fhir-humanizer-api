# Use a lightweight OpenJDK 21 image based on Alpine Linux
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy project files into the container
COPY . .

# Auto-detect build tool and build the application
RUN if [ -f ./gradlew ]; then ./gradlew build --no-daemon; \
    elif [ -f ./mvnw ]; then ./mvnw clean package -DskipTests; \
    else echo "No recognized build tool found!"; exit 1; fi

# Set the PORT environment variable (default to 8080, can be overridden by Render)
ENV PORT=8080

# Expose the default Spring Boot port
EXPOSE $PORT

# Run the JAR file, ensuring Spring Boot uses the PORT environment variable
CMD ["sh", "-c", "java -jar $(find build/libs -name '*.jar' | head -n 1) --server.port=${PORT}"]