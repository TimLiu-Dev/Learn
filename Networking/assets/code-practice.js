// Interactive runnable in-page code practice widget.
// Communicates with local LabRunner daemon (http://127.0.0.1:8765) to compile
// and run real Java 25 code against test cases with instant feedback.
//
// Markup:
//   <div class="code-practice" data-key="0001-delay-calc">
//     <div class="practice-bar">
//       <span class="practice-title">Interactive Code Practice · Java 25</span>
//       <span class="runner-badge" title="Daemon connection status">checking runner…</span>
//     </div>
//     <p class="practice-desc">Problem instructions...</p>
//     <div class="editor-shell">
//       <textarea class="practice-editor" spellcheck="false" autocomplete="off" autocapitalize="off" rows="12">...starter code...</textarea>
//     </div>
//     <div class="practice-actions">
//       <button type="button" class="btn btn-run">▶ Run &amp; Check</button>
//       <button type="button" class="btn btn-reset">Reset</button>
//       <button type="button" class="btn btn-hint">💡 Hint</button>
//       <button type="button" class="btn btn-solution">Show Solution</button>
//     </div>
//     <div class="practice-hints" hidden></div>
//     <div class="practice-solution" hidden></div>
//     <div class="practice-console" hidden>
//       <div class="console-head">Test Output:</div>
//       <pre class="console-body"></pre>
//     </div>
//     <script type="text/plain" class="starter-code">...</script>
//     <script type="text/plain" class="test-harness">...</script>
//     <script type="text/plain" class="solution-code">...</script>
//     <script type="application/json" class="hints-data">["Hint 1", "Hint 2"]</script>
//   </div>
//   <script src="../assets/code-practice.js"></script>
(function () {
  var RUNNER_URL = "http://127.0.0.1:8765";
  var runnerAvailable = false;

  // Check runner health on load
  function pingRunner(callback) {
    var controller = new AbortController();
    var timeoutId = setTimeout(function () { controller.abort(); }, 1200);

    fetch(RUNNER_URL + "/health", { signal: controller.signal })
      .then(function (res) { return res.json(); })
      .then(function (data) {
        clearTimeout(timeoutId);
        runnerAvailable = (data && data.status === "ok");
        if (callback) callback(true, data);
      })
      .catch(function () {
        clearTimeout(timeoutId);
        runnerAvailable = false;
        if (callback) callback(false, null);
      });
  }

  document.querySelectorAll(".code-practice").forEach(function (widget) {
    var key = widget.dataset.key || ("practice_" + Math.random().toString(36).substring(2, 8));
    var storageKey = "practice_code_" + key;

    var badge = widget.querySelector(".runner-badge");
    var editor = widget.querySelector(".practice-editor");
    var runBtn = widget.querySelector(".btn-run");
    var resetBtn = widget.querySelector(".btn-reset");
    var hintBtn = widget.querySelector(".btn-hint");
    var solutionBtn = widget.querySelector(".btn-solution");
    var hintsBox = widget.querySelector(".practice-hints");
    var solutionBox = widget.querySelector(".practice-solution");
    var consoleBox = widget.querySelector(".practice-console");
    var consoleBody = widget.querySelector(".console-body");

    var starterScript = widget.querySelector("script.starter-code");
    var starterCode = starterScript ? starterScript.textContent.trim() : (editor ? editor.value.trim() : "");
    var harnessScript = widget.querySelector("script.test-harness");
    var harness = harnessScript ? harnessScript.textContent.trim() : "";
    var solutionScript = widget.querySelector("script.solution-code");
    var solutionCode = solutionScript ? solutionScript.textContent.trim() : "";
    var hintsScript = widget.querySelector("script.hints-data");
    var hints = [];
    if (hintsScript) {
      try { hints = JSON.parse(hintsScript.textContent); } catch (e) {}
    }

    // Restore saved draft or load starter code
    var savedDraft = null;
    try { savedDraft = localStorage.getItem(storageKey); } catch (e) {}
    if (savedDraft && savedDraft.trim()) {
      editor.value = savedDraft;
    } else if (starterCode) {
      editor.value = starterCode;
    }

    // Tab key indentation support in textarea
    editor.addEventListener("keydown", function (e) {
      if (e.key === "Tab") {
        e.preventDefault();
        var start = this.selectionStart;
        var end = this.selectionEnd;
        this.value = this.value.substring(0, start) + "    " + this.value.substring(end);
        this.selectionStart = this.selectionEnd = start + 4;
      }
    });

    // Auto-save draft on input
    editor.addEventListener("input", function () {
      try { localStorage.setItem(storageKey, editor.value); } catch (e) {}
    });

    // Update runner badge
    function updateBadge(online, data) {
      if (!badge) return;
      if (online) {
        badge.className = "runner-badge online";
        badge.textContent = "● Java 25 runner online";
        badge.title = "Connected to local LabRunner daemon on 127.0.0.1:8765";
      } else {
        badge.className = "runner-badge offline";
        badge.textContent = "○ runner offline";
        badge.title = "Local runner daemon offline. Run ./scripts/open-lesson.sh or ./scripts/runner.sh start";
      }
    }

    pingRunner(updateBadge);

    // Run & Check execution
    runBtn.addEventListener("click", function () {
      var code = editor.value;
      consoleBox.hidden = false;
      consoleBody.className = "console-body running";
      consoleBody.textContent = "⏳ Compiling and running tests in Java 25...";

      var payload = {
        code: code,
        harness: harness
      };

      var controller = new AbortController();
      var timeout = setTimeout(function () { controller.abort(); }, 4500);

      fetch(RUNNER_URL + "/run", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        signal: controller.signal
      })
      .then(function (res) { return res.json(); })
      .then(function (res) {
        clearTimeout(timeout);
        updateBadge(true);

        if (res.compileError) {
          consoleBody.className = "console-body error";
          consoleBody.textContent = "❌ COMPILATION ERROR:\n" + res.compileError;
          return;
        }

        if (res.timeout) {
          consoleBody.className = "console-body error";
          consoleBody.textContent = "⏱ TIMEOUT: Code execution exceeded 3 seconds. Check for infinite loops.";
          return;
        }

        var lines = (res.output || "").trim();
        if (res.success) {
          consoleBody.className = "console-body success";
          consoleBody.textContent = (lines ? lines + "\n\n" : "") + "🎉 ALL TESTS PASSED! (" + res.passed + "/" + res.total + ")";
        } else {
          consoleBody.className = "console-body fail";
          consoleBody.textContent = (lines ? lines + "\n\n" : "") + "⚠️ SOME CHECKS FAILED: " + res.passed + "/" + res.total + " passed.";
        }
      })
      .catch(function (err) {
        clearTimeout(timeout);
        updateBadge(false);
        consoleBody.className = "console-body warning";
        consoleBody.innerHTML =
          "<strong>⚠️ Local Java Runner Daemon is offline.</strong>\n\n" +
          "To enable in-browser execution with real Java 25:\n" +
          "  1. In Terminal, run: <code>./scripts/runner.sh start</code>\n" +
          "     (Or re-open this lesson via: <code>./scripts/open-lesson.sh &lt;num&gt;</code>)\n" +
          "  2. Click '▶ Run &amp; Check' again.\n\n" +
          "In the meantime, you can review the 💡 Hints and canonical Solution below.";
      });
    });

    // Reset button
    resetBtn.addEventListener("click", function () {
      if (confirm("Reset editor back to starter code?")) {
        editor.value = starterCode;
        try { localStorage.removeItem(storageKey); } catch (e) {}
        consoleBox.hidden = true;
      }
    });

    // Hints progressive disclosure
    var currentHintIndex = 0;
    if (hintBtn && hints.length > 0) {
      hintBtn.addEventListener("click", function () {
        hintsBox.hidden = false;
        if (currentHintIndex < hints.length) {
          var p = document.createElement("div");
          p.className = "hint-item";
          p.innerHTML = "<strong>Hint " + (currentHintIndex + 1) + ":</strong> " + hints[currentHintIndex];
          hintsBox.appendChild(p);
          currentHintIndex++;
        }
        if (currentHintIndex >= hints.length) {
          hintBtn.textContent = "All Hints Shown (" + hints.length + ")";
          hintBtn.disabled = true;
        } else {
          hintBtn.textContent = "💡 Hint (" + (currentHintIndex + 1) + "/" + hints.length + ")";
        }
      });
    } else if (hintBtn) {
      hintBtn.style.display = "none";
    }

    // Solution disclosure
    if (solutionBtn && solutionCode) {
      solutionBtn.addEventListener("click", function () {
        if (solutionBox.hidden) {
          solutionBox.hidden = false;
          solutionBox.innerHTML =
            "<div class=\"solution-header\">Canonical Reference Solution:</div>" +
            "<pre><code>" + escapeHtml(solutionCode) + "</code></pre>" +
            "<button type=\"button\" class=\"btn btn-apply-sol\">Load Solution into Editor</button>";

          var applyBtn = solutionBox.querySelector(".btn-apply-sol");
          if (applyBtn) {
            applyBtn.addEventListener("click", function () {
              if (confirm("Replace current editor contents with canonical solution?")) {
                editor.value = solutionCode;
                try { localStorage.setItem(storageKey, solutionCode); } catch (e) {}
              }
            });
          }
          solutionBtn.textContent = "Hide Solution";
        } else {
          solutionBox.hidden = true;
          solutionBtn.textContent = "Show Solution";
        }
      });
    } else if (solutionBtn) {
      solutionBtn.style.display = "none";
    }
  });

  function escapeHtml(s) {
    return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
  }
})();
