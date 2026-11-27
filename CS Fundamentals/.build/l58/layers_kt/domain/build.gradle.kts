plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories { mavenCentral() }

kotlin { jvmToolchain(25) }

// No dependencies block: the rules need nothing but the Kotlin stdlib and the JDK.
