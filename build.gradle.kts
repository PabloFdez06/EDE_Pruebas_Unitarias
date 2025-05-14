plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))


    testImplementation(kotlin("test"))
    implementation("at.favre.lib:bcrypt:0.9.0")
    implementation("org.jline:jline:3.29.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}