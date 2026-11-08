/**
 * Senior OOD Quiz Engine
 * Powers interactive multiple-choice quizzes with in-depth senior architectural rationales
 */

class QuizEngine {
  constructor(config) {
    this.containerId = config.containerId || 'quizContainer';
    this.storageKey = config.storageKey || 'ood-quiz-state';
    this.questions = config.questions || [];
    this.container = document.getElementById(this.containerId);
    this.answersState = JSON.parse(localStorage.getItem(this.storageKey) || '{}');
    
    this.init();
  }

  init() {
    if (!this.container) return;
    this.render();
  }

  render() {
    this.container.innerHTML = '';

    this.questions.forEach((q, qIndex) => {
      const card = document.createElement('div');
      card.className = 'quiz-card';
      card.id = `quiz-card-${qIndex}`;

      const savedSelection = this.answersState[qIndex];
      const isAnswered = savedSelection !== undefined;

      const header = document.createElement('div');
      header.className = 'quiz-meta';
      header.innerHTML = `
        <span>Question ${qIndex + 1} of ${this.questions.length} • ${q.category || 'Senior Architecture'}</span>
        <span class="badge ${q.difficulty === 'Big Tech' ? 'badge-bigtech' : 'badge-midtech'}">${q.difficulty || 'Mid Tech'}</span>
      `;
      card.appendChild(header);

      const questionTitle = document.createElement('div');
      questionTitle.className = 'quiz-question';
      questionTitle.innerHTML = q.prompt;
      card.appendChild(questionTitle);

      const optionsGroup = document.createElement('div');
      optionsGroup.className = 'quiz-options';

      q.options.forEach((opt, optIndex) => {
        const optionLabel = document.createElement('label');
        optionLabel.className = 'quiz-option';
        optionLabel.id = `q${qIndex}-opt${optIndex}`;

        const isChecked = savedSelection === optIndex;
        let optionClass = '';

        if (isAnswered) {
          if (optIndex === q.correctIndex) {
            optionClass = 'correct';
          } else if (isChecked && optIndex !== q.correctIndex) {
            optionClass = 'wrong';
          }
        }

        if (optionClass) {
          optionLabel.classList.add(optionClass);
        }

        optionLabel.innerHTML = `
          <input type="radio" name="q_${qIndex}" value="${optIndex}" ${isChecked ? 'checked' : ''} ${isAnswered ? 'disabled' : ''}>
          <div>
            <strong>${String.fromCharCode(65 + optIndex)}.</strong> ${opt.text}
          </div>
        `;

        if (!isAnswered) {
          optionLabel.addEventListener('click', () => {
            this.handleSelection(qIndex, optIndex);
          });
        }

        optionsGroup.appendChild(optionLabel);
      });

      card.appendChild(optionsGroup);

      // Feedback block
      const feedback = document.createElement('div');
      feedback.id = `feedback-${qIndex}`;
      feedback.className = `quiz-feedback ${isAnswered ? 'show' : ''} ${isAnswered && savedSelection === q.correctIndex ? 'feedback-correct' : 'feedback-wrong'}`;

      if (isAnswered) {
        const isCorrect = savedSelection === q.correctIndex;
        feedback.innerHTML = this.buildFeedbackHTML(q, isCorrect, savedSelection);
      }

      card.appendChild(feedback);
      this.container.appendChild(card);
    });
  }

  handleSelection(qIndex, optIndex) {
    this.answersState[qIndex] = optIndex;
    localStorage.setItem(this.storageKey, JSON.stringify(this.answersState));
    this.render();
  }

  buildFeedbackHTML(q, isCorrect, userChoice) {
    let breakdownHTML = '<div class="feedback-breakdown"><dl>';
    
    q.options.forEach((opt, i) => {
      const letter = String.fromCharCode(65 + i);
      const isThisCorrect = i === q.correctIndex;
      breakdownHTML += `
        <dt style="color: ${isThisCorrect ? 'var(--accent-emerald)' : 'var(--accent-rose)'};">
          Option ${letter}: ${isThisCorrect ? '✓ Correct Choice' : '✕ Flawed / Suboptimal'}
        </dt>
        <dd>${opt.rationale}</dd>
      `;
    });
    
    breakdownHTML += '</dl></div>';

    return `
      <div>
        <strong>${isCorrect ? '✅ Well done! Exact Senior Architectural assessment.' : '❌ Not quite. Watch out for subtle production traps.'}</strong>
        <p style="margin-top: 6px;">${q.explanation}</p>
      </div>
      ${breakdownHTML}
    `;
  }

  reset() {
    this.answersState = {};
    localStorage.removeItem(this.storageKey);
    this.render();
  }
}

window.QuizEngine = QuizEngine;
