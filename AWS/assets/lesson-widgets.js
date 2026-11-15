/* ==========================================================================
   Senior AWS Engineering Curriculum — Reusable Interactive Lesson Widgets
   Zero external dependencies; runs cleanly over file:// or http://
   ========================================================================== */

(function (global) {
  "use strict";

  const STORAGE_KEY = "aws_2027_curriculum_progress_v1";

  function loadProgress() {
    try {
      const raw = global.localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : { completedLessons: {}, quizScores: {}, codePassed: {} };
    } catch (_) {
      return { completedLessons: {}, quizScores: {}, codePassed: {} };
    }
  }

  function saveProgress(data) {
    try {
      global.localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch (_) {
      // Ignore storage errors in restricted contexts
    }
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  /**
   * Initializes a live runnable JS code practice widget with assertions,
   * progressive hints, reset, and final answer reveal.
   */
  function initCodePractice(containerId, config) {
    const root = document.getElementById(containerId);
    if (!root) return;

    let hintIndex = 0;

    root.innerHTML = `
      <div class="widget-box">
        <div class="widget-header">
          <h3 class="widget-title">${escapeHtml(config.title || "Interactive Code Practice")}</h3>
          <span class="pill" data-role="practice-status">Not run yet</span>
        </div>
        ${config.description ? `<p>${config.description}</p>` : ""}
        <div class="code-editor-wrap">
          <div class="code-editor-bar">
            <span>${escapeHtml(config.filename || "practice.js")} (JavaScript — runs live in browser)</span>
            <span>Tab = 2 spaces</span>
          </div>
          <textarea class="code-editor" spellcheck="false" aria-label="Code editor">${escapeHtml(config.starterCode)}</textarea>
        </div>
        <div class="btn-row">
          <button type="button" class="btn primary" data-action="run">Run &amp; Check</button>
          <button type="button" class="btn" data-action="hint">Hint (0/${(config.hints || []).length})</button>
          <button type="button" class="btn" data-action="answer">Show Final Answer</button>
          <button type="button" class="btn subtle" data-action="reset">Reset</button>
        </div>
        <div class="hint-box" data-role="hint-box"></div>
        <div class="runner-output" data-role="output-box"></div>
        <div class="runner-output" data-role="answer-box"></div>
      </div>
    `;

    const textarea = root.querySelector("textarea.code-editor");
    const runBtn = root.querySelector('[data-action="run"]');
    const hintBtn = root.querySelector('[data-action="hint"]');
    const answerBtn = root.querySelector('[data-action="answer"]');
    const resetBtn = root.querySelector('[data-action="reset"]');
    const hintBox = root.querySelector('[data-role="hint-box"]');
    const outputBox = root.querySelector('[data-role="output-box"]');
    const answerBox = root.querySelector('[data-role="answer-box"]');
    const statusPill = root.querySelector('[data-role="practice-status"]');

    if (!config.hints || config.hints.length === 0) {
      hintBtn.style.display = "none";
    }

    // Tab indentation support
    textarea.addEventListener("keydown", function (e) {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = this.selectionStart;
        const end = this.selectionEnd;
        this.value = this.value.substring(0, start) + "  " + this.value.substring(end);
        this.selectionStart = this.selectionEnd = start + 2;
      }
    });

    function runCode() {
      const userCode = textarea.value;
      const logs = [];
      const mockConsole = {
        log: function () {
          const line = Array.from(arguments)
            .map((a) => (typeof a === "object" ? JSON.stringify(a) : String(a)))
            .join(" ");
          logs.push(line);
        }
      };

      let exportedScope = {};
      let runtimeError = null;

      try {
        const exportNames = config.exports || [];
        const returnObj = exportNames.map((n) => `${n}: (typeof ${n} !== "undefined" ? ${n} : undefined)`).join(", ");
        const wrapped = `
          "use strict";
          const console = __mockConsole;
          ${userCode}
          return { ${returnObj} };
        `;
        const fn = new Function("__mockConsole", wrapped);
        exportedScope = fn(mockConsole);
      } catch (err) {
        runtimeError = err;
      }

      outputBox.classList.add("visible");

      if (runtimeError) {
        statusPill.textContent = "Error";
        statusPill.className = "pill removed";
        outputBox.innerHTML = `
          <strong>Runtime / Syntax Error:</strong>
          <div class="console-log">${escapeHtml(runtimeError.name + ": " + runtimeError.message)}</div>
        `;
        return;
      }

      let passedCount = 0;
      const resultsHtml = (config.tests || []).map((test) => {
        let outcome;
        try {
          outcome = test.run(exportedScope, logs);
        } catch (err) {
          outcome = { pass: false, message: err.message };
        }
        const ok = Boolean(outcome && outcome.pass);
        if (ok) passedCount++;
        const detail = outcome && outcome.message ? ` — ${outcome.message}` : "";
        return `<li class="${ok ? "pass" : "fail"}">${ok ? "PASS" : "FAIL"}: ${escapeHtml(test.name)}${escapeHtml(detail)}</li>`;
      });

      const total = (config.tests || []).length;
      const allPassed = total > 0 && passedCount === total;

      statusPill.textContent = allPassed ? `Passed (${passedCount}/${total})` : `${passedCount}/${total} Checks`;
      statusPill.className = allPassed ? "pill verified" : "pill part2";

      if (allPassed && config.lessonId) {
        const progress = loadProgress();
        progress.codePassed[config.lessonId] = true;
        saveProgress(progress);
      }

      const logsHtml = logs.length
        ? `<div><strong>Console Output:</strong><div class="console-log">${escapeHtml(logs.join("\n"))}</div></div>`
        : "";

      outputBox.innerHTML = `
        <strong>Check Results (${passedCount}/${total} passed):</strong>
        <ul class="test-list">${resultsHtml.join("")}</ul>
        ${logsHtml}
      `;
    }

    runBtn.addEventListener("click", runCode);

    hintBtn.addEventListener("click", function () {
      const hints = config.hints || [];
      if (hintIndex < hints.length) {
        hintIndex++;
      }
      hintBtn.textContent = `Hint (${hintIndex}/${hints.length})`;
      hintBox.classList.add("visible");
      const items = hints.slice(0, hintIndex).map((h, i) => `<p><strong>Hint ${i + 1}:</strong> ${h}</p>`).join("");
      hintBox.innerHTML = items;
    });

    answerBtn.addEventListener("click", function () {
      const isVisible = answerBox.classList.toggle("visible");
      answerBtn.textContent = isVisible ? "Hide Final Answer" : "Show Final Answer";
      if (isVisible) {
        answerBox.innerHTML = `
          <strong>Reference Solution:</strong>
          ${config.solutionExplanation ? `<p>${config.solutionExplanation}</p>` : ""}
          <pre><code>${escapeHtml(config.solutionCode)}</code></pre>
          <button type="button" class="btn" data-action="load-solution">Load into Editor &amp; Run</button>
        `;
        const loadBtn = answerBox.querySelector('[data-action="load-solution"]');
        loadBtn.addEventListener("click", function () {
          textarea.value = config.solutionCode;
          runCode();
        });
      }
    });

    resetBtn.addEventListener("click", function () {
      textarea.value = config.starterCode;
      hintIndex = 0;
      hintBtn.textContent = `Hint (0/${(config.hints || []).length})`;
      hintBox.classList.remove("visible");
      hintBox.innerHTML = "";
      outputBox.classList.remove("visible");
      outputBox.innerHTML = "";
      statusPill.textContent = "Not run yet";
      statusPill.className = "pill";
    });
  }

  /**
   * Initializes a multiple-choice quiz with immediate per-option feedback
   * explaining why the correct answer is right AND why every wrong option is wrong.
   */
  function initQuiz(containerId, config) {
    const root = document.getElementById(containerId);
    if (!root) return;

    const questions = config.questions || [];
    const answersState = new Array(questions.length).fill(null);

    function render() {
      const letters = ["A", "B", "C", "D", "E"];
      const answeredCount = answersState.filter((v) => v !== null).length;
      const correctCount = answersState.filter((v, idx) => v === questions[idx].correctIndex).length;

      const questionsHtml = questions
        .map((q, qIdx) => {
          const chosen = answersState[qIdx];
          const isAnswered = chosen !== null;

          const optionsHtml = q.options
            .map((optText, oIdx) => {
              let cls = "quiz-option";
              if (isAnswered) {
                if (oIdx === chosen && oIdx === q.correctIndex) cls += " selected-correct";
                else if (oIdx === chosen && oIdx !== q.correctIndex) cls += " selected-wrong";
                else if (oIdx === q.correctIndex) cls += " reveal-correct";
              }
              return `
                <button type="button" class="${cls}" data-q="${qIdx}" data-o="${oIdx}" ${isAnswered ? "disabled" : ""}>
                  <strong>${letters[oIdx]}.</strong>
                  <span>${escapeHtml(optText)}</span>
                </button>
              `;
            })
            .join("");

          let feedbackHtml = "";
          if (isAnswered) {
            const isCorrect = chosen === q.correctIndex;
            const rationaleItems = q.explanations
              .map((exp, oIdx) => {
                const tag = oIdx === q.correctIndex ? "Correct" : "Why wrong";
                return `<li><strong>${letters[oIdx]} (${tag}):</strong> ${exp}</li>`;
              })
              .join("");

            feedbackHtml = `
              <div class="quiz-feedback visible">
                <strong>${isCorrect ? "Correct!" : "Incorrect."}</strong>
                ${isCorrect ? "Here is why this holds and why the distractors fail:" : `You chose ${letters[chosen]}, while ${letters[q.correctIndex]} is correct:`}
                <ul class="quiz-rationale-list">${rationaleItems}</ul>
              </div>
            `;
          }

          return `
            <div class="quiz-question">
              <div class="quiz-prompt">Q${qIdx + 1}. ${q.prompt}</div>
              <div class="quiz-options">${optionsHtml}</div>
              ${feedbackHtml}
            </div>
          `;
        })
        .join("");

      const allDone = answeredCount === questions.length && questions.length > 0;
      if (allDone && config.lessonId) {
        const progress = loadProgress();
        progress.quizScores[config.lessonId] = { correct: correctCount, total: questions.length };
        if (correctCount === questions.length) {
          progress.completedLessons[config.lessonId] = true;
        }
        saveProgress(progress);
      }

      root.innerHTML = `
        <div class="widget-box">
          <div class="widget-header">
            <h3 class="widget-title">${escapeHtml(config.title || "Retrieval Quiz")}</h3>
            <span class="pill ${allDone && correctCount === questions.length ? "verified" : ""}">
              Score: ${correctCount}/${questions.length} (${answeredCount} answered)
            </span>
          </div>
          ${questionsHtml}
          <div class="btn-row" style="margin-top: 1.25rem;">
            <button type="button" class="btn subtle" data-action="reset-quiz">Reset Quiz for Retrieval Practice</button>
          </div>
        </div>
      `;

      root.querySelectorAll("button.quiz-option").forEach((btn) => {
        btn.addEventListener("click", function () {
          const qIdx = Number(this.getAttribute("data-q"));
          const oIdx = Number(this.getAttribute("data-o"));
          answersState[qIdx] = oIdx;
          render();
        });
      });

      const resetBtn = root.querySelector('[data-action="reset-quiz"]');
      if (resetBtn) {
        resetBtn.addEventListener("click", function () {
          answersState.fill(null);
          render();
        });
      }
    }

    render();
  }

  /**
   * Updates Master Plan completion bar & per-lesson badges from localStorage
   */
  function initMasterPlan() {
    const progress = loadProgress();
    const rows = document.querySelectorAll("[data-lesson-id]");
    let doneCount = 0;

    rows.forEach((row) => {
      const id = row.getAttribute("data-lesson-id");
      const badge = row.querySelector("[data-role='lesson-badge']");
      const check = row.querySelector("input[type='checkbox']");
      const isDone = Boolean(progress.completedLessons[id]);

      if (isDone) doneCount++;
      if (check) {
        check.checked = isDone;
        check.addEventListener("change", function () {
          const updated = loadProgress();
          if (this.checked) updated.completedLessons[id] = true;
          else delete updated.completedLessons[id];
          saveProgress(updated);
          initMasterPlan();
        });
      }
      if (badge && isDone) {
        badge.textContent = "Completed";
        badge.className = "lesson-status-tag ready";
      }
    });

    const bar = document.getElementById("master-progress-bar");
    const label = document.getElementById("master-progress-label");
    const total = rows.length || 1;
    const pct = Math.round((doneCount / total) * 100);
    if (bar) bar.style.width = `${pct}%`;
    if (label) label.textContent = `${doneCount} of ${total} lessons completed (${pct}%)`;
  }

  global.LessonWidgets = {
    initCodePractice: initCodePractice,
    initQuiz: initQuiz,
    initMasterPlan: initMasterPlan
  };
})(window);
