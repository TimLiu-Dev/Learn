plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories { mavenCentral() }

kotlin { jvmToolchain(25) }

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Shared plumbing with no domain knowledge: the in-process event bus. No dependencies.
