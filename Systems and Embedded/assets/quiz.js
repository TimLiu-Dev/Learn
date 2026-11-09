/**
 * Systems & Embedded Courseware Reusable Quiz Runner
 * Handles instant feedback, distractor analysis, and local persistence.
 */

window.SystemsQuiz = (function () {
  const quizState = {};

  function init(lessonKey, totalQuestions) {
    quizState.lessonKey = lessonKey;
    quizState.total = totalQuestions;
    quizState.passed = 0;

    // Check existing progress in localStorage
    const saved = localStorage.getItem(`${lessonKey}_quiz`);
    if (saved === 'done') {
      const pill = document.getElementById('status-quiz');
      if (pill) pill.classList.add('completed');
    }
  }

  function handleAnswer(questionNumber, isCorrect, clickedElement) {
    const card = document.getElementById(`quiz-${questionNumber}`);
    if (!card) return;

    // Clear previous option styles within this question card
    const options = card.querySelectorAll('.quiz-option');
    options.forEach(opt => opt.classList.remove('selected', 'correct', 'wrong'));

    // Apply selection styles
    clickedElement.classList.add('selected');
    if (isCorrect) {
      clickedElement.classList.add('correct');
      quizState[`q${questionNumber}`] = true;
    } else {
      clickedElement.classList.add('wrong');
      quizState[`q${questionNumber}`] = false;
    }

    // Reveal explanation
    const exp = document.getElementById(`quiz-${questionNumber}-exp`);
    if (exp) exp.classList.add('show');

    // Check total completion
    let allPassed = true;
    for (let i = 1; i <= quizState.total; i++) {
      if (!quizState[`q${i}`]) {
        allPassed = false;
        break;
      }
    }

    if (allPassed) {
      const pill = document.getElementById('status-quiz');
      if (pill) pill.classList.add('completed');
      localStorage.setItem(`${quizState.lessonKey}_quiz`, 'done');
    }
  }

  return {
    init: init,
    handleAnswer: handleAnswer
  };
})();
