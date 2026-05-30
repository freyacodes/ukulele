import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
}

group = "dev.arbjerg"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    flatDir {
        dirs("lib")
    }
    maven { url = uri("https://m2.dv8tion.net/releases") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.lavalink.dev/releases") }
}

dependencies {
    implementation("com.github.DV8FromTheWorld:JDA:v5.0.0-beta.23")
    implementation("dev.arbjerg:lavaplayer:2.2.2")
    implementation("dev.lavalink.youtube:v2:1.8.3")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    runtimeOnly("com.h2database:h2")
    implementation("io.r2dbc:r2dbc-h2")
    implementation("org.flywaydb:flyway-core")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.0")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<BootJar> {
    archiveFileName.set("ukulele.jar")
    doLast {
        //copies the jar into a place where the Dockerfile can find it easily (and users maybe too)
        copy {
            from("build/libs/ukulele.jar")
            into(".")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
