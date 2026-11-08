/**
 * Shared Quiz Component for System Design Mastery Lessons
 * Zero external dependencies. Enforces equal-length answers & detailed rationales.
 */

class QuizEngine {
  constructor(containerId, quizData) {
    this.container = document.getElementById(containerId);
    this.quizData = quizData;
    this.selectedOption = null;
    this.isSubmitted = false;
    this.storageKey = `quiz_${quizData.id || window.location.pathname}_${containerId}`;

    if (this.container) {
      this.init();
    }
  }

  init() {
    this.render();
    this.restoreState();
  }

  render() {
    const q = this.quizData;
    const isMulti = q.multiple === true;

    let optionsHtml = q.options.map((opt, idx) => {
      const inputType = isMulti ? 'checkbox' : 'radio';
      const nameAttr = `quiz_opt_${this.container.id}`;
      return `
        <label class="quiz-option" data-index="${idx}">
          <input type="${inputType}" name="${nameAttr}" value="${idx}">
          <span class="quiz-option-text">${opt.text}</span>
        </label>
      `;
    }).join('');

    this.container.innerHTML = `
      <div class="quiz-widget">
        <div class="quiz-badge"><span class="badge badge-mid">Active Recall Check</span></div>
        <div class="quiz-question">${q.question}</div>
        <div class="quiz-options">${optionsHtml}</div>
        <div class="widget-actions" style="margin-top: 1rem;">
          <button class="btn btn-primary submit-btn">Check Answer</button>
          <button class="btn btn-secondary reset-btn" style="display: none;">Retry</button>
        </div>
        <div class="quiz-explanation"></div>
      </div>
    `;

    const options = this.container.querySelectorAll('.quiz-option');
    options.forEach(opt => {
      opt.addEventListener('click', (e) => {
        if (this.isSubmitted) return;
        const input = opt.querySelector('input');
        if (e.target !== input) {
          if (isMulti) {
            input.checked = !input.checked;
          } else {
            input.checked = true;
          }
        }
      });
    });

    const submitBtn = this.container.querySelector('.submit-btn');
    submitBtn.addEventListener('click', () => this.checkAnswer());

    const resetBtn = this.container.querySelector('.reset-btn');
    resetBtn.addEventListener('click', () => this.resetQuiz());
  }

  checkAnswer() {
    const inputs = this.container.querySelectorAll('input:checked');
    if (inputs.length === 0) {
      alert('Please select an option before checking.');
      return;
    }

    this.isSubmitted = true;
    const selectedIndices = Array.from(inputs).map(i => parseInt(i.value, 10));
    const q = this.quizData;
    const explanationBox = this.container.querySelector('.quiz-explanation');
    const optionCards = this.container.querySelectorAll('.quiz-option');
    const submitBtn = this.container.querySelector('.submit-btn');
    const resetBtn = this.container.querySelector('.reset-btn');

    let allCorrect = true;
    const correctIndices = q.correctIndices || [q.correctIndex];

    optionCards.forEach((card, idx) => {
      const isCardCorrect = correctIndices.includes(idx);
      const isCardSelected = selectedIndices.includes(idx);

      if (isCardCorrect) {
        card.classList.add('correct');
      } else if (isCardSelected) {
        card.classList.add('incorrect');
        allCorrect = false;
      }
    });

    // Check if user missed any correct answers
    for (let c of correctIndices) {
      if (!selectedIndices.includes(c)) {
        allCorrect = false;
      }
    }

    // Compose rationale report
    let rationaleHtml = `
      <div class="callout ${allCorrect ? 'callout-tip' : 'callout-danger'}">
        <div class="callout-title">${allCorrect ? '✓ Correct Understanding!' : '✗ Conceptual Gap Detected'}</div>
        <p>${q.summary || ''}</p>
        <div style="margin-top: 0.85rem; border-top: 1px solid var(--border-subtle); padding-top: 0.75rem;">
          <strong>Detailed Breakdown:</strong>
          <ul style="margin-top: 0.5rem; margin-bottom: 0;">
            ${q.options.map((opt, idx) => `
              <li>
                <strong>Option ${String.fromCharCode(65 + idx)} (${correctIndices.includes(idx) ? 'Correct' : 'Incorrect'}):</strong>
                ${opt.rationale}
              </li>
            `).join('')}
          </ul>
        </div>
      </div>
    `;

    explanationBox.innerHTML = rationaleHtml;
    explanationBox.classList.add('visible');

    submitBtn.style.display = 'none';
    resetBtn.style.display = 'inline-flex';

    // Persist
    localStorage.setItem(this.storageKey, JSON.stringify({
      selectedIndices,
      allCorrect,
      completedAt: new Date().toISOString()
    }));
  }

  resetQuiz() {
    this.isSubmitted = false;
    const optionCards = this.container.querySelectorAll('.quiz-option');
    optionCards.forEach(c => {
      c.classList.remove('correct', 'incorrect');
      const input = c.querySelector('input');
      if (input) input.checked = false;
    });

    const explanationBox = this.container.querySelector('.quiz-explanation');
    explanationBox.innerHTML = '';
    explanationBox.classList.remove('visible');

    const submitBtn = this.container.querySelector('.submit-btn');
    const resetBtn = this.container.querySelector('.reset-btn');
    submitBtn.style.display = 'inline-flex';
    resetBtn.style.display = 'none';

    localStorage.removeItem(this.storageKey);
  }

  restoreState() {
    const saved = localStorage.getItem(this.storageKey);
    if (!saved) return;
    try {
      const data = JSON.parse(saved);
      if (data && data.selectedIndices) {
        data.selectedIndices.forEach(idx => {
          const input = this.container.querySelector(`input[value="${idx}"]`);
          if (input) input.checked = true;
        });
        this.checkAnswer();
      }
    } catch (e) {
      // ignore
    }
  }
}

window.QuizEngine = QuizEngine;
