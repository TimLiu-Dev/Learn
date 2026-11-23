# Software Testing & SDET Engineering Resources

## Knowledge

- [Guide: _Fundamentals of Testing Android Apps_ — Android Developers](https://developer.android.com/training/testing/fundamentals)
  Official Android documentation on test scopes (small/medium/big), local JVM vs instrumented execution (`src/test` vs `src/androidTest`), and what to test. Use for: Android test pyramid architecture and test doubles strategy.
- [Article: "The Practical Test Pyramid" — Ham Vocke (martinfowler.com)](https://martinfowler.com/articles/practical-test-pyramid/)
  Foundational architectural guide to balancing unit, integration, contract, and E2E tests and avoiding the Ice-Cream Cone anti-pattern. Use for: test layer trade-offs and CI feedback loops.
- [Article: "Write tests. Not too many. Mostly integration." — Kent C. Dodds](https://kentcdodds.com/blog/write-tests)
  Origin of the Testing Trophy philosophy and critique of chasing 100% coverage over behavior confidence. Use for: integration-first ROI and avoiding implementation-coupled tests.
- [Blog: _Google Testing Blog_](https://testing.googleblog.com/)
  Engineering articles from Google on test flakiness, hermetic tests, test sizes, and code health. Use for: real-world patterns on determinism, test doubles, and scaling CI.
- [Docs: _JUnit 5 User Guide_](https://junit.org/junit5/docs/current/user-guide/)
  Primary documentation for JUnit Platform, JUnit Jupiter, `@ParameterizedTest`, `@Nested`, and Extensions. Use for: JVM unit and integration test mechanics.
- [Docs: _MockK Documentation_](https://mockk.io/)
  Official Kotlin mocking library documentation covering `every`, `coEvery`, `verify`, spies, and relaxed mocks. Use for: Kotlin and coroutine test doubles.
- [Repo/Blog: _Cash App Turbine_](https://github.com/cashapp/turbine)
  Official repository and guide for deterministic testing of `kotlinx.coroutines.flow.Flow`. Use for: ViewModel `StateFlow` and reactive stream assertions.
- [Docs: _Playwright Documentation_](https://playwright.dev/)
  Official docs for modern TypeScript/multi-language web & API E2E testing, auto-waiting, network interception, and Trace Viewer. Use for: Web E2E and API integration layers.
- [Docs: _Appium 2/3 Official Documentation_](https://appium.io/docs/latest/)
  Official cross-platform mobile automation docs covering UiAutomator2 and XCUITest drivers. Use for: enterprise cross-platform mobile E2E and device farm automation.
- [Docs: _Maestro Official Documentation_](https://docs.maestro.dev/)
  Official docs for declarative YAML mobile UI E2E flows. Use for: fast, flake-tolerant Android/iOS smoke automation.
- [Docs: _Jetpack Compose Testing_ — Android Developers](https://developer.android.com/jetpack/compose/testing)
  Official guide for `ComposeTestRule`, semantics trees, finders, actions, and synchronization. Use for: Compose UI component & screen tests.
- [Repo: _Roborazzi_ (`takahirom/roborazzi`)](https://github.com/takahirom/roborazzi) &amp; [Repo: _Paparazzi_ (`cashapp/paparazzi`)](https://github.com/cashapp/paparazzi)
  JVM-speed Android visual regression libraries (Robolectric Native Graphics vs `layoutlib`). Use for: snapshot testing Compose/Views without emulators.
- [Repo: _Kaspresso_ (`KasperskyLab/Kaspresso`)](https://github.com/KasperskyLab/Kaspresso)
  Android UI automation wrapper over Espresso and UI Automator with built-in flakiness protection and Kakao DSL. Use for: hybrid View/Compose flows and system dialogs.
- [Docs: _kotlinx-coroutines-test_ — Kotlinlang](https://kotlinlang.org/docs/coroutines-test.html)
  Official guide for `runTest`, `TestDispatcher`, and virtual time scheduling. Use for: deterministic async Kotlin testing.
- [Docs: _Kotest Property-Based Testing_](https://kotest.io/docs/proptest/property-based-testing.html)
  Official guide for `checkAll`/`forAll` generators and shrinking. Use for: fuzzing invariants across thousands of inputs.
- [Docs: _PIT Mutation Testing (Pitest)_](https://pitest.org/)
  Canonical JVM mutation testing engine verifying whether unit test assertions catch injected faults. Use for: measuring true test suite strength.
- [Docs: _Testcontainers_](https://testcontainers.com/)
  Official docs for ephemeral Dockerized databases, Redis, Kafka, and services in integration tests. Use for: true environment integration testing.
- [Docs: _Pact Contract Testing_](https://docs.pact.io/)
  Official guide for consumer-driven contract verification between mobile/web consumers and API providers. Use for: eliminating cross-team API schema regressions.
- [Docs: _Grafana k6 Load Testing_](https://grafana.com/docs/k6/latest/)
  Developer-centric JavaScript/TypeScript load and threshold testing tool. Use for: API stress, soak, and latency P95/P99 verification.
- [Standard: _OWASP MASVS / MASTG_](https://mas.owasp.org/)
  Industry-standard Mobile Application Security Verification Standard &amp; Testing Guide. Use for: Android storage, auth, deep-link, and network security verification.
- [Docs: _Spring Boot Official Testing Guide_](https://docs.spring.io/spring-boot/reference/testing/index.html)
  Canonical Spring Boot reference covering `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`, `@MockitoBean`, and `@ServiceConnection`. Use for: all Part 1A Spring Boot Java &amp; Kotlin slices and container integration.
- [Repo: _SpringMockK (`Ninja-Squad/springmockk`)_](https://github.com/Ninja-Squad/springmockk)
  Idiomatic `@MockkBean` and `@SpykBean` integration for Spring Boot Kotlin tests. Use for: Kotlin Spring Boot test slices.
- [Docs: _Awaitility_](http://www.awaitility.org/)
  Fluent Java &amp; Kotlin DSL for asserting asynchronous `@Async`, `@Scheduled`, and Kafka consumer state safely without `Thread.sleep()`. Use for: Part 1A async &amp; event-driven backend tests.

## Wisdom (Communities)

- [Ministry of Testing (The Club)](https://club.ministryoftesting.com/)
  High-signal practitioner community for QA engineers and SDETs discussing tool migrations, test strategy, and career growth. Use for: peer critique, real-world flakiness war stories, and SDET strategy debates.
- [Android Developers Community / Kotlin Slack (`#testing`)](https://kotlinlang.slack.com/)
  Practitioner channels for Kotlin, Coroutines, Compose UI testing, and Roborazzi/Paparazzi troubleshooting. Use for: Android-specific test harness feedback.

## Gaps

- Add team-specific CI runner benchmarks once the Capstone ("ShopPulse") GitHub Actions workflow is exercised locally or in CI.
