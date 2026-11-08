// Synchronized [Java | Kotlin] tabbed code blocks with clipboard copy.
//
// Markup:
//   <div class="code-tabs">
//     <div class="code-tabs-nav">
//       <button type="button" class="tab-btn" data-lang="java">Java 25</button>
//       <button type="button" class="tab-btn" data-lang="kotlin">Kotlin</button>
//       <button type="button" class="code-copy-btn" aria-label="Copy code">Copy</button>
//     </div>
//     <div class="tab-panel" data-lang="java">
//       <pre><code>// Java implementation</code></pre>
//     </div>
//     <div class="tab-panel" data-lang="kotlin">
//       <pre><code>// Kotlin implementation</code></pre>
//     </div>
//   </div>
//   <script src="../assets/code-tabs.js"></script>
(function () {
  var STORAGE_KEY = "preferred_code_lang";
  var preferredLang = localStorage.getItem(STORAGE_KEY) || "java";

  function setLanguage(lang) {
    preferredLang = lang;
    try { localStorage.setItem(STORAGE_KEY, lang); } catch (e) {}

    document.querySelectorAll(".code-tabs").forEach(function (block) {
      block.querySelectorAll(".tab-btn").forEach(function (btn) {
        var match = btn.dataset.lang === lang;
        btn.classList.toggle("active", match);
        btn.setAttribute("aria-selected", match ? "true" : "false");
      });
      block.querySelectorAll(".tab-panel").forEach(function (panel) {
        var match = panel.dataset.lang === lang;
        panel.classList.toggle("active", match);
        panel.hidden = !match;
      });
    });
  }

  document.querySelectorAll(".code-tabs").forEach(function (block) {
    var nav = block.querySelector(".code-tabs-nav");
    if (!nav) return;

    nav.querySelectorAll(".tab-btn").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var lang = btn.dataset.lang;
        if (lang) setLanguage(lang);
      });
    });

    var copyBtn = nav.querySelector(".code-copy-btn");
    if (copyBtn) {
      copyBtn.addEventListener("click", function () {
        var activePanel = block.querySelector(".tab-panel.active") || block.querySelector(".tab-panel");
        if (!activePanel) return;
        var code = activePanel.querySelector("code");
        var text = code ? code.innerText : activePanel.innerText;

        navigator.clipboard.writeText(text).then(function () {
          var orig = copyBtn.textContent;
          copyBtn.textContent = "Copied!";
          copyBtn.classList.add("copied");
          setTimeout(function () {
            copyBtn.textContent = orig;
            copyBtn.classList.remove("copied");
          }, 1500);
        }).catch(function () {
          copyBtn.textContent = "Press Cmd+C";
        });
      });
    }
  });

  // Apply initial preference
  setLanguage(preferredLang);
})();
