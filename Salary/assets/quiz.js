/* ==========================================================================
   Reusable Multiple-Choice Code Quiz Widget
   Explains why the correct option is right AND why each wrong option is wrong.
   ========================================================================== */

(function (global) {
  'use strict';

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function mount(containerId, config) {
    const root = typeof containerId === 'string'
      ? document.getElementById(containerId)
      : containerId;
    if (!root) return;

    const questions = config.questions || [];
    const answersState = new Array(questions.length).fill(null);

    root.classList.add('quiz-widget');

    const cardsHtml = questions.map(function (q, qIdx) {
      const letters = ['A', 'B', 'C', 'D', 'E'];
      const optionsHtml = q.options.map(function (opt, oIdx) {
        return `
          <label class="quiz-option" data-q="${qIdx}" data-opt="${oIdx}">
            <input type="radio" name="${escapeHtml(config.id)}_q${qIdx}" value="${oIdx}" />
            <span><strong>${letters[oIdx]}.</strong> ${escapeHtml(opt.text)}</span>
          </label>
        `;
      }).join('');

      return `
        <article class="quiz-card" data-q-card="${qIdx}">
          <div class="quiz-q-header">
            <span class="quiz-q-num">Question ${qIdx + 1} of ${questions.length}</span>
            <span class="pill pill-planned" data-q-badge="${qIdx}">Unanswered</span>
          </div>
          <p style="margin-bottom:0.75rem;"><strong>${q.prompt}</strong></p>
          ${q.code ? `<pre><code>${escapeHtml(q.code)}</code></pre>` : ''}
          <div class="quiz-options">${optionsHtml}</div>
          <div style="display:flex;gap:0.6rem;align-items:center;">
            <button type="button" class="btn btn-primary" data-check-q="${qIdx}">Check Answer</button>
          </div>
          <div class="quiz-feedback hidden" data-q-feedback="${qIdx}"></div>
        </article>
      `;
    }).join('');

    root.innerHTML = cardsHtml + `
      <div class="quiz-summary" data-role="summary">
        <div><strong>Quiz Score:</strong> <span data-role="score">0 / ${questions.length}</span> answered correctly</div>
        <button type="button" class="btn" data-action="reset-quiz">Reset Quiz</button>
      </div>
    `;

    function updateSummary() {
      const correctCount = answersState.filter(function (v) { return v === true; }).length;
      root.querySelector('[data-role="score"]').textContent = correctCount + ' / ' + questions.length;
    }

    questions.forEach(function (q, qIdx) {
      const card = root.querySelector('[data-q-card="' + qIdx + '"]');
      const checkBtn = card.querySelector('[data-check-q="' + qIdx + '"]');
      const feedbackEl = card.querySelector('[data-q-feedback="' + qIdx + '"]');
      const badgeEl = card.querySelector('[data-q-badge="' + qIdx + '"]');
      const optionEls = Array.from(card.querySelectorAll('.quiz-option'));

      checkBtn.addEventListener('click', function () {
        const selectedInput = card.querySelector('input[type="radio"]:checked');
        if (!selectedInput) {
          feedbackEl.classList.remove('hidden');
          feedbackEl.innerHTML = '<div class="quiz-banner wrong">Pick an option first before checking.</div>';
          return;
        }

        const chosenIdx = Number(selectedInput.value);
        const isCorrect = chosenIdx === q.correctIndex;
        answersState[qIdx] = isCorrect;

        optionEls.forEach(function (el, idx) {
          el.classList.remove('is-correct', 'is-wrong');
          if (idx === q.correctIndex) {
            el.classList.add('is-correct');
          } else if (idx === chosenIdx && !isCorrect) {
            el.classList.add('is-wrong');
          }
        });

        badgeEl.textContent = isCorrect ? 'Correct' : 'Review';
        badgeEl.className = 'pill ' + (isCorrect ? 'pill-live' : 'pill-planned');

        const letters = ['A', 'B', 'C', 'D', 'E'];
        const rationaleHtml = q.options.map(function (opt, idx) {
          const right = idx === q.correctIndex;
          const prefix = right ? 'Why ' + letters[idx] + ' is CORRECT:' : 'Why ' + letters[idx] + ' is WRONG:';
          return `
            <div class="rationale-item ${right ? 'right' : 'wrong'}">
              <strong>${prefix}</strong> ${opt.why}
            </div>
          `;
        }).join('');

        feedbackEl.classList.remove('hidden');
        feedbackEl.innerHTML = `
          <div class="quiz-banner ${isCorrect ? 'correct' : 'wrong'}">
            ${isCorrect
              ? 'Correct — Option ' + letters[q.correctIndex] + ' is right.'
              : 'Not quite — you picked ' + letters[chosenIdx] + ', but the correct answer is ' + letters[q.correctIndex] + '.'}
          </div>
          <div class="rationale-list">${rationaleHtml}</div>
        `;

        updateSummary();
      });
    });

    root.querySelector('[data-action="reset-quiz"]').addEventListener('click', function () {
      answersState.fill(null);
      root.querySelectorAll('input[type="radio"]').forEach(function (r) { r.checked = false; });
      root.querySelectorAll('.quiz-option').forEach(function (el) {
        el.classList.remove('is-correct', 'is-wrong');
      });
      root.querySelectorAll('.quiz-feedback').forEach(function (el) {
        el.classList.add('hidden');
        el.innerHTML = '';
      });
      root.querySelectorAll('[data-q-badge]').forEach(function (el) {
        el.textContent = 'Unanswered';
        el.className = 'pill pill-planned';
      });
      updateSummary();
    });
  }

  global.QuizWidget = {
    mount: mount
  };
})(window);
