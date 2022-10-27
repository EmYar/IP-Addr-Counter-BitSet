import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val applicationDefaultXmx: String by project

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "me.emyar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation(kotlin("test"))
}

tasks.test {
    maxHeapSize = applicationDefaultXmx
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("me.emyar.MainKt")
    applicationDefaultJvmArgs = listOf("-Xmx$applicationDefaultXmx")
}