# ----------------------------------------------------------------------
# STAGE 1: COMPILACIÓN (BUILDER)
# Usamos una imagen con JDK y Maven para construir el JAR.
# ----------------------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-21 AS builder

# Establece el directorio de trabajo dentro del contenedor builder
WORKDIR /app

# Copia los archivos de configuración de Maven (pom.xml) y descargas las dependencias.
# Esto mejora el caching si solo cambia el código fuente.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el código fuente completo
COPY src ./src

# Compila y empaqueta el código
RUN mvn clean package -DskipTests

# ----------------------------------------------------------------------
# STAGE 2: EJECUCIÓN (PRODUCTION)
# Usamos la imagen base pequeña (JRE-Alpine) para el entorno de producción.
# ----------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="LeonelP7"
LABEL service="trip-service"

# Configuración de usuario (la que ya tenías)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# === COPIA EL ARTEFACTO DESDE LA ETAPA ANTERIOR ===
# Copia el JAR resultante de la etapa 'builder'
# Asume que el JAR final está en /app/target/app.jar (o similar, verifica el nombre)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8085

ENV JAVA_OPTS=""

# Añade curl para el HEALTHCHECK (como se discutió anteriormente)
RUN apk update && apk add curl

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl --fail http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]



#FROM eclipse-temurin:21-jre-alpine
#
#LABEL maintainer="LeonelP7"
#LABEL service="trip-service"
#
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#
#WORKDIR /app
#
#COPY target/*.jar app.jar
#
#EXPOSE 8085
#
#ENV JAVA_OPTS=""
#
#HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#  CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1
#
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]