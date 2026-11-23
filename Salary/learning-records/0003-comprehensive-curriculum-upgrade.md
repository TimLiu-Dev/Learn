# Comprehensive 13-Lesson Curriculum & Tooling Upgrade

Before the learner began Lesson 01, a full end-to-end audit of `master-plan.html`, Lessons 01–11, `reference/`, `assets/`, and `offerledger/` identified structural gaps, missing progression mechanics, and interactive tooling bugs. The learner approved a **Full Upgrade** expanding the course into 12 tightly scoped lessons + Capstone 13.

## Key Upgrades & Fixes
- **Added Lesson 04 (`0004-equity-taxes-geo-tiering-and-timing.html`):** Covers the 22% supplemental RSU withholding trap (IRS Pub 15), 90-day PTEW golden handcuffs & ISO AMT phantom income, 83(b) early-exercise window, double-trigger pre-IPO RSUs, geographic pay tiers (-10% to -25% remote/MCOL discounts), and start-date / vest-cliff / bonus-proration timing.
- **Added Lesson 10 (`0010-inside-progression-raises-and-retention.html`):** Covers on-the-job progression to top end—performance rating multipliers (0.5x–2.0x on bonus & refreshers), building a brag doc, pre-budget off-cycle raise negotiations, JHT $5k bonus payout timing, and why accepting retention counter-offers usually backfires.
- **Upgraded Existing Lessons & Renumbered Cleanly (`0001`–`0013`):**
  - Fixed Lesson 03 quiz alignment (taught gross vs. net cross-year clawback math in the body before testing it) + Mega Backdoor Roth.
  - Added agency vs. internal recruiters, wide transparency ranges, recovery scripts, and background check / The Work Number facts to Lesson 05 (formerly 04).
  - Added full Android L3→L7+ leveling rubric and pre-interview loop-level calibration to Lesson 06 (formerly 05).
  - Added stealth employed job-search rules and JHT $5k payout timing to Lesson 07 (formerly 06).
  - Added counter-anchor calculation formula and Base > RSU > Sign-On compounding hierarchy to Lesson 08 (formerly 07).
  - Fixed the flipped negative question in Lesson 09's lab (formerly 08), added Pause/Mirror/Defer phone tactics and reneging trade-offs.
  - Upgraded 2-option lab selects to 3–4 realistic choices across all labs and restored the "Ask your teacher" prompt in Lessons 02–13.
- **Fixed `offerledger/` & `assets/practice-lab.js` Bugs:**
  - Fixed `app.js` passing `counterTc` and total-comp `bandMax` into `swapCompLevers()`, adding realistic `BASE_CAPS` per level/tier in `engine.js`, Year-2 Sign-On UI input, and separate 401k/ESPP inputs.
  - Fixed `<label for>` matching IDs and full hint/solution panel cleanup on `Reset` in `assets/practice-lab.js`.
