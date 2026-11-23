# Ayuda rápida — API Plataforma Cursos Online

Esta guía es un índice breve. La documentación completa está en [README.md](README.md).

## Ejecución local

```bash
bash gradlew clean test
bash gradlew bootRun
```

Base URL: `http://localhost:8081`. Requiere JDK 21 y PostgreSQL (o variables `DB_URL`, `DB_USER`, `DB_PASSWORD`).

## Construcción y despliegue

```bash
bash gradlew bootJar
docker build -t api-cursos:latest .
kind load docker-image api-cursos:latest --name cursos-local
mkdir -p .kube-local
kind export kubeconfig --name cursos-local --kubeconfig .kube-local/config
export KUBECONFIG=.kube-local/config
kubectl apply -f k8s/
kubectl port-forward svc/api-cursos-service 8081:8081
```

## CI/CD

```bash
act -j build
```

El job `build` ejecuta pruebas, genera el JAR, construye la imagen y despliega en Kubernetes solo en `push` o con `act`. En GitHub Actions se crea un cluster kind efímero si no existe; con `act` se requiere un cluster previo en el host.

## Enlaces útiles

- [Documentación de Spring Boot 4.0.8](https://docs.spring.io/spring-boot/4.0.8/reference/)
- [Gradle 9.7.1](https://docs.gradle.org/9.7.1/userguide/userguide.html)
- [kind](https://kind.sigs.k8s.io/docs/user/quick-start/)
- [act](https://github.com/nektos/act#readme)
- [SpringDoc OpenAPI](https://springdoc.org/)
