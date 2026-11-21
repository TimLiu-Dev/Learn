# Modern Android Development Glossary (2027 Senior & KMP Standards)

Canonical terminology, architectural definitions, and runtime mental models across all 9 phases of the curriculum.

---

## 1. Kotlin 2.x, Coroutines & Reactive Streams (Phase I)

- **K2 Compiler (FIR)**: Kotlin's frontend intermediate representation compiler (default in Kotlin 2.x) delivering ~2x faster compilation and **Smart Cast 2.0** across inline lambdas, local boolean variables, and complex `when` expressions.
- **Sealed Interface (`sealed interface`)**: A closed type hierarchy whose direct subtypes are known at compile time within the same module/package, enabling exhaustive `when` expressions without a fallback `else` branch and eliminating impossible UI states.
- **Inline Value Class (`@JvmInline value class`)**: A single-property wrapper type (e.g., `UserId(val value: String)`) that enforces strict compile-time domain type safety while unboxing to the underlying primitive/reference on the JVM heap whenever possible.
- **Reified Type Parameter (`inline fun <reified T>`)**: Preserves generic type information `T` at runtime by inlining the function bytecode at the call site, bypassing JVM type erasure for reflection-free serialization, navigation routes, and `Intent`/`Bundle` extraction.
- **Context Parameters (`context(...)`)**: Kotlin 2.x language mechanism replacing experimental context receivers, allowing functions to declare implicit ambient dependencies (such as transaction scopes, logging contexts, or theme tokens) without polluting explicit parameter lists.
- **Continuation-Passing Style (CPS)**: The compiler transformation that rewrites `suspend` functions into finite state machines (`Continuation<T>` with a `label` switch), enabling non-blocking suspension and resumption without allocating OS threads.
- **Structured Concurrency**: The guarantee that every coroutine runs within a `CoroutineScope` with a strict parent-child `Job` hierarchy: parent jobs wait for all children, parent cancellation cascades downward, and uncaught child exceptions propagate upward unless isolated by a `SupervisorJob` or `supervisorScope`.
- **Cooperative Cancellation & `CancellationException`**: Coroutines cancel cooperatively by throwing `CancellationException` at suspension points. Catching `Exception` or using `runCatching` without rethrowing `CancellationException` breaks structured cancellation and leaks background work.
- **Cold Flow (`Flow<T>`) vs. Hot Stream (`StateFlow<T>` / `SharedFlow<T>`)**:
  - **Cold `Flow`**: Lazy producer block that executes from scratch for each collector (`collect`).
  - **`StateFlow`**: Hot state holder with a mandatory initial value, `replay = 1`, and structural equality (`equals()`) conflation.
  - **`SharedFlow`**: Hot broadcast stream with configurable `replay`, `extraBufferCapacity`, and `BufferOverflow` policy, without conflating equal values.
- **`SharingStarted.WhileSubscribed(5_000)`**: The production `stateIn` strategy in Android `ViewModel`s that keeps upstream flows alive for 5 seconds after the last UI collector detaches—surviving configuration changes (rotation/folding) without restarting database/network streams, while stopping collection 5 seconds after the app goes to the background.

---

## 2. Jetpack Compose Internals, UI & Material 3 (Phase II)

- **Three Phases of a Compose Frame**:
  1. **Composition**: Executes `@Composable` functions and updates the Slot Table UI tree (*What* to show).
  2. **Layout**: Measures and places layout nodes in a single pass (*Where* and *how big*).
  3. **Draw**: Renders drawing instructions onto hardware-accelerated Canvas/RenderNodes (*How* it looks).
  Deferring state reads (via lambda modifiers like `Modifier.offset { ... }` or `Modifier.drawWithCache`) skips Composition and Layout altogether.
- **Slot Table & Gap Buffer**: The flat, contiguous in-memory array where the Compose runtime stores composition groups, remembered values, and node pointers, using a gap buffer to perform O(1) localized insertions and updates during recomposition.
- **State Hoisting**: Moving state out of a composable to its caller and replacing it with an immutable value parameter (`value: T`) and event lambdas (`onValueChange: (T) -> Unit`), making the composable stateless, reusable, and previewable.
- **Strong Skipping Mode**: Enabled by default in Kotlin 2.0.20+, allowing composables with unstable parameters to be skipped by comparing **unstable parameters via referential equality (`===`)** and **stable parameters via structural equality (`equals()`)**, while automatically memoizing lambdas.
- **`@Immutable` vs. `@Stable`**: Compose compiler contract annotations:
  - `@Immutable`: Promises all public properties will never change after construction.
  - `@Stable`: Promises that `equals()` is consistent and Compose will be notified (via Snapshot `State`) if any public property mutates.
