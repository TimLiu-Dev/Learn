# 0006. Comprehensive 2027 Curriculum Upgrade & Bug Fixes

- **Date:** 2026-11-21
- **Status:** Accepted

## Context
A full pre-study audit of the master plan and all 38 lessons (`0001`–`0038`) revealed several structural, technical, and pedagogical gaps:
1. `0026-bitmask-state-dp.html` contained an orphaned Java `canPartition` code block with 4 extra closing `</div>` tags, an incorrect Day 3/4 numeric trace, an unescaped `<<` in HTML prose, and lacked a Bitmask DP (`dp[mask]`) code template.
2. `0018` lacked a `dist` guard in the 0-1 BFS template (risking infinite loops on cyclic graphs); `0018`, `0019`, `0021` omitted Python imports; `0019` lacked the 3-color DFS Topo Sort template; `0021` lacked negative-cycle detection and Floyd-Warshall; `0003` had a broken 12-char YouTube ID.
3. Lessons `0013`–`0036` had Warm-ups quizzing the *current* lesson instead of spaced retrieval from 1/3/7 sessions prior.
4. ~19 lessons had unescaped `<` in HTML prose/hints and 10+ files had raw markdown backticks; `assets/trace.js` was loaded in all 38 lessons but unused; `assets/practice.js` failed when users pasted `class Solution:` from solution drawers; 5 design lessons had only 1 practice test case.
5. High-ROI FAANG problems and 2027 interview formats were missing (*Vertical Order Traversal*, *Sliding Window Maximum*, *Difference Array*, *2D Prefix Sums*, *Find Peak Element*, *Find First/Last Position*, *Copy List with Random Pointer*, *Binary Tree Maximum Path Sum*, *BST Iterator*, *Merge k Sorted Lists*, *Time Based Key-Value Store*, *Snapshot Array*, *Web Crawler Multithreaded*, *Minimum Remove to Make Valid Parentheses*, *Randomized Quickselect* template, *Bitwise Trie* template, *Bug-Hunt & 60s Diagnosis* drills, and *2-Medium Meta Timed Sprint*).

## Decisions
1. **Engine & Visualizers (`assets/practice.js`, `0002`, `0003`, `0004`, `0007`, `0008`):** Updated `assets/practice.js` to auto-bind `class Solution` methods and provide Skulpt-safe generic typing shims; converted PEP 585 `list[...]` in Practice Solution Drawers to `List[...]`; wired interactive `figure.trace` visualizers using `assets/trace.js`.
2. **Bug & HTML Hygiene:** Fixed `0026` DOM/trace/Bitmask template, `0018` 0-1 BFS distance guard, `0019` 3-color DFS template, `0021` Bellman-Ford cycle check + Floyd-Warshall, `0003` YouTube link, `0031` Skulpt-safe fallback, and sanitized all unescaped `<` and markdown backticks across `lessons/*.html` and `reference/*.html`.
3. **Spaced Review & Practice Depth:** Rewrote `0013`–`0036` warm-ups for genuine 1/3/7-session spaced retrieval; eliminated `0001` 3x repetition of *Running Sum*; expanded 1–2 test suites in `0012`–`0016`, `0025`, `0027`, `0029`–`0031`, `0037` to 3+ test cases.
4. **FAANG & 2027 Upgrades + Master Plan Sync:** Added all missing high-ROI problems/templates, added 2027 bug-hunt & 2-Medium sprint formats in `0037`–`0038`, aligned priority tags across `reference/masterlist.html` and `reference/roadmap.html`, added an ROI study-order guide, and updated `NOTES.md`.

## Consequences
All 38 lessons and reference documents are now structurally valid, internally consistent, verified against embedded test suites, and aligned with 2027 Big Tech coding interview standards.
