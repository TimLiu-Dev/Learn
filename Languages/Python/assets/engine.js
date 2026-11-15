/* ==========================================================================
   Python 2027 Senior Engineer Curriculum — Interactive Runtime & Quiz Engine
   ========================================================================== */

(function () {
  "use strict";

  const STORAGE_KEY = "py2027_progress_v1";
  const THEME_KEY = "py2027_theme_v1";

  let pyodideInstance = null;
  let pyodidePromise = null;

  /* ---------------- Progress & Theme Storage ---------------- */
  function loadProgress() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : { lessons: {} };
    } catch {
      return { lessons: {} };
    }
  }

  function saveProgress(data) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch {
      // Ignore storage quota errors
    }
  }

  function getLessonProgress(lessonId) {
    const data = loadProgress();
    if (!data.lessons[lessonId]) {
      data.lessons[lessonId] = { practices: {}, quizzes: {}, completed: false };
    }
    return { data, lesson: data.lessons[lessonId] };
  }

  function initTheme() {
    const saved = localStorage.getItem(THEME_KEY) || "dark";
    document.documentElement.setAttribute("data-theme", saved);
    const btn = document.getElementById("theme-toggle");
    if (btn) {
      btn.textContent = saved === "dark" ? "Light Mode" : "Dark Mode";
      btn.addEventListener("click", () => {
        const current = document.documentElement.getAttribute("data-theme") || "dark";
        const next = current === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", next);
        localStorage.setItem(THEME_KEY, next);
        btn.textContent = next === "dark" ? "Light Mode" : "Dark Mode";
      });
    }
  }

  /* ---------------- Pyodide Runtime Loader ---------------- */
  function setRuntimeStatus(state, text) {
    const pill = document.getElementById("runtime-pill");
    const label = document.getElementById("runtime-label");
    if (!pill || !label) return;
    pill.classList.remove("ready", "error");
    if (state === "ready") pill.classList.add("ready");
    if (state === "error") pill.classList.add("error");
    label.textContent = text;
  }

  async function ensurePyodide() {
    if (pyodideInstance) return pyodideInstance;
    if (pyodidePromise) return pyodidePromise;

    if (typeof window.loadPyodide !== "function") {
      setRuntimeStatus("error", "Python runtime unavailable (offline?)");
      throw new Error(
        "Pyodide failed to load. Check your internet connection so the browser can fetch Pyodide from jsDelivr CDN."
      );
    }

    setRuntimeStatus("loading", "Loading Python 3.12...");
    pyodidePromise = window
      .loadPyodide({
        indexURL: "https://cdn.jsdelivr.net/pyodide/v0.27.6/full/",
      })
      .then((py) => {
        pyodideInstance = py;
        py.runPython(`
import sys, io, json, traceback

def _py2027_run_isolated(user_code, tests_json=None):
    ns = {"__name__": "__main__"}
    buf_out = io.StringIO()
    buf_err = io.StringIO()
    old_out, old_err = sys.stdout, sys.stderr
    sys.stdout, sys.stderr = buf_out, buf_err
    error_msg = None
    test_results = []

    try:
        exec(compile(user_code, "<lesson_cell>", "exec"), ns, ns)
    except Exception:
        error_msg = traceback.format_exc()
    finally:
        sys.stdout, sys.stderr = old_out, old_err

    if error_msg is None and tests_json:
        tests = json.loads(tests_json)
        for t in tests:
            t_out = io.StringIO()
            t_err = io.StringIO()
            sys.stdout, sys.stderr = t_out, t_err
            try:
                exec(compile(t["code"], f"<test:{t['name']}>", "exec"), ns, ns)
                test_results.append({"name": t["name"], "passed": True, "detail": ""})
            except AssertionError as e:
                detail = str(e) if str(e) else "Assertion failed"
                test_results.append({"name": t["name"], "passed": False, "detail": detail})
            except Exception as e:
                test_results.append({"name": t["name"], "passed": False, "detail": f"{type(e).__name__}: {e}"})
            finally:
                sys.stdout, sys.stderr = old_out, old_err

    return json.dumps({
        "stdout": buf_out.getvalue(),
        "stderr": buf_err.getvalue(),
        "error": error_msg,
        "tests": test_results,
    })
`);
        setRuntimeStatus("ready", "Python 3.12 Ready");
        return py;
      })
      .catch((err) => {
        pyodidePromise = null;
        setRuntimeStatus("error", "Python load failed");
        throw err;
      });

    return pyodidePromise;
  }

  async function runPythonIsolated(userCode, tests) {
    const py = await ensurePyodide();
    const runner = py.globals.get("_py2027_run_isolated");
    const testsJson = tests ? JSON.stringify(tests) : null;
    const rawResult = runner(userCode, testsJson);
    return JSON.parse(rawResult);
  }

  /* ---------------- Code Editor Ergonomics ---------------- */
  function attachEditorShortcuts(textarea, onRun, onCheck) {
    textarea.addEventListener("keydown", (e) => {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const val = textarea.value;
        textarea.value = val.slice(0, start) + "    " + val.slice(end);
        textarea.selectionStart = textarea.selectionEnd = start + 4;
      } else if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        if (e.shiftKey && onCheck) {
          onCheck();
        } else if (onRun) {
          onRun();
        }
      }
    });
  }

  /* ---------------- Render & Bind Lesson Playground & Practices ---------------- */
  function updateLessonProgressUI(lessonId) {
    const lessonData = window.LESSON_DATA;
    if (!lessonData) return;

    const { data, lesson } = getLessonProgress(lessonId);
    const totalPractices = Object.keys(lessonData.practices || {}).length;
    const solvedPractices = Object.keys(lessonData.practices || {}).filter(
      (id) => lesson.practices[id]
    ).length;

    const totalQuizzes = (lessonData.quizzes || []).length;
    const correctQuizzes = (lessonData.quizzes || []).filter(
      (q) => lesson.quizzes[q.id] && lesson.quizzes[q.id].correct
    ).length;

    const totalItems = totalPractices + totalQuizzes;
    const completedItems = solvedPractices + correctQuizzes;
    const pct = totalItems > 0 ? Math.round((completedItems / totalItems) * 100) : 0;

    if (totalItems > 0 && completedItems === totalItems) {
      lesson.completed = true;
      saveProgress(data);
    }

    const fill = document.getElementById("lesson-progress-fill");
    const text = document.getElementById("lesson-progress-text");
    const stats = document.getElementById("lesson-progress-stats");
    if (fill) fill.style.width = `${pct}%`;
    if (text) text.textContent = `${pct}%`;
    if (stats) {
      stats.textContent = `Practices: ${solvedPractices}/${totalPractices} · Quizzes: ${correctQuizzes}/${totalQuizzes}`;
    }
  }

  function initCodeBoxes(lessonId) {
    const lessonData = window.LESSON_DATA || { practices: {} };
    const boxes = document.querySelectorAll(".code-box");

    boxes.forEach((box) => {
      const editor = box.querySelector("textarea.code-editor");
      const runBtn = box.querySelector(".btn-run");
      const checkBtn = box.querySelector(".btn-check");
      const resetBtn = box.querySelector(".btn-reset");
      const hintBtn = box.querySelector(".btn-hint");
      const answerBtn = box.querySelector(".btn-answer");
      const loadAnswerBtn = box.querySelector(".btn-load-answer");

      const outputEl = box.querySelector(".output-panel");
      const testResultsEl = box.querySelector(".test-results");
      const hintDrawer = box.querySelector(".hint-drawer");
      const solutionDrawer = box.querySelector(".solution-drawer");

      if (!editor) return;
      const initialCode = editor.value;
      const practiceId = box.getAttribute("data-practice-id");
      const practiceSpec = practiceId ? lessonData.practices[practiceId] : null;

      if (practiceId) {
        const { lesson } = getLessonProgress(lessonId);
        if (lesson.practices[practiceId]) {
          box.classList.add("is-solved");
        }
      }

      async function handleRun() {
        if (!outputEl) return;
        outputEl.classList.add("visible");
        outputEl.classList.remove("has-error");
        outputEl.textContent = "Running...";
        if (testResultsEl) testResultsEl.classList.remove("visible");

        try {
          const res = await runPythonIsolated(editor.value, null);
          const combined = [res.stdout, res.stderr, res.error].filter(Boolean).join("\n");
          if (res.error) outputEl.classList.add("has-error");
          outputEl.textContent = combined || "(Program finished with no output)";
        } catch (err) {
          outputEl.classList.add("has-error");
          outputEl.textContent = String(err.message || err);
        }
      }

      async function handleCheck() {
        if (!practiceSpec || !testResultsEl) return;
        outputEl.classList.add("visible");
        outputEl.classList.remove("has-error");
        outputEl.textContent = "Running code & test suite...";
        testResultsEl.classList.remove("visible");

        try {
          const res = await runPythonIsolated(editor.value, practiceSpec.tests);
          const combined = [res.stdout, res.stderr, res.error].filter(Boolean).join("\n");
          if (res.error) {
            outputEl.classList.add("has-error");
            outputEl.textContent = combined;
            testResultsEl.classList.add("visible");
            testResultsEl.innerHTML = `<div class="test-item fail">✗ Execution error before tests could run. Fix the error above and re-check.</div>`;
            return;
          }

          outputEl.textContent = combined || "(No stdout produced)";
          testResultsEl.classList.add("visible");
          testResultsEl.innerHTML = "";

          let allPassed = res.tests.length > 0;
          res.tests.forEach((t) => {
            const row = document.createElement("div");
            row.className = `test-item ${t.passed ? "pass" : "fail"}`;
            row.textContent = t.passed
              ? `✓ PASS: ${t.name}`
              : `✗ FAIL: ${t.name} — ${t.detail}`;
            testResultsEl.appendChild(row);
            if (!t.passed) allPassed = false;
          });

          if (allPassed) {
            box.classList.add("is-solved");
            const { data, lesson } = getLessonProgress(lessonId);
            lesson.practices[practiceId] = true;
            saveProgress(data);
            updateLessonProgressUI(lessonId);

            const summary = document.createElement("div");
            summary.className = "test-item pass";
            summary.style.fontWeight = "700";
            summary.style.marginTop = "0.4rem";
            summary.textContent = "All checks passed! Practice marked complete.";
            testResultsEl.appendChild(summary);
          }
        } catch (err) {
          outputEl.classList.add("has-error");
          outputEl.textContent = String(err.message || err);
        }
      }

      if (runBtn) runBtn.addEventListener("click", handleRun);
      if (checkBtn) checkBtn.addEventListener("click", handleCheck);

      if (resetBtn) {
        resetBtn.addEventListener("click", () => {
          editor.value = initialCode;
          if (outputEl) {
            outputEl.classList.remove("visible", "has-error");
            outputEl.textContent = "";
          }
          if (testResultsEl) {
            testResultsEl.classList.remove("visible");
            testResultsEl.innerHTML = "";
          }
        });
      }

      if (hintBtn && hintDrawer) {
        let revealedCount = 0;
        const steps = hintDrawer.querySelectorAll(".hint-step");
        steps.forEach((s) => (s.style.display = "none"));

        hintBtn.addEventListener("click", () => {
          hintDrawer.classList.add("visible");
          if (revealedCount < steps.length) {
            steps[revealedCount].style.display = "block";
            revealedCount++;
            hintBtn.textContent =
              revealedCount < steps.length
                ? `Next Hint (${revealedCount}/${steps.length})`
                : `All Hints Shown`;
          } else {
            hintDrawer.classList.toggle("visible");
          }
        });
      }

      if (answerBtn && solutionDrawer) {
        answerBtn.addEventListener("click", () => {
          const isOpen = solutionDrawer.classList.toggle("visible");
          answerBtn.textContent = isOpen ? "Hide Final Answer" : "Final Answer";
        });
      }

      if (loadAnswerBtn && practiceSpec && practiceSpec.solution) {
        loadAnswerBtn.addEventListener("click", () => {
          editor.value = practiceSpec.solution;
        });
      }

      attachEditorShortcuts(editor, handleRun, practiceSpec ? handleCheck : null);
    });
  }

  /* ---------------- Render & Bind Quizzes ---------------- */
  function escapeHtml(str) {
    return str
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function initQuizzes(lessonId) {
    const container = document.getElementById("quiz-container");
    const lessonData = window.LESSON_DATA;
    if (!container || !lessonData || !lessonData.quizzes) return;

    const { data, lesson } = getLessonProgress(lessonId);

    function renderAll() {
      container.innerHTML = "";
      lessonData.quizzes.forEach((q, idx) => {
        const card = document.createElement("div");
        card.className = "quiz-card";
        const savedChoice = lesson.quizzes[q.id]?.selected || null;

        let codeHtml = "";
        if (q.code) {
          codeHtml = `<div class="quiz-code"><pre>${escapeHtml(q.code)}</pre></div>`;
        }

        const optionsHtml = q.options
          .map((opt) => {
            let cls = "quiz-option";
            if (savedChoice) {
              if (opt.key === savedChoice && opt.key === q.answer) {
                cls += " selected-correct";
              } else if (opt.key === savedChoice && opt.key !== q.answer) {
                cls += " selected-wrong";
              } else if (opt.key === q.answer) {
                cls += " reveal-correct";
              }
            }
            return `
              <button class="${cls}" data-key="${opt.key}" ${savedChoice ? "disabled" : ""}>
                <span class="option-key">${opt.key}</span>
                <span>${escapeHtml(opt.text)}</span>
              </button>`;
          })
          .join("");

        const rationaleItems = q.options
          .map((opt) => {
            const isRight = opt.key === q.answer;
            return `
              <li class="rationale-item ${isRight ? "is-right" : "is-wrong"}">
                <strong>${opt.key} (${isRight ? "Correct" : "Why this is wrong"}):</strong>
                ${escapeHtml(opt.why)}
              </li>`;
          })
          .join("");

        const statusHtml = savedChoice
          ? savedChoice === q.answer
            ? `<div class="rationale-status correct">✓ Correct! (${q.answer})</div>`
            : `<div class="rationale-status wrong">✗ You chose ${savedChoice}. Correct answer is ${q.answer}.</div>`
          : "";

        card.innerHTML = `
          <div class="quiz-question">Q${idx + 1}. ${escapeHtml(q.question)}</div>
          ${codeHtml}
          <div class="quiz-options">${optionsHtml}</div>
          <div class="quiz-rationale ${savedChoice ? "visible" : ""}">
            ${statusHtml}
            <ul class="rationale-list">${rationaleItems}</ul>
          </div>
        `;

        card.querySelectorAll(".quiz-option").forEach((btn) => {
          btn.addEventListener("click", () => {
            const chosen = btn.getAttribute("data-key");
            const isCorrect = chosen === q.answer;
            const latest = getLessonProgress(lessonId);
            latest.lesson.quizzes[q.id] = { selected: chosen, correct: isCorrect };
            saveProgress(latest.data);
            lesson.quizzes[q.id] = { selected: chosen, correct: isCorrect };
            renderAll();
            updateLessonProgressUI(lessonId);
          });
        });

        container.appendChild(card);
      });

      const scoreEl = document.getElementById("quiz-score-banner");
      if (scoreEl) {
        const answered = lessonData.quizzes.filter((q) => lesson.quizzes[q.id]).length;
        const right = lessonData.quizzes.filter((q) => lesson.quizzes[q.id]?.correct).length;
        scoreEl.textContent = `Quiz Score: ${right} / ${lessonData.quizzes.length} correct (${answered} answered)`;
      }
    }

    const resetQuizBtn = document.getElementById("reset-quiz-btn");
    if (resetQuizBtn) {
      resetQuizBtn.addEventListener("click", () => {
        const latest = getLessonProgress(lessonId);
        latest.lesson.quizzes = {};
        latest.lesson.completed = false;
        saveProgress(latest.data);
        lesson.quizzes = {};
        renderAll();
        updateLessonProgressUI(lessonId);
      });
    }

    renderAll();
  }

  /* ---------------- Master Plan Progress Dashboard ---------------- */
  function initMasterPlan() {
    const progress = loadProgress();
    const cards = document.querySelectorAll(".lesson-card[data-lesson-id]");
    let readyCount = 0;
    let completedCount = 0;

    cards.forEach((card) => {
      const id = card.getAttribute("data-lesson-id");
      const isReady = card.getAttribute("data-status") === "ready";
      if (isReady) readyCount++;
      const lp = progress.lessons[id];
      const badge = card.querySelector(".lesson-progress-tag");
      if (lp && lp.completed) {
        completedCount++;
        if (badge) {
          badge.textContent = "Completed ✓";
          badge.className = "status-pill status-ready lesson-progress-tag";
        }
      } else if (lp && (Object.keys(lp.practices || {}).length > 0 || Object.keys(lp.quizzes || {}).length > 0)) {
        const pCount = Object.values(lp.practices || {}).filter(Boolean).length;
        const qCount = Object.values(lp.quizzes || {}).filter((q) => q && q.correct).length;
        if (badge) {
          badge.textContent = `In Progress (${pCount}P · ${qCount}Q)`;
        }
      }
    });

    const summaryEl = document.getElementById("master-progress-summary");
    if (summaryEl) {
      summaryEl.textContent = `${completedCount} of ${cards.length} modules completed · ${readyCount} interactive module(s) built`;
    }

    const resetAllBtn = document.getElementById("reset-all-progress");
    if (resetAllBtn) {
      resetAllBtn.addEventListener("click", () => {
        if (confirm("Reset all saved lesson progress and quiz scores in this browser?")) {
          localStorage.removeItem(STORAGE_KEY);
          location.reload();
        }
      });
    }
  }

  /* ---------------- Boot ---------------- */
  document.addEventListener("DOMContentLoaded", () => {
    initTheme();

    const pageType = document.body.getAttribute("data-page");
    if (pageType === "master-plan") {
      initMasterPlan();
    } else if (pageType === "lesson") {
      const lessonId = document.body.getAttribute("data-lesson-id") || "lesson-01";
      initCodeBoxes(lessonId);
      initQuizzes(lessonId);
      updateLessonProgressUI(lessonId);
      // Preload Pyodide quietly in the background so Run is instant
      ensurePyodide().catch(() => {});
    }
  });

  window.PY2027 = {
    loadProgress,
    ensurePyodide,
  };
})();
