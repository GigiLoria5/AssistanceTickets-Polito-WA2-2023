import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.8.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.20"
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    id("io.freefair.lombok") version "6.2.0"
}

group = "it.polito.wa2.g29"
version = "0.4"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

jib {
    from {
        image = "eclipse-temurin:17-jre"
    }
    to {
        image = "pepaunz/assistance-tickets"
        tags = setOf("v0.4")
    }

    container {
        ports = listOf("8080")
        environment = mapOf(
            "JAVA_OPTS" to "-Xmx512m"
        )
        jvmFlags = listOf("-Djava.security.egd=file:/dev/./urandom")
    }
}

extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.keycloak:keycloak-admin-client:21.1.1")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    // Observability and logging
    implementation ("org.springframework.boot:spring-boot-starter-aop")
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("io.micrometer:micrometer-registry-prometheus")
    implementation ("io.micrometer:micrometer-tracing-bridge-brave")
    implementation ("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation ("com.github.loki4j:loki-logback-appender:1.4.0-rc2")
    implementation ("io.github.microutils:kotlin-logging-jvm:2.0.11")


    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
