# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app

# 1. Копируем только корневой pom.xml
COPY pom.xml .

# 2. Копируем все модули (включая их pom.xml)
COPY digitalkeyhub-app digitalkeyhub-app
COPY digitalkeyhub-user digitalkeyhub-user
COPY digitalkeyhub-storage digitalkeyhub-storage
COPY digitalkeyhub-api digitalkeyhub-api
COPY digitalkeyhub-security digitalkeyhub-security
COPY digitalkeyhub-comment digitalkeyhub-comment
COPY digitalkeyhub-payment digitalkeyhub-payment
COPY digitalkeyhub-product digitalkeyhub-product
COPY digitalkeyhub-notification digitalkeyhub-notification
COPY digitalkeyhub-order digitalkeyhub-order
COPY digitalkeyhub-common-config digitalkeyhub-common-config

# 3. Собираем только нужные модули с их зависимостями
RUN mvn clean package -pl digitalkeyhub-app -am -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/digitalkeyhub-app/target/digitalkeyhub-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
