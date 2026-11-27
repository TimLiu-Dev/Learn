// Root project: declare the Kotlin plugin's version once, apply it in each subproject (L25).
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}
