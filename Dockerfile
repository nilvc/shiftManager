FROM node:22-alpine AS frontend-builder

WORKDIR /workspace/src/main/frontend

COPY src/main/frontend/package*.json ./
RUN npm ci

COPY src/main/frontend/ ./
RUN npm run build

FROM eclipse-temurin:17-jdk AS backend-builder

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
COPY --from=frontend-builder /workspace/src/main/resources/static/ src/main/resources/static/

RUN chmod +x mvnw
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=backend-builder /workspace/target/*.jar app.jar

RUN mkdir -p /app/data

EXPOSE 8080

CMD ["sh", "-c", "java ${JAVA_OPTS:-} -jar /app/app.jar --server.port=${PORT:-8080}"]
