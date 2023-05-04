import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.8.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.20"
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "it.polito.wa2.g29"
version = "0.2"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

jib {
    from {
        image = "eclipse-temurin:17-jre"
    }
    to {
        image = "pepaunz/ticket-assistance"
        tags = setOf("latest")
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
