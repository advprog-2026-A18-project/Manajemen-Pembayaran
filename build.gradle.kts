plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.2.0.6526"
    jacoco
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "ManajemenPembayaran"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

sonar {
    properties {
        property("sonar.projectKey", "advprog-2026-A18-project_Manajemen-Pembayaran")
        property("sonar.organization", "advprog-2026-a18-project")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
//    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
