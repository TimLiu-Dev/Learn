plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jackson.databind)          // JSON (L32); brings jackson-core and jackson-annotations
    implementation(libs.sqlite.jdbc)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "shelf.App"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")   // sqlite-jdbc loads a native library
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    testLogging {
        events("passed", "failed", "skipped")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// Run a main() from the test source set, e.g. ./gradlew -q runMain -Pmain=shelf.load.RaceDemo
tasks.register<JavaExec>("runMain") {
    classpath = sourceSets["test"].runtimeClasspath
    mainClass = providers.gradleProperty("main")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
