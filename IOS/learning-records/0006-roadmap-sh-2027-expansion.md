# Expansion Record: `roadmap.sh/ios` Audit & 2027 Senior iOS Frontier (Phase 8 + Phase 9)

Audited the live `roadmap.sh/ios` curriculum against our Junior-Android-to-Senior-iOS transition workspace and expanded the syllabus from 49 to **61 lessons** (Phases 1–9) calibrated for the 2027 iOS engineering bar (Swift 6.3 / Xcode 26 / iOS 26+).

## Decisions & Additions
- **Kept Intact (No Trimming)**: All 48 core lessons in Phases 1–7 remain untouched (`0001`–`0048`), with `0048` linked forward into the new Phase 8 frontier track (`0049`).
- **Added Phase 8 — 2027 Senior iOS Frontier & Platform Expansion (`0049`–`0060`)**:
  - `0049` (Lesson 8.1): Objective-C Interoperability & Legacy Runtime Bridging (`@objc`, `dynamic`, `NSError**`, `Unmanaged<T>`).
  - `0050` (Lesson 8.2): Swift 6.2/6.3 Approachable Concurrency (`nonisolated(nonsending)`, `@concurrent`) & `Synchronization.Mutex`.
  - `0051` (Lesson 8.3): Zero-Copy & Noncopyable Swift (`~Copyable`, `InlineArray`, `Span<T>`).
  - `0052` (Lesson 8.4): The Composable Architecture (TCA) vs. `@Observable` UDF.
  - `0053` (Lesson 8.5): File System Sandboxing, `FileProtectionType`, & Direct SQLite / GRDB.
  - `0054` (Lesson 8.6): `XCFramework`s, Library Evolution (`.swiftinterface`), & Mergeable Libraries.
  - `0055` (Lesson 8.7): Code Quality Gates (`swift-format`, SwiftLint) & Xcode `.xctestplan` Matrices.
  - `0056` (Lesson 8.8): iOS 26 Liquid Glass, `PhaseAnimator`/`KeyframeAnimator`, & `.xcstrings` String Catalogs.
  - `0057` (Lesson 8.9): On-Device AI (`FoundationModels` `@Generable` + `Core ML`).
  - `0058` (Lesson 8.10): `AppIntents`, `AppEntity`, Siri, Spotlight, & System Intelligence.
  - `0059` (Lesson 8.11): `StoreKit 2` JWS Verification & FIDO2 Passkeys.
  - `0060` (Lesson 8.12): Xcode Cloud & Multi-Channel Release Engineering (Phased Rollouts).
- **Moved & Upgraded Capstone (`0061` / Milestone 9.1 / Phase 9)**:
  - Removed obsolete `0049-capstone-portfolio-blueprint` and regenerated Capstone at `0061-capstone-portfolio-blueprint` incorporating the 2027 deliverables.
- **Explicitly Excluded from `roadmap.sh/ios`** (documented in `MISSION.md`): Carthage, CocoaPods authoring, RxSwift, Storyboards/XIBs/IBOutlets, Alamofire, deprecated `NavigationView`, and `Tailor`.

## Verification
- Verified all 12 Phase 8 lessons (`test_phase8_frontier.py`) and all 6 Phase 7 & 9 lessons (`test_phase7_and_8.py`) against `swift` (`Apple Swift version 6.3.3`) with 100% passing assertions.
- Updated `index.html` (`0 of 61 completed`), `MASTER_PLAN.md`, `MISSION.md`, `RESOURCES.md`, `GLOSSARY.md`, `reference/glossary.html`, and added `reference/2027-frontier-cheatsheet.html`.
