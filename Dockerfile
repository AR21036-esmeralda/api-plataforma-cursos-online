# Etapa 1: construcción con el wrapper de Gradle incluido en el repositorio.
# El wrapper fija Gradle 9.7.1 en gradle/wrapper/gradle-wrapper.properties,
# por eso NO se usa una imagen gradle:<version> que pueda desincronizarse.
FROM eclipse-temurin:21-jdk-alpine AS builder

RUN apk add --no-cache bash ca-certificates

WORKDIR /app

# Copiar primero los archivos del wrapper y del build para aprovechar la caché de capas.
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
# El wrapper no tiene permiso de ejecución en el repositorio: siempre invocarlo con bash.
RUN chmod +x gradlew
RUN bash gradlew --version --no-daemon

COPY src src

# bootJar genera el jar ejecutable. Si falla, la capa falla (sin `|| true`).
RUN bash gradlew clean bootJar --no-daemon \
    && test -f build/libs/api-cursos-online-0.0.1-SNAPSHOT.jar \
    && cp build/libs/api-cursos-online-0.0.1-SNAPSHOT.jar /app/api.jar

# Etapa 2: imagen final ligera de ejecución (solo JRE).
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=builder /app/api.jar app.jar

USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
