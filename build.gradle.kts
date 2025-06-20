import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "me.play"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"
val httpclientVersion by extra("5.4.4")
val micrometerVersion by extra("1.4.5")
val swaggerVersion by extra("2.8.6")
val kotlinLoggingVersion by extra("3.0.5")

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    implementation("org.springframework.cloud:spring-cloud-config-client")

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:${kotlinLoggingVersion}")

    // http client
    implementation("org.apache.httpcomponents.client5:httpclient5:${httpclientVersion}")

    //Micrometer tracing
    implementation("io.micrometer:micrometer-tracing-bridge-brave:${micrometerVersion}")

    //swagger
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${swaggerVersion}")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
