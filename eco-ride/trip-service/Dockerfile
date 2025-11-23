FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="LeonelP7"
LABEL service="trip-service"

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8085

ENV JAVA_OPTS=""

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]