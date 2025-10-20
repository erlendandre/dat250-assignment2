## Build & Run Options

You can run the application in several ways:

### 1. Frontend only (Svelte)
```
cd frontend
npm install
npm run dev
```

Runs the Svelte app on http://localhost:5173

### 2. Spring Boot (local)

```
./gradlew bootRun
```

Starts the backend locally on http://localhost:8080 without Docker.

### 3. Full stack with Docker
```
./gradlew dockerUp
```

Builds and starts both backend and Redis containers using Docker Compose.
Access the app at http://localhost:8080.
