import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "app"
version = "1.0"

application {
    mainClassName = "app.AppKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":generator"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}