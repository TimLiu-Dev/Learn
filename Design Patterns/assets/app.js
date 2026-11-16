/* ==========================================================================
   Design Patterns 2027 — Shared Interactive Lesson & Progress Engine
   100% file:// compatible (no ES modules, no fetch required)
   ========================================================================== */
(function () {
  "use strict";

  var PROGRESS_KEY = "dp_2027_progress_v1";
  var THEME_KEY = "dp_2027_theme";

  function loadProgress() {
    try {
      var raw = localStorage.getItem(PROGRESS_KEY);
      if (!raw) return { lessons: {} };
      var parsed = JSON.parse(raw);
      return parsed && typeof parsed === "object" ? parsed : { lessons: {} };
    } catch (e) {
      return { lessons: {} };
    }
  }

  function saveProgress(data) {
    try {
      localStorage.setItem(PROGRESS_KEY, JSON.stringify(data));
    } catch (e) {
      // Ignore storage quota / private mode restrictions
    }
  }

  function ensureLessonRecord(progress, lessonId) {
    if (!progress.lessons) progress.lessons = {};
    if (!progress.lessons[lessonId]) {
      progress.lessons[lessonId] = { labs: {}, quizzes: {}, completed: false };
    }
    return progress.lessons[lessonId];
  }

  // Theme Toggle
  function initTheme() {
    var saved = "dark";
    try {
      saved = localStorage.getItem(THEME_KEY) || "dark";
    } catch (e) {}
    document.documentElement.setAttribute("data-theme", saved);

    var btn = document.getElementById("theme-toggle");
    if (!btn) return;
    updateThemeLabel(btn, saved);
    btn.addEventListener("click", function () {
      var current = document.documentElement.getAttribute("data-theme") || "dark";
      var next = current === "dark" ? "light" : "dark";
      document.documentElement.setAttribute("data-theme", next);
      try {
        localStorage.setItem(THEME_KEY, next);
      } catch (e) {}
      updateThemeLabel(btn, next);
    });
  }

  function updateThemeLabel(btn, theme) {
    btn.textContent = theme === "dark" ? "☀️ Light Mode" : "🌙 Dark Mode";
  }

  // Static code copy buttons
  function initCopyButtons() {
    var buttons = document.querySelectorAll("[data-copy-btn]");
    buttons.forEach(function (btn) {
      btn.addEventListener("click", function () {
        var wrap = btn.closest(".code-wrap");
        if (!wrap) return;
        var codeEl = wrap.querySelector("pre code");
        if (!codeEl) return;
        var text = codeEl.innerText;
        if (navigator.clipboard && navigator.clipboard.writeText) {
          navigator.clipboard.writeText(text).then(function () {
            flashCopied(btn);
          });
        } else {
          var ta = document.createElement("textarea");
          ta.value = text;
          document.body.appendChild(ta);
          ta.select();
          document.execCommand("copy");
          document.body.removeChild(ta);
          flashCopied(btn);
        }
      });
    });
  }

  function flashCopied(btn) {
    var orig = btn.textContent;
    btn.textContent = "✓ Copied";
    setTimeout(function () {
      btn.textContent = orig;
    }, 1400);
  }

  // Kotlin Playground initialization with graceful offline fallback
  function initKotlinPlaygrounds() {
    var statusBadges = document.querySelectorAll(".kotlin-runtime-badge");
    var hasPlayground = typeof window.KotlinPlayground === "function";

    if (hasPlayground) {
      try {
        window.KotlinPlayground(".kotlin-code", {
          onChange: function () {},
          getInstance: function () {}
        });
        statusBadges.forEach(function (el) {
          el.textContent = "Kotlin Compiler: Online Ready";
          el.classList.add("online");
          el.classList.remove("offline");
        });
      } catch (err) {
        markOffline(statusBadges);
      }
    } else {
      markOffline(statusBadges);
    }
  }

  function markOffline(statusBadges) {
    statusBadges.forEach(function (el) {
      el.textContent = "Kotlin Compiler Offline — Use JS Sandbox Tab";
      el.classList.add("offline");
      el.classList.remove("online");
    });
    document.querySelectorAll(".kotlin-offline-note").forEach(function (note) {
      note.classList.remove("hidden");
    });
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  // Interactive Lab Engine
  function initLabs(lessonId, labConfigs, onProgressChange) {
    var progress = loadProgress();
    var lessonRec = ensureLessonRecord(progress, lessonId);

    labConfigs.forEach(function (cfg) {
      var container = document.getElementById("lab-" + cfg.id);
      if (!container) return;

      // Tab switching inside this lab
      var tabBtns = container.querySelectorAll(".tab-btn");
      var tabPanes = container.querySelectorAll(".tab-pane");
      tabBtns.forEach(function (btn) {
        btn.addEventListener("click", function () {
          var target = btn.getAttribute("data-tab");
          tabBtns.forEach(function (b) { b.classList.remove("active"); });
          tabPanes.forEach(function (p) { p.classList.remove("active"); });
          btn.classList.add("active");
          var targetPane = container.querySelector('[data-pane="' + target + '"]');
          if (targetPane) targetPane.classList.add("active");
        });
      });

      var editor = container.querySelector(".js-editor");
      var runBtn = container.querySelector(".btn-run-js");
      var resetBtn = container.querySelector(".btn-reset-js");
      var hintBtn = container.querySelector(".btn-next-hint");
      var solBtn = container.querySelector(".btn-toggle-solution");
      var loadSolBtn = container.querySelector(".btn-load-solution");
      var consolePre = container.querySelector(".console-pre");
      var checkList = container.querySelector(".check-list");
      var hintDrawer = container.querySelector(".hint-drawer");
      var solDrawer = container.querySelector(".solution-drawer");
      var labBadge = container.querySelector(".lab-pass-badge");

      if (!editor) return;
      editor.value = cfg.starterJs;

      // Update saved pass badge
      if (lessonRec.labs[cfg.id]) {
        labBadge.textContent = "✓ Passed";
        labBadge.className = "badge status-live lab-pass-badge";
      }

      // Tab indentation + Cmd/Ctrl+Enter shortcut
      editor.addEventListener("keydown", function (e) {
        if (e.key === "Tab") {
          e.preventDefault();
          var start = editor.selectionStart;
          var end = editor.selectionEnd;
          editor.value = editor.value.substring(0, start) + "  " + editor.value.substring(end);
          editor.selectionStart = editor.selectionEnd = start + 2;
        } else if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
          e.preventDefault();
          runTests();
        }
      });

      function runTests() {
        var logs = [];
        var mockConsole = {
          log: function () {
            logs.push(Array.prototype.slice.call(arguments).map(formatArg).join(" "));
          },
          warn: function () {
            logs.push("[WARN] " + Array.prototype.slice.call(arguments).map(formatArg).join(" "));
          },
          error: function () {
            logs.push("[ERROR] " + Array.prototype.slice.call(arguments).map(formatArg).join(" "));
          }
        };

        var userCode = editor.value;
        var results = [];
        var runtimeError = null;

        try {
          var wrapped = '"use strict";\n' + userCode + "\n;\nreturn (function(){\n" + cfg.testHarnessJs + "\n})();";
          var fn = new Function("console", wrapped);
          results = fn(mockConsole);
          if (!Array.isArray(results)) {
            throw new Error("Test harness did not return an array of checks.");
          }
        } catch (err) {
          runtimeError = err;
        }

        if (runtimeError) {
          consolePre.textContent = (logs.length ? logs.join("\n") + "\n\n" : "") + "❌ Execution Error: " + runtimeError.message;
          checkList.innerHTML = '<li class="check-item fail"><strong>Code failed to execute:</strong> ' + escapeHtml(runtimeError.message) + "</li>";
          return;
        }

        consolePre.textContent = logs.length ? logs.join("\n") : "(No console output — checks ran silently)";
        var allPassed = results.length > 0 && results.every(function (r) { return Boolean(r.passed); });

        checkList.innerHTML = results.map(function (r) {
          var cls = r.passed ? "check-item pass" : "check-item fail";
          var icon = r.passed ? "✅" : "❌";
          return '<li class="' + cls + '">' + icon + " <strong>" + escapeHtml(r.name) + "</strong>" +
            (r.detail ? " — " + escapeHtml(r.detail) : "") + "</li>";
        }).join("");

        if (allPassed) {
          var latest = loadProgress();
          var rec = ensureLessonRecord(latest, lessonId);
          rec.labs[cfg.id] = true;
          saveProgress(latest);
          labBadge.textContent = "✓ Passed";
          labBadge.className = "badge status-live lab-pass-badge";
          if (typeof onProgressChange === "function") onProgressChange();
        }
      }

      runBtn.addEventListener("click", runTests);

      resetBtn.addEventListener("click", function () {
        editor.value = cfg.starterJs;
        consolePre.textContent = "Editor reset to starter code. Click 'Run & Check Tests' to execute.";
        checkList.innerHTML = '<li class="check-item">Waiting for test run...</li>';
      });

      // Progressive Hints
      var hintIndex = 0;
      if (hintBtn && hintDrawer) {
        hintBtn.textContent = "💡 Show Hint (0/" + cfg.hints.length + ")";
        hintBtn.addEventListener("click", function () {
          if (hintIndex >= cfg.hints.length) return;
          hintDrawer.classList.remove("hidden");
          var item = document.createElement("div");
          item.className = "hint-item";
          item.innerHTML = "<strong>Hint " + (hintIndex + 1) + ":</strong> " + escapeHtml(cfg.hints[hintIndex]);
          hintDrawer.appendChild(item);
          hintIndex++;
          if (hintIndex >= cfg.hints.length) {
            hintBtn.textContent = "💡 All Hints Shown (" + hintIndex + "/" + cfg.hints.length + ")";
            hintBtn.disabled = true;
          } else {
            hintBtn.textContent = "💡 Show Next Hint (" + hintIndex + "/" + cfg.hints.length + ")";
          }
        });
      }

      // Final Answer Toggle & Load
      if (solBtn && solDrawer) {
        solBtn.addEventListener("click", function () {
          var isHidden = solDrawer.classList.toggle("hidden");
          solBtn.textContent = isHidden ? "🔑 Reveal Final Answer" : "🙈 Hide Final Answer";
        });
      }

      if (loadSolBtn) {
        loadSolBtn.addEventListener("click", function () {
          editor.value = cfg.solutionJs;
          runTests();
        });
      }
    });
  }

  function formatArg(val) {
    if (typeof val === "string") return val;
    try {
      return JSON.stringify(val);
    } catch (e) {
      return String(val);
    }
  }

  // Interactive Quiz Engine
  function initQuizzes(lessonId, quizzes, onProgressChange) {
    var root = document.getElementById("quiz-container");
    if (!root) return;

    var progress = loadProgress();
    var lessonRec = ensureLessonRecord(progress, lessonId);

    function render() {
      var currentProg = loadProgress();
      var currentRec = ensureLessonRecord(currentProg, lessonId);

      root.innerHTML = quizzes.map(function (q, qIdx) {
        var stored = currentRec.quizzes[q.id];
        var chosenIdx = typeof stored === "number"
          ? stored
          : (stored && typeof stored.selected === "number" ? stored.selected : null);
        var answered = chosenIdx !== null;

        var codeHtml = q.code
          ? '<div class="code-wrap"><div class="code-head"><span>' + escapeHtml(q.lang || "Kotlin") + '</span></div><pre><code>' + escapeHtml(q.code) + "</code></pre></div>"
          : "";

        var optionsHtml = q.options.map(function (opt, oIdx) {
          var key = String.fromCharCode(65 + oIdx);
          var cls = "quiz-opt";
          if (answered) {
            if (opt.isCorrect) cls += " reveal-correct";
            if (chosenIdx === oIdx && opt.isCorrect) cls += " selected-correct";
            if (chosenIdx === oIdx && !opt.isCorrect) cls += " selected-wrong";
          }
          return '<button type="button" class="' + cls + '" data-qid="' + escapeHtml(q.id) + '" data-oidx="' + oIdx + '"' +
            (answered ? " disabled" : "") + '>' +
            '<span class="quiz-opt-key">' + key + "</span>" +
            "<span>" + escapeHtml(opt.text) + "</span></button>";
        }).join("");

        var breakdownHtml = "";
        if (answered) {
          var userGotIt = q.options[chosenIdx] && q.options[chosenIdx].isCorrect;
          breakdownHtml = '<div class="quiz-breakdown">' +
            '<div style="font-weight:700;margin-bottom:0.25rem;color:' + (userGotIt ? "var(--accent-emerald)" : "var(--accent-rose)") + ';">' +
            (userGotIt ? "✅ Correct! Here is the full breakdown:" : "❌ Not quite — here is why each choice works or fails:") +
            "</div>" +
            q.options.map(function (opt, oIdx) {
              var key = String.fromCharCode(65 + oIdx);
              var cls = opt.isCorrect ? "rationale-item is-right" : "rationale-item is-wrong";
              var tag = opt.isCorrect ? "✓ Correct" : "✗ Incorrect";
              return '<div class="' + cls + '"><strong>' + key + " (" + tag + "):</strong> " + escapeHtml(opt.why) + "</div>";
            }).join("") +
            "</div>";
        }

        return '<article class="quiz-card" id="quiz-' + escapeHtml(q.id) + '">' +
          '<p class="quiz-q">Q' + (qIdx + 1) + ". " + escapeHtml(q.question) + "</p>" +
          codeHtml +
          '<div class="quiz-options">' + optionsHtml + "</div>" +
          breakdownHtml +
          "</article>";
      }).join("");

      root.querySelectorAll(".quiz-opt").forEach(function (btn) {
        btn.addEventListener("click", function () {
          var qid = btn.getAttribute("data-qid");
          var oidx = parseInt(btn.getAttribute("data-oidx"), 10);
          var qObj = quizzes.find(function (item) { return item.id === qid; });
          var isCorrect = Boolean(qObj && qObj.options[oidx] && qObj.options[oidx].isCorrect);
          var latest = loadProgress();
          var rec = ensureLessonRecord(latest, lessonId);
          rec.quizzes[qid] = { selected: oidx, isCorrect: isCorrect };
          saveProgress(latest);
          render();
          if (typeof onProgressChange === "function") onProgressChange();
        });
      });

      updateQuizScoreUI(quizzes, currentRec);
    }

    var resetQuizBtn = document.getElementById("reset-quiz-btn");
    if (resetQuizBtn) {
      resetQuizBtn.addEventListener("click", function () {
        var latest = loadProgress();
        var rec = ensureLessonRecord(latest, lessonId);
        rec.quizzes = {};
        saveProgress(latest);
        render();
        if (typeof onProgressChange === "function") onProgressChange();
      });
    }

    render();
  }

  function updateQuizScoreUI(quizzes, rec) {
    var scoreEl = document.getElementById("quiz-score-pill");
    if (!scoreEl) return;
    var answered = 0;
    var correct = 0;
    quizzes.forEach(function (q) {
      var stored = rec.quizzes[q.id];
      var idx = typeof stored === "number"
        ? stored
        : (stored && typeof stored.selected === "number" ? stored.selected : null);
      if (idx !== null) {
        answered++;
        if (q.options[idx] && q.options[idx].isCorrect) correct++;
      }
    });
    scoreEl.textContent = correct + " / " + quizzes.length + " correct (" + answered + " answered)";
  }

  // Declarative Lab Renderer (reusable component for Lessons 0002–0039)
  function mountLabs(lessonId, labConfigs, onProgressChange) {
    labConfigs.forEach(function (cfg) {
      var host = document.getElementById("lab-" + cfg.id);
      if (!host) return;
      var hasBoth = Boolean(cfg.javaCode && cfg.kotlinCode);
      var isJavaOnly = Boolean(cfg.javaCode && !cfg.kotlinCode);

      var tabButtonsHtml = '<button type="button" class="tab-btn active" data-tab="js">⚡ Offline JS Sandbox (Auto-Graded)</button>';
      var extraPanesHtml = "";
      var badgeHtml = "";

      if (hasBoth) {
        tabButtonsHtml +=
          '<button type="button" class="tab-btn" data-tab="java">☕ Java 21+ Spring Boot</button>' +
          '<button type="button" class="tab-btn" data-tab="kotlin">🟣 Kotlin Spring Boot</button>';
        badgeHtml = '<span class="runtime-status online">Side-by-Side: Java + Kotlin Spring Boot</span>';
        extraPanesHtml =
          '<div class="tab-pane" data-pane="java">' +
            '<div class="code-wrap" style="margin-top:0;"><div class="code-head"><span>Java 21+ / Spring Boot Implementation (Copy to IDE)</span><button class="btn btn-sm" data-copy-btn type="button">Copy</button></div><pre><code>' + escapeHtml(cfg.javaCode) + "</code></pre></div>" +
          "</div>" +
          '<div class="tab-pane" data-pane="kotlin">' +
            '<div class="code-wrap" style="margin-top:0;"><div class="code-head"><span>Kotlin / Spring Boot Implementation (Copy to IDE)</span><button class="btn btn-sm" data-copy-btn type="button">Copy</button></div><pre><code>' + escapeHtml(cfg.kotlinCode) + "</code></pre></div>" +
          "</div>";
      } else if (isJavaOnly) {
        tabButtonsHtml += '<button type="button" class="tab-btn" data-tab="kotlin">☕ Java 21+ / Spring Boot Reference</button>';
        badgeHtml = '<span class="runtime-status online">Java 21+ / Spring Boot Reference</span>';
        extraPanesHtml =
          '<div class="tab-pane" data-pane="kotlin">' +
            '<div class="code-wrap" style="margin-top:0;"><div class="code-head"><span>Java 21+ / Spring Boot 3.x–4.x Reference (Copy to IntelliJ)</span><button class="btn btn-sm" data-copy-btn type="button">Copy</button></div><pre><code>' + escapeHtml(cfg.javaCode) + "</code></pre></div>" +
          "</div>";
      } else {
        tabButtonsHtml += '<button type="button" class="tab-btn" data-tab="kotlin">🟣 Kotlin Playground (Online Compiler)</button>';
        badgeHtml = '<span class="runtime-status kotlin-runtime-badge">Checking Kotlin compiler...</span>';
        extraPanesHtml =
          '<div class="tab-pane" data-pane="kotlin">' +
            '<div class="callout warn kotlin-offline-note hidden">' +
              '<span class="callout-title">Offline Mode Detected</span>' +
              "JetBrains Kotlin Playground requires internet access. Use the <strong>Offline JS Sandbox</strong> tab for instant test checking, or copy this Kotlin code into Android Studio." +
            "</div>" +
            '<div class="kotlin-code" theme="darcula" data-autocomplete="true">' + escapeHtml(cfg.kotlinCode) + "</div>" +
          "</div>";
      }

      host.className = "lab-card";
      host.innerHTML =
        '<div class="lab-header">' +
          '<div class="lab-title-group">' +
            "<h3>" + escapeHtml(cfg.title) + "</h3>" +
            "<p>" + escapeHtml(cfg.subtitle) + "</p>" +
          "</div>" +
          '<span class="badge status-planned lab-pass-badge">Not Passed Yet</span>' +
        "</div>" +
        '<div class="lab-body">' +
          '<p style="font-size:0.92rem;margin-top:0;"><strong>Goal:</strong> ' + cfg.goalHtml + "</p>" +
          '<div class="runtime-tabs">' +
            '<div class="tab-buttons">' + tabButtonsHtml + "</div>" +
            badgeHtml +
          "</div>" +
          '<div class="tab-pane active" data-pane="js">' +
            '<textarea class="code-editor js-editor" spellcheck="false"></textarea>' +
            '<div class="lab-toolbar">' +
              '<button type="button" class="btn btn-primary btn-run-js">▶ Run &amp; Check Tests</button>' +
              '<button type="button" class="btn btn-amber btn-next-hint">💡 Show Hint</button>' +
              '<button type="button" class="btn btn-violet btn-toggle-solution">🔑 Reveal Final Answer</button>' +
              '<button type="button" class="btn btn-sm btn-reset-js">↺ Reset Code</button>' +
            "</div>" +
            '<div class="lab-output-grid">' +
              '<div class="console-box"><span class="panel-label">Console Output</span><pre class="console-pre">Click "Run &amp; Check Tests" to execute your code.</pre></div>' +
              '<div class="checks-box"><span class="panel-label">Automated Assertions</span><ul class="check-list"><li class="check-item">Waiting for test run...</li></ul></div>' +
            "</div>" +
            '<div class="hint-drawer hidden"><strong>Progressive Hints:</strong></div>' +
            '<div class="solution-drawer hidden">' +
              '<div style="display:flex;justify-content:space-between;align-items:center;gap:0.5rem;flex-wrap:wrap;margin-bottom:0.5rem;">' +
                "<strong>Final Senior Answer</strong>" +
                '<button type="button" class="btn btn-sm btn-primary btn-load-solution">Load Answer into Editor &amp; Run</button>' +
              "</div>" +
              '<p style="font-size:0.86rem;color:var(--text-muted);margin:0;">' + escapeHtml(cfg.solutionNote || "Review how the pattern decouples responsibilities cleanly.") + "</p>" +
            "</div>" +
          "</div>" +
          extraPanesHtml +
        "</div>";
    });
    initLabs(lessonId, labConfigs, onProgressChange);
    initCopyButtons();
    initKotlinPlaygrounds();
  }

  function bindCompletion(lessonId, labs, quizzes, labelPrefix) {
    function update() {
      var progress = loadProgress();
      var rec = ensureLessonRecord(progress, lessonId);
      var labsPassed = labs.filter(function (l) { return Boolean(rec.labs[l.id]); }).length;
      var quizCorrect = quizzes.filter(function (q) {
        var qv = rec.quizzes[q.id];
        return qv && qv.isCorrect;
      }).length;

      var pill = document.getElementById("lesson-progress-pill");
      if (pill) pill.textContent = (labsPassed + quizCorrect) + " / " + (labs.length + quizzes.length) + " checks";

      var summaryEl = document.getElementById("completion-summary-text");
      var btn = document.getElementById("mark-complete-btn");
      var prefix = labelPrefix || lessonId;
      if (btn && summaryEl) {
        if (rec.completed) {
          btn.textContent = "✅ " + prefix + " Completed (Click to Toggle)";
          summaryEl.textContent = "Locked in! Labs passed: " + labsPassed + "/" + labs.length + " • Quizzes correct: " + quizCorrect + "/" + quizzes.length + ". Synced to Master Plan.";
        } else {
          btn.textContent = "✓ Mark " + prefix + " Complete";
          summaryEl.textContent = "Current score: " + labsPassed + "/" + labs.length + " Labs passed • " + quizCorrect + "/" + quizzes.length + " Quizzes correct.";
        }
      }
    }

    var btn = document.getElementById("mark-complete-btn");
    if (btn) {
      btn.addEventListener("click", function () {
        var progress = loadProgress();
        var rec = ensureLessonRecord(progress, lessonId);
        rec.completed = !rec.completed;
        saveProgress(progress);
        update();
      });
    }

    mountLabs(lessonId, labs, update);
    initQuizzes(lessonId, quizzes, update);
    update();
  }

  // Expose public API
  window.DPApp = {
    loadProgress: loadProgress,
    saveProgress: saveProgress,
    ensureLessonRecord: ensureLessonRecord,
    initTheme: initTheme,
    initCopyButtons: initCopyButtons,
    initKotlinPlaygrounds: initKotlinPlaygrounds,
    initLabs: initLabs,
    mountLabs: mountLabs,
    initQuizzes: initQuizzes,
    bindCompletion: bindCompletion
  };

  document.addEventListener("DOMContentLoaded", function () {
    initTheme();
    initCopyButtons();
    initKotlinPlaygrounds();
  });
})();
