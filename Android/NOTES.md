# Workspace Notes & Pedagogical Strategy

## Learner Profile & Objectives
- **Target**: High-Impact Modern Android Engineer (2026/2027 standards).
- **Background**: Junior Android Developer new to the industry. Calibrate lessons with clear mental models, visual diagrams, explicit explanations of Android internals and terminology, and clear anti-patterns ("Junior Pitfall" vs. "Production Standard").
- **Learning Mode**: 100% Upfront generation & verified curriculum (Option B).
- **Outcome**: Job-readiness at top tech companies, senior architectural foundations, resume-grade capstone project ("Aether Portfolio").
- **Learning Principles**: High-retention pedagogy (fluency vs storage strength), self-contained interactive HTML lessons with runnable code examples, comment-stripped verification IDEs, progressive hints, reference answers with explanation, and multi-question code quizzes with full explanations of both correct and wrong answers.

## Modern Tech Stack Standard (2026/2027)
- **Language**: Kotlin 2.x (K2 Compiler, Context Parameters, Sealed Hierarchies, Type-safe Builders)
- **UI Engine**: Jetpack Compose (100% Declarative, Material 3 Expressive, Composition Optimization, Strong Skipping Mode)
- **Edge-to-Edge & Insets**: Mandatory `enableEdgeToEdge()`, `WindowInsets.safeDrawing`, `PredictiveBackHandler`
- **Graphics & Gestures**: Compose `Canvas` (`DrawScope`) & `Modifier.pointerInput` (`detectDragGestures`)
- **Accessibility**: Compose `Semantics` & TalkBack optimization
- **Navigation**: Jetpack Navigation 3 (`androidx.navigation3` user-owned back stack & adaptive multi-pane) alongside Navigation Compose 2.8+ Type-Safe Navigation
- **Architecture**: Modern Android Architecture (Now In Android style, Unidirectional Data Flow, Clean Architecture Layering, Multi-Module)
- **Asynchronous & Reactive**: Kotlin Coroutines & Flow (Structured concurrency, StateFlow, SharedFlow, Lifecycle-aware collection)
- **Dependency Injection**: Dagger Hilt with KSP (Kotlin Symbol Processing)
- **Networking & Serialization**: Retrofit / Ktor Client + `kotlinx.serialization` (JSON, streaming, HTTP/3)
- **Pagination & Media**: Jetpack Paging 3 + Coil 3 Coroutine Image Loader
- **Local Persistence**: Room (KSP, reactive flows, migrations) + Jetpack DataStore (Preferences & Proto)
- **Background Tasks**: Jetpack WorkManager (CoroutineWorker, expedited sync, battery-conscious constraints)
- **Testing**: JUnit 5, MockK, Turbine, Robolectric, Compose Test Rule, Maestro E2E
- **Build & Optimization**: Gradle Version Catalogs (`libs.versions.toml`), Kotlin DSL (`build.gradle.kts`), Convention Plugins (`build-logic`), Baseline Profiles, R8 Full Mode

## Current Progress & Curriculum State
- **Master Plan**: 60 Lessons across 8 Phases fully structured in `MASTER_PLAN.md` and `MASTER_PLAN.html`.
- **Reference Cheat Sheets**:
  1. `reference/01-kotlin-coroutines-flow.html`
  2. `reference/02-jetpack-compose-m3.html`
  3. `reference/03-modern-android-architecture.html`
- **Interactive Engine v2**: Universal JS & CSS in `assets/lesson-interactives.js` and `assets/interactives.css` supporting runnable code examples, comment-stripped verification IDEs, progressive hints, solution injection, and dual quizzes (code reading + architectural) with explanations for every choice.
