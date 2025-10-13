import com.github.gradle.node.npm.task.NpmTask

plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.github.node-gradle.node") version "7.0.2"
}

group = "no.hvl.dat250"
version = "0.0.1-SNAPSHOT"
description = "DAT250 Assignment 2 demo project for Spring Boot"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JPA/Hibernate
    implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")


    implementation("com.h2database:h2:2.3.232")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // Transaksjons-API som Hibernate trenger
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")

    // Logging som Hibernate bruker under panseret
    implementation("org.jboss.logging:jboss-logging:3.5.1.Final")


    implementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    implementation("org.jboss.logging:jboss-logging:3.6.1.Final")

    // Redis
    implementation("redis.clients:jedis:6.2.0")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

node {
    version.set("22.12.0")
    npmVersion.set("10.9.0")
    download.set(true)
    nodeProjectDir.set(file("frontend"))
}

tasks.register<NpmTask>("npmBuildFrontend") {
    dependsOn("npmInstall")
    args.set(listOf("run", "build"))
    workingDir.set(file("frontend"))
}

tasks.register<Copy>("copyWebApp") {
    dependsOn("npmBuildFrontend")
    from("frontend/dist")
    into("src/main/resources/static")
}

tasks.register("buildFrontend") {
    dependsOn("copyWebApp")
}

tasks.named("bootJar") {
    dependsOn("buildFrontend")
}

tasks.named("bootRun") {
    dependsOn("buildFrontend")
}

tasks.named("processResources") {
    dependsOn("copyWebApp")
}