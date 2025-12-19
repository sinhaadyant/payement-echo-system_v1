FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Build the application
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR
COPY --from=build /app/build/libs/payment-echo-system-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will set PORT env variable)
EXPOSE ${PORT:-8080}

# Run the application
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=${PORT:-8080} /app/app.jar"]
