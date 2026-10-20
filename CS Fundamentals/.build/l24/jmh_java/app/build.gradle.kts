plugins {
    java
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

jmh {
    warmupIterations = 3   // let the JIT compile the code first...
    warmup = "1s"          // ...for three 1-second rounds
    iterations = 5         // then measure five rounds
    timeOnIteration = "1s" // of 1 second each
    fork = 1               // in one fresh JVM
}
