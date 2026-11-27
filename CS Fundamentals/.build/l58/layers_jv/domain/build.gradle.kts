plugins {
    java
}

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

// No dependencies block: the rules need nothing but the JDK.
