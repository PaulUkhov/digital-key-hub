# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /workspace

# 1. Copy entire project structure
COPY . .

# 2. Build with optimized settings
RUN mvn clean package -pl digitalkeyhub-app -am \
    -DskipTests \
    -T 1C \
    -Dmaven.compile.fork=true \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy built JAR
COPY --from=builder /workspace/digitalkeyhub-app/target/digitalkeyhub-app-*.jar app.jar

# JVM optimizations
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Non-root user
RUN groupadd -r appuser && \
    useradd -r -g appuser appuser && \
    chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]
