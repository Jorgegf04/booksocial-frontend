# syntax=docker/dockerfile:1

# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Manifiesto primero para aprovechar caché de capas de Docker.
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -q

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -q

# ── Stage 2: runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# curl para el healthcheck del contenedor.
RUN apk add --no-cache curl

COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

USER appuser

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
