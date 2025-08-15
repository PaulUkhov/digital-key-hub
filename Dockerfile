# Stage 1: Build dependencies layer
FROM maven:3.9.6-eclipse-temurin-21 as dependencies

WORKDIR /app
COPY pom.xml .

RUN mvn dependency:go-offline -B

# Stage 2: Build application
FROM dependencies as builder

COPY . .

RUN mvn package -pl digitalkeyhub-app -am \
    -DskipTests \
    -T 1C \
    -Dmaven.compile.fork=true \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true

# Stage 3: Minimal runtime image
FROM eclipse-temurin:21-jre-jammy as runtime

WORKDIR /app

COPY --from=builder /app/digitalkeyhub-app/target/digitalkeyhub-app-*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

RUN addgroup --system javauser && \
    adduser --system --ingroup javauser javauser && \
    chown -R javauser:javauser /app
USER javauser

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]
