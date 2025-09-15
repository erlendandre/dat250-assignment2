plugins {
    java
    id("org.springframework.boot") version "3.4.9"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.node-gradle.node") version "7.0.2"
}

group = "no.hvl.dat250"
version = "0.0.1-SNAPSHOT"
description = "DAT250 Assignment 2 demo project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
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

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// --- Node/Frontend build configuration ---
node {
    version.set("22.0.0")
    npmVersion.set("10.5.1")
    download.set(true)
    workDir.set(file("${project.buildDir}/node"))
    nodeProjectDir.set(file("frontend"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuildFrontend") {
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