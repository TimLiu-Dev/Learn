plugins {
    kotlin("jvm")
    `java-library`              // adds the api configuration for consumers
}

repositories { mavenCentral() }

kotlin { jvmToolchain(21) }

// No dependencies block: core needs nothing but the Kotlin stdlib.
