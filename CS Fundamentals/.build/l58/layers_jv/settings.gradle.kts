plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "library"
include("domain", "app")   // the rules, and everything that touches the outside world
