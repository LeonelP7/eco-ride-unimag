FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="LeonelP7"
LABEL service="api-gateway"

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado
COPY target/*.jar app.jar

# Puerto por defecto (ajusta según tu configuración)
EXPOSE 8080

# Variables de entorno opcionales
ENV JAVA_OPTS=""

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]