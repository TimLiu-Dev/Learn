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

dependencies {
    implementation(project(":platform"))   // to publish events
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
