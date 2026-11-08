/**
 * Universal Interactive Quiz Component for KMP Lessons
 * Supports uniform options, instant feedback, and detailed "Why Correct / Why Wrong" explanations.
 */

class KmpQuiz {
  constructor(containerId, config) {
    this.container = document.getElementById(containerId);
    if (!this.container) return;
    this.config = config;
    this.selectedOption = null;
    this.render();
  }

  render() {
    const { question, codeSnippet, options, correctIndex, explanation } = this.config;

    let html = `
      <div class="kmp-quiz-wrapper">
        <div class="quiz-header">
          <span class="quiz-badge">🧠 Retrieval Practice Quiz</span>
        </div>
        <p class="quiz-question">${question}</p>
    `;

    if (codeSnippet) {
      html += `<pre class="quiz-code"><code>${this.escapeHtml(codeSnippet)}</code></pre>`;
    }

    html += `<div class="quiz-options-group">`;
    options.forEach((opt, idx) => {
      html += `
        <button class="quiz-option-btn" data-index="${idx}">
          <span class="option-letter">${String.fromCharCode(65 + idx)}</span>
          <span class="option-text">${opt}</span>
        </button>
      `;
    });
    html += `</div>`;

    html += `
      <div class="quiz-explanation-drawer" style="display: none;">
        <div class="explanation-status"></div>
        <div class="why-correct-box">
          <h5>✅ Why the correct answer is right:</h5>
          <p class="why-correct-text">${explanation.whyCorrect}</p>
        </div>
        <div class="why-wrong-box">
          <h5>❌ Breakdown of incorrect options:</h5>
          <ul class="why-wrong-list">
            ${explanation.whyWrong.map(item => `<li><strong>Option ${item.letter}:</strong> ${item.reason}</li>`).join('')}
          </ul>
        </div>
      </div>
    </div>`;

    this.container.innerHTML = html;
    this.attachEvents();
  }

  attachEvents() {
    const buttons = this.container.querySelectorAll('.quiz-option-btn');
    const drawer = this.container.querySelector('.quiz-explanation-drawer');
    const statusBox = this.container.querySelector('.explanation-status');
    const { correctIndex } = this.config;

    buttons.forEach(btn => {
      btn.addEventListener('click', () => {
        const selectedIdx = parseInt(btn.dataset.index, 10);
        this.selectedOption = selectedIdx;

        // Reset state of all buttons
        buttons.forEach((b, idx) => {
          b.disabled = true;
          b.classList.remove('selected-correct', 'selected-wrong');
          if (idx === correctIndex) {
            b.classList.add('reveal-correct');
          }
        });

        const isCorrect = selectedIdx === correctIndex;
        if (isCorrect) {
          btn.classList.add('selected-correct');
          statusBox.className = 'explanation-status status-pass';
          statusBox.innerHTML = '<strong>Spot on!</strong> You chose the correct answer.';
        } else {
          btn.classList.add('selected-wrong');
          statusBox.className = 'explanation-status status-fail';
          statusBox.innerHTML = '<strong>Not quite.</strong> See the detailed breakdown below to fix the mental model.';
        }

        drawer.style.display = 'block';
      });
    });
  }

  escapeHtml(str) {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }
}
