import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.google.cloud.tools.appengine") version "2.4.2"
}

group = "org.freenet.website"
version = "1.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.google.appengine:appengine-api-1.0-sdk:2.0.9")

    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.github.kwebio:kweb-core:2f02d41869")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation("com.stripe:stripe-java:21.8.0")

    implementation("com.google.cloud:google-cloud-firestore:3.5.0")

    implementation("com.github.mfornos:humanize-slim:1.2.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.0")
    implementation("com.google.cloud:google-cloud-logging-logback:0.127.10-alpha")

    implementation("org.kohsuke:github-api:1.313")


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

appengine {
    stage {
        setArtifact("build/libs/freenet-website-1.2-SNAPSHOT-all.jar")
    }
    deploy {
        version = "GCLOUD_CONFIG"
        projectId = "GCLOUD_CONFIG"
    }
}