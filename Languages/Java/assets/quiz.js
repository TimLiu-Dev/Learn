// Antigravity Senior Java Quiz Engine
// Supports:
// 1. new JavaQuiz({ id, questions: [{ question, code?, correctIndex, options: [{ text, explanation }] }] })
//    Renders multiple-choice code & concept quizzes with full per-option explanations (why correct & why each wrong choice fails).
// 2. Legacy checkAnswer(questionId, selectedIdx, correctIdx, explanation)
// 3. Legacy initQuiz(containerId)

function escapeQuizHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function formatInlineCode(str) {
  // Escape HTML first, then convert `inline code` to <code>inline code</code>
  const escaped = escapeQuizHtml(str);
  return escaped.replace(/`([^`]+)`/g, '<code>$1</code>');
}

class JavaQuiz {
  constructor(config) {
    this.id = config.id;
    this.questions = config.questions || [];
    this.letters = ['A', 'B', 'C', 'D', 'E', 'F'];
    this.init();
  }

  init() {
    this.container = document.getElementById(this.id);
    if (!this.container) return;
    this.renderAll();
  }

  renderAll() {
    this.container.innerHTML = '';
    this.questions.forEach((q, qIdx) => {
      const card = document.createElement('div');
      card.className = 'quiz-box';
      card.dataset.qIndex = String(qIdx);
      this.renderQuestionCard(card, q, qIdx);
      this.container.appendChild(card);
    });
  }

  renderQuestionCard(card, q, qIdx) {
    const codeBlock = q.code
      ? `<pre class="quiz-code"><code>${escapeQuizHtml(q.code)}</code></pre>`
      : '';

    let optionsHtml = '<div class="quiz-options">';
    q.options.forEach((opt, optIdx) => {
      const letter = this.letters[optIdx] || String(optIdx + 1);
      optionsHtml += `
        <button type="button" class="quiz-option" data-opt-idx="${optIdx}">
          <span class="quiz-opt-letter">${letter}</span>
          <span class="quiz-opt-text">${formatInlineCode(opt.text)}</span>
        </button>`;
    });
    optionsHtml += '</div>';

    card.innerHTML = `
      <div class="quiz-question">${formatInlineCode(q.question)}</div>
      ${codeBlock}
      ${optionsHtml}
      <div class="quiz-feedback" aria-live="polite"></div>
    `;

    const buttons = card.querySelectorAll('.quiz-option');
    buttons.forEach((btn) => {
      btn.addEventListener('click', () => {
        const selectedIdx = Number(btn.dataset.optIdx);
        this.evaluateSelection(card, q, qIdx, selectedIdx);
      });
    });
  }

  evaluateSelection(card, q, qIdx, selectedIdx) {
    const correctIdx = q.correctIndex;
    const isCorrect = selectedIdx === correctIdx;
    const buttons = card.querySelectorAll('.quiz-option');
    const feedback = card.querySelector('.quiz-feedback');

    buttons.forEach((btn, idx) => {
      btn.classList.add('is-locked');
      btn.disabled = true;
      if (idx === correctIdx) {
        btn.classList.add('is-correct-choice');
      } else if (idx === selectedIdx && !isCorrect) {
        btn.classList.add('is-wrong-choice');
      }
    });

    const selectedLetter = this.letters[selectedIdx] || String(selectedIdx + 1);
    const correctLetter = this.letters[correctIdx] || String(correctIdx + 1);
    const selectedOpt = q.options[selectedIdx] || { explanation: '' };
    const correctOpt = q.options[correctIdx] || { explanation: '' };

    let verdictHtml = '';
    if (isCorrect) {
      verdictHtml = `
        <div class="quiz-verdict">&check; Correct! Option ${correctLetter} is the right answer.</div>
        <div>${formatInlineCode(correctOpt.explanation)}</div>
      `;
    } else {
      verdictHtml = `
        <div class="quiz-verdict">&cross; Not quite &mdash; you chose Option ${selectedLetter} (Correct answer: Option ${correctLetter}).</div>
        <div style="margin-bottom:0.45rem;"><strong>Why Option ${selectedLetter} is wrong:</strong> ${formatInlineCode(selectedOpt.explanation)}</div>
        <div><strong>Why Option ${correctLetter} is correct:</strong> ${formatInlineCode(correctOpt.explanation)}</div>
      `;
    }

    let breakdownHtml = `<div class="quiz-breakdown"><div class="quiz-breakdown-title">Complete Option Breakdown (Why Correct vs. Why Wrong)</div>`;
    q.options.forEach((opt, idx) => {
      const letter = this.letters[idx] || String(idx + 1);
      const right = idx === correctIdx;
      breakdownHtml += `
        <div class="quiz-breakdown-item ${right ? 'bd-correct' : 'bd-wrong'}">
          <span class="bd-tag">${right ? '✓ ' + letter + ' (CORRECT)' : '✗ ' + letter + ' (WRONG)'}:</span>
          <span>${formatInlineCode(opt.explanation)}</span>
        </div>`;
    });
    breakdownHtml += `</div><div><button type="button" class="btn-retry-quiz">&#8635; Reset Question</button></div>`;

    feedback.className = `quiz-feedback ${isCorrect ? 'correct' : 'incorrect'}`;
    feedback.innerHTML = verdictHtml + breakdownHtml;

    const retryBtn = feedback.querySelector('.btn-retry-quiz');
    if (retryBtn) {
      retryBtn.addEventListener('click', () => {
        this.renderQuestionCard(card, q, qIdx);
      });
    }
  }
}

