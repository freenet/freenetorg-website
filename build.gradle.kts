import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.8.0"
}

group = "org.freenet.website"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("com.google.guava:guava:31.1-jre")

    implementation("io.kweb:kweb-core:1.3.8")

    implementation("org.slf4j:slf4j-simple:2.0.5")

    implementation("com.google.cloud:google-cloud-firestore:3.9.1")

    implementation("com.github.mfornos:humanize-slim:1.2.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("com.google.cloud:google-cloud-logging-logback:0.127.10-alpha")

    implementation("org.kohsuke:github-api:1.314")
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0-rc2") {
            because("""
                CVE-2022-42003 7.5 Deserialization of Untrusted Data vulnerability pending CVSS allocation
                CVE-2022-42004 7.5 Deserialization of Untrusted Data vulnerability pending CVSS allocation
                """.trimIndent())
        }
    }

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.commonmark:commonmark:0.21.0")

    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.freenet.website.MainKt")
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType(ShadowJar::class.java) {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/services")
            include("org.eclipse.jetty.http.HttpFieldPreEncoder")
        }
    }

}