/**
 * Reusable Multiple-Choice & Code Quiz Engine
 * Features:
 * - Immediate diagnostic feedback
 * - Per-option explanations (why the right option works AND why every wrong option fails)
 * - Persistent progress tracking in localStorage
 */
(function (global) {
  "use strict";

  const LETTERS = ["A", "B", "C", "D", "E"];

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function loadState(storageKey) {
    try {
      const raw = global.localStorage.getItem(storageKey);
      return raw ? JSON.parse(raw) : {};
    } catch (_) {
      return {};
    }
  }

  function saveState(storageKey, state) {
    try {
      global.localStorage.setItem(storageKey, JSON.stringify(state));
    } catch (_) {
      // Ignore storage quota / private mode issues
    }
  }

  function init(containerId, config) {
    const root = document.getElementById(containerId);
    if (!root) return;

    const lessonId = config.lessonId || "lesson";
    const questions = config.questions || [];
    const storageKey = `ai_media_quiz_${lessonId}`;
    let answers = loadState(storageKey);

    function render() {
      const total = questions.length;
      let answeredCount = 0;
      let correctCount = 0;

      questions.forEach((q) => {
        if (typeof answers[q.id] === "number") {
          answeredCount += 1;
          if (answers[q.id] === q.correctIndex) {
            correctCount += 1;
          }
        }
      });

      const pct = total > 0 ? Math.round((correctCount / total) * 100) : 0;

      let html = `
        <div class="quiz-wrapper">
          <div class="quiz-toolbar">
            <div class="quiz-score">
              Score: ${correctCount} / ${total} (${pct}%) &middot; Answered: ${answeredCount} / ${total}
            </div>
            <button type="button" class="quiz-reset-btn" data-action="reset">Reset Quiz</button>
          </div>
      `;

      questions.forEach((q, qIdx) => {
        const chosen = answers[q.id];
        const isAnswered = typeof chosen === "number";
        const isCorrect = isAnswered && chosen === q.correctIndex;

        html += `<article class="quiz-question" data-qid="${escapeHtml(q.id)}">`;
        html += `<div class="quiz-q-meta">Question ${qIdx + 1} &middot; ${escapeHtml(q.category || "Concept")}</div>`;
        html += `<div class="quiz-q-prompt">${escapeHtml(q.prompt)}</div>`;

        if (q.code) {
          html += `<pre><code>${escapeHtml(q.code)}</code></pre>`;
        }

        html += `<div class="quiz-options">`;
        q.options.forEach((opt, optIdx) => {
          const classes = ["quiz-option-btn"];
          if (isAnswered) {
            if (optIdx === chosen && isCorrect) {
              classes.push("is-correct");
            } else if (optIdx === chosen && !isCorrect) {
              classes.push("is-wrong");
            } else if (optIdx === q.correctIndex) {
              classes.push("is-reveal-correct");
            }
          }
          html += `
            <button
              type="button"
              class="${classes.join(" ")}"
              data-qid="${escapeHtml(q.id)}"
              data-opt="${optIdx}"
              ${isAnswered ? "disabled" : ""}
            >
              <span class="quiz-option-letter">${LETTERS[optIdx]}.</span>
              <span>${escapeHtml(opt.text)}</span>
            </button>
          `;
        });
        html += `</div>`;

        if (isAnswered) {
          html += `<div class="quiz-feedback">`;
          if (isCorrect) {
            html += `<div class="quiz-verdict ok">Correct (${LETTERS[chosen]}). Here is the full diagnostic breakdown:</div>`;
          } else {
            html += `<div class="quiz-verdict bad">Not quite (${LETTERS[chosen]}). The correct answer is ${LETTERS[q.correctIndex]}. Review why each option passes or fails:</div>`;
          }
          html += `<ul class="quiz-breakdown">`;
          q.options.forEach((opt, optIdx) => {
            const right = optIdx === q.correctIndex;
            const tag = right ? "CORRECT" : "WRONG";
            const youPicked = optIdx === chosen ? " (Your pick)" : "";
            html += `
              <li class="${right ? "opt-right" : "opt-wrong"}">
                <strong>${LETTERS[optIdx]} [${tag}${youPicked}]:</strong> ${escapeHtml(opt.why)}
              </li>
            `;
          });
          html += `</ul></div>`;
        }

        html += `</article>`;
      });

      html += `</div>`;
      root.innerHTML = html;

      root.querySelectorAll(".quiz-option-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
          const qid = btn.getAttribute("data-qid");
          const optIdx = Number(btn.getAttribute("data-opt"));
          answers[qid] = optIdx;
          saveState(storageKey, answers);
          render();
        });
      });

      const resetBtn = root.querySelector('[data-action="reset"]');
      if (resetBtn) {
        resetBtn.addEventListener("click", () => {
          answers = {};
          saveState(storageKey, answers);
          render();
        });
      }
    }

    render();
  }

  global.LessonQuiz = { init };
})(window);
