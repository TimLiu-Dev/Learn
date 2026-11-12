/* ==========================================================================
   Reusable Diagnostic Multiple-Choice Code Quiz Engine
   Displays balanced options and per-option autopsy (why right AND why wrong).
   ========================================================================== */
(function () {
  const quizRegistry = {};
  const LETTERS = ["A", "B", "C", "D", "E"];

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function mountQuizSuite(container, quizId, suite) {
    const savedSuite = window.FDECourseProgress?.getQuizState(suite.lessonId, quizId) || {};
    container.innerHTML = "";

    suite.items.forEach((item, idx) => {
      const card = document.createElement("article");
      card.className = "quiz-card";
      const prev = savedSuite[item.id];
      let selectedIdx = typeof prev?.chosenIndex === "number" ? prev.chosenIndex : null;
      let checked = typeof prev?.chosenIndex === "number";

      function render() {
        const optionsHtml = item.options
          .map((optText, optIdx) => {
            let cls = "quiz-option";
            if (checked) {
              if (optIdx === item.correctIndex) cls += " correct";
              else if (optIdx === selectedIdx) cls += " wrong";
            } else if (optIdx === selectedIdx) {
              cls += " selected";
            }
            return `
              <label class="${cls}">
                <input type="radio" name="${escapeHtml(quizId)}_${escapeHtml(item.id)}" value="${optIdx}"
                  ${selectedIdx === optIdx ? "checked" : ""} ${checked ? "disabled" : ""}>
                <span><strong>${LETTERS[optIdx]}.</strong> ${escapeHtml(optText)}</span>
              </label>`;
          })
          .join("");

        let feedbackHtml = "";
        if (checked && selectedIdx !== null) {
          const isRight = selectedIdx === item.correctIndex;
          const autopsyRows = item.optionFeedback
            .map((reason, rIdx) => {
              const rightOpt = rIdx === item.correctIndex;
              return `<li class="${rightOpt ? "is-right" : "is-wrong"}">
                <strong>${LETTERS[rIdx]} (${rightOpt ? "Correct" : "Wrong"}):</strong> ${escapeHtml(reason)}
              </li>`;
            })
            .join("");

          feedbackHtml = `
            <div class="quiz-feedback ${isRight ? "correct" : "wrong"}">
              <div class="quiz-feedback-summary">
                ${isRight ? "Correct." : `Not quite — ${LETTERS[item.correctIndex]} is right.`}
                ${escapeHtml(item.summary)}
              </div>
              <ul class="option-autopsy">${autopsyRows}</ul>
            </div>`;
        }

        card.innerHTML = `
          <div class="quiz-head">
            <span class="quiz-num">Question ${idx + 1} of ${suite.items.length} · ${escapeHtml(item.tag || "FDE Diagnosis")}</span>
            ${
              checked
                ? `<span class="badge ${selectedIdx === item.correctIndex ? "badge-ready" : "badge-planned"}">
                    ${selectedIdx === item.correctIndex ? "Verified" : "Review"}
                   </span>`
                : ""
            }
          </div>
          <p class="quiz-prompt">${escapeHtml(item.prompt)}</p>
          ${item.code ? `<pre class="quiz-code"><code>${escapeHtml(item.code)}</code></pre>` : ""}
          <div class="quiz-options">${optionsHtml}</div>
          <div class="quiz-actions">
            ${
              !checked
                ? `<button type="button" class="btn btn-primary" data-check ${selectedIdx === null ? "disabled" : ""}>Check Answer</button>`
                : `<button type="button" class="btn btn-ghost" data-retry>Retry Question</button>`
            }
          </div>
          ${feedbackHtml}
        `;

        card.querySelectorAll('input[type="radio"]').forEach((radio) => {
          radio.addEventListener("change", (e) => {
            selectedIdx = Number(e.target.value);
            render();
          });
        });

        const checkBtn = card.querySelector("[data-check]");
        if (checkBtn) {
          checkBtn.addEventListener("click", () => {
            if (selectedIdx === null) return;
            checked = true;
            const isRight = selectedIdx === item.correctIndex;
            window.FDECourseProgress?.markQuizAnswered(
              suite.lessonId,
              quizId,
              item.id,
              selectedIdx,
              isRight
            );
            render();
          });
        }

        const retryBtn = card.querySelector("[data-retry]");
        if (retryBtn) {
          retryBtn.addEventListener("click", () => {
            checked = false;
            selectedIdx = null;
            render();
          });
        }
      }

      render();
      container.appendChild(card);
    });
  }

  window.FDEQuizzes = {
    register(quizId, suite) {
      quizRegistry[quizId] = suite;
    },
    mountAll() {
      document.querySelectorAll("[data-quiz-id]").forEach((el) => {
        const id = el.getAttribute("data-quiz-id");
        if (quizRegistry[id]) {
          mountQuizSuite(el, id, quizRegistry[id]);
        }
      });
    }
  };

  document.addEventListener("DOMContentLoaded", () => {
    window.FDEQuizzes.mountAll();
  });
})();