// Legacy helper: checkAnswer(questionId, selectedIdx, correctIdx, explanation)
function checkAnswer(questionId, selectedIdx, correctIdx, explanation) {
  const container = document.getElementById(questionId);
  if (!container) return;
  const feedback = container.querySelector('.quiz-feedback');
  const options = container.querySelectorAll('.quiz-option');

  options.forEach((opt, idx) => {
    opt.style.pointerEvents = 'none';
    if (idx === correctIdx) {
      opt.classList.add('is-correct-choice');
    } else if (idx === selectedIdx && selectedIdx !== correctIdx) {
      opt.classList.add('is-wrong-choice');
    }
  });

  if (feedback) {
    feedback.className = `quiz-feedback ${selectedIdx === correctIdx ? 'correct' : 'incorrect'}`;
    feedback.innerHTML = selectedIdx === correctIdx
      ? `<div class="quiz-verdict">&check; Correct!</div><div>${explanation}</div>`
      : `<div class="quiz-verdict">&cross; Not quite.</div><div>${explanation}</div>`;
  }
}

// Legacy helper: initQuiz(containerId)
function initQuiz(containerId) {
  const container = document.getElementById(containerId);
  if (!container) return;
  const checkBtn = container.querySelector('.btn-check-quiz');
  if (!checkBtn) return;

  checkBtn.addEventListener('click', () => {
    const cards = container.querySelectorAll('.quiz-card');
    cards.forEach((card) => {
      const correctIdx = Number(card.dataset.correct);
      const checked = card.querySelector('input[type="radio"]:checked');
      const feedback = card.querySelector('.quiz-feedback');
      const labels = card.querySelectorAll('.quiz-option');
      if (!feedback) return;

      if (!checked) {
        feedback.className = 'quiz-feedback incorrect';
        feedback.innerHTML = '<strong>Select an option first.</strong>';
        return;
      }

      const selectedIdx = Number(checked.value);
      labels.forEach((lbl, idx) => {
        if (idx === correctIdx) lbl.classList.add('is-correct-choice');
        else if (idx === selectedIdx) lbl.classList.add('is-wrong-choice');
      });

      feedback.className = `quiz-feedback ${selectedIdx === correctIdx ? 'correct' : 'incorrect'}`;
      feedback.innerHTML = selectedIdx === correctIdx
        ? '<strong>&check; Correct!</strong>'
        : `<strong>&cross; Incorrect.</strong> Option ${['A', 'B', 'C', 'D'][correctIdx] || (correctIdx + 1)} is the right choice.`;
    });
  });
}
