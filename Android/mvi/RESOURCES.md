# MVI Architecture (Android) Resources

## Knowledge

- [Guide to app architecture — developer.android.com](https://developer.android.com/topic/architecture)
  Official Google architecture guidance. Not MVI-branded, but defines the unidirectional-data-flow (UDF) vocabulary (state holder, single source of truth, events-up/state-down) that MVI is built on. Use for: grounding terms like "state holder" and "UDF" in the official, interview-safe vocabulary.
- [UI layer — developer.android.com](https://developer.android.com/topic/architecture/ui-layer)
  Official deep-dive on the UI update loop: events flow up from UI → state holder, state flows down. Use for: the exact mental model MVI's View→Intent→State loop specializes.
- [Reactive Apps with Model-View-Intent (series) — Hannes Dorfmann](https://hannesdorfmann.com/series/reactive-apps-with-model-view-intent/)
  The original, canonical series that introduced MVI to Android (adapted from cycle.js). Primary source for *why* MVI exists, not just how to code it. Use for: Model/Intent/State-reducer reasoning, the single-source-of-truth argument, and the debugging/testability case for MVI over ad-hoc MVVM.
  - [Part 3: State Reducer](https://hannesdorfmann.com/android/mosby3-mvi-3/) — the specific post behind Lesson 2: why the reducer takes a Result (not the raw Intent) and stays a pure function.
- [UI events — developer.android.com](https://developer.android.com/topic/architecture/ui-layer/events)
  Google's official stance on one-off events: "ViewModel events should always result in a UI state update", with the `userMessage` + `userMessageShown()` pattern, and a note warning that Channels/reactive streams don't guarantee delivery. Use for: Lesson 3's "Answer A", which is the interview-safe official position.
- [ViewModel: One-off event antipatterns — Manuel Vivo (Android Developers, Medium)](https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95)
  The longer argument behind Google's guidance, linked from the UI events page. Medium blocks automated fetching, so its content wasn't verified directly and is cited only via the official page.
- [kotlinx.coroutines #2886 — Primitive or Channel that guarantees delivery and processing](https://github.com/Kotlin/kotlinx.coroutines/issues/2886)
  Opened by Manuel Vivo in Aug 2021 and still open. Roman Elizarov explains that a Channel can't guarantee an item is *processed* (receive, then cancel, means a lost event) and gives the `Dispatchers.Main.immediate` workaround. Use for: the precise failure mode of "Answer B".
- [Events as State are an Antipattern — Nek.12 (dev.to)](https://dev.to/nek12/events-as-state-are-an-antipattern-in-mvi-and-mvvm-heres-why-15d2)
  The opposing view, from the FlowMVI ecosystem: modelling events as state causes flag bloat and reset bugs, and a Channel is acceptable. Use for: presenting the trade-off honestly in interviews.
- [5 steps to implement MVI architecture with Jetpack Compose — Kotlin Academy](https://blog.kotlin-academy.com/5-steps-to-implement-mvi-architecture-with-jetpack-compose-383c4f72406f)
  Modern, practical Compose + StateFlow walkthrough. Use for: concrete Kotlin code shape (sealed `Intent`, `data class State`, reducer function) to anchor hands-on exercises.
- [SavedStateHandle — developer.android.com](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate)
  Official process-death guidance: saved state "must be simple and lightweight" (IDs, user input), and you reload everything else. Use for: Lesson 4's process-death answer.
- [MutableStateFlow.update — kotlinx.coroutines API](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/update.html)
  "Updates the value atomically", but the "function may be evaluated multiple times". Use for: why a reducer applied through update must be pure (Lesson 4).
- [Orbit MVI — GitHub (orbit-mvi/orbit-mvi)](https://github.com/orbit-mvi/orbit-mvi)
  A production-grade, widely-used MVI framework for Kotlin/Compose Multiplatform. Use for: the library survey once fundamentals are solid — "what does a real library add on top of the plain pattern" (lifecycle-safe flows, test utilities, plugins).

- [Orbit docs (v12)](https://orbit-mvi.org/) — [Core](https://orbit-mvi.org/Core/), [Compose](https://orbit-mvi.org/Compose/), [Test](https://orbit-mvi.org/Test/)
  Atomic `reduce`, side effects cached with no observer (FAN_OUT by default), and the `expectState`/`expectSideEffect` test DSL. Note the v12 rename to `OrbitContainerHost`/`orbitContainer`. Use for: Lesson 6.
- [Mavericks docs (Airbnb, v3.1)](https://github.com/airbnb/mavericks/tree/main/docs)
  `MavericksState`/`setState { copy() }`, `Async<T>` + `execute`, queued setState threading model, `@PersistState`. (mavericks.airbnb.tech didn't render for fetching, so the raw markdown on GitHub was used.) Use for: Lesson 6.
- [MVIKotlin docs (v4.4)](https://arkivanov.github.io/MVIKotlin/store/)
  Store / Executor / Message / Reducer / Label / Bootstrapper; Kotlin Multiplatform; time-travel debugging. Use for: Lesson 6, as the closest library match to Dorfmann's original pattern.

## Wisdom (Communities)

- [r/androiddev](https://reddit.com/r/androiddev)
  High-signal, well-moderated subreddit for Android engineers. Use for: real debates on MVI vs. MVVM trade-offs, interview-question threads, and sanity-checking opinions against working engineers.
- Kotlin Slack (kotlinlang.slack.com), `#android` channel
  Use for: live, real-time feedback on specific implementation questions from practitioners.
- Orbit channel on Kotlin Slack (https://kotlinlang.slack.com/messages/CPM6UMD2P)
  Use for: questions specific to Orbit, if Tim adopts it.

## Gaps

- No single authoritative source compares MVI vs. MVVM head-to-head with Google's blessing — Google's official docs describe UDF/MVVM, not MVI by name. Lessons will need to synthesize the contrast explicitly, citing the UDF doc + Dorfmann's series together.
