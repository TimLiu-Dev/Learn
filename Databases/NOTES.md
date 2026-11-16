# Teaching Notes & Learner Preferences

- **Background**: Junior Android developer targeting 2027 Senior Engineer bar. Closest prior DB knowledge is basic Android Room DB.
- **Teaching Workflow (`/teach` skill active)**:
  - Ground every lesson in `MISSION.md`, `RESOURCES.md`, and `learning-records/*.md` (Zone of Proximal Development).
  - Build storage strength via retrieval practice, spacing, interleaving, and positive + negative verification tests.
  - Keep `reference/glossary.html` and `reference/postgres-sqlite-cheatsheet.html` as canonical quick-reference artifacts.
- **Format Preferences**:
  - Prefers interactive HTML files that run SQL directly in the browser (`assets/sql-asm.js` SQLite 3.45.2 engine, works offline via `file://`).
  - Wants automated verification checks (`Check Answer`) with positive + negative test assertions (non-cheatable, testing `EXPLAIN QUERY PLAN` and boundary failures where applicable).
  - Wants progressive hints (step-by-step) and a toggleable final answer with explanation.
  - Wants multiple-choice quizzes where **every option** explains why it is correct or wrong.
  - Wants a Master Plan HTML acting as a legend for the entire course, plus an end-to-end resume-grade capstone project (`PulseLedger`).
- **Quiz Design Rule (per `teach` skill)**: Keep all 4 options balanced in word count, character length, and technical formatting so option length or style never leaks the correct answer, and distribute correct answers evenly across A, B, C, and D.
- **Pedagogical Reminders**: Include a primary source recommendation and a reminder to ask follow-up questions to the AI teacher in lessons and references.
