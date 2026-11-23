# Project 1 Starter Blueprint — PulseCart Android (Part 1 Milestone)

Companion starter guide for [Lesson 0013: Project 1 — PulseCart Android](../../lessons/0013-project-1-senior-dev-suite.html).

## Target Module Layout

```text
pulsecart-android/
  gradle/libs.versions.toml
  feature-cart/
    build.gradle.kts                  # JUnit 5, Kover (85% branch), Pitest, Roborazzi
    src/main/kotlin/
      domain/CalculateCartUseCase.kt  # Pure Kotlin + injected java.time.Clock
      data/CartRepositoryImpl.kt      # Room + Retrofit + 401 Authenticator
      ui/CartViewModel.kt             # StateFlow + injected DispatcherProvider
      ui/CartScreen.kt                # Stateless hoisted Compose UI + semantics
    src/testFixtures/kotlin/
      FakeCartRepository.kt           # Shared across :feature-* modules
    src/test/kotlin/
      domain/CalculateCartUseCaseTest.kt
      domain/CalculateCartPropertyTest.kt
      ui/CartViewModelTest.kt         # StandardTestDispatcher + Turbine
      data/CartApiMockWebServerTest.kt
      ui/CartSnapshotTest.kt          # Roborazzi Light / Dark / 2.0x Font / RTL
    src/androidTest/kotlin/
      data/CartDbMigrationTest.kt     # Room MigrationTestHelper v1 -> v2
      ui/CartNavAndUiTest.kt          # ComposeTestRule + TestNavHostController
```

## Verification Checklist Before Marking Part 1 Complete

1. **Pure Domain + Mutation Gate:** `./gradlew :feature-cart:pitest` achieves **>= 90% mutation score** on `domain.*`.
2. **Async ViewModel:** `CartViewModelTest` uses `StandardTestDispatcher` + Turbine `awaitItem()` (zero `Thread.sleep`).
3. **Network & Persistence:** `CartApiMockWebServerTest` covers `200`, `401 -> /refresh`, `429 Retry-After`, and `502` HTML; `CartDbMigrationTest` validates v1 -> v2 with `MigrationTestHelper`.
4. **Compose UI & Visual Matrix:** `CartScreenTest` uses accessibility semantics (`hasRole` / `onNodeWithText`) + `TestNavHostController`, and `verifyRoborazziDebug` covers Light, Dark, 200% font scale, and RTL.
