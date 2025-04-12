# Use official Gradle image with JDK 21
FROM gradle:8.6-jdk21-jammy

# Set working directory
WORKDIR /app

# Copy Gradle config files
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Make gradlew executable
RUN chmod +x gradlew

# Resolve dependencies early (to cache them)
RUN ./gradlew --no-daemon dependencies || true

# Copy application source code
COPY src src

# Disable Gradle daemon inside container
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

# Expose the application port
EXPOSE 8080

# Run the Spring Boot app (good for dev)
CMD ["./gradlew", "bootRun"]