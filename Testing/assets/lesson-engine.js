/* ==========================================================================
   Testing Mastery 2027 — Interactive Lesson & Test Runner Engine
   Zero external dependencies; runs natively via file:// or http(s)://
   ========================================================================== */

(function (global) {
  "use strict";

  const STORAGE_KEY = "testing_mastery_2027_progress";
  const THEME_KEY = "testing_mastery_2027_theme";

  function loadProgress() {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY) || "{}");
    } catch (_) {
      return {};
    }
  }

  function saveProgress(data) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch (_) {
      // Ignore storage quota / privacy mode errors
    }
  }

  function updateLessonProgress(lessonId, patch) {
    const all = loadProgress();
    all[lessonId] = Object.assign(
      { labsPassed: {}, quizPassed: {}, completed: false, updatedAt: Date.now() },
      all[lessonId] || {},
      patch,
      { updatedAt: Date.now() }
    );
    saveProgress(all);
    refreshProgressUI(lessonId);
  }

  function refreshProgressUI(lessonId) {
    const bar = document.getElementById("lesson-progress-fill");
    const label = document.getElementById("lesson-progress-text");
    if (!bar || !label || !lessonId) return;

    const data = loadProgress()[lessonId] || {};
    const totalLabs = document.querySelectorAll(".lab-card").length;
    const totalQuizzes = document.querySelectorAll(".quiz-card").length;
    const passedLabs = Object.values(data.labsPassed || {}).filter(Boolean).length;
    const passedQuizzes = Object.values(data.quizPassed || {}).filter(Boolean).length;
    const totalItems = totalLabs + totalQuizzes + (data.completed ? 0 : 0);

    const done = passedLabs + passedQuizzes;
    const pct = totalItems > 0 ? Math.round((done / totalItems) * 100) : 0;
    bar.style.width = pct + "%";
    label.textContent = `${done}/${totalItems} checks (${pct}%)`;

    const completeBtn = document.getElementById("mark-lesson-complete");
    if (completeBtn) {
      const labelName = completeBtn.getAttribute("data-label") || lessonId.replace("-", " ").toUpperCase();
      if (data.completed) {
        completeBtn.textContent = "Completed (Saved to Master Plan)";
        completeBtn.classList.add("btn-success");
        completeBtn.classList.remove("btn-primary");
      } else {
        completeBtn.textContent = "Mark " + labelName + " Complete";
        completeBtn.classList.remove("btn-success");
        completeBtn.classList.add("btn-primary");
      }
    }
  }

  /* ------------------------------------------------------------------------
     Theme Switcher
     ------------------------------------------------------------------------ */
  function initTheme() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.documentElement.setAttribute("data-theme", saved);
    const btn = document.getElementById("theme-toggle");
    if (btn) {
      btn.textContent = saved === "dark" ? "Light Mode" : "Dark Mode";
      btn.addEventListener("click", function () {
        const cur = document.documentElement.getAttribute("data-theme") || "dark";
        const next = cur === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", next);
        localStorage.setItem(THEME_KEY, next);
        btn.textContent = next === "dark" ? "Light Mode" : "Dark Mode";
      });
    }
  }

  /* ------------------------------------------------------------------------
     Code Tabs & Copy Buttons
     ------------------------------------------------------------------------ */
  function initCodeTabs() {
    document.querySelectorAll(".code-tabs").forEach(function (group) {
      const buttons = group.querySelectorAll(".tab-btn");
      const panels = group.querySelectorAll(".tab-panel");
      buttons.forEach(function (btn) {
        btn.addEventListener("click", function () {
          const target = btn.getAttribute("data-tab");
          buttons.forEach(function (b) { b.classList.toggle("active", b === btn); });
          panels.forEach(function (p) {
            p.classList.toggle("active", p.getAttribute("data-panel") === target);
          });
        });
      });

      const copyBtn = group.querySelector(".copy-code-btn");
      if (copyBtn) {
        copyBtn.addEventListener("click", function () {
          const activePanel = group.querySelector(".tab-panel.active code") || group.querySelector(".tab-panel.active");
          if (!activePanel) return;
          const text = activePanel.textContent;
          if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(function () {
              const old = copyBtn.textContent;
              copyBtn.textContent = "Copied!";
              setTimeout(function () { copyBtn.textContent = old; }, 1200);
            });
          }
        });
      }
    });
  }

  /* ------------------------------------------------------------------------
     In-Browser Test Runner Primitives (JUnit-style API in JS)
     ------------------------------------------------------------------------ */
  function deepEqual(a, b) {
    if (Object.is(a, b)) return true;
    if (typeof a !== typeof b || a === null || b === null) return false;
    if (typeof a !== "object") return false;
    if (Array.isArray(a) !== Array.isArray(b)) return false;
    const keysA = Object.keys(a);
    const keysB = Object.keys(b);
    if (keysA.length !== keysB.length) return false;
    return keysA.every(function (k) {
      return Object.prototype.hasOwnProperty.call(b, k) && deepEqual(a[k], b[k]);
    });
  }

  function formatVal(v) {
    if (v === undefined) return "undefined";
    try {
      return typeof v === "string" ? JSON.stringify(v) : JSON.stringify(v);
    } catch (_) {
      return String(v);
    }
  }

  function buildAssertions(logs) {
    function assertEquals(expected, actual, msg) {
      if (!deepEqual(expected, actual)) {
        throw new Error((msg ? msg + " — " : "") + `Expected ${formatVal(expected)}, but got ${formatVal(actual)}`);
      }
    }
    function assertNotEquals(unexpected, actual, msg) {
      if (deepEqual(unexpected, actual)) {
        throw new Error((msg ? msg + " — " : "") + `Expected value NOT to equal ${formatVal(unexpected)}`);
      }
    }
    function assertTrue(cond, msg) {
      if (cond !== true) {
        throw new Error((msg ? msg + " — " : "") + `Expected true, got ${formatVal(cond)}`);
      }
    }
    function assertFalse(cond, msg) {
      if (cond !== false) {
        throw new Error((msg ? msg + " — " : "") + `Expected false, got ${formatVal(cond)}`);
      }
    }
    function assertThrows(fn, expectedSubstring) {
      let threw = false;
      let caught = null;
      try {
        fn();
      } catch (err) {
        threw = true;
        caught = err;
      }
      if (!threw) {
        throw new Error("Expected function to throw an error, but it returned normally.");
      }
      if (expectedSubstring) {
        const msg = caught && caught.message ? caught.message : String(caught);
        if (!msg.toLowerCase().includes(expectedSubstring.toLowerCase())) {
          throw new Error(`Expected error message to include "${expectedSubstring}", got "${msg}"`);
        }
      }
    }
    function assertContains(haystack, needle, msg) {
      const ok = typeof haystack === "string"
        ? haystack.includes(String(needle))
        : Array.isArray(haystack) && haystack.some(function (x) { return deepEqual(x, needle); });
      if (!ok) {
        throw new Error((msg ? msg + " — " : "") + `Expected ${formatVal(haystack)} to contain ${formatVal(needle)}`);
      }
    }
    function createSpy(impl) {
      const calls = [];
      const spy = function () {
        const args = Array.prototype.slice.call(arguments);
        calls.push(args);
        return impl ? impl.apply(this, args) : undefined;
      };
      spy.calls = calls;
      spy.callCount = function () { return calls.length; };
      return spy;
    }
    function createVirtualClock(startMs) {
      let nowMs = startMs || 0;
      const timers = [];
      return {
        now: function () { return nowMs; },
        schedule: function (delayMs, fn) {
          timers.push({ runAt: nowMs + Math.max(0, delayMs), fn: fn });
          timers.sort(function (a, b) { return a.runAt - b.runAt; });
        },
        advanceBy: function (ms) {
          const target = nowMs + ms;
          while (timers.length && timers[0].runAt <= target) {
            const next = timers.shift();
            nowMs = next.runAt;
            next.fn();
          }
          nowMs = target;
        },
        runUntilIdle: function () {
          while (timers.length) {
            const next = timers.shift();
            nowMs = next.runAt;
            next.fn();
          }
        }
      };
    }
    function log() {
      const line = Array.prototype.slice.call(arguments).map(function (x) {
        return typeof x === "string" ? x : formatVal(x);
      }).join(" ");
      logs.push(line);
    }
    return { assertEquals, assertNotEquals, assertTrue, assertFalse, assertThrows, assertContains, createSpy, createVirtualClock, log };
  }

  function executeUserTestSuite(userCode, sutBindings) {
    const tests = [];
    const logs = [];
    const assertions = buildAssertions(logs);

    function test(name, fn) {
      if (typeof name !== "string" || typeof fn !== "function") {
        throw new Error("test(name, fn) requires a test name string and a function.");
      }
      tests.push({ name, fn });
    }

    const argNames = [
      "test",
      "assertEquals",
      "assertNotEquals",
      "assertTrue",
      "assertFalse",
      "assertThrows",
      "assertContains",
      "createSpy",
      "createVirtualClock",
      "log"
    ].concat(Object.keys(sutBindings));

    const argValues = [
      test,
      assertions.assertEquals,
      assertions.assertNotEquals,
      assertions.assertTrue,
      assertions.assertFalse,
      assertions.assertThrows,
      assertions.assertContains,
      assertions.createSpy,
      assertions.createVirtualClock,
      assertions.log
    ].concat(Object.values(sutBindings));

    try {
      const compiled = new Function(argNames.join(", "), '"use strict";\n' + userCode);
      compiled.apply(null, argValues);
    } catch (compileOrTopErr) {
      return {
        syntaxOrSetupError: compileOrTopErr.message || String(compileOrTopErr),
        results: [],
        logs
      };
    }

    const results = tests.map(function (t) {
      try {
        t.fn();
        return { name: t.name, passed: true, error: null };
      } catch (err) {
        return { name: t.name, passed: false, error: err.message || String(err) };
      }
    });

    return { syntaxOrSetupError: null, results, logs };
  }

  /* ------------------------------------------------------------------------
     Lab Registration & Interactive Execution
     ------------------------------------------------------------------------ */
  function escapeHtml(str) {
    return str
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");
  }

  function registerLab(cfg) {
    const card = document.getElementById(cfg.id);
    if (!card) return;

    const editor = card.querySelector(".code-editor");
    const runBtn = card.querySelector(".run-lab-btn");
    const resetBtn = card.querySelector(".reset-lab-btn");
    const hintBtn = card.querySelector(".hint-lab-btn");
    const answerBtn = card.querySelector(".answer-lab-btn");
    const loadAnswerBtn = card.querySelector(".load-answer-btn");
    const consoleEl = card.querySelector(".console-output");
    const hintBox = card.querySelector(".hint-box");
    const answerBox = card.querySelector(".answer-box");
    const statusBadge = card.querySelector(".lab-status");

    let hintIndex = 0;
    editor.value = cfg.starterCode;

    // Restore passed status if previously completed
    const saved = loadProgress()[cfg.lessonId];
    if (saved && saved.labsPassed && saved.labsPassed[cfg.id]) {
      statusBadge.textContent = "PASSED";
      statusBadge.className = "lab-status passed";
    }

    // Tab indentation + Ctrl/Cmd+Enter run shortcut
    editor.addEventListener("keydown", function (e) {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = editor.selectionStart;
        const end = editor.selectionEnd;
        editor.value = editor.value.substring(0, start) + "  " + editor.value.substring(end);
        editor.selectionStart = editor.selectionEnd = start + 2;
      } else if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        runBtn.click();
      }
    });

    resetBtn.addEventListener("click", function () {
      editor.value = cfg.starterCode;
      consoleEl.classList.remove("visible");
      consoleEl.innerHTML = "";
      statusBadge.textContent = "Not Run";
      statusBadge.className = "lab-status pending";
    });

    if (hintBtn && hintBox && cfg.hints && cfg.hints.length) {
      hintBtn.addEventListener("click", function () {
        hintIndex = Math.min(hintIndex + 1, cfg.hints.length);
        const visibleHints = cfg.hints.slice(0, hintIndex).map(function (h, i) {
          return `<div style="margin-bottom:0.45rem;"><strong>Hint ${i + 1}:</strong> ${h}</div>`;
        }).join("");
        hintBox.innerHTML = visibleHints;
        hintBox.classList.add("visible");
        hintBtn.textContent = hintIndex < cfg.hints.length
          ? `Next Hint (${hintIndex}/${cfg.hints.length})`
          : `All Hints Shown (${cfg.hints.length}/${cfg.hints.length})`;
      });
    }

    if (answerBtn && answerBox) {
      answerBtn.addEventListener("click", function () {
        const isShown = answerBox.classList.toggle("visible");
        answerBtn.textContent = isShown ? "Hide Final Answer" : "Show Final Answer";
      });
    }

    if (loadAnswerBtn) {
      loadAnswerBtn.addEventListener("click", function () {
        editor.value = cfg.solutionCode;
      });
    }

    runBtn.addEventListener("click", function () {
      const code = editor.value;
      const lines = [];
      let overallPassed = false;

      // 1. Run against Reference SUT
      const sut = cfg.referenceSut();
      const refRun = executeUserTestSuite(code, sut);

      if (refRun.syntaxOrSetupError) {
        lines.push(`<span class="log-fail">[SYNTAX / RUNTIME ERROR]</span> ${escapeHtml(refRun.syntaxOrSetupError)}`);
      } else if (refRun.results.length === 0) {
        lines.push(`<span class="log-warn">[NO TESTS FOUND]</span> Define at least one test("description", () => { ... }).`);
      } else {
        const passedCount = refRun.results.filter(function (r) { return r.passed; }).length;
        lines.push(`<span class="log-info">=== Reference Implementation Run (${passedCount}/${refRun.results.length} passed) ===</span>`);
        refRun.results.forEach(function (r) {
          if (r.passed) {
            lines.push(`<span class="log-pass">  PASS</span> ${escapeHtml(r.name)}`);
          } else {
            lines.push(`<span class="log-fail">  FAIL</span> ${escapeHtml(r.name)}\n       -> ${escapeHtml(r.error)}`);
          }
        });

        if (refRun.logs.length) {
          lines.push(`\n<span class="log-info">Console output:</span>`);
          refRun.logs.forEach(function (l) { lines.push(`  > ${escapeHtml(l)}`); });
        }

        const allRefPassed = passedCount === refRun.results.length;
        const minTests = cfg.minTests || 1;

        if (!allRefPassed) {
          lines.push(`\n<span class="log-fail">[CHECK FAILED]</span> All your tests must PASS on the valid implementation first.`);
        } else if (refRun.results.length < minTests) {
          lines.push(`\n<span class="log-warn">[NEED MORE COVERAGE]</span> Write at least ${minTests} distinct test() cases (found ${refRun.results.length}).`);
        } else {
          // 2. Hidden behavioral verification for implementation labs (prevents dummy test bypass)
          let hiddenOk = true;
          if (cfg.hiddenTests) {
            const hiddenRun = executeUserTestSuite(code + "\n;\n" + cfg.hiddenTests, sut);
            if (hiddenRun.syntaxOrSetupError) {
              hiddenOk = false;
              lines.push(`\n<span class="log-fail">[IMPLEMENTATION CHECK ERROR]</span> ${escapeHtml(hiddenRun.syntaxOrSetupError)}`);
            } else {
              const hiddenFailed = hiddenRun.results.slice(refRun.results.length).filter(function (r) { return !r.passed; });
              if (hiddenFailed.length) {
                hiddenOk = false;
                lines.push(`\n<span class="log-fail">[HIDDEN SPEC CHECK FAILED]</span> Your function failed ${hiddenFailed.length} edge-case verification(s):`);
                hiddenFailed.forEach(function (hf) {
                  lines.push(`<span class="log-fail">  FAIL</span> ${escapeHtml(hf.name)}\n       -> ${escapeHtml(hf.error)}`);
                });
              } else {
                lines.push(`\n<span class="log-pass">[HIDDEN SPEC CHECK]</span> Implementation passed all ${hiddenRun.results.length - refRun.results.length} hidden edge-case assertions.`);
              }
            }
          }

          // 3. Custom validator if provided
          let customOk = hiddenOk;
          if (hiddenOk && cfg.customCheck) {
            const checkMsg = cfg.customCheck(code, refRun);
            if (checkMsg !== true) {
              customOk = false;
              lines.push(`\n<span class="log-fail">[VERIFICATION CHECK]</span> ${escapeHtml(String(checkMsg))}`);
            }
          }

          // 4. Mutation Testing if mutants are configured
          if (customOk && cfg.mutants && cfg.mutants.length) {
            lines.push(`\n<span class="log-info">=== Mutation Testing (Do your tests actually catch bugs?) ===</span>`);
            let killed = 0;
            cfg.mutants.forEach(function (m, idx) {
              const mutRun = executeUserTestSuite(code, m.sut());
              const mutFailed = mutRun.syntaxOrSetupError || mutRun.results.some(function (r) { return !r.passed; });
              if (mutFailed) {
                killed += 1;
                lines.push(`<span class="log-pass">  KILLED Mutant #${idx + 1}:</span> ${escapeHtml(m.name)}`);
              } else {
                lines.push(`<span class="log-fail">  SURVIVED Mutant #${idx + 1}:</span> ${escapeHtml(m.name)} — Your tests passed even with this bug! Add a test to catch: ${escapeHtml(m.hint)}`);
              }
            });

            if (killed === cfg.mutants.length) {
              overallPassed = true;
              lines.push(`\n<span class="log-pass">LAB PASSED! Mutation Score: ${killed}/${cfg.mutants.length} (100%). Your test suite is rock-solid.</span>`);
            } else {
              lines.push(`\n<span class="log-warn">Mutation Score: ${killed}/${cfg.mutants.length}. Kill all surviving mutants to pass this lab.</span>`);
            }
          } else if (customOk) {
            overallPassed = true;
            lines.push(`\n<span class="log-pass">LAB PASSED! All assertions & checks succeeded.</span>`);
          }
        }
      }

      consoleEl.innerHTML = lines.join("\n");
      consoleEl.classList.add("visible");

      if (overallPassed) {
        statusBadge.textContent = "PASSED";
        statusBadge.className = "lab-status passed";
        const cur = loadProgress()[cfg.lessonId] || {};
        const labsPassed = Object.assign({}, cur.labsPassed || {}, { [cfg.id]: true });
        updateLessonProgress(cfg.lessonId, { labsPassed });
      } else {
        statusBadge.textContent = "Needs Work";
        statusBadge.className = "lab-status failed";
      }
    });
  }

  /* ------------------------------------------------------------------------
     Multiple-Choice Quiz Engine (with right & wrong explanations for all options)
     ------------------------------------------------------------------------ */
  function registerQuiz(cfg) {
    const container = document.getElementById(cfg.containerId);
    if (!container) return;

    const letters = ["A", "B", "C", "D", "E"];
    const curProgress = loadProgress()[cfg.lessonId] || {};

    cfg.questions.forEach(function (q, qIdx) {
      const card = document.createElement("div");
      card.className = "quiz-card";
      card.id = q.id;

      const codeHtml = q.code
        ? `<pre style="background:var(--bg-code);border:1px solid var(--border);border-radius:8px;padding:0.85rem;margin:0 0 0.9rem;overflow-x:auto;"><code>${escapeHtml(q.code)}</code></pre>`
        : "";

      const optionsHtml = q.options.map(function (opt, oIdx) {
        return `
          <button class="quiz-option" type="button" data-idx="${oIdx}">
            <span class="opt-letter">${letters[oIdx]}</span>
            <span>${escapeHtml(opt.text)}</span>
          </button>
        `;
      }).join("");

      card.innerHTML = `
        <div class="quiz-header">
          <span class="chip chip-int">Question ${qIdx + 1} of ${cfg.questions.length} &bull; ${escapeHtml(q.topic)}</span>
          <span class="quiz-badge chip">${curProgress.quizPassed && curProgress.quizPassed[q.id] ? "Mastered" : "Unanswered"}</span>
        </div>
        <p class="quiz-q">${escapeHtml(q.question)}</p>
        ${codeHtml}
        <div class="quiz-options">${optionsHtml}</div>
        <div class="quiz-feedback"></div>
      `;

      const buttons = card.querySelectorAll(".quiz-option");
      const feedbackEl = card.querySelector(".quiz-feedback");
      const badgeEl = card.querySelector(".quiz-badge");

      buttons.forEach(function (btn) {
        btn.addEventListener("click", function () {
          const chosen = Number(btn.getAttribute("data-idx"));
          const isCorrect = chosen === q.correctIndex;

          buttons.forEach(function (b, idx) {
            b.classList.remove("correct", "wrong");
            if (idx === q.correctIndex) b.classList.add("correct");
            else if (idx === chosen) b.classList.add("wrong");
          });

          const breakdown = q.options.map(function (opt, idx) {
            const right = idx === q.correctIndex;
            const tag = right ? "CORRECT" : "WRONG";
            return `
              <li class="rationale-item ${right ? "is-right" : "is-wrong"}">
                <strong>${letters[idx]} (${tag}):</strong> ${escapeHtml(opt.why)}
              </li>
            `;
          }).join("");

          feedbackEl.innerHTML = `
            <div style="font-weight:700;color:${isCorrect ? "var(--green)" : "var(--red)"};">
              ${isCorrect ? "Correct!" : "Not quite — review the breakdown below:"}
            </div>
            <p style="margin:0.45rem 0 0.6rem;font-size:0.93rem;">${escapeHtml(q.summary)}</p>
            <ul class="rationale-list">${breakdown}</ul>
          `;
          feedbackEl.classList.add("visible");

          if (isCorrect) {
            badgeEl.textContent = "Mastered";
            badgeEl.className = "quiz-badge chip chip-unit";
            const latest = loadProgress()[cfg.lessonId] || {};
            const quizPassed = Object.assign({}, latest.quizPassed || {}, { [q.id]: true });
            updateLessonProgress(cfg.lessonId, { quizPassed });
          } else {
            badgeEl.textContent = "Reviewing";
          }
        });
      });

      container.appendChild(card);
    });
    refreshProgressUI(cfg.lessonId);
  }

  /* ------------------------------------------------------------------------
     Declarative Lab Mounter (keeps lessons DRY without duplicated chrome)
     ------------------------------------------------------------------------ */
  function mountLab(cfg) {
    const host = document.getElementById(cfg.id);
    if (!host) return;
    host.className = "lab-card";
    const hintCount = (cfg.hints || []).length;
    const modeLabel = cfg.mutants && cfg.mutants.length
      ? "Run & Verify (Mutation Check)"
      : "Run & Verify";
    host.innerHTML = `
      <div class="lab-header">
        <div>
          <h3 class="lab-title">${escapeHtml(cfg.title)}</h3>
          <span style="font-size:0.84rem;color:var(--text-muted);">${escapeHtml(cfg.focus || "")}</span>
        </div>
        <span class="lab-status pending">Not Run</span>
      </div>
      <div class="lab-body">
        <p style="margin-top:0;font-size:0.94rem;">${cfg.promptHtml || escapeHtml(cfg.prompt || "")}</p>
        <div class="lab-split">
          <div class="sut-box">
            <span class="sut-label">${escapeHtml(cfg.sutTitle || "System Under Test (Reference Spec)")}</span>
            <pre><code>${escapeHtml(cfg.sutDisplay)}</code></pre>
          </div>
          <div>
            <div class="editor-wrap">
              <div class="editor-toolbar">
                <span>${escapeHtml(cfg.fileName || "testSuite.test.js")}</span>
                <span>Cmd/Ctrl + Enter to Run</span>
              </div>
              <textarea class="code-editor" spellcheck="false"></textarea>
            </div>
          </div>
        </div>
        <div class="lab-controls">
          <button class="btn btn-primary run-lab-btn" type="button">${modeLabel}</button>
          <button class="btn btn-warn hint-lab-btn" type="button">Show Hint (0/${hintCount})</button>
          <button class="btn answer-lab-btn" type="button">Show Final Answer</button>
          <button class="btn reset-lab-btn" type="button">Reset</button>
        </div>
        <div class="console-output"></div>
        <div class="hint-box"></div>
        <div class="answer-box">
          <div style="display:flex;justify-content:space-between;align-items:center;gap:0.5rem;flex-wrap:wrap;margin-bottom:0.5rem;">
            <strong>Senior Reference Solution:</strong>
            <button class="btn btn-sm load-answer-btn" type="button">Load into Editor</button>
          </div>
          <pre style="background:var(--bg-code);padding:0.85rem;border-radius:8px;overflow-x:auto;"><code>${escapeHtml(cfg.solutionCode)}</code></pre>
          ${cfg.answerWhy ? `<p style="margin:0.6rem 0 0;font-size:0.88rem;">${escapeHtml(cfg.answerWhy)}</p>` : ""}
        </div>
      </div>
    `;
    registerLab(cfg);
    refreshProgressUI(cfg.lessonId);
  }

  /* ------------------------------------------------------------------------
     Lesson Completion & Master Plan Sync
     ------------------------------------------------------------------------ */
  function initLessonShell(lessonId) {
    initTheme();
    initCodeTabs();
    refreshProgressUI(lessonId);

    const completeBtn = document.getElementById("mark-lesson-complete");
    if (completeBtn && lessonId) {
      completeBtn.addEventListener("click", function () {
        const cur = loadProgress()[lessonId] || {};
        updateLessonProgress(lessonId, { completed: !cur.completed });
      });
    }
  }

  global.TestingEngine = {
    initTheme: initTheme,
    initCodeTabs: initCodeTabs,
    initLessonShell: initLessonShell,
    registerLab: registerLab,
    mountLab: mountLab,
    registerQuiz: registerQuiz,
    loadProgress: loadProgress,
    saveProgress: saveProgress
  };
})(window);
