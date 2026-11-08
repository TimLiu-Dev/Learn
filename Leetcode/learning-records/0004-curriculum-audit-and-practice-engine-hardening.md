# 0004. Curriculum Audit & Practice Engine Hardening

- **Date:** 2026-11-07
- **Status:** Accepted

## Context
A full audit of the 33-session curriculum was performed prior to learning start. Several defects were identified: practice textareas in ~22 lessons had pre-filled answers (defeating active practice), pointer/tree runners were using fake array math instead of authentic `ListNode`/`TreeNode` instances, retrieval quizzes suffered from a 92% "Option A" answer bias, `reference/roadmap.html` was stuck on an outdated 26-session draft, `reference/patterns.html` truncated at Session 10, and two duplicate problems existed across sessions.

## Decisions
1. **Embedded Data Structure Serialization (`assets/practice.js`):** Injected lightweight `ListNode` and `TreeNode` classes, builders, and serializers directly into the Skulpt execution harness. Updated `0009` (Linked Lists), `0010` (Trees DFS), `0011` (Trees BFS), and `0012` (BST) to execute genuine pointer manipulations and tree traversals.
2. **Starter Boilerplate Reset:** Cleared completed solutions from practice textareas across all 22 affected lessons, replacing them with proper starter skeletons (`# TODO: write your solution here\n pass`). Verified solutions remain in the collapsible trilingual solution drawers.
3. **Quiz Choice Rebalancing:** Rebalanced correct answers across all 99 quiz questions into an exact 25/25/25/24 distribution (A: 24, B: 25, C: 25, D: 25). Verified that every option retains its per-choice `data-why` breakdown explaining why the answer is right or wrong.
4. **Pruning Duplicate Problems:**
   - In Session 5 (`0006-stacks-and-queues.html`), replaced duplicate *Daily Temperatures* with *Implement Queue using Stacks* (LC 232).
   - In Session 25 (`0026-bitmask-state-dp.html`), replaced duplicate *Partition Equal Subset Sum* with *Best Time to Buy and Sell Stock with Cooldown* (LC 309).
5. **Reference Synchronization:**
   - Regenerated `reference/roadmap.html` to cover all 33 sessions across 8 phases with active links, difficulty tags, and checkpoints.
   - Regenerated `reference/patterns.html` with all 32 technical patterns, signals, Kotlin templates, Big-O bounds, and canonical problem lists.

## Consequences
All 33 lessons now provide genuine, interactive in-browser coding practice, progressive hints, trilingual solutions, and rigorous multiple-choice retrieval quizzes with zero spoiler bias. The curriculum is fully verified and ready for learning.
