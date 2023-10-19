val applicationDefaultXmx: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    application
}

group = "me.emyar"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(20)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("me.emyar.MainKt")
    applicationDefaultJvmArgs = listOf("-Xmx$applicationDefaultXmx", "-XX:+UseParallelGC")
}

tasks.test {
    maxHeapSize = applicationDefaultXmx
    useJUnitPlatform()
}