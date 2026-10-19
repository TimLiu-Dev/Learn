// Free-recall component: write an answer from memory, then (optionally) compare.
//
// Markup:
//   <div class="recall" data-key="0003-layers">
//     <p class="r-prompt">Explain, from memory, …</p>
//     <div class="keypoints">Key points to compare against (optional).</div>
//   </div>
//   <script src="../assets/recall.js"></script>
//
// Without .keypoints the prompt is answered in chat with the teacher.
// Drafts are kept in this browser only (localStorage).
(function () {
  document.querySelectorAll(".recall").forEach(function (box, n) {
    var key = "recall:" + (box.dataset.key || location.pathname + "#" + n);
    var ta = document.createElement("textarea");
    ta.placeholder = "Write from memory — no peeking at the lesson.";
    ta.setAttribute("aria-label", "Your answer");
    try { ta.value = localStorage.getItem(key) || ""; } catch (e) {}
    ta.addEventListener("input", function () {
      try { localStorage.setItem(key, ta.value); } catch (e) {}
    });
    var points = box.querySelector(".keypoints");
    box.insertBefore(ta, points || null);
    if (points) {
      var btn = document.createElement("button");
      btn.type = "button";
      btn.className = "btn";
      btn.textContent = "I've written my answer — show key points";
      btn.addEventListener("click", function () {
        box.classList.toggle("revealed");
        btn.textContent = box.classList.contains("revealed") ? "Hide key points" : "I've written my answer — show key points";
      });
      box.insertBefore(btn, points);
    } else {
      var hint = document.createElement("p");
      hint.className = "q-status";
      hint.textContent = "Bring this answer to chat — your teacher will give feedback.";
      box.appendChild(hint);
    }
  });
})();
