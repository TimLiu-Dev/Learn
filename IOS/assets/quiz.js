/**
 * Reusable Interactive Quiz Engine with Comprehensive Option Breakdown
 * Explains WHY the correct answer is right AND WHY each distractor is wrong.
 */
document.addEventListener('DOMContentLoaded', () => {
  const quizzes = document.querySelectorAll('.quiz-widget');

  quizzes.forEach((quiz, quizIdx) => {
    const options = quiz.querySelectorAll('.quiz-btn');
    const feedback = quiz.querySelector('.quiz-feedback');
    const breakdown = quiz.querySelector('.quiz-breakdown');
    const correctIndex = parseInt(quiz.getAttribute('data-correct'), 10);
    const quizId = quiz.id || `quiz-${quizIdx}`;

    options.forEach((btn, index) => {
      btn.addEventListener('click', () => {
        // Clear previous states
        options.forEach((b) => b.classList.remove('selected', 'correct', 'incorrect'));
        btn.classList.add('selected');

        const isCorrect = (index === correctIndex);
        const selectedReason = btn.getAttribute('data-reason') || '';
        const correctBtn = options[correctIndex];
        const correctReason = correctBtn ? correctBtn.getAttribute('data-reason') || '' : '';

        if (isCorrect) {
          btn.classList.add('correct');
          if (feedback) {
            feedback.className = 'quiz-feedback visible correct-box';
            feedback.innerHTML = `<strong>✅ Correct!</strong> ${selectedReason}`;
          }
        } else {
          btn.classList.add('incorrect');
          if (correctBtn) correctBtn.classList.add('correct');
          if (feedback) {
            feedback.className = 'quiz-feedback visible incorrect-box';
            feedback.innerHTML = `<strong>❌ Incorrect.</strong> ${selectedReason}<br><br><strong>Correct Answer:</strong> ${correctReason}`;
          }
        }

        // Render exhaustive breakdown of ALL options
        if (breakdown) {
          let html = `<div class="breakdown-card">
            <h4>Detailed Analysis of All Choices:</h4>
            <ul class="breakdown-list">`;

          options.forEach((opt, optIdx) => {
            const isOptCorrect = (optIdx === correctIndex);
            const optText = opt.innerText.replace(/^[A-D]\.\s*/, '').trim();
            const reason = opt.getAttribute('data-reason') || 'No additional explanation available.';
            const badgeClass = isOptCorrect ? 'badge-success' : 'badge-danger';
            const badgeLabel = isOptCorrect ? 'Correct Choice' : 'Incorrect Choice';

            html += `
              <li class="breakdown-item ${isOptCorrect ? 'is-correct' : 'is-wrong'}">
                <div class="breakdown-item-header">
                  <span class="breakdown-badge ${badgeClass}">${badgeLabel}</span>
                  <strong>${String.fromCharCode(65 + optIdx)}. ${optText}</strong>
                </div>
                <p class="breakdown-reason">${reason}</p>
              </li>`;
          });

          html += `</ul></div>`;
          breakdown.innerHTML = html;
          breakdown.style.display = 'block';
        }

        // Save progress to localStorage
        try {
          const pageKey = `quiz_progress_${window.location.pathname}`;
          const progress = JSON.parse(localStorage.getItem(pageKey) || '{}');
          progress[quizId] = isCorrect;
          localStorage.setItem(pageKey, JSON.stringify(progress));
        } catch (e) {
          // Ignore localStorage errors
        }
      });
    });
  });
});
