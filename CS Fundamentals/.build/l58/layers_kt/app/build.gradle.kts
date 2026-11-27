plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories { mavenCentral() }

kotlin { jvmToolchain(25) }

dependencies {
    implementation(project(":domain"))   // app depends on the rules...
    implementation(libs.sqlite.jdbc)     // ...and on the detail; domain sees neither

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "library.app.AppKt"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")   // sqlite-jdbc loads native code
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
