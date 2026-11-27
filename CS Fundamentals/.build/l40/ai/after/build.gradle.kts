plugins {
    kotlin("jvm") version "2.4.20"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-json-utils:1.2.0")
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