- **`derivedStateOf`**: Converts one or more high-frequency Snapshot states (e.g., `lazyListState.firstVisibleItemScrollOffset`) into a lower-frequency boolean/derived value so recomposition only triggers when the derived result crosses a threshold.
- **`Modifier.Node`**: The low-level, lifecycle-aware modifier API (`ModifierNodeElement` + `Modifier.Node`) that allocates once and survives recompositions, replacing the deprecated `Modifier.composed` which re-executed on every recomposition.
- **`SubcomposeLayout` vs. `Layout`**: `SubcomposeLayout` defers composition of child slots until measurement constraints are known (used internally by `BoxWithConstraints` and `LazyColumn`), but incurs higher overhead than a standard single-pass `Layout` or `IntrinsicSize`.
- **Edge-to-Edge & `WindowInsets.safeDrawing`**: Mandatory on Android 15/16/17 (`targetSdk 35+`); apps draw behind system bars by default and must apply `WindowInsets.safeDrawing` / `Scaffold` `innerPadding` with `consumeWindowInsets` so interactive elements are never obscured by status bars, navigation gestures, or display cutouts.

---

## 3. Navigation, UDF/MVI Architecture & Dependency Injection (Phase III)

- **Jetpack Navigation 2.8+ Type-Safe Routes**: Uses `@Serializable` objects and data classes (`navController.navigate(AssetDetailRoute(id = "btc"))`) generated via `kotlinx.serialization`, replacing fragile string-template URLs in brownfield and single-pane Compose apps.
- **Jetpack Navigation 3 (`androidx.navigation3`)**: The next-generation Compose-first navigation library where the developer owns the back stack directly as a reactive snapshot list (`rememberNavBackStack`) rendered by `NavDisplay` and `Scene` strategies for adaptive multi-pane layouts.
- **Unidirectional Data Flow (UDF) / MVI**: Architectural loop where state flows **downward** (`StateFlow<UiState>`) from the `ViewModel` to the UI, and user intents/actions (`sealed interface UiEvent`) flow **upward** to the `ViewModel` reducer.
- **`collectAsStateWithLifecycle()`**: Collects a `Flow`/`StateFlow` in Compose bound to `Lifecycle.State.STARTED` via `repeatOnLifecycle` under the hood, automatically cancelling upstream collection when the Activity stops in the background.
- **Brownfield Compose Interop (`ComposeView` & `AndroidView`)**:
  - `ComposeView`: Hosts Compose UI inside legacy XML/Fragments; requires `ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed` in Fragments to avoid leaking compositions when the Fragment's View is destroyed.
  - `AndroidView`: Hosts native Android `View`s (`MapView`, `WebView`, Media3 `PlayerView`) inside Compose with `factory`, `update`, `onReset` (for lazy list recycling), and `onRelease` lifecycle callbacks.

---

## 4. Networking, Offline-First Persistence & Background Sync (Phase IV)

- **Single Source of Truth (SSOT)**: The offline-first rule where the UI layer observes **only** the local database (`Room` `Flow<List<T>>`). Network responses never feed directly to the UI; instead, synchronization writes into `Room` via `@Upsert`, which reactively notifies all collectors.
- **Mutex Token Refresh**: Using `kotlinx.coroutines.sync.Mutex` (`mutex.withLock`) inside an HTTP `Authenticator`/interceptor and re-checking whether the access token was already refreshed by an earlier concurrent 401 request before issuing a network refresh call.
- **Room `InvalidationTracker`**: SQLite table-level trigger system managed by Room that monitors `@Upsert`/`@Insert`/`@Update`/`@Delete` transactions and re-emits updated query results on any active `Flow<T>` observing the modified tables.
- **Proto DataStore vs. Preferences DataStore**: Asynchronous, transactional replacements for `SharedPreferences` (which blocks the main thread on `apply()` during `Activity.onStop()`, causing ANRs). Proto DataStore enforces compile-time schema safety via Protocol Buffers/kotlinx.serialization.
- **Jetpack WorkManager (`CoroutineWorker`)**: Persistent, constraint-aware background execution engine guaranteed to run across process death and device reboots, supporting `Constraints` (`NetworkType.UNMETERED`, `requiresBatteryNotLow`) and `setExpedited()` for time-critical syncs.

---

## 5. Multi-Module Build Engineering, Profiling & Security (Phases V–VI)

