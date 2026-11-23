/* ==========================================================================
   Testing Mastery 2027 — Master Curriculum & Legend Data
   Three-Track Roadmap:
     Part 1  (0001-0013): Senior Android Developer Quality Track + Project 1
     Part 1A (0027-0031): Senior Backend Developer (Spring Boot Java & Kotlin) + Project 1A
     Part 2  (0014-0026): Senior Testing Engineer (SDET) Track + Capstone Project 2
   ========================================================================== */

window.CURRICULUM_DATA = {
  legend: {
    layers: [
      { id: "unit", label: "UNIT", chipClass: "chip-unit", desc: "Fastest JVM/in-memory tests verifying single functions, ViewModels, or domain rules in milliseconds." },
      { id: "int", label: "INT / CONTRACT", chipClass: "chip-int", desc: "Component boundaries: Room DB, MockWebServer, Hilt DI, Robolectric, Testcontainers, and Pact contracts." },
      { id: "e2e", label: "UI / E2E", chipClass: "chip-e2e", desc: "Full user journeys across Compose, Espresso, Kaspresso, Maestro, Appium 2/3, and Playwright Web E2E." },
      { id: "nfr", label: "NON-FUNCTIONAL", chipClass: "chip-nfr", desc: "Performance, Macrobenchmark, k6 load testing, accessibility, memory leaks, and OWASP MASVS security." },
      { id: "ops", label: "STRATEGY / OPS", chipClass: "chip-ops", desc: "CI/CD sharding, deflaking, test architecture, AI-age evals, telemetry, and Senior SDET leadership." }
    ],
    paradigms: [
      { name: "White-Box", meaning: "Testing with full knowledge of source code, branches, loops, coverage, and mutation survival." },
      { name: "Black-Box", meaning: "Testing solely from requirements/specs via Equivalence Partitioning, Boundary Values, Decision Tables, and State Transitions." },
      { name: "Gray-Box", meaning: "Combining black-box flows with internal visibility (intercepting network calls, seeding DBs, inspecting logs/tokens)." },
      { name: "Shift-Left", meaning: "Preventing defects early via testable architecture, TDD, contracts, PR quality gates, and static analysis." },
      { name: "Shift-Right", meaning: "Validating quality in production using synthetic checks, canary releases, feature flags, and crash/ANR telemetry." }
    ]
  },

  toolingRadar: [
    { tool: "JUnit 5 + Truth / Kotest", category: "Unit & Assertions", status: "CORE 2027", verdict: "Must-Master", notes: "Industry standard for JVM unit tests. JUnit 5 @Nested, @ParameterizedTest, and Extensions replace legacy JUnit 4 boilerplate." },
    { tool: "MockK + Turbine", category: "Test Doubles & Async", status: "CORE 2027", verdict: "Must-Master", notes: "MockK is idiomatic Kotlin mocking (coroutines, objects, relaxed mocks). Turbine makes testing Kotlin Flows deterministic." },
    { tool: "Compose UI Test + Espresso + Kaspresso", category: "Native Android UI", status: "CORE 2027", verdict: "Must-Master", notes: "ComposeTestRule handles declarative UI via semantics trees; Espresso/UIAutomator/Kaspresso cover XML Views, permissions, and system dialogs." },
    { tool: "Paparazzi / Roborazzi / Compose Preview", category: "Visual Regression", status: "RISING FAST", verdict: "High ROI", notes: "JVM-speed screenshot diffing (plus Google's Compose Preview Screenshot Testing) without booting slow emulators." },
    { tool: "Spring Boot Slices + @ServiceConnection", category: "Backend (Java & Kotlin)", status: "CORE 2027", verdict: "Must-Master", notes: "@WebMvcTest (MockMvcTester), @MockitoBean/@MockkBean, @DataJpaTest + Flyway Postgres, Awaitility, Kafka DLT, and ArchUnit." },
    { tool: "Maestro", category: "Mobile E2E", status: "HOT 2027", verdict: "Adopt", notes: "Declarative YAML flows with built-in flakiness tolerance for iOS & Android. Rapidly replacing brittle UI scripts for smoke E2E." },
    { tool: "Appium 2.x / 3.x", category: "Cross-Platform Mobile E2E", status: "ENTERPRISE STAPLE", verdict: "Know When to Use", notes: "Still heavily used in 2027 for cross-platform iOS+Android suites, React Native/Flutter, and cloud device farms (BrowserStack/Sauce Labs)." },
    { tool: "Playwright (TypeScript)", category: "Web & API E2E", status: "CORE 2027", verdict: "Must-Master", notes: "Dominant cross-browser E2E + API automation framework. Auto-waiting, Trace Viewer, and network interception set the bar for SDETs." },
    { tool: "MockWebServer + Testcontainers + Pact", category: "Integration & Contract", status: "CORE 2027", verdict: "Must-Master", notes: "Fake HTTP servers, real Dockerized DBs/services, and consumer-driven contracts eliminating 'mocked everything, broke in staging'." },
    { tool: "Macrobenchmark + k6 + OWASP MASVS", category: "Perf, Load & Security", status: "SENIOR DIFFERENTIATOR", verdict: "Learn Deeply", notes: "Startup latency, frame jank, backend load thresholds, and mobile auth/storage/deep-link security." },
    { tool: "AI Test Tools & LLM Evals", category: "2027 AI Era", status: "EMERGING STANDARD", verdict: "Critical Literacy", notes: "Self-healing locators, AI-assisted test generation, and writing deterministic eval harnesses for non-deterministic AI features." },
    { tool: "OpenTelemetry + Ephemeral Envs", category: "Shift-Right & Scale", status: "SENIOR DIFFERENTIATOR", verdict: "Must-Know", notes: "Trace/span assertions, synthetic canary rollback gates, and PR-scoped preview environments for flake-free E2E." }
  ],

  phases: [
    {
      id: "phase-1",
      part: "part1",
      title: "PART 1 — Phase 1: Foundations, Design Techniques & Testable Architecture",
      subtitle: "Senior Developer Foundation: stop guessing test cases and build testable Kotlin modules.",
      targetLevel: "Part 1: Junior -> Mid Dev",
      lessons: [
        {
          id: "lesson-01",
          num: "01",
          title: "Testing Foundations, Shift-Left, Test Pyramid vs Trophy & Bulletproof Anatomy",
          file: "lessons/0001-testing-foundations-and-test-anatomy.html",
          isBenchmark: true,
          layers: ["unit", "int", "e2e"],
          paradigms: ["White-Box", "Black-Box", "Shift-Left"],
          tools: ["JUnit 5", "Kotlin", "JS Test Lab"],
          duration: "35 min",
          summary: "Error vs Fault vs Failure, Test Pyramid vs Trophy, AAA & F.I.R.S.T. principles, Test Oracles, and mutation-verified labs."
        },
        {
          id: "lesson-02",
          num: "02",
          title: "Black-Box Test Design: Equivalence Partitioning, BVA, Decision Tables & State Machines",
          file: "lessons/0002-black-box-test-design.html",
          layers: ["unit", "e2e"],
          paradigms: ["Black-Box"],
          tools: ["Decision Matrices", "Pairwise (PICT)", "BVA"],
          duration: "40 min",
          summary: "Systematically derive minimal high-yield test suites from specs for forms, pricing tiers, state machines, and pairwise combinations."
        },
        {
          id: "lesson-03",
          num: "03",
          title: "White-Box Testing, Coverage Myths (Line vs Branch vs MC/DC) & Mutation Testing",
          file: "lessons/0003-white-box-coverage-and-mutation.html",
          layers: ["unit"],
          paradigms: ["White-Box"],
          tools: ["JaCoCo", "Kover", "Pitest / Stryker"],
          duration: "40 min",
          summary: "Why 95% line coverage still ships bugs, branch/condition coverage, cyclomatic complexity, and mutation testing score."
        },
        {
          id: "lesson-04",
          num: "04",
          title: "Testable Android Architecture: Clean Architecture, DI & Breaking Singletons",
          file: "lessons/0004-testable-android-architecture.html",
          layers: ["unit", "int"],
          paradigms: ["White-Box", "Shift-Left"],
          tools: ["Hilt", "Koin", "Clock/Dispatcher Injection"],
          duration: "45 min",
          summary: "Refactor static singletons, System.currentTimeMillis(), and Android Context coupling into pure, injectable, deterministic Kotlin."
        }
      ]
    },
    {
      id: "phase-2",
      part: "part1",
      title: "PART 1 — Phase 2: Unit Testing, Test Doubles & Async Kotlin",
      subtitle: "Senior Developer Core: fast, isolated, deterministic JVM test suites for modern Kotlin.",
      targetLevel: "Part 1: Mid -> Senior Dev",
      lessons: [
        {
          id: "lesson-05",
          num: "05",
          title: "Modern JUnit 5, Lifecycle Rules, Nested Suites & Fluent Assertions (Truth / Kotest)",
          file: "lessons/0005-junit5-and-fluent-assertions.html",
          layers: ["unit"],
          paradigms: ["White-Box"],
          tools: ["JUnit 5", "Google Truth", "Kotest"],
          duration: "40 min",
          summary: "Parameterized tests, @Nested behavioral grouping, custom JUnit 5 Extensions, soft assertions, and diagnostic failure messages."
        },
        {
          id: "lesson-06",
          num: "06",
          title: "Test Doubles Deep Dive: Dummy, Stub, Spy, Fake vs Mock (MockK & Anti-Patterns)",
          file: "lessons/0006-test-doubles-and-mockk.html",
          layers: ["unit"],
          paradigms: ["White-Box"],
          tools: ["MockK", "In-Memory Fakes", "Spies"],
          duration: "45 min",
          summary: "When to choose a Fake Repository vs MockK coEvery/verify, and how to prevent brittle mock-heavy tests."
        },
        {
          id: "lesson-07",
          num: "07",
          title: "Testing Kotlin Coroutines, Dispatchers, TestScope, StateFlow & Turbine",
          file: "lessons/0007-coroutines-flows-and-turbine.html",
          layers: ["unit", "int"],
          paradigms: ["White-Box"],
          tools: ["kotlinx-coroutines-test", "Turbine", "TestDispatcher"],
          duration: "50 min",
          summary: "Control virtual time with runTest/advanceUntilIdle, test ViewModel StateFlow emissions cleanly, and catch coroutine leaks."
        },
        {
          id: "lesson-08",
          num: "08",
          title: "TDD (Red-Green-Refactor), BDD & Property-Based Testing",
          file: "lessons/0008-tdd-bdd-and-property-testing.html",
          layers: ["unit"],
          paradigms: ["Black-Box", "White-Box", "Shift-Left"],
          tools: ["Kotest Property", "Given-When-Then", "Invariants"],
          duration: "45 min",
          summary: "Practice Red-Green-Refactor, write high-signal BDD specs, and fuzz 1,000+ inputs automatically using algebraic invariants."
        }
      ]
    },
    {
      id: "phase-3",
      part: "part1",
      title: "PART 1 — Phase 3: Android Integration, Compose UI, Snapshots & Part 1 Project",
      subtitle: "Complete Senior Developer Quality Suite for Android + Portfolio Project 1.",
      targetLevel: "Part 1: Senior Android Dev",
      lessons: [
        {
          id: "lesson-09",
          num: "09",
          title: "Android Component Integration: Robolectric, Room, DataStore, Paging & Hilt",
          file: "lessons/0009-android-integration-robolectric-room.html",
          layers: ["int"],
          paradigms: ["Gray-Box"],
          tools: ["Robolectric", "Room Migrations", "DataStore / Paging 3", "WorkManager & Hilt"],
          duration: "50 min",
          summary: "Verify SQL queries, Room migrations, Flow DAOs, DataStore, Paging 3 snapshots, Hilt @TestInstallIn, and WorkManager reliably."
        },
        {
          id: "lesson-10",
          num: "10",
          title: "API & Network Layer Testing: OkHttp MockWebServer, Retrofit/Ktor & Error Parsing",
          file: "lessons/0010-mockwebserver-and-network-testing.html",
          layers: ["int"],
          paradigms: ["Gray-Box", "Black-Box"],
          tools: ["OkHttp MockWebServer", "Retrofit/Ktor MockEngine", "HTTP 401/429"],
          duration: "45 min",
          summary: "Test token refresh interceptors, HTTP 429 backoff, malformed JSON, timeouts, and pagination without flaky live servers."
        },
        {
          id: "lesson-11",
          num: "11",
          title: "Jetpack Compose UI Testing: ComposeTestRule, Semantics & TestNavHostController",
          file: "lessons/0011-compose-ui-testing.html",
          layers: ["e2e", "int"],
          paradigms: ["Gray-Box", "Black-Box"],
          tools: ["ComposeTestRule", "SemanticsMatcher", "TestNavHostController", "waitUntil"],
          duration: "50 min",
          summary: "Test Compose screens, state hoisting, Navigation routes, lazy lists, and clock-driven animations using accessibility semantics."
        },
        {
          id: "lesson-12",
          num: "12",
          title: "Visual & Screenshot Regression at JVM Speed (Roborazzi, Paparazzi & Compose Preview)",
          file: "lessons/0012-screenshot-testing-paparazzi-roborazzi.html",
          layers: ["int", "e2e"],
          paradigms: ["Black-Box"],
          tools: ["Roborazzi", "Paparazzi", "Compose Preview Screenshot", "Dark/RTL/Font Matrix"],
          duration: "45 min",
          summary: "Catch clipped text, broken dark mode, and RTL layout regressions in seconds on JVM without booting an emulator."
        },
        {
          id: "project-1",
          num: "PROJ 1",
          title: "PART 1 PROJECT: 'PulseCart Android' — Senior Developer Quality Suite",
          file: "lessons/0013-project-1-senior-dev-suite.html",
          isProject: true,
          layers: ["unit", "int", "e2e"],
          paradigms: ["White-Box", "Black-Box", "Gray-Box", "Shift-Left"],
          tools: ["JUnit 5 + MockK + Turbine", "Room + MockWebServer", "ComposeTestRule + Roborazzi", "Pitest/Kover"],
          duration: "Project (4-6 hrs)",
          summary: "Milestone 1: Architect and verify a production-ready Android Checkout module suite strong enough for Senior Android Developer interviews."
        }
      ]
    },
    {
      id: "phase-3a",
      part: "part1a",
      title: "PART 1A — Senior Backend Developer Track (Spring Boot with Java & Kotlin)",
      subtitle: "Master backend quality architecture: test slices, @ServiceConnection Postgres, Security, Kafka & concurrency.",
      targetLevel: "Part 1A: Senior Backend Dev",
      lessons: [
        {
          id: "lesson-0027",
          num: "1A.1",
          title: "Spring Boot Test Architecture, Context Caching & @WebMvcTest (MockMvcTester)",
          file: "lessons/0027-spring-boot-unit-and-slices.html",
          layers: ["unit", "int"],
          paradigms: ["White-Box", "Gray-Box"],
          tools: ["JUnit 5", "@WebMvcTest / MockMvcTester", "@MockitoBean / @MockkBean", "@RestartScope"],
          duration: "50 min",
          summary: "Stop booting @SpringBootTest everywhere: isolate pure services, validate DTOs/ControllerAdvice with MockMvcTester, and eliminate context-cache misses."
        },
        {
          id: "lesson-0028",
          num: "1A.2",
          title: "Spring Data JPA, Flyway, N+1 Queries, Redis & @ServiceConnection Testcontainers",
          file: "lessons/0028-spring-data-jpa-flyway-testcontainers.html",
          layers: ["int"],
          paradigms: ["Gray-Box"],
          tools: ["@DataJpaTest", "@ServiceConnection", "Postgres & Redis Containers", "Flyway"],
          duration: "50 min",
          summary: "Ditch H2 illusions: test real Postgres constraints, Flyway migrations, Redis caching, Hibernate L1 flush/clear, and SQL N+1 query ceilings."
        },
        {
          id: "lesson-0029",
          num: "1A.3",
          title: "RestClient / WebClient, WireMock, Spring Security JWT, Awaitility & Kafka DLT",
          file: "lessons/0029-spring-webclient-kafka-async.html",
          layers: ["int", "nfr"],
          paradigms: ["Gray-Box", "Black-Box"],
          tools: ["@RestClientTest / WebTestClient", "WireMock", "Security jwt()", "Awaitility & Kafka DLT"],
          duration: "55 min",
          summary: "Verify 401/403 RBAC filters, outbound HTTP adapters (RestClient/WebClient/WireMock), @Async polling without sleeps, and Kafka DLT routing."
        },
        {
          id: "lesson-0030",
          num: "1A.4",
          title: "Spring Transactions, Optimistic @Version Races, ArchUnit & Contracts",
          file: "lessons/0030-spring-concurrency-transactions-and-perf.html",
          layers: ["int", "nfr"],
          paradigms: ["White-Box", "Gray-Box"],
          tools: ["CountDownLatch", "@Version Optimistic Lock", "ArchUnit", "Pact / OpenAPI"],
          duration: "55 min",
          summary: "Expose hidden LazyInitializationException and self-invocation traps, enforce architecture with ArchUnit, and race-test @Version locking."
        },
        {
          id: "project-1a",
          num: "PROJ 1A",
          title: "PART 1A PROJECT: 'PulseOrder Spring Boot' — Senior Backend Developer Quality Suite",
          file: "lessons/0031-project-1a-senior-backend-suite.html",
          isProject1A: true,
          layers: ["unit", "int", "nfr"],
          paradigms: ["White-Box", "Black-Box", "Gray-Box", "Shift-Left"],
          tools: ["Java 21 & Kotlin", "@WebMvcTest + Security", "@DataJpaTest + Postgres + Flyway", "Kafka DLT + @Version + Pitest"],
          duration: "Project (4-6 hrs)",
          summary: "Milestone 1A: Deliver a Senior Backend Developer quality suite across Spring Boot controllers, real Postgres/Flyway, Kafka webhooks, and concurrency."
        }
      ]
    },
    {
      id: "phase-4",
      part: "part2",
      title: "PART 2 — Phase 4: Distributed Contracts, Chaos & Multi-Platform E2E",
      subtitle: "Transition to Senior Testing Engineer (SDET): own backend, mobile, and web E2E.",
      targetLevel: "Part 2: Senior SDET Core",
      lessons: [
        {
          id: "lesson-14",
          num: "14",
          title: "True Integration with Testcontainers & Consumer-Driven Contracts with Pact",
          file: "lessons/0014-testcontainers-and-pact-contracts.html",
          layers: ["int"],
          paradigms: ["Gray-Box", "Shift-Left"],
          tools: ["Testcontainers (Docker)", "Pact Broker", "OpenAPI"],
          duration: "50 min",
          summary: "Eliminate 'mocked everything, broke in staging' bugs with ephemeral Docker DBs and Pact contracts between Android & backend."
        },
        {
          id: "lesson-15",
          num: "15",
          title: "Offline-First Sync, Caching Conflicts, Idempotency & Chaos / Fault Injection",
          file: "lessons/0015-offline-sync-and-chaos-testing.html",
          layers: ["int", "e2e"],
          paradigms: ["Gray-Box", "Black-Box"],
          tools: ["Toxiproxy", "Idempotency Keys", "Retry/Conflict Matrix"],
          duration: "45 min",
          summary: "Verify airplane mode mid-checkout, duplicate retry safety, stale cache invalidation, and out-of-order server responses."
        },
        {
          id: "lesson-16",
          num: "16",
          title: "Espresso, UI Automator & Kaspresso: Hybrid Views, Permissions & System Dialogs",
          file: "lessons/0016-espresso-uiautomator-kaspresso.html",
          layers: ["e2e"],
          paradigms: ["Black-Box", "Gray-Box"],
          tools: ["Espresso", "UI Automator", "Kaspresso", "IdlingResource"],
          duration: "45 min",
          summary: "Automate legacy XML screens, runtime OS permission dialogs, WebViews, and cross-app intents cleanly with Kaspresso."
        },
        {
          id: "lesson-17",
          num: "17",
          title: "Cross-Platform Mobile E2E: Appium 2/3 (UiAutomator2/XCUITest) vs Maestro",
          file: "lessons/0017-appium-vs-maestro-mobile-e2e.html",
          layers: ["e2e"],
          paradigms: ["Black-Box"],
          tools: ["Appium 2/3", "WebdriverIO Page Objects", "Maestro YAML"],
          duration: "55 min",
          summary: "Build both an Appium Screenplay/POM flow and a Maestro YAML flow, and master the 2027 architectural tradeoff between them."
        },
        {
          id: "lesson-18",
          num: "18",
          title: "Modern Web & Full-Stack E2E with Playwright (TypeScript, Auto-Wait & Trace Viewer)",
          file: "lessons/0018-playwright-web-and-api-e2e.html",
          layers: ["e2e", "int"],
          paradigms: ["Black-Box", "Gray-Box"],
          tools: ["Playwright", "TypeScript", "HAR Mocking", "StorageState"],
          duration: "55 min",
          summary: "Automate web dashboards and REST APIs with Playwright's auto-waiting locators, route interception, and time-travel traces."
        }
      ]
    },
    {
      id: "phase-5",
      part: "part2",
      title: "PART 2 — Phase 5: Non-Functional Quality & 2027 AI-Era Testing",
      subtitle: "Specialized Senior SDET domains: Accessibility, Performance/Load, Security & AI Evals.",
      targetLevel: "Part 2: Senior SDET Specialist",
      lessons: [
        {
          id: "lesson-19",
          num: "19",
          title: "Accessibility Testing: TalkBack, Compose Semantics, Contrast & Axe Audits",
          file: "lessons/0019-accessibility-testing-a11y.html",
          layers: ["nfr", "e2e"],
          paradigms: ["Black-Box", "White-Box"],
          tools: ["AccessibilityChecks", "axe-core + Playwright", "48dp / Contrast"],
          duration: "40 min",
          summary: "Automate WCAG 2.2 contrast, 48dp touch-target checks, screen-reader traversal order, and 200% font scaling gates in CI."
        },
        {
          id: "lesson-20",
          num: "20",
          title: "Performance, Jank & Load Testing: Macrobenchmark, LeakCanary & Grafana k6",
          file: "lessons/0020-performance-macrobenchmark-and-k6.html",
          layers: ["nfr"],
          paradigms: ["Gray-Box", "Black-Box"],
          tools: ["Jetpack Macrobenchmark", "Baseline Profiles", "LeakCanary", "k6"],
          duration: "50 min",
          summary: "Measure cold startup & frame jank (P95/P99), detect memory leaks, and gate backend endpoints with Grafana k6 load thresholds."
        },
        {
          id: "lesson-21",
          num: "21",
          title: "Mobile & API Security Testing: OWASP MASVS, AuthZ/IDOR, Pinning & Deep Links",
          file: "lessons/0021-security-testing-owasp-masvs.html",
          layers: ["nfr", "int"],
          paradigms: ["Black-Box", "Gray-Box"],
          tools: ["OWASP MASVS/MASTG", "JWT/BOLA Checks", "Deep-Link Fuzzing"],
          duration: "50 min",
          summary: "Test exported Android components, deep-link injection, broken object-level authorization (IDOR/BOLA), and EncryptedSharedPreferences."
        },
        {
          id: "lesson-22",
          num: "22",
          title: "Testing in the AI Era: Evaluating LLMs/RAG & Using AI Safely in QA",
          file: "lessons/0022-ai-testing-and-llm-evals.html",
          layers: ["ops", "nfr"],
          paradigms: ["Black-Box", "Shift-Right"],
          tools: ["LLM Eval Rubrics", "Schema/Grounding Checks", "Self-Healing Critique"],
          duration: "45 min",
          summary: "Build deterministic evaluation harnesses for non-deterministic AI features and recognize where AI-generated tests create illusion."
        }
      ]
    },
    {
      id: "phase-6",
      part: "part2",
      title: "PART 2 — Phase 6: Scale, CI/CD, SDET Leadership & Part 2 E2E Capstone",
      subtitle: "Lead org-wide quality engineering and ship your Senior SDET Portfolio Capstone.",
      targetLevel: "Part 2: Senior / Staff SDET",
      lessons: [
        {
          id: "lesson-23",
          num: "23",
          title: "Deflaking & Determinism at Scale: 7 Flake Root Causes, Quarantine & Flake SLOs",
          file: "lessons/0023-deflaking-and-determinism-at-scale.html",
          layers: ["ops", "e2e"],
          paradigms: ["Gray-Box", "Shift-Left"],
          tools: ["Android Test Orchestrator", "Data Factories", "Flake Rate SLOs"],
          duration: "45 min",
          summary: "Root-cause async races, shared DB state, animations, and external API flakes, and enforce <1% flake SLOs with quarantine policies."
        },
        {
          id: "lesson-24",
          num: "24",
          title: "CI/CD Test Orchestration: GitHub Actions, Sharding (Flank) & Device Farms",
          file: "lessons/0024-cicd-sharding-and-device-farms.html",
          layers: ["ops"],
          paradigms: ["Shift-Left", "Shift-Right"],
          tools: ["GitHub Actions", "Flank / Firebase Test Lab", "Allure / Gradle Managed Devices"],
          duration: "50 min",
          summary: "Architect a 3-tier CI pipeline: <5m PR checks, parallel sharded device matrices, and nightly E2E + visual + load suites."
        },
        {
          id: "lesson-25",
          num: "25",
          title: "Senior SDET Leadership: Test Strategy RFCs, Risk Matrices & Shift-Right Telemetry",
          file: "lessons/0025-senior-sdet-leadership-and-strategy.html",
          layers: ["ops"],
          paradigms: ["Shift-Left", "Shift-Right"],
          tools: ["Risk Matrices", "DORA Metrics", "OpenTelemetry & Canary Gates"],
          duration: "50 min",
          summary: "Author high-impact Test Strategy RFCs, enforce OpenTelemetry/synthetic Shift-Right gates, and ace Senior SDET system design loops."
        },
        {
          id: "project-2",
          num: "CAPSTONE",
          title: "PART 2 CAPSTONE: 'ShopPulse' Full-Stack End-to-End Quality Engineering Platform",
          file: "lessons/0026-project-2-sdet-e2e-capstone.html",
          isCapstone: true,
          layers: ["unit", "int", "e2e", "nfr", "ops"],
          paradigms: ["White-Box", "Black-Box", "Gray-Box", "Shift-Left", "Shift-Right"],
          tools: ["Kotlin + Compose + Roborazzi", "Testcontainers + Pact", "Maestro + Appium 2 + Playwright", "k6 + OWASP + Sharded GitHub Actions"],
          duration: "Capstone (10-15 hrs)",
          summary: "Final Milestone: End-to-end multi-platform portfolio quality suite (Android + API + Web + CI/CD + Resume & Interview Defense Kit)."
        }
      ]
    }
  ]
};
