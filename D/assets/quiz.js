/**
 * Reusable Multiple-Choice Quiz Engine for End-to-End Dating Lessons.
 * Reads JSON from <script type="application/json" id="quiz-data"> inside the lesson.
 * Displays immediate feedback explaining BOTH why the correct option is right
 * AND why every incorrect option is wrong.
 */
(function () {
  "use strict";

  const LETTERS = ["A", "B", "C", "D", "E", "F"];

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function initQuiz() {
    const dataEl = document.getElementById("quiz-data");
    const root = document.getElementById("quiz-root");
    if (!dataEl || !root) return;

    let questions;
    try {
      questions = JSON.parse(dataEl.textContent);
    } catch (err) {
      root.innerHTML = "<p>Quiz configuration error.</p>";
      return;
    }

    const state = {
      answers: new Array(questions.length).fill(null)
    };

    function render() {
      const answeredCount = state.answers.filter((a) => a !== null).length;
      const correctCount = state.answers.reduce((acc, ans, idx) => {
        if (ans === null) return acc;
        return acc + (ans === questions[idx].correctIndex ? 1 : 0);
      }, 0);

      let html = `
        <div class="quiz-header">
          <h3 class="quiz-title">Retrieval Check — Scenario Quiz</h3>
          <div class="quiz-controls">
            <span class="quiz-score">Score: ${correctCount} / ${questions.length}</span>
            <button type="button" class="btn" id="quiz-reset-btn">Reset Quiz</button>
          </div>
        </div>
      `;

      questions.forEach((q, qIdx) => {
        const chosen = state.answers[qIdx];
        const isAnswered = chosen !== null;
        const isCorrect = chosen === q.correctIndex;

        html += `<div class="quiz-question" data-q="${qIdx}">`;
        html += `<p class="quiz-prompt">${qIdx + 1}. ${escapeHtml(q.prompt)}</p>`;
        html += `<div class="quiz-options">`;

        q.options.forEach((optText, oIdx) => {
          let cls = "quiz-opt";
          if (isAnswered) {
            if (oIdx === q.correctIndex) cls += " is-correct";
            else if (oIdx === chosen) cls += " is-wrong";
          }
          html += `
            <button
              type="button"
              class="${cls}"
              data-q="${qIdx}"
              data-o="${oIdx}"
              ${isAnswered ? "disabled" : ""}
            >
              <span class="quiz-opt-letter">${LETTERS[oIdx]}.</span>
              <span>${escapeHtml(optText)}</span>
            </button>
          `;
        });

        html += `</div>`;

        if (isAnswered) {
          html += `<div class="quiz-feedback">`;
          html += `<div class="quiz-verdict ${isCorrect ? "ok" : "bad"}">${
            isCorrect
              ? `Correct (${LETTERS[q.correctIndex]}).`
              : `Incorrect — you selected ${LETTERS[chosen]}, correct answer is ${LETTERS[q.correctIndex]}.`
          }</div>`;
          html += `<ul class="quiz-breakdown">`;
          q.options.forEach((_, oIdx) => {
            const right = oIdx === q.correctIndex;
            const tag = right ? "Why Correct" : "Why Wrong";
            html += `
              <li class="${right ? "correct-item" : ""}">
                <strong>${LETTERS[oIdx]} (${tag}):</strong> ${escapeHtml(q.explanations[oIdx])}
              </li>
            `;
          });
          html += `</ul></div>`;
        }

        html += `</div>`;
      });

      if (answeredCount === questions.length) {
        const pct = Math.round((correctCount / questions.length) * 100);
        const verdict =
          pct >= 80
            ? "Strong calibration. You are ready for the Field Micro-Rep below."
            : "Review the highlighted breakdowns and click Reset Quiz before doing the Field Micro-Rep.";
        html += `
          <div class="quiz-summary">
            <strong>Final Score: ${correctCount} / ${questions.length} (${pct}%).</strong> ${escapeHtml(verdict)}
          </div>
        `;
      }

      root.innerHTML = html;

      root.querySelectorAll(".quiz-opt").forEach((btn) => {
        btn.addEventListener("click", function () {
          const qIdx = Number(this.getAttribute("data-q"));
          const oIdx = Number(this.getAttribute("data-o"));
          if (state.answers[qIdx] === null) {
            state.answers[qIdx] = oIdx;
            render();
          }
        });
      });

      const resetBtn = document.getElementById("quiz-reset-btn");
      if (resetBtn) {
        resetBtn.addEventListener("click", function () {
          state.answers = new Array(questions.length).fill(null);
          render();
        });
      }
    }

    render();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initQuiz);
  } else {
    initQuiz();
  }
})();
