# DAT250 – Docker

[Full repository](https://github.com/erlendandre/dat250-assignment2)

## Dockerfile
- **Multi-stage build**: uses `gradle:8.14.3-jdk21` to build the JAR (including Svelte frontend), then runs it on a lightweight `eclipse-temurin:21-jdk-jammy` image
- Installs **Node.js 22** manually for frontend build
- Exposes port **8080** and runs `java -jar app.jar`

## .dockerignore
Excludes build and system files


## docker-compose.yml
Defines two services on a shared network:
- **pollapp** – Spring Boot app built from the Dockerfile 
- **redis** – lightweight Redis instance (`redis:7-alpine`)

Environment variables: `SPRING_DATA_REDIS_HOST=redis` and `SPRING_DATA_REDIS_PORT=6379`


## Code Changes
- **RedisConfig**: removed custom `RedisConnectionFactory` (now uses Spring Boot’s auto-config)
- **PollManager**: connects dynamically to Redis using environment variables or localhost fallback


## GitHub Actions CI – Docker build and test
The workflow `.github/workflows/docker.yml` automatically builds and tests the application on each push or pull request to `main`.