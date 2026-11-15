plugins {
    kotlin("jvm")
    application
}

repositories { mavenCentral() }

kotlin { jvmToolchain(21) }

dependencies {
    implementation(project(":core"))   // another project in this build, not a download
}

application {
    mainClass = "app.MainKt"
}
