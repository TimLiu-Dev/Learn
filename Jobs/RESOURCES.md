# Job Search Mastery Resources

## Knowledge

- [Guide: _Tech Interview Handbook_ — Yangshun Tay](https://www.techinterviewhandbook.org/)
  Open-source end-to-end SWE job search, ATS-safe resume blueprint, Grind 75 coding prep, behavioral frameworks, and offer negotiation. Use for: resume structure, FAANG interview loop prep, and negotiation baselines.
- [Book & Newsletter: _The Pragmatic Engineer_ & _The Tech Resume Inside Out_ — Gergely Orosz](https://www.pragmaticengineer.com/)
  Industry-leading analysis of SWE hiring markets, ATS parsing realities, single-column resume engineering, and referral dynamics. Use for: market context, ATS myths vs. reality, and mid-to-senior career progression.
- [Documentation: _Greenhouse Recruiting — Boolean Search Syntax_](https://support.greenhouse.io/)
  Primary-source documentation on how a dominant tech ATS indexes candidate text and evaluates `AND`, `OR`, `NOT`, wildcards, and phrase matching. Use for: Lesson 1 and understanding recruiter database queries.
- [Repo: _SimplifyJobs/New-Grad-Positions_ & Raw `listings.json`](https://github.com/SimplifyJobs/New-Grad-Positions)
  Community-curated SWE job feed with a machine-readable JSON source (`.github/scripts/listings.json` on `dev`) containing `company`, `title`, `locations`, `url`, `date_posted`, `active`, and `sponsorship`. Use for: automated GitHub feed ingestion and 0–2 YoE job discovery.
- [Repo: _speedyapply/2027-SWE-College-Jobs_ (`NEW_GRAD_USA.md`)](https://github.com/speedyapply/2027-SWE-College-Jobs/blob/main/NEW_GRAD_USA.md)
  Daily-updated Markdown table of 2027 US SWE roles (plus `NEW_GRAD_INTL.md` and `2027-AI-College-Jobs`) with company, title, location, salary, link, and posting age. Use for: Markdown table parsing and rapid early-career application sweeps.
- [Data & Benchmarks: _Levels.fyi — Engineering Levels & Compensation_](https://www.levels.fyi/)
  Crowdsourced leveling mappings (Google L3/L4/L5, Meta E3/E4/E5, Amazon L4/L5/L6) and compensation breakdowns. Use for: calibrating the 1 YoE → 2 YoE Mid-Tech → FAANG ladder and offer negotiation.
- [Official Docs: _Android Developers Architecture_ & _Spring Boot Reference_](https://developer.android.com/topic/architecture)
  Canonical architecture references for modern Kotlin/Compose/Hilt Android apps and Spring Boot microservices. Use for: aligning 2027 Mid-Tech dual-stack (Android + Spring Boot) portfolio proof.
- [AI Crawling & Browser Frameworks: `Crawl4AI`, `Stagehand`, `browser-use`](https://github.com/unclecode/crawl4ai)
  Open-source Playwright-backed LLM crawling (`unclecode/crawl4ai`), self-healing DOM extraction (`browserbase/stagehand`), and autonomous browser agent loops (`browser-use/browser-use`). Use for: Phase 4 crawler/auto-fill architecture and Capstone.
- [Local 128GB Inference: `llama.cpp` (Metal / ROCm HIP) & Apple `MLX`](https://github.com/ggml-org/llama.cpp)
  High-throughput local LLM inference backends: MLX/Metal on Apple M5 Max (128GB, ~614 GB/s) and Linux ROCm/hipBLASLt on AMD Ryzen AI Max+ 395 (128GB). Use for: self-hosting the JD scoring, extraction, and resume tailoring pipeline at zero token cost.
- [Platform Guide: _Wellfound (AngelList)_ & _Built In_](https://wellfound.com/)
  Startup and regional scale-up marketplaces with salary bands, funding stage filters, and direct engineering/founder access. Use for: Phase 2 channel strategies.

## Wisdom (Communities)

- [r/androiddev](https://www.reddit.com/r/androiddev/)
  Active community of Android engineers and hiring managers debating Kotlin/Compose portfolios, Play Store vs. GitHub signals, and hiring realities. Use for: real-world critique of Android portfolio repos.
- [r/ExperiencedDevs](https://www.reddit.com/r/ExperiencedDevs/)
  Moderated forum for engineers with 3+ YoE discussing interviewing, Spring Boot/backend transitions, recruiter behavior, leveling, and career progression. Use for: Mid-Tech to FAANG transition wisdom.
- [r/LocalLLaMA](https://www.reddit.com/r/LocalLLaMA/)
  Practitioner community benchmarking MLX on Apple Silicon and ROCm/`llama.cpp` tuning on AMD Strix Halo (Ryzen AI Max+ 395) 128GB systems. Use for: optimizing local model speeds and quantization choices.

## Gaps

- Canonical, well-maintained GitHub aggregators specifically for **Mid-Level (2+ YoE) Android & Spring Boot** roles (most public GitHub Markdown lists focus on Intern/New-Grad, so Phase 2/4 will teach building our own ATS feed aggregator from company lists + Greenhouse/Lever/Ashby endpoints).
- Empirical callback-rate tracking comparing frontier-tailored vs. local-70B-tailored resumes (to be measured in the Capstone CRM).
