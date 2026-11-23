/* ==========================================================================
   Reusable Multiple-Choice Code Quiz Engine (./assets/quiz.js)
   Renders code-based MCQs with immediate feedback and full per-option
   "why right / why wrong" explanations. Zero external dependencies.
   ========================================================================== */

(function () {
  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function formatInlineCode(str) {
    return escapeHtml(str).replace(/`([^`]+)`/g, "<code>$1</code>");
  }

  function shuffleArray(items) {
    const arr = items.slice();
    for (let i = arr.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [arr[i], arr[j]] = [arr[j], arr[i]];
    }
    return arr;
  }

  function initQuizzes() {
    const containers = document.querySelectorAll(".quiz-widget[data-quiz-id]");
    const scoreEl = document.getElementById("quiz-score-value");
    const totalEl = document.getElementById("quiz-total-value");
    const statusEl = document.getElementById("quiz-mastery-status");
    const initialStatus = statusEl
      ? statusEl.textContent
      : "Complete all quizzes below to verify lesson mastery.";
    const state = { total: containers.length, correctMap: {} };

    if (totalEl) totalEl.textContent = String(state.total);

    function updateScoreboard() {
      const correctCount = Object.values(state.correctMap).filter(Boolean).length;
      const answeredCount = Object.keys(state.correctMap).length;
      if (scoreEl) scoreEl.textContent = String(correctCount);
      if (statusEl) {
        if (answeredCount === 0) {
          statusEl.textContent = initialStatus;
        } else if (correctCount === state.total && answeredCount === state.total) {
          statusEl.textContent = "All quizzes verified! Ready to log your win and advance.";
        } else {
          statusEl.textContent = `${answeredCount}/${state.total} answered — review the right & wrong breakdowns carefully.`;
        }
      }
    }

    containers.forEach((container, index) => {
      const dataScript = container.querySelector("script.quiz-data[type='application/json']");
      if (!dataScript) return;

      let quiz;
      try {
        quiz = JSON.parse(dataScript.textContent);
      } catch (err) {
        container.innerHTML = `<p>Quiz configuration error: ${escapeHtml(err.message)}</p>`;
        return;
      }

      const quizId = container.getAttribute("data-quiz-id") || `q-${index + 1}`;
      const letters = ["A", "B", "C", "D", "E"];
      let activeOptions = shuffleArray(quiz.options || []);

      const card = document.createElement("div");
      card.className = "quiz-card";

      const codeHtml = quiz.code
        ? `<pre><code>${escapeHtml(quiz.code)}</code></pre>`
        : "";

      card.innerHTML = `
        <div class="quiz-header">
          <span><strong>Quiz ${index + 1}</strong> · ${escapeHtml(quiz.tag || "Code Verification")}</span>
          <button type="button" class="btn-small quiz-reset" hidden>Reset</button>
        </div>
        <p class="quiz-prompt">${formatInlineCode(quiz.prompt)}</p>
        ${codeHtml}
        <div class="quiz-options" role="group" aria-label="Quiz options"></div>
        <div class="quiz-feedback" hidden></div>
      `;

      container.appendChild(card);

      const optionsContainer = card.querySelector(".quiz-options");
      const feedbackEl = card.querySelector(".quiz-feedback");
      const resetBtn = card.querySelector(".quiz-reset");

      function renderOptions() {
        optionsContainer.innerHTML = activeOptions
          .map((opt, idx) => {
            const key = letters[idx] || String(idx + 1);
            const labelHtml = opt.isCode
              ? `<code>${escapeHtml(opt.text)}</code>`
              : formatInlineCode(opt.text);
            return `
              <button type="button" class="quiz-option" data-opt-index="${idx}">
                <span class="quiz-option-key">${key}.</span>
                <span>${labelHtml}</span>
              </button>
            `;
          })
          .join("");

        optionsContainer.querySelectorAll(".quiz-option").forEach((btn) => {
          btn.addEventListener("click", () => {
            const idx = Number(btn.getAttribute("data-opt-index"));
            renderFeedback(idx);
          });
        });
      }

      function renderFeedback(selectedIdx) {
        const chosen = activeOptions[selectedIdx];
        const isCorrect = Boolean(chosen && chosen.correct);
        state.correctMap[quizId] = isCorrect;
        updateScoreboard();

        const optionButtons = optionsContainer.querySelectorAll(".quiz-option");
        optionButtons.forEach((btn, idx) => {
          btn.disabled = true;
          btn.classList.remove("is-correct", "is-wrong");
          if (activeOptions[idx].correct) {
            btn.classList.add("is-correct");
          } else if (idx === selectedIdx) {
            btn.classList.add("is-wrong");
          }
        });

        const breakdownItems = activeOptions
          .map((opt, idx) => {
            const key = letters[idx];
            const statusLabel = opt.correct ? "Why Correct" : "Why Wrong";
            const itemClass = [
              opt.correct ? "is-right-item" : "is-wrong-item",
              idx === selectedIdx ? "is-selected-item" : ""
            ]
              .filter(Boolean)
              .join(" ");
            const yourPick = idx === selectedIdx ? " (Your pick)" : "";
            return `
              <li class="${itemClass}">
                <strong>${key} — ${statusLabel}${yourPick}:</strong> ${formatInlineCode(opt.why)}
              </li>
            `;
          })
          .join("");

        feedbackEl.innerHTML = `
          <div class="quiz-verdict ${isCorrect ? "ok" : "wrong"}">
            ${isCorrect
              ? `Correct (${letters[selectedIdx]}). Here is the full breakdown:`
              : `Not quite (${letters[selectedIdx]}). Review why the right option works and why each distractor fails:`}
          </div>
          <ul class="quiz-breakdown">${breakdownItems}</ul>
        `;
        feedbackEl.hidden = false;
        resetBtn.hidden = false;
      }

      resetBtn.addEventListener("click", () => {
        delete state.correctMap[quizId];
        activeOptions = shuffleArray(quiz.options || []);
        updateScoreboard();
        renderOptions();
        feedbackEl.hidden = true;
        feedbackEl.innerHTML = "";
        resetBtn.hidden = true;
      });

      renderOptions();
    });

    updateScoreboard();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initQuizzes);
  } else {
    initQuizzes();
  }
})();
