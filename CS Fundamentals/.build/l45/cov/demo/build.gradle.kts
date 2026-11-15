plugins {
    kotlin("jvm") version "2.4.0"
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging { events("passed", "failed") }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports { csv.required = true }
}
