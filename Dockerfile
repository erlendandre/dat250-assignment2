FROM gradle:8.14.3-jdk21 AS builder

USER root

# Install Node.js 22 manually (from NodeSource)
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g npm@10 && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /home/gradle/project

COPY settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
COPY src src
COPY frontend frontend

RUN chown -R gradle:gradle /home/gradle/project
USER gradle

RUN gradle bootJar --no-daemon

RUN mv build/libs/*.jar app.jar


FROM eclipse-temurin:21-jdk-jammy

RUN addgroup --system app && adduser --system --ingroup app app
USER app
WORKDIR /app

COPY --from=builder --chown=app:app /home/gradle/project/app.jar .

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]