- **Feature vs. Core Module Dependency Rule & ABI Compilation Avoidance**: `:feature:*` modules depend **only** on `:core:*` modules (`:core:data`, `:core:domain`, `:core:designsystem`, `:core:model`) and **never** on sibling `:feature:*` modules. Using `implementation` instead of `api` hides transitive dependencies from consumers' compile classpaths so internal changes do not alter the module's public ABI or trigger downstream recompilation.
- **Gradle Convention Plugins (`build-logic`), Project Isolation & Configuration Cache**: Reusable Kotlin DSL plugins (`AndroidLibraryComposeConventionPlugin`) in an included build that centralize `compileSdk`, Kotlin compiler options, Compose flags, and Detekt/Lint config across 20+ modules, enabling parallel Project Isolation and serialized Configuration Cache task graphs.
- **Baseline Profiles, Macrobenchmark, `ReportDrawnWhen` & Jetpack App Startup**: Ahead-of-Time (AOT) compilation profiles (`baseline-prof.txt`) generated via `BaselineProfileRule` that instruct ART/Cloud Profiles to pre-compile critical startup and scrolling code paths at install time, paired with `ReportDrawnWhen` for accurate Time-To-Full-Display (TTFD) tracking and `androidx.startup.Initializer` to eliminate cold-start `ContentProvider` bloat.
- **R8 Full Mode (`android.enableR8.fullMode=true`)**: Aggressive whole-program shrinking, tree-shaking, class merging, and obfuscation; requires explicit `-keep` rules only for reflection or JNI entry points (largely avoided when using KSP and `kotlinx.serialization`).
- **Android Keystore (`StrongBox` / TEE) & `BiometricPrompt.CryptoObject`**: Hardware-backed cryptographic key storage where private/AES keys never enter application process RAM in plaintext and require biometric user presence at the hardware gate to unlock a `Cipher`.

---

## 6. 2027 Senior Mandates, Observability, KMP & System Surfaces (Phase IX)

- **16 KB ELF Memory Page Alignment**: Hard Google Play mandate (effective Feb 1, 2027) requiring all native `.so` shared libraries (`lib/arm64-v8a/*.so`) to align `LOAD` segments on `16384`-byte (`0x4000`) boundaries (`-Wl,-z,max-page-size=16384` / NDK r28+) so 16 KB kernel devices do not throw `UnsatisfiedLinkError`.
- **Android 17 Large-Screen Adaptive Mandate**: Removes manifest opt-outs (`screenOrientation="portrait"`, `resizeableActivity="false"`) on displays with smallest width $\ge 600\text{dp}$ (tablets, foldables, desktop windowing), requiring responsive layouts driven by `WindowSizeClass`.
- **`PredictiveBackHandler`**: Compose gesture-driven back navigation hook that streams back-swipe progress (`Flow<BackEventCompat>`) on Android 15/16/17 (`enableOnBackInvokedCallback="true"`), allowing live preview animations before committing or cancelling navigation.
- **`CredentialManager` & Passkeys**: Unified Android authentication API supporting FIDO2/WebAuthn Passkeys, passwords, and federated sign-in (`Sign in with Google`) in a single system bottom sheet.
- **Play Integrity API**: Hardware-backed device and binary attestation service where a nonce-bound integrity verdict is requested on the client and decrypted/verified on your backend server to block tampered APKs, emulators, and replay attacks.
- **`ApplicationExitInfo`**: Forensic OS API (`ActivityManager.getHistoricalProcessExitReasons()`) queried on the next app launch to diagnose silent process terminations that crash reporters miss (`REASON_ANR`, `REASON_LOW_MEMORY`, `REASON_CRASH_NATIVE`, `REASON_EXCESSIVE_RESOURCE_USAGE`).
- **Kotlin Multiplatform (`:shared` / `commonMain`)**: Sharing pure Kotlin domain and data layers (`Ktor`, `Room KMP` with `BundledSQLiteDriver`, `DataStore`, `Koin`) compiled to JVM bytecode on Android and native ARM64 binaries via Kotlin/Native (`SKIE` / Swift interop) on iOS.
- **Jetpack Media3 (`ExoPlayer`) & `CameraX`**:
  - **Media3**: Unified media playback architecture supporting single-player pooling, `CacheDataSource` pre-warming for vertical video feeds, and background `MediaSessionService`.
  - **CameraX**: Lifecycle-aware camera pipeline (`Preview`, `ImageCapture`, `ImageAnalysis`, `VideoCapture` UseCases) handling device-specific Camera2 quirks and HDR automatically.
- **Jetpack Glance, FCM Data Push & Android 16/17 Live Updates**:
  - **Glance**: Declarative framework (`GlanceAppWidget`) that translates a special `androidx.glance` Composition tree into system `RemoteViews` for home-screen and lock-screen widgets.
  - **Live Updates (Promoted Ongoing Notifications)**: Android 16/17 high-visibility lock-screen and status-bar progress notifications (`Notification.ProgressStyle`) for rideshare, delivery, navigation, and live financial alerts, driven in the background by FCM data-only payloads (`FirebaseMessagingService.onMessageReceived`).
