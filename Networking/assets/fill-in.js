// Fill-in exercise: blanks placed anywhere in ordinary markup (a table, a list,
// a sentence), checked when the learner asks.
//
// Markup:
//   <div class="widget fill-in">
//     …any HTML…
//     <input class="blank" data-answer="203.0.113.7" size="13" aria-label="Source IP after NAT">
//     …
//   </div>
//   <script src="../assets/fill-in.js"></script>
//
// data-answer: the expected text. Several accepted answers are separated by "|".
// Comparison ignores surrounding spaces and is otherwise exact, case included
// (list both spellings in data-answer if case shouldn't matter).
// The script adds a "Check" button (Enter in any blank also checks), marks each
// blank right or wrong, and after the first check offers "Show answers".
(function () {
  document.querySelectorAll(".fill-in").forEach(function (box) {
    var blanks = box.querySelectorAll("input.blank");
    blanks.forEach(function (b) {
      b.type = "text";
      b.autocomplete = "off";
      b.spellcheck = false;
      b.setAttribute("autocapitalize", "off");
      b.style.fontFamily = "var(--mono)";
      b.addEventListener("input", function () { b.style.borderColor = ""; b.removeAttribute("aria-invalid"); });
      b.addEventListener("keydown", function (e) { if (e.key === "Enter") { e.preventDefault(); check(); } });
    });

    var bar = document.createElement("p");
    var checkBtn = document.createElement("button");
    checkBtn.type = "button"; checkBtn.className = "btn"; checkBtn.textContent = "Check";
    var showBtn = document.createElement("button");
    showBtn.type = "button"; showBtn.className = "btn"; showBtn.textContent = "Show answers";
    showBtn.style.marginLeft = ".5rem"; showBtn.hidden = true;
    var fb = document.createElement("span");
    fb.className = "feedback"; fb.setAttribute("aria-live", "polite"); fb.style.marginLeft = ".8rem";
    bar.appendChild(checkBtn); bar.appendChild(showBtn); bar.appendChild(fb);
    box.appendChild(bar);

    function accepted(b) { return b.dataset.answer.split("|").map(function (s) { return s.trim(); }); }

    function check() {
      var right = 0;
      blanks.forEach(function (b) {
        var ok = accepted(b).indexOf(b.value.trim()) !== -1;
        if (ok) right++;
        b.style.borderColor = ok ? "var(--ok)" : "var(--bad)";
        if (ok) b.removeAttribute("aria-invalid"); else b.setAttribute("aria-invalid", "true");
      });
      fb.className = "feedback " + (right === blanks.length ? "ok" : "bad");
      fb.textContent = right === blanks.length ? "All " + right + " right." : right + " of " + blanks.length + " right. Fix the red ones.";
      showBtn.hidden = false;
    }

    checkBtn.addEventListener("click", check);
    showBtn.addEventListener("click", function () {
      blanks.forEach(function (b) { b.value = accepted(b)[0]; });
      check();
      fb.textContent = "Answers filled in. Make sure you can explain each one.";
    });
  });
})();
