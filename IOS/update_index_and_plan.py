#!/usr/bin/env python3
import os
import re

from data_phase1 import PHASE1_LESSONS
from data_phase2_combined import ALL_PHASE2
from data_phase3_combined import ALL_PHASE3
from data_phase4 import PHASE4_LESSONS
from data_phase4_part2 import PHASE4_PART2
from data_phase5 import PHASE5_LESSONS
from data_phase6 import PHASE6_LESSONS
from data_phase7_and_8 import PHASE7_AND_8_LESSONS

all_lessons = (
    PHASE1_LESSONS +
    ALL_PHASE2 +
    ALL_PHASE3 +
    PHASE4_LESSONS +
    PHASE4_PART2 +
    PHASE5_LESSONS +
    PHASE6_LESSONS +
    PHASE7_AND_8_LESSONS
)

phases_meta = [
    {
        "num": 1,
        "title": "Phase 1: Swift Compiler & Language Internals",
        "subtitle": "Core Language, Value Semantics, ARC Memory Safety, Protocols & Swift 6 Macros",
        "badge": "8 Lessons Available"
    },
    {
        "num": 2,
        "title": "Phase 2: Modern Swift 6 Concurrency & Enterprise Networking",
        "subtitle": "Structured Concurrency, Actors, Swift 6 Data-Race Safety, Combine & URLSession",
        "badge": "10 Lessons Available"
    },
    {
        "num": 3,
        "title": "Phase 3: High-Performance Declarative UI (SwiftUI & UIKit)",
        "subtitle": "Render Tree, Observation Macro, Custom Layouts, NavigationStack & UIKit Core",
        "badge": "9 Lessons Available"
    },
    {
        "num": 4,
        "title": "Phase 4: Local Persistence, App Security & Offline-First",
        "subtitle": "Keychain, CryptoKit, SSL Pinning, SwiftData, Core Data & Offline Sync",
        "badge": "6 Lessons Available"
    },
    {
        "num": 5,
        "title": "Phase 5: Architecture, Dependency Injection & Modularization",
        "subtitle": "Clean Architecture, UDF, SPM Multi-Package Architecture & dyld4 Launch Time",
        "badge": "4 Lessons Available"
    },
    {
        "num": 6,
        "title": "Phase 6: Testing, Debugging & Performance Profiling",
        "subtitle": "Swift Testing (@Test/#expect), Mocking, LLDB, Instruments & MetricKit",
        "badge": "6 Lessons Available"
    },
    {
        "num": 7,
        "title": "Phase 7: App Store Readiness, Ecosystem & DevOps",
        "subtitle": "Apple HIG, Dynamic Type, Push Notifications, WidgetKit, Signing & CI/CD",
        "badge": "5 Lessons Available"
    },
    {
        "num": 8,
        "title": "Phase 8: Capstone Portfolio Project ('PulseFlow Enterprise')",
        "subtitle": "Resume-Worthy End-to-End Multi-Module Production App Blueprint & Practice",
        "badge": "Capstone Available"
    }
]

# Generate Phase Cards HTML
phase_cards_html = []
for p in phases_meta:
    phase_lessons = [l for l in all_lessons if l['phase_num'] == p['num']]
    items_html = []
    for l in phase_lessons:
        item = f"""          <li class="lesson-item">
            <input type="checkbox" class="lesson-checkbox" id="chk-{l['id']}" onchange="updateProgress()">
            <div class="lesson-info">
              <a href="./lessons/{l['slug']}.html" class="lesson-link">{l['lesson_num']}: {l['title']}</a>
              <p class="lesson-desc">{l['subtitle']}</p>
            </div>
            <span class="badge badge-status done">Available</span>
          </li>"""
        items_html.append(item)

    card = f"""    <!-- PHASE {p['num']} -->
    <div class="phase-card" id="phase-{p['num']}">
      <div class="phase-header" onclick="togglePhase('phase-{p['num']}')">
        <div class="phase-title-group">
          <div class="phase-number">{p['num']}</div>
          <div>
            <div class="phase-title">{p['title']}</div>
            <div class="phase-subtitle">{p['subtitle']}</div>
          </div>
        </div>
        <div style="display: flex; align-items: center; gap: 0.75rem;">
          <span class="badge badge-status done">{p['badge']}</span>
          <span class="phase-toggle-icon">▼</span>
        </div>
      </div>
      <div class="phase-content">
        <ul class="lesson-list">
{chr(10).join(items_html)}
        </ul>
      </div>
    </div>"""
    phase_cards_html.append(card)

