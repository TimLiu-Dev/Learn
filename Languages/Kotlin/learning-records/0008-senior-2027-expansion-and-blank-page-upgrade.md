# Session 0008 · 2027 Senior Kotlin Expansion (`0071`–`0072`) & Blank-Page Authoring Upgrade

**Date:** 2026-11-21  
**Scope:** Full course audit and upgrade for a Junior Android Developer ("can read some Kotlin, unable to write it") targeting **Senior Kotlin Engineer in 2027** across all Four Pillars (Android/Compose, KMP, Spring Boot Backend, and DSA Coding Interviews).

---

## 1. New 2027 Senior Modules Added to Section 13

1. **[Lesson 13.5 · Compose Compiler & Kotlin Runtime Mental Model](../lessons/0071-compose-compiler-runtime-kotlin.html)**
   - **Concepts**: Compose stability (`@Immutable` vs `@Stable`), Kotlin 2.0+ Strong Skipping Mode (`===` referential equality for unstable types vs `==` structural equality for stable types), why in-place `MutableList` mutations break `StateFlow` and Compose diffing, lambda closure capture & `rememberUpdatedState` inside non-restarting `LaunchedEffect(Unit)` coroutines, and `derivedStateOf` vs `snapshotFlow`.
   - **Practice**: 2 runnable `kt-run` demos, Blank-Page Syntax Blueprint, 4 balanced quizzes, `0071-ex1` (Write from blank: `UpdatedStateHolder`, `evaluateRecomposition`, and `simulatedSnapshotFlow`), and `0071-ex2` (Refactor in-place `CartUiState` mutation & stale callback closure capture).

2. **[Lesson 13.6 · Multi-Module Architecture, `explicitApi()` & Pure-Kotlin DI](../lessons/0072-multi-module-explicit-api-di.html)**
   - **Concepts**: Enforcing Gradle multi-module boundaries (`public interface` + `internal class` implementations), `api(...)` vs `implementation(...)` classpath isolation, Kotlin's `kotlin { explicitApi() }` compiler mode (mandatory visibility modifiers and explicit return types on public symbols), and pure-Kotlin Dependency Injection (`AppGraph` composition roots with `by lazy` singletons + `fun interface` assisted factories).
   - **Practice**: 2 runnable `kt-run` demos, Blank-Page Syntax Blueprint, 4 balanced quizzes, `0072-ex1` (Write from blank: `explicitApi()`-compliant `TokenStore`, `internal class InMemoryTokenStore`, `SessionValidatorFactory`, and lazy `AuthGraph`), and `0072-ex2` (Refactor global `StripeGatewaySingleton` service locator into constructor-injected `PaymentGateway`).

---

## 2. Course-Wide Pedagogical & Engine Upgrades ("Reading → Writing From Blank")

- **Automated Verification on All 46 Previously Unchecked `-ex1` ("Write from blank") Exercises**:
  - Populated deterministic `expectedOutput` values across `0001-ex1` through `0054-ex1` in [`assets/answers.js`](../assets/answers.js) (`147 / 147` exercises now verified) and aligned `0001-ex1`, `0003-ex1`, `0020-ex1`, and `0046-ex1` prompts/starters.
  - Upgraded [`assets/lesson.js`](../assets/lesson.js) to detect unmodified starter code on **"▶ Run & Check"** so running an untouched starter stub or unrefactored `-ex2` snippet displays `🚧 Starter code unmodified — write or refactor your solution first` instead of a false green pass.
  - Unified the solution tab label to **`Model Answer`** in [`assets/lesson.js`](../assets/lesson.js).
- **Blank-Page Syntax Blueprints & 40 New Interactive Concept Labs**:
  - Added a `.callout.blueprint` (**"Blank-Page Syntax Blueprint — How to Type It From Memory"**) to all **59 teaching lessons** (`0001`–`0072`).
  - Added **40 live runnable `<code class="kt-run">` Interactive Concept Labs** to Sections 3–11 (`0013`–`0060`), ensuring every teaching lesson in the curriculum has runnable concept code before its quiz and exercises.
- **Section 10 Flow Sequencing, Review Expansion & Quiz Rebalancing**:
  - Added `debounce` + `distinctUntilChanged` + `flatMapLatest` to [`0052-flow-cold-streams.html`](../lessons/0052-flow-cold-streams.html) and `.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)` to [`0053-stateflow-sharedflow-hot-streams.html`](../lessons/0053-stateflow-sharedflow-hot-streams.html) so [`0054-review-coroutines-flow.html`](../lessons/0054-review-coroutines-flow.html) (`10.R`) only tests concepts already taught.
  - Expanded all Review lessons (`0017`, `0025`, `0029`, `0033`, `0040`, `0044`, `0048`, `0054`, `0061`, `0065`, `0070`) to `>= 4` spaced-retrieval recall items (`5` in `0061`) and `>= 4` interleaved quizzes (`5` in `0061` and `0070`).
  - Rebalanced all **17 length-skewed quizzes** across the corpus (`0` remaining skewed quizzes across all `253` quizzes).
  - Kept [`0058-capstone-backend-spring.html`](../lessons/0058-capstone-backend-spring.html) as a full Core Pillar Capstone and unified [`index.html`](../index.html) styling and per-lesson `<span class="goal">` summaries across all **73 lessons**.
