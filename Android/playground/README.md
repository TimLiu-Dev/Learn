# Modern Android & KMP 2027 — Live K2 Compiler Playground

This Gradle project gives you a **real Kotlin 2.x (K2) compiler, `kotlinx.coroutines.test` (`runTest`), `kotlinx.serialization`, Context Parameters (`-Xcontext-parameters`), and CashApp `Turbine` test runner** right alongside the 76 interactive HTML lessons.

## Quick Usage

1. **Open in Android Studio**: `File -> Open -> /Users/tl/Documents/Learns/Android/playground` (and for Android UI/Compose previews or the Phase VIII Capstone, create `./aether/` alongside `./playground/`).
2. **Run all tests from terminal** (uses Android Studio's bundled JDK 21):
   ```bash
   JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew test
   ```
3. **Where to write lesson code**:
   - Implementation: `core-practice/src/main/kotlin/com/aether/playground/`
   - Unit & Flow tests: `core-practice/src/test/kotlin/com/aether/playground/`
