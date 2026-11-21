# Expand to 116 Lessons (109 Core + 7 Optional) & Full Runtime Verification

Updated [[MISSION.md]] and the curriculum from 110 to **116 problems** (`109` Core 2027 Mid-Tech + `7` Optional / Low-ROI preserved in a dedicated track), repaired all broken interactive steppers/attributes across `0058`–`0110`, expanded thin Practice Arena test suites to ≥ 3 test cases each, and upgraded [[master-plan.html]], [[reference/pattern-reference.html]], [[GLOSSARY.md]], and [[assets/lesson-runtime.js]].

## Key Changes
- **6 High-ROI Mid-Tech Lessons Added (`0111`–`0116`)**: LC `#528` Random Pick with Weight, LC `#34` Find First and Last Position, LC `#2` Add Two Numbers, LC `#143` Reorder List, LC `#152` Maximum Product Subarray, and LC `#1091` Shortest Path in Binary Matrix.
- **Optional / Low-ROI Track (7 Preserved)**: `#0003` (`#1768`), `#0020` (`#136`), `#0021` (`#191`), `#0022` (`#338`), `#0053` (`#841`), `#0065` (`#853`), and `#0077` (`#452`) remain fully playable in a dedicated Optional section and filter in `master-plan.html`.
- **Interactive Fixes & Verification**: Fixed `currentStep` / `updateUI()` / `handleCustomInput()` / broken attribute quotes in `0058`–`0110`, fixed `0067` Kotlin starter stub syntax, added stateful `Solution` constructor OOP support and negative method-call indexing (`path.split('/')[-1]`) in `lesson-runtime.js`, and verified **348 / 348** language-lesson test suites passing.
