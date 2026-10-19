/* Shared retrieval-practice quiz widget for lessons.
 *
 * Usage, inside a lesson HTML file:
 *
 *   <div id="quiz1"></div>
 *   <script src="../assets/quiz.js"></script>
 *   <script>
 *     renderQuiz('quiz1', [
 *       {
 *         q: "Question text?",
 *         options: ["Answer one", "Answer two", "Answer three", "Answer four"],
 *         correct: 2 // zero-based index into options
 *       },
 *       // ...more questions
 *     ]);
 *   </script>
 *
 * Design rules this widget enforces for the author:
 * - Renders options in the order given — vary `correct` across questions
 *   yourself so the correct position isn't always the same.
 * - No styling hint distinguishes the correct answer until after it's
 *   picked.
 * - Feedback is immediate per-question (this is the whole point: a tight
 *   feedback loop for retrieval practice).
 */
function renderQuiz(containerId, questions) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.classList.add('quiz');
  const style = document.createElement('style');
  style.textContent = `
    .quiz { margin: 1.5em 0; }
    .quiz-q { margin-bottom: 1.6em; }
    .quiz-q p.prompt { font-weight: 600; margin-bottom: 0.6em; }
    .quiz-options { list-style: none; padding: 0; margin: 0; }
    .quiz-options li { margin-bottom: 0.5em; }
    .quiz-options button {
      display: block; width: 100%; text-align: left;
      font-family: inherit; font-size: 0.95em;
      padding: 0.55em 0.8em; cursor: pointer;
      border: 1px solid var(--rule, #ddd); border-radius: 5px;
      background: var(--paper, #fff); color: inherit;
    }
    .quiz-options button:hover { border-color: var(--accent, #8a3b2f); }
    .quiz-options button.correct { background: var(--good, #2f6b3f); color: #fff; border-color: transparent; }
    .quiz-options button.incorrect { background: var(--bad, #a33b2f); color: #fff; border-color: transparent; }
    .quiz-options button:disabled { cursor: default; }
    .quiz-feedback { font-size: 0.88em; margin-top: 0.5em; color: var(--muted, #666); }
    .quiz-score { font-weight: 600; margin-top: 1em; }
  `;
  container.appendChild(style);

  let score = 0;
  let answered = 0;

  questions.forEach((item, qIndex) => {
    const qDiv = document.createElement('div');
    qDiv.className = 'quiz-q';

    const prompt = document.createElement('p');
    prompt.className = 'prompt';
    prompt.textContent = `${qIndex + 1}. ${item.q}`;
    qDiv.appendChild(prompt);

    const list = document.createElement('ul');
    list.className = 'quiz-options';

    const feedback = document.createElement('div');
    feedback.className = 'quiz-feedback';

    item.options.forEach((optionText, optIndex) => {
      const li = document.createElement('li');
      const btn = document.createElement('button');
      btn.textContent = optionText;
      btn.addEventListener('click', () => {
        const buttons = list.querySelectorAll('button');
        buttons.forEach(b => b.disabled = true);

        if (optIndex === item.correct) {
          btn.classList.add('correct');
          feedback.textContent = item.why ? `Correct. ${item.why}` : 'Correct.';
          score++;
        } else {
          btn.classList.add('incorrect');
          buttons[item.correct].classList.add('correct');
          feedback.textContent = item.why
            ? `Not quite. ${item.why}`
            : 'Not quite — correct answer highlighted above.';
        }
        answered++;
        if (answered === questions.length) {
          const scoreDiv = document.createElement('div');
          scoreDiv.className = 'quiz-score';
          scoreDiv.textContent = `Score: ${score} / ${questions.length}`;
          container.appendChild(scoreDiv);
        }
      });
      li.appendChild(btn);
      list.appendChild(li);
    });

    qDiv.appendChild(list);
    qDiv.appendChild(feedback);
    container.appendChild(qDiv);
  });
}
