// Quiz component: multiple-choice retrieval practice with instant feedback
// and per-option explanations ("why it is right / why it is wrong").
//
// Markup:
//   <div class="quiz">
//     <div class="q" data-answer="2">            <!-- 0-based index of correct option -->
//       <p class="q-prompt">Question text...</p>
//       <ol class="options">
//         <li data-why="Wrong because of X...">Option A</li>
//         <li data-why="Wrong because of Y...">Option B</li>
//         <li data-why="Right because of Z...">Option C</li>
//         <li data-why="Wrong because of W...">Option D</li>
//       </ol>
//       <div class="q-explain">Comprehensive synthesis shown once solved.</div>
//     </div>
//   </div>
//   <script src="../assets/quiz.js"></script>
//
// Wrong picks display why that choice is wrong and are struck through;
// the score counts first-try answers only for honest retrieval practice.
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

      var optionLis = q.querySelectorAll("ol.options > li");
      optionLis.forEach(function (li, i) {
        var why = li.dataset.why || "";
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
            var prefix = attempts === 1 ? "<strong>Correct, first try!</strong> " : "<strong>Correct!</strong> ";
            status.innerHTML = prefix + (why ? why : "");
            if (attempts === 1) firstTry++;
            answered++;
            score.textContent = "First-try score: " + firstTry + " / " + questions.length +
              (answered === questions.length ? " — done." : "");
          } else {
            btn.classList.add("wrong");
            btn.disabled = true;
            status.innerHTML = "<strong>Not quite.</strong> " + (why ? why : "Think again.");
          }
        });
      });
    });
  });
})();
