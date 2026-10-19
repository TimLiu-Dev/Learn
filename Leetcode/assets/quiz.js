// Multiple-choice retrieval quiz.
// Markup:
//   <div class="quiz">
//     <div class="q" data-answer="1">          <!-- 0-based index of the correct choice -->
//       <p class="prompt">Question?</p>
//       <ol class="choices"><li>A</li><li>B</li><li>C</li></ol>
//       <p class="why">Explanation shown after answering.</p>
//     </div>
//   </div>
document.querySelectorAll(".quiz").forEach((quiz) => {
  const questions = [...quiz.querySelectorAll(".q")];
  const score = document.createElement("p");
  score.className = "quiz-score";
  quiz.appendChild(score);
  let answered = 0, right = 0;

  questions.forEach((q) => {
    const answer = Number(q.dataset.answer);
    const items = [...q.querySelectorAll(".choices li")];
    const buttons = items.map((li, i) => {
      const b = document.createElement("button");
      b.type = "button";
      b.innerHTML = li.innerHTML;
      li.replaceChildren(b);
      b.addEventListener("click", () => {
        buttons.forEach((x) => (x.disabled = true));
        buttons[answer].classList.add("right");
        if (i !== answer) b.classList.add("wrong");
        q.classList.add("answered");
        answered++;
        if (i === answer) right++;
        if (answered === questions.length) {
          score.textContent = `${right} / ${questions.length} correct` +
            (right < questions.length ? " — reread the explanations for the misses." : " — nice.");
        }
      });
      return b;
    });
  });
});
