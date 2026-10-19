// Shared behaviour for lessons and reference pages.
//
// Components:
//   <code class="kt-run">   runnable, editable Kotlin (JetBrains Kotlin Playground embed)
//   <code class="kt-show">  syntax-highlighted, read-only Kotlin
//   <div class="quiz" data-answer="b">  multiple choice; options are <button data-key="a">
//   <p class="quiz-score">  optional running score for the page's quizzes
//
// Runnable code needs the page served over http (./serve.sh), not opened as file://.

(function () {
  const PLAYGROUND_SRC = "https://unpkg.com/kotlin-playground@1.34.0";

  function prefersDark() {
    const forced = document.documentElement.dataset.theme;
    if (forced) return forced === "dark";
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
  }

  function initPlayground() {
    const blocks = document.querySelectorAll("code.kt-run, code.kt-show");
    if (!blocks.length) return;

    const theme = prefersDark() ? "darcula" : "idea";
    blocks.forEach((el) => {
      if (!el.hasAttribute("theme")) el.setAttribute("theme", theme);
      el.setAttribute("data-crosslink", "enabled");
      if (el.classList.contains("kt-show")) {
        el.setAttribute("data-highlight-only", "");
        if (!el.hasAttribute("none-markers")) el.setAttribute("none-markers", "");
      } else {
        el.setAttribute("match-brackets", "true");
        el.setAttribute("auto-indent", "true");
      }
    });

    if (location.protocol === "file:" && document.querySelector("code.kt-run")) {
      const warn = document.createElement("p");
      warn.className = "server-warning";
      warn.innerHTML =
        "Run buttons need the local server. In the workspace folder run <code>./serve.sh</code>, " +
        "then open this page from <code>http://localhost:8000</code>. " +
        "Or use the “open in Playground” link on each snippet.";
      const main = document.querySelector("main");
      main.insertBefore(warn, main.firstChild);
    }

    const s = document.createElement("script");
    s.src = PLAYGROUND_SRC;
    s.onload = () => window.KotlinPlayground("code.kt-run, code.kt-show");
    document.head.appendChild(s);
  }

  function initQuizzes() {
    const quizzes = Array.from(document.querySelectorAll(".quiz"));
    const scoreEl = document.querySelector(".quiz-score");
    let answered = 0;
    let correct = 0;

    function updateScore() {
      if (!scoreEl) return;
      scoreEl.textContent = `${correct} / ${answered} correct · ${quizzes.length - answered} left`;
    }

    quizzes.forEach((quiz) => {
      const answer = quiz.dataset.answer;
      const buttons = Array.from(quiz.querySelectorAll(".options button"));
      const explain = quiz.querySelector(".explain");

      buttons.forEach((btn) => {
        btn.type = "button";
        btn.addEventListener("click", () => {
          if (quiz.classList.contains("answered")) return;
          quiz.classList.add("answered");
          const right = btn.dataset.key === answer;
          answered += 1;
          if (right) correct += 1;

          buttons.forEach((b) => {
            b.disabled = true;
            if (b.dataset.key === answer) b.classList.add("correct");
          });
          if (!right) btn.classList.add("wrong");

          if (explain) {
            const verdict = document.createElement("span");
            verdict.className = "verdict " + (right ? "good" : "bad");
            verdict.textContent = right ? "Correct." : "Not quite.";
            explain.insertBefore(verdict, explain.firstChild);
          }
          updateScore();
        });
      });
    });
    updateScore();
  }

  document.addEventListener("DOMContentLoaded", () => {
    initQuizzes();
    initPlayground();
  });
})();
