# Teaching Notes & User Preferences

## Learner Profile
- **Current role:** Junior Android Developer (knows Kotlin/Java, mobile client lifecycle, Retrofit/JSON consumers, Git, basic coroutines/async).
- **Target role & timeline:** Senior Forward Deployed Engineer (FDE) in 2027.
- **Target outcome:** Comprehensive end-to-end mastery + a resume-worthy end-to-end capstone project strong enough to land a job.

## Explicit Format Preferences
- **Master Plan first (`master-plan.html`):** Acts as the comprehensive, end-to-end legend and roadmap for all lessons.
- **Lesson 1 as benchmark (`lessons/0001-dev-vs-delta-discovery-ingestion.html`):** Sets the quality, interactivity, and visual standard for every future lesson.
- **Code examples + Runnable in-HTML code practice:** Exercises must execute directly in the HTML, run automated checks, and offer progressive hints plus a final answer.
- **Code quizzes:** Multiple-choice questions where feedback explains **why the correct answer is right AND why each wrong answer is wrong** (and per `SKILL.md`, keep option word/character counts closely matched so formatting never hints the answer).
- **Ending Capstone Project:** Comprehensive, end-to-end, resume-grade project woven cleanly into the master plan.

## Pedagogical Strategy
- **Bridge from Android:** Translate unfamiliar enterprise FDE concepts through familiar Android equivalents (e.g., strict Moshi/Gson DTOs vs. resilient DLQ parsers; `ViewModel` state vs. SSE agent streams; Android `WorkManager` vs. idempotent durable queues).
- **Respect working memory:** Keep individual lessons tightly scoped (~20–30 min, 1 clear tangible win) while keeping the overall 40-unit (`0000`–`0039`) 2-Track master plan comprehensive end-to-end (Track 1 Mid-Level + Capstone I → Job Search Launch → Track 2 Senior + Capstone II).
- **Browser Labs + Real Capstone Repo:** Use pure sandboxed JavaScript in lesson pages (with Kotlin -> TS/Python/SQL Rosetta Stone examples) so `file://` execution is instant and 100% offline-safe; execute real Python/FastAPI, Postgres/`pgvector` SQL, TypeScript/React, Terraform/K8s, and GitHub Actions evals inside `./aegisops/`.
