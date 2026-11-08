// Mobile System Design 2027 - Reusable Quiz Engine
document.addEventListener('DOMContentLoaded', () => {
  const quizzes = document.querySelectorAll('.quiz-container');

  quizzes.forEach((quiz, index) => {
    const quizId = quiz.dataset.quizId || `quiz-${index}`;
    const options = quiz.querySelectorAll('.quiz-option');
    const feedbackEl = quiz.querySelector('.quiz-feedback');
    const explanationEl = quiz.querySelector('.quiz-explanation-text');
    const breakdownEl = quiz.querySelector('.quiz-breakdown-text');

    // Restore saved state
    const saved = localStorage.getItem(`quiz_result_${quizId}`);
    if (saved) {
      try {
        const state = JSON.parse(saved);
        applySelection(quiz, state.selectedIndex, false);
      } catch (e) {
        console.error("Failed to restore quiz state", e);
      }
    }

    options.forEach((opt, optIndex) => {
      opt.addEventListener('click', () => {
        applySelection(quiz, optIndex, true);
      });
    });
  });

  function applySelection(quiz, selectedIndex, shouldSave) {
    const quizId = quiz.dataset.quizId;
    const options = quiz.querySelectorAll('.quiz-option');
    const feedbackEl = quiz.querySelector('.quiz-feedback');
    const explanationEl = quiz.querySelector('.quiz-explanation-text');
    const breakdownEl = quiz.querySelector('.quiz-breakdown-text');
    const correctIndex = parseInt(quiz.dataset.correctIndex, 10);

    options.forEach((opt, i) => {
      opt.classList.remove('correct', 'incorrect');
      if (i === correctIndex) {
        opt.classList.add('correct');
      } else if (i === selectedIndex) {
        opt.classList.add('incorrect');
      }
    });

    const isCorrect = selectedIndex === correctIndex;
    feedbackEl.classList.remove('correct', 'incorrect');
    feedbackEl.classList.add(isCorrect ? 'correct' : 'incorrect');
    feedbackEl.classList.add('show');

    if (explanationEl) {
      const whyMessage = isCorrect 
        ? "<strong>Correct!</strong> " + (quiz.dataset.correctReason || "Great architectural judgment.")
        : "<strong>Incorrect.</strong> " + (options[selectedIndex]?.dataset?.whyWrong || "This design pattern violates scalability or lifecycle safety.");
      explanationEl.innerHTML = whyMessage;
    }

    if (breakdownEl) {
      breakdownEl.style.display = 'block';
    }

    if (shouldSave && quizId) {
      localStorage.setItem(`quiz_result_${quizId}`, JSON.stringify({
        selectedIndex,
        isCorrect,
        timestamp: Date.now()
      }));
    }
  }
});
