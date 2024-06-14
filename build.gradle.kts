val applicationDefaultXmx: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "me.emyar"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("me.emyar.MainKt")
    applicationDefaultJvmArgs = listOf("-Xmx$applicationDefaultXmx", "-XX:+UseParallelGC")
}

tasks {
    wrapper {
        version = 8.8
    }

    test {
        maxHeapSize = applicationDefaultXmx
        useJUnitPlatform()
    }
}