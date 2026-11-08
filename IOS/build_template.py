#!/usr/bin/env python3
"""
Phase 1 Lesson & Exercise Builder: Swift Compiler & Language Internals
Generates Lessons 0001 through 0008 with complete in-HTML playgrounds,
quizzes with per-choice explanations, hints, solutions, and runnable terminal exercises.
"""

import os
import subprocess

def render_lesson_html(data):
    quizzes_html = ""
    for q_idx, q in enumerate(data['quizzes']):
        opts_html = ""
        for opt_idx, opt in enumerate(q['options']):
            opts_html += f'<button class="quiz-btn" data-reason="{opt["reason"]}">{chr(65+opt_idx)}. {opt["text"]}</button>\n'
        
        quizzes_html += f"""
        <div class="quiz-widget" id="quiz-{data['id']}-{q_idx+1}" data-correct="{q['correct_idx']}">
          <div class="quiz-question">Question {q_idx+1}: {q['question']}</div>
          <div class="quiz-options">
            {opts_html}
          </div>
          <div class="quiz-feedback"></div>
          <div class="quiz-breakdown" style="display:none;"></div>
        </div>
        """

    hints_html = ""
    for h_idx, hint in enumerate(data['exercise']['hints']):
        hints_html += f'<div class="hint-item hint-{h_idx+1}"><strong>Hint {h_idx+1}:</strong> {hint}</div>\n'

    sources_html = ""
    for src in data['primary_sources']:
        sources_html += f'<li><a href="{src["url"]}" target="_blank">{src["title"]}</a> — {src["desc"]}</li>\n'

    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>{data['lesson_num']}: {data['title']} (Senior iOS Engineering)</title>
  <link rel="stylesheet" href="../assets/style.css">
  <script src="../assets/quiz.js" defer></script>
  <script src="../assets/playground.js" defer></script>
</head>
<body>

  <!-- LESSON HEADER -->
  <header>
    <div class="lesson-meta">
      <span>{data['phase_title']}</span>
      <span>•</span>
      <span>{data['lesson_num']}</span>
    </div>
    <h1>{data['title']}</h1>
    <p style="font-size: 1.1rem; color: var(--text-secondary); margin-bottom: 0.5rem;">
      {data['subtitle']}
    </p>
    <p style="font-size: 0.85rem; color: var(--text-muted);">
      <a href="../index.html">← Master Curriculum & Systems Legend</a> • <a href="{data['cheatsheet_url']}">📖 {data['cheatsheet_title']}</a> • <a href="../reference/glossary.html">📚 Systems Glossary</a>
    </p>
  </header>

  <!-- ANCHOR CONCEPT -->
  <section>
    <h2>
      <span>🧠</span>
      <span>The Senior Android Anchor: Systems Comparison</span>
    </h2>
    {data['android_anchor_html']}
  </section>

  <!-- CORE KNOWLEDGE -->
  <section style="margin-top: 2rem;">
    <h2>
      <span>⚡</span>
      <span>Low-Level Core: How Swift Works Under the Hood</span>
    </h2>
    {data['core_sections_html']}
  </section>

  <!-- ANTI-PATTERN -->
  <section>
    <div class="callout warning">
      <strong>⚠️ Senior Failure Mode & Anti-Pattern:</strong>
      {data['failure_mode_html']}
    </div>
  </section>

  <!-- INTERACTIVE QUIZZES -->
  <section>
    <h2>
      <span>🧪</span>
      <span>Knowledge Check: Systems Retrieval Practice</span>
    </h2>
    <p>
      Select the best answer. After choosing, an exhaustive breakdown will reveal exactly why the right answer is correct and why each distractor is wrong.
    </p>
    {quizzes_html}
  </section>

  <!-- IN-HTML CODE PRACTICE PLAYGROUND -->
  <section>
    <h2>
      <span>💻</span>
      <span>In-Browser Swift Practice & Verification</span>
    </h2>
    <div class="practice-playground" id="pg-{data['id']}" data-exercise-path="exercises/{data['slug']}/practice.swift">
      <div class="playground-header">
        <div class="playground-title">
          <span>⚡</span>
          <span>Challenge: {data['exercise']['title']}</span>
        </div>
        <div class="playground-actions">
          <button class="btn btn-secondary reset-code-btn" type="button">↺ Reset</button>
          <button class="btn btn-primary run-code-btn" type="button">▶ Run & Check</button>
        </div>
      </div>

      <p class="playground-instructions">
        {data['exercise']['instructions']}
      </p>

      <div class="code-editor-container">
        <textarea class="code-editor" spellcheck="false" autocomplete="off" autocapitalize="off">{data['exercise']['starter_code']}</textarea>
      </div>

      <div class="terminal-container">
        <div class="terminal-header">
          <span>Console Output & Test Assertions</span>
          <span class="status-indicator waiting">Ready to Run</span>
        </div>
        <pre class="terminal-output">Click "▶ Run & Check" to compile and run tests against the Swift 6 compiler...</pre>
      </div>

      <!-- Progressive Hints -->
      <div class="hints-container">
        <div class="hints-header">
          <span>💡 Need a hint?</span>
          <div class="hint-buttons">
            <button class="hint-btn" type="button" data-hint="1">Hint 1</button>
            <button class="hint-btn" type="button" data-hint="2">Hint 2</button>
            <button class="hint-btn" type="button" data-hint="3">Hint 3</button>
          </div>
        </div>
        <div class="hint-content">
          {hints_html}
        </div>
      </div>

      <!-- Final Answer Reveal -->
      <details class="solution-details">
        <summary>🔑 Reveal Final Reference Solution & Architecture Walkthrough</summary>
        <div class="solution-content">
          <pre><code>{data['exercise']['solution_code']}</code></pre>
          <div class="solution-explanation">
            <strong>Senior Engineering Walkthrough:</strong>
            <p>{data['exercise']['solution_explanation']}</p>
          </div>
        </div>
      </details>
    </div>

    <div class="callout tip">
      <strong>Run standalone in terminal:</strong>
      <pre><code>swift -module-cache-path /tmp/clang-module-cache exercises/{data['slug']}/practice.swift</code></pre>
    </div>
  </section>

  <!-- PRIMARY SOURCE -->
  <section style="margin-top: 2rem;">
    <h2>
      <span>📖</span>
      <span>Primary Source Recommendations</span>
    </h2>
    <ul>
      {sources_html}
    </ul>
  </section>

  <!-- REINFORCEMENT & FOLLOWUP -->
  <section class="callout">
    <strong>💬 Senior Architectural Coaching:</strong>
    <p style="margin-top: 0.5rem; margin-bottom: 0;">
      {data['coaching_prompt']} Remember: you can ask me any follow-up questions at any time in chat!
    </p>
  </section>

  <!-- NAVIGATION -->
  <div class="nav-buttons">
    <a href="{data['prev_url']}" class="nav-btn">← Previous</a>
    <a href="{data['next_url']}" class="nav-btn">Next Lesson →</a>
  </div>

  <footer class="footer">
    <span>{data['phase_title']} • {data['lesson_num']}</span>
    <span><a href="{data['cheatsheet_url']}">View Cheatsheet</a></span>
  </footer>

</body>
</html>
"""
    return html

print("Template builder loaded successfully.")