all_phases_rendered = "\n\n".join(phase_cards_html)

# Read index.html and update it
with open("index.html", "r", encoding="utf-8") as f:
    index_content = f.read()

# 1. Update quick-links to include Glossary
if "./reference/glossary.html" not in index_content:
    index_content = index_content.replace(
        '<a class="quick-link-btn" href="./NOTES.md" target="_blank">📝 Learner Notes</a>',
        '<a class="quick-link-btn" href="./reference/glossary.html" target="_blank">📖 Systems Glossary</a>\n      <a class="quick-link-btn" href="./NOTES.md" target="_blank">📝 Learner Notes</a>'
    )

# 2. Update progress count
index_content = re.sub(
    r'<span id="progress-count"[^>]*>[^<]+</span>',
    f'<span id="progress-count" style="font-size: 0.9rem; color: var(--text-muted); margin-left: 0.5rem;">0 of {len(all_lessons)} completed</span>',
    index_content
)

# 3. Replace the curriculum section between <!-- PHASE 1 --> and </section>\n\n  <!-- CAPSTONE SPECIFICATION SHOWCASE -->
curriculum_regex = re.compile(r'<!-- PHASE 1 -->.*?<!-- CAPSTONE SPECIFICATION SHOWCASE -->', re.DOTALL)
replacement_text = all_phases_rendered + "\n  </section>\n\n  <!-- CAPSTONE SPECIFICATION SHOWCASE -->"

if curriculum_regex.search(index_content):
    index_content = curriculum_regex.sub(replacement_text, index_content)
    print("Successfully replaced curriculum phases in index.html")
else:
    print("Could not match curriculum section in index.html")

with open("index.html", "w", encoding="utf-8") as f:
    f.write(index_content)
print(f"Updated index.html with all {len(all_lessons)} lessons!")

# Update MASTER_PLAN.md
plan_syllabus_lines = []
plan_tracker_lines = []

for p in phases_meta:
    phase_lessons = [l for l in all_lessons if l['phase_num'] == p['num']]
    plan_syllabus_lines.append(f"### {p['title']}")
    plan_tracker_lines.append(f"- [ ] **{p['title']}**")
    for l in phase_lessons:
        plan_syllabus_lines.append(f"- **{l['lesson_num']}**: {l['title']} ({l['subtitle']})")
        plan_tracker_lines.append(f"  - [ ] {l['lesson_num']}: {l['title']}")
    plan_syllabus_lines.append("")

with open("MASTER_PLAN.md", "r", encoding="utf-8") as f:
    plan_content = f.read()

# Replace syllabus section
syllabus_pattern = re.compile(r'### Phase 1: Swift Compiler & Language Internals Through an Android Lens.*?---', re.DOTALL)
new_syllabus = "\n".join(plan_syllabus_lines) + "---\n"
plan_content = syllabus_pattern.sub(new_syllabus, plan_content)

# Replace Progress Tracker section
tracker_pattern = re.compile(r'## Progress Tracker\n\n- \[.\] \*\*Phase 1.*', re.DOTALL)
new_tracker = "## Progress Tracker\n\n" + "\n".join(plan_tracker_lines) + "\n"
plan_content = tracker_pattern.sub(new_tracker, plan_content)

with open("MASTER_PLAN.md", "w", encoding="utf-8") as f:
    f.write(plan_content)
print("Updated MASTER_PLAN.md with 49-lesson syllabus and reset progress checkboxes!")
