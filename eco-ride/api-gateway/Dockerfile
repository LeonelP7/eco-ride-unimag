FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiar solo lo necesario para aprovechar cache de Docker
COPY pom.xml .
COPY src ./src

# Compilar el JAR (ajusta flags según necesites)
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="LeonelP7"
LABEL service="api-gateway"

WORKDIR /app

# Instalar wget para el HEALTHCHECK (se hace como root)
RUN apk add --no-cache wget

# Crear usuario no-root y luego copiar el JAR construido
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /workspace/target/*.jar app.jar
RUN chown spring:spring app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

USER spring:spring
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]