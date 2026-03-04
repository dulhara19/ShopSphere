# Dockerfile for Payment Service

FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Add Spring Boot application JAR
ARG JAR_FILE=target/payment-service-1.0.0.jar
COPY ${JAR_FILE} payment-service.jar

# Create non-root user
RUN useradd -m -u 1000 payment && chown -R payment:payment /app

USER payment

# Expose port
EXPOSE 3005

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:3005/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
