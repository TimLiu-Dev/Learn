// Quiz component: multiple-choice retrieval practice with instant feedback.
//
// Markup:
//   <div class="quiz">
//     <div class="q" data-answer="2">            <!-- 0-based index of the correct option -->
//       <p class="q-prompt">Question text</p>
//       <ol class="options"><li>…</li><li>…</li><li>…</li><li>…</li></ol>
//       <div class="q-explain">Why the answer is right (shown once solved).</div>
//     </div>
//   </div>
//   <script src="../assets/quiz.js"></script>
//
// Wrong picks are struck through and the learner keeps trying; the score counts
// first-try answers only, because the first attempt is the honest retrieval.
(function () {
  document.querySelectorAll(".quiz").forEach(function (quiz) {
    var questions = quiz.querySelectorAll(".q");
    var firstTry = 0, answered = 0;
    var score = document.createElement("p");
    score.className = "quiz-score";
    score.setAttribute("aria-live", "polite");
    score.textContent = "First-try score: 0 / " + questions.length;
    quiz.appendChild(score);

    questions.forEach(function (q, qi) {
      var answer = parseInt(q.dataset.answer, 10);
      var prompt = q.querySelector(".q-prompt");
      if (prompt) prompt.dataset.n = qi + 1;
      var status = document.createElement("div");
      status.className = "q-status";
      status.setAttribute("aria-live", "polite");
      var explain = q.querySelector(".q-explain");
      q.insertBefore(status, explain || null);
      var attempts = 0;

      q.querySelectorAll("ol.options > li").forEach(function (li, i) {
        var btn = document.createElement("button");
        btn.type = "button";
        btn.className = "opt";
        btn.innerHTML = li.innerHTML;
        li.innerHTML = "";
        li.appendChild(btn);
        btn.addEventListener("click", function () {
          if (q.classList.contains("solved")) return;
          attempts++;
          if (i === answer) {
            btn.classList.add("right");
            q.classList.add("solved");
            q.querySelectorAll(".opt").forEach(function (b) { b.disabled = true; });
            status.textContent = attempts === 1 ? "Correct, first try." : "Correct.";
            if (attempts === 1) firstTry++;
            answered++;
            score.textContent = "First-try score: " + firstTry + " / " + questions.length +
              (answered === questions.length ? " — done." : "");
          } else {
            btn.classList.add("wrong");
            btn.disabled = true;
            status.textContent = "Not quite. Think again.";
          }
        });
      });
    });
  });
})();
