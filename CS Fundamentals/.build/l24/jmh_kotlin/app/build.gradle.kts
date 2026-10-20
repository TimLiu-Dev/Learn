plugins {
    alias(libs.plugins.kotlin.jvm)
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

jmh {
    warmupIterations = 3
    warmup = "1s"
    iterations = 5
    timeOnIteration = "1s"
    fork = 1
}
