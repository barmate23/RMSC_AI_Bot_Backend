# ============================================================
# Multi-stage Dockerfile for RMSC AI Bot
# Stage 1: Build with Maven + JDK 21
# Stage 2: Runtime with JRE 21 (minimal image)
# ============================================================

# --------------- Stage 1: Maven Build -----------------------
# Use the official Maven image that bundles Maven 3.9 + JDK 21
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy source code and build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -B --no-transfer-progress

# --------------- Stage 2: Runtime Image ---------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the fat jar from the builder stage
COPY --from=builder /build/target/rmsc-ai-bot-*.jar app.jar

# Create log directory
RUN mkdir -p /app/logs && chown appuser:appgroup /app/logs

USER appuser

# Expose application port
EXPOSE 8080

# JVM tuning for containerized Java 21
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
