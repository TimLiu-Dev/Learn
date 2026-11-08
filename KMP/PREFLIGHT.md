# KMP Developer Environment Pre-Flight Checklist (macOS)

Before you begin Lesson 01, ensure your local macOS development environment is properly configured. Kotlin Multiplatform compiles for both the JVM (Android) and native Apple LLVM targets (iOS), requiring correct Java and Xcode path configurations.

---

## 🔍 Diagnostic Summary on This Machine

| Tool / Dependency | Status | Path / Recommended Fix |
| :--- | :---: | :--- |
| **Android Studio** | ✅ Installed | `/Applications/Android Studio.app` |
| **Xcode** | ✅ Installed | `/Applications/Xcode.app` |
| **IntelliJ IDEA** | ✅ Installed | `/Applications/IntelliJ IDEA.app` |
| **Java Runtime (JDK)** | ⚠️ Unbound | Use bundled JDK 21 in Android Studio |
| **xcode-select** | ⚠️ CommandLineTools | Switch to `/Applications/Xcode.app/Contents/Developer` |
| **kdoctor** | ℹ️ Optional | Install via Homebrew for automated health checks |

---

## ⚡ Quick 2-Minute Setup Steps

Run the following commands in your macOS terminal to configure your shell profile (`~/.zshrc`):

### 1. Configure `JAVA_HOME` (OpenJDK 21)
Android Studio includes a high-performance JetBrains Runtime (JDK 21). Export it in your `~/.zshrc`:

```bash
# Add to ~/.zshrc:
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

Verify in terminal:
```bash
java -version
# Expected: openjdk version "21.0.10"
```

---

### 2. Configure Xcode Developer Tools for Kotlin/Native
Kotlin/Native requires the full Xcode toolchain to compile iOS arm64 and Simulator binaries. Run:

```bash
sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
```

Verify:
```bash
xcode-select -p
# Expected: /Applications/Xcode.app/Contents/Developer
```

---

### 3. Install & Run `kdoctor` (JetBrains Official Verifier)
JetBrains provides `kdoctor` to automatically scan your system and verify that Android SDK, Xcode, CocoaPods/SPM, and Java are healthy:

```bash
brew install kdoctor
kdoctor
```

You should see green checkmarks `[v]` across:
- `[v] User Home`
- `[v] Java Runtime`
- `[v] Android Studio`
- `[v] Xcode`

---

## 🛠️ Recommended IDE Plugins

1. **In Android Studio / IntelliJ IDEA**:
   - **Kotlin Multiplatform Mobile (KMM / KMP)** plugin (Official JetBrains)
   - **Compose Multiplatform IDE Support** plugin
2. **In Xcode**:
   - Ensure iOS Simulator runtimes are installed (e.g. iOS 17 or 18 via Xcode Settings &rarr; Platforms).

---

## 🚀 You Are Ready!
Once your `JAVA_HOME` and `xcode-select` are set, proceed directly to [Lesson 01: Anatomy of a Modern KMP Project](lessons/0001-anatomy-of-a-modern-kmp-project.html)!
