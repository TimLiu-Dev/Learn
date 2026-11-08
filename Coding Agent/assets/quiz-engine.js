/**
 * Interactive Quiz Component
 * Adheres strictly to pedagogical guidelines:
 * - Equal-length phrasing across choices to avoid formatting leaks
 * - Immediate feedback showing why the answer is correct or incorrect
 * - Complete breakdown of why each individual distractor is wrong
 */

class QuizEngine {
  constructor(options) {
    this.containerId = options.containerId;
    this.container = document.getElementById(this.containerId);
    if (!this.container) return;

    this.questions = options.questions || [];
    this.userAnswers = {};
    this.render();
  }

  render() {
    let html = "";
    this.questions.forEach((q, qIndex) => {
      html += `
        <div class="quiz-card" id="quiz-card-${qIndex}">
          <div class="quiz-card-header">
            <span class="quiz-badge">Question ${qIndex + 1} of ${this.questions.length}</span>
            <span class="tag ${q.difficulty === 'Senior' ? 'benchmark' : 'time'}">${q.difficulty || 'Concept'}</span>
          </div>
          <div class="quiz-question">${q.question}</div>
          <div class="quiz-options">
            ${q.options.map((opt, optIndex) => {
              const letter = String.fromCharCode(65 + optIndex);
              return `
                <div class="quiz-option" data-q="${qIndex}" data-opt="${optIndex}">
                  <span class="option-letter">${letter}</span>
                  <span class="option-text">${opt.text}</span>
                </div>
              `;
            }).join("")}
          </div>
          <div class="quiz-explanation" id="quiz-exp-${qIndex}">
            <h5 class="exp-title"></h5>
            <div class="exp-body"></div>
            <div class="distractor-breakdown"></div>
          </div>
        </div>
      `;
    });

    this.container.innerHTML = html;

    // Attach listeners
    this.container.querySelectorAll(".quiz-option").forEach(el => {
      el.addEventListener("click", (e) => {
        const qIndex = parseInt(el.getAttribute("data-q"), 10);
        const optIndex = parseInt(el.getAttribute("data-opt"), 10);
        this.selectOption(qIndex, optIndex);
      });
    });
  }

  selectOption(qIndex, optIndex) {
    const q = this.questions[qIndex];
    const card = document.getElementById(`quiz-card-${qIndex}`);
    const expBox = document.getElementById(`quiz-exp-${qIndex}`);
    const options = card.querySelectorAll(".quiz-option");

    const isCorrect = optIndex === q.correctIndex;
    this.userAnswers[qIndex] = optIndex;

    options.forEach((optEl, i) => {
      optEl.classList.remove("selected-correct", "selected-wrong", "show-correct");
      if (i === optIndex) {
        if (isCorrect) {
          optEl.classList.add("selected-correct");
        } else {
          optEl.classList.add("selected-wrong");
        }
      }
      if (i === q.correctIndex && !isCorrect) {
        optEl.classList.add("show-correct");
      }
    });

    // Populate explanation
    expBox.classList.remove("correct", "incorrect");
    expBox.classList.add("show");
    expBox.classList.add(isCorrect ? "correct" : "incorrect");

    const titleEl = expBox.querySelector(".exp-title");
    const bodyEl = expBox.querySelector(".exp-body");
    const distractorEl = expBox.querySelector(".distractor-breakdown");

    if (isCorrect) {
      titleEl.innerHTML = `✓ Correct! Excellent architectural rationale.`;
    } else {
      titleEl.innerHTML = `✗ Incorrect. Review the mechanics below:`;
    }

    bodyEl.innerHTML = `<strong>Core Rationale:</strong> ${q.explanation}`;

    let breakdownHtml = `<strong style="display:block; margin-bottom:6px; color:#e2e8f0;">Option Breakdown:</strong>`;
    q.options.forEach((opt, i) => {
      const letter = String.fromCharCode(65 + i);
      const isThisCorrect = i === q.correctIndex;
      const statusIcon = isThisCorrect ? "✅" : "❌";
      breakdownHtml += `
        <div style="margin-bottom:6px;">
          <strong>${statusIcon} [${letter}]:</strong> ${opt.rationale}
        </div>
      `;
    });
    distractorEl.innerHTML = breakdownHtml;
  }
}

window.QuizEngine = QuizEngine;
