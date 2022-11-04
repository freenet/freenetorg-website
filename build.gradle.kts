import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.freenet.website"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("com.google.guava:guava:31.1-jre")

    implementation("io.kweb:kweb-core:1.1.2.1")

    implementation("org.slf4j:slf4j-simple:2.0.3")

    implementation("com.google.cloud:google-cloud-firestore:3.7.0")

    implementation("com.github.mfornos:humanize-slim:1.2.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.3")
    implementation("com.google.cloud:google-cloud-logging-logback:0.127.10-alpha")

    implementation("org.kohsuke:github-api:1.313")
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0-rc2") {
            because("""
                CVE-2022-42003 7.5 Deserialization of Untrusted Data vulnerability pending CVSS allocation
                CVE-2022-42004 7.5 Deserialization of Untrusted Data vulnerability pending CVSS allocation
                """.trimIndent())
        }
    }

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
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