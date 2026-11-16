(function () {
  "use strict";

  const STORAGE_KEY = "db_mastery_progress_v1";
  const THEME_KEY = "db_mastery_theme";

  let sqlEnginePromise = null;

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function getProgress() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : { lessons: {} };
    } catch (_) {
      return { lessons: {} };
    }
  }

  function saveProgress(data) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch (_) {
      // Ignore storage quota errors
    }
  }

  function markItemDone(lessonId, kind, itemId) {
    if (!lessonId) return;
    const progress = getProgress();
    if (!progress.lessons[lessonId]) {
      progress.lessons[lessonId] = { labs: {}, quizzes: {} };
    }
    progress.lessons[lessonId][kind][itemId] = true;
    saveProgress(progress);
    updateProgressUI();
  }

  function getLessonStats(lessonId, totalLabs, totalQuizzes) {
    const progress = getProgress();
    const lesson = progress.lessons[lessonId] || { labs: {}, quizzes: {} };
    const labsDone = Object.keys(lesson.labs || {}).filter((k) => lesson.labs[k]).length;
    const quizzesDone = Object.keys(lesson.quizzes || {}).filter((k) => lesson.quizzes[k]).length;
    const total = totalLabs + totalQuizzes;
    const done = Math.min(labsDone, totalLabs) + Math.min(quizzesDone, totalQuizzes);
    const pct = total > 0 ? Math.round((done / total) * 100) : 0;
    return { labsDone, quizzesDone, done, total, pct };
  }

  function updateProgressUI() {
    const lessonId = document.body.getAttribute("data-lesson-id");
    if (lessonId) {
      const labCount = Object.keys(window.COURSE_LABS || {}).length;
      const quizCount = Object.keys(window.COURSE_QUIZZES || {}).length;
      const stats = getLessonStats(lessonId, labCount, quizCount);
      const fill = document.getElementById("lesson-progress-fill");
      const text = document.getElementById("lesson-progress-text");
      if (fill) fill.style.width = stats.pct + "%";
      if (text) {
        text.textContent = `${stats.done}/${stats.total} completed (${stats.pct}%)`;
      }
    }

    // Update master plan progress indicators if present
    let overallDone = 0;
    let overallTotal = 0;
    document.querySelectorAll("[data-master-lesson-progress]").forEach((el) => {
      const targetLesson = el.getAttribute("data-master-lesson-progress");
      const totalLabs = parseInt(el.getAttribute("data-total-labs") || "0", 10);
      const totalQuizzes = parseInt(el.getAttribute("data-total-quizzes") || "0", 10);
      const stats = getLessonStats(targetLesson, totalLabs, totalQuizzes);
      overallDone += stats.done;
      overallTotal += stats.total;
      if (stats.total > 0) {
        el.textContent =
          stats.done === stats.total
            ? `Completed (${stats.done}/${stats.total})`
            : stats.done > 0
            ? `In Progress (${stats.done}/${stats.total})`
            : `0/${stats.total} exercises`;
      }
    });
    const overallFill = document.getElementById("overall-course-progress-fill");
    const overallText = document.getElementById("overall-course-progress-text");
    if (overallFill && overallTotal > 0) {
      const overallPct = Math.round((overallDone / overallTotal) * 100);
      overallFill.style.width = overallPct + "%";
      if (overallText) {
        overallText.textContent = `${overallDone}/${overallTotal} total exercises (${overallPct}%)`;
      }
    }
  }

  // Theme handling
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

  // SQL Engine loader
  function getSqlEngine() {
    if (sqlEnginePromise) return sqlEnginePromise;
    const pill = document.getElementById("engine-status");
    if (typeof window.initSqlJs !== "function") {
      const err = new Error("sql-asm.js failed to load. Ensure assets/sql-asm.js is present.");
      if (pill) {
        pill.classList.add("error");
        pill.querySelector(".engine-label").textContent = "SQL Engine Error";
      }
      return Promise.reject(err);
    }
    sqlEnginePromise = window.initSqlJs().then(
      (SQL) => {
        if (pill) {
          pill.classList.add("ready");
          const db = new SQL.Database();
          const ver = db.exec("SELECT sqlite_version()")[0].values[0][0];
          db.close();
          pill.querySelector(".engine-label").textContent = `SQLite ${ver} Live`;
        }
        return SQL;
      },
      (err) => {
        if (pill) {
          pill.classList.add("error");
          pill.querySelector(".engine-label").textContent = "SQL Engine Failed";
        }
        throw err;
      }
    );
    return sqlEnginePromise;
  }

  function renderResultTables(results, elapsedMs, rowsModified) {
    if (!results || results.length === 0) {
      return `
        <div class="output-status">
          <span>Executed in ${elapsedMs.toFixed(1)} ms</span>
          <span>Rows affected (last statement): ${rowsModified}</span>
        </div>
        <div class="output-ok">Statement(s) executed cleanly with no result set returned.</div>
      `;
    }
    let html = `
      <div class="output-status">
        <span>Executed in ${elapsedMs.toFixed(1)} ms</span>
        <span>${results.length} result set${results.length > 1 ? "s" : ""}</span>
      </div>
    `;
    results.forEach((res, idx) => {
      html += `<div class="data-table-wrap"><table class="data-table"><thead><tr>`;
      res.columns.forEach((col) => {
        html += `<th>${escapeHtml(col)}</th>`;
      });
      html += `</tr></thead><tbody>`;
      if (res.values.length === 0) {
        html += `<tr><td colspan="${res.columns.length}" class="null-val">(0 rows)</td></tr>`;
      } else {
        res.values.forEach((row) => {
          html += `<tr>`;
          row.forEach((cell) => {
            if (cell === null || cell === undefined) {
              html += `<td class="null-val">NULL</td>`;
            } else {
              html += `<td>${escapeHtml(cell)}</td>`;
            }
          });
          html += `</tr>`;
        });
      }
      html += `</tbody></table></div>`;
      if (results.length > 1 && idx < results.length - 1) {
        html += `<div style="height:0.35rem;"></div>`;
      }
    });
    return html;
  }

  function bindEditorShortcuts(textarea, onRun) {
    textarea.addEventListener("keydown", (e) => {
      if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        onRun();
      } else if (e.key === "Tab") {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value =
          textarea.value.substring(0, start) + "  " + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 2;
      }
    });
  }

  function runSqlWithSetup(SQL, setupSql, userSql) {
    const db = new SQL.Database();
    try {
      if (setupSql && setupSql.trim()) {
        db.run(setupSql);
      }
      const t0 = performance.now();
      const results = db.exec(userSql);
      const elapsed = performance.now() - t0;
      const modified = db.getRowsModified();
      return { results, elapsed, modified };
    } finally {
      db.close();
    }
  }

  // Initialize Runnable Code Demos
  function initCodeDemos() {
    document.querySelectorAll(".code-demo").forEach((demo) => {
      const editor = demo.querySelector(".sql-editor");
      const runBtn = demo.querySelector("[data-action='run-demo']");
      const resetBtn = demo.querySelector("[data-action='reset-demo']");
      const output = demo.querySelector(".output-pane");
      const setupScript = demo.querySelector("script.demo-setup");
      const setupSql = setupScript ? setupScript.textContent : "";
      const initialCode = editor ? editor.value : "";

      async function executeDemo() {
        if (!editor || !output) return;
        output.classList.remove("hidden");
        output.innerHTML = `<div class="output-status"><span>Running SQL...</span></div>`;
        try {
          const SQL = await getSqlEngine();
          const { results, elapsed, modified } = runSqlWithSetup(SQL, setupSql, editor.value);
          output.innerHTML = renderResultTables(results, elapsed, modified);
        } catch (err) {
          output.innerHTML = `<div class="output-error">${escapeHtml(err.message)}</div>`;
        }
      }

      if (runBtn) runBtn.addEventListener("click", executeDemo);
      if (resetBtn && editor) {
        resetBtn.addEventListener("click", () => {
          editor.value = initialCode;
          if (output) {
            output.classList.add("hidden");
            output.innerHTML = "";
          }
        });
      }
      if (editor) bindEditorShortcuts(editor, executeDemo);
    });
  }

  // Initialize Coding Labs
  function initLabs() {
    const labs = window.COURSE_LABS || {};
    const lessonId = document.body.getAttribute("data-lesson-id");

    document.querySelectorAll(".lab-card[data-lab-id]").forEach((card) => {
      const labId = card.getAttribute("data-lab-id");
      const spec = labs[labId];
      if (!spec) return;

      let hintsRevealed = 0;
      const totalHints = (spec.hints || []).length;

      card.innerHTML = `
        <div class="lab-header">
          <div>
            <div class="meta-row">
              <span class="badge badge-green">Hands-On Coding Lab</span>
              <span class="badge badge-muted">${escapeHtml(spec.difficulty || "Practice")}</span>
              <span class="badge badge-blue hidden" data-role="solved-badge">Passed</span>
            </div>
            <h3>${escapeHtml(spec.title)}</h3>
          </div>
          <button class="btn btn-sm" type="button" data-action="toggle-schema">View Seeded Schema</button>
        </div>
        <div class="lab-body">
          ${spec.promptHtml}
        </div>
        <div class="lab-panel hidden" data-role="schema-panel"></div>
        <textarea class="sql-editor" spellcheck="false" aria-label="SQL Lab Editor">${escapeHtml(
          spec.starterSql || ""
        )}</textarea>
        <div class="lab-toolbar">
          <div class="lab-toolbar-group">
            <button class="btn btn-primary btn-sm" type="button" data-action="run-lab">Run SQL <kbd>⌘/Ctrl+Enter</kbd></button>
            <button class="btn btn-success btn-sm" type="button" data-action="check-lab">Check Answer</button>
          </div>
          <div class="lab-toolbar-group">
            <button class="btn btn-warning btn-sm" type="button" data-action="hint-lab">Hint (0/${totalHints})</button>
            <button class="btn btn-sm" type="button" data-action="solution-lab">Final Answer</button>
            <button class="btn btn-sm" type="button" data-action="reset-lab">Reset</button>
          </div>
        </div>
        <div class="output-pane hidden" data-role="lab-output"></div>
        <div class="lab-panel hidden" data-role="hints-panel"></div>
        <div class="lab-panel hidden" data-role="solution-panel"></div>
      `;

      const editor = card.querySelector(".sql-editor");
      const outputPane = card.querySelector("[data-role='lab-output']");
      const hintsPanel = card.querySelector("[data-role='hints-panel']");
      const solutionPanel = card.querySelector("[data-role='solution-panel']");
      const schemaPanel = card.querySelector("[data-role='schema-panel']");
      const hintBtn = card.querySelector("[data-action='hint-lab']");
      const solvedBadge = card.querySelector("[data-role='solved-badge']");

      // Restore solved state if already passed
      const progress = getProgress();
      if (
        lessonId &&
        progress.lessons[lessonId] &&
        progress.lessons[lessonId].labs &&
        progress.lessons[lessonId].labs[labId]
      ) {
        solvedBadge.classList.remove("hidden");
      }

      async function runLab() {
        outputPane.classList.remove("hidden");
        outputPane.innerHTML = `<div class="output-status"><span>Executing query...</span></div>`;
        try {
          const SQL = await getSqlEngine();
          const { results, elapsed, modified } = runSqlWithSetup(
            SQL,
            spec.setupSql || "",
            editor.value
          );
          outputPane.innerHTML = renderResultTables(results, elapsed, modified);
        } catch (err) {
          outputPane.innerHTML = `<div class="output-error">${escapeHtml(err.message)}</div>`;
        }
      }

      async function checkLab() {
        outputPane.classList.remove("hidden");
        outputPane.innerHTML = `<div class="output-status"><span>Running verification checks...</span></div>`;
        try {
          const SQL = await getSqlEngine();
          const report = spec.verify(SQL, editor.value, spec.setupSql || "");
          let html = report.passed
            ? `<div class="output-ok">All checks passed! Senior-grade solution verified.</div>`
            : `<div class="output-error">Some checks did not pass yet. Inspect the diagnostics below.</div>`;

          html += `<ul class="check-list">`;
          report.checks.forEach((chk) => {
            html += `
              <li class="check-item ${chk.passed ? "pass" : "fail"}">
                <span>${chk.passed ? "PASS" : "FAIL"}</span>
                <div>
                  <strong>${escapeHtml(chk.label)}</strong>
                  ${chk.detail ? `<div style="color:var(--text-muted);font-size:0.82rem;">${escapeHtml(chk.detail)}</div>` : ""}
                </div>
              </li>
            `;
          });
          html += `</ul>`;
          outputPane.innerHTML = html;

          if (report.passed) {
            solvedBadge.classList.remove("hidden");
            markItemDone(lessonId, "labs", labId);
          }
        } catch (err) {
          outputPane.innerHTML = `<div class="output-error">Verification error: ${escapeHtml(
            err.message
          )}</div>`;
        }
      }

      card.querySelector("[data-action='run-lab']").addEventListener("click", runLab);
      card.querySelector("[data-action='check-lab']").addEventListener("click", checkLab);
      bindEditorShortcuts(editor, runLab);

      hintBtn.addEventListener("click", () => {
        if (totalHints === 0) return;
        if (hintsRevealed < totalHints) {
          hintsRevealed += 1;
        }
        hintBtn.textContent = `Hint (${hintsRevealed}/${totalHints})`;
        hintsPanel.classList.remove("hidden");
        let html = `<strong style="font-size:0.88rem;">Progressive Hints (${hintsRevealed}/${totalHints})</strong>`;
        for (let i = 0; i < hintsRevealed; i++) {
          html += `<div class="hint-box"><strong>Hint ${i + 1}:</strong> ${spec.hints[i]}</div>`;
        }
        hintsPanel.innerHTML = html;
      });

      card.querySelector("[data-action='solution-lab']").addEventListener("click", () => {
        const isHidden = solutionPanel.classList.contains("hidden");
        if (!isHidden) {
          solutionPanel.classList.add("hidden");
          return;
        }
        solutionPanel.classList.remove("hidden");
        solutionPanel.innerHTML = `
          <div style="display:flex;justify-content:space-between;align-items:center;gap:0.5rem;flex-wrap:wrap;">
            <strong style="font-size:0.9rem;color:var(--accent);">Reference Solution & Senior Breakdown</strong>
            <button class="btn btn-sm btn-primary" type="button" data-action="load-solution">Load into Editor</button>
          </div>
          <div class="solution-box">
            <pre><code>${escapeHtml(spec.solutionSql)}</code></pre>
            <div style="font-size:0.9rem;margin-top:0.5rem;">${spec.solutionExplanationHtml || ""}</div>
          </div>
        `;
        solutionPanel
          .querySelector("[data-action='load-solution']")
          .addEventListener("click", () => {
            editor.value = spec.solutionSql;
          });
      });

      card.querySelector("[data-action='reset-lab']").addEventListener("click", () => {
        editor.value = spec.starterSql || "";
        outputPane.classList.add("hidden");
        outputPane.innerHTML = "";
      });

      card.querySelector("[data-action='toggle-schema']").addEventListener("click", async () => {
        if (!schemaPanel.classList.contains("hidden")) {
          schemaPanel.classList.add("hidden");
          return;
        }
        schemaPanel.classList.remove("hidden");
        if (!spec.setupSql || !spec.setupSql.trim()) {
          schemaPanel.innerHTML = `<div style="font-size:0.88rem;color:var(--text-muted);">Empty database — you are creating the schema from scratch in this lab.</div>`;
          return;
        }
        try {
          const SQL = await getSqlEngine();
          const db = new SQL.Database();
          db.run(spec.setupSql);
          const tablesRes = db.exec(
            "SELECT name, sql FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name;"
          );
          let html = `<strong style="font-size:0.88rem;">Pre-Seeded Tables in This Lab</strong>`;
          if (tablesRes.length === 0 || tablesRes[0].values.length === 0) {
            html += `<p style="font-size:0.86rem;color:var(--text-muted);margin:0.35rem 0 0;">No pre-seeded tables.</p>`;
          } else {
            tablesRes[0].values.forEach(([tblName, ddl]) => {
              const countRes = db.exec(`SELECT COUNT(*) FROM "${tblName}";`);
              const rowCount = countRes[0].values[0][0];
              html += `
                <div style="margin-top:0.65rem;">
                  <div style="font-family:var(--font-mono);font-size:0.82rem;color:var(--accent);">${escapeHtml(
                    tblName
                  )} (${rowCount} rows)</div>
                  <pre style="margin:0.35rem 0 0;"><code>${escapeHtml(ddl)}</code></pre>
                </div>
              `;
            });
          }
          db.close();
          schemaPanel.innerHTML = html;
        } catch (err) {
          schemaPanel.innerHTML = `<div class="output-error">${escapeHtml(err.message)}</div>`;
        }
      });
    });
  }

  // Initialize Multiple-Choice Quizzes
  function initQuizzes() {
    const quizzes = window.COURSE_QUIZZES || {};
    const lessonId = document.body.getAttribute("data-lesson-id");
    const letters = ["A", "B", "C", "D", "E", "F"];

    document.querySelectorAll(".quiz-card[data-quiz-id]").forEach((card) => {
      const quizId = card.getAttribute("data-quiz-id");
      const spec = quizzes[quizId];
      if (!spec) return;

      let selectedIndex = null;

      let optionsHtml = spec.options
        .map(
          (opt, idx) => `
          <button type="button" class="quiz-option" data-opt-index="${idx}">
            <span class="quiz-letter">${letters[idx]}</span>
            <div>${opt.labelHtml}</div>
          </button>
        `
        )
        .join("");

      card.innerHTML = `
        <div class="meta-row" style="margin-bottom:0.55rem;">
          <span class="badge badge-pink">Knowledge Check</span>
          <span class="badge badge-muted">${escapeHtml(spec.tag || "Architecture & SQL")}</span>
          <span class="badge badge-green hidden" data-role="quiz-solved-badge">Mastered</span>
        </div>
        <div class="quiz-question">${spec.questionHtml}</div>
        ${spec.codeHtml ? `<pre><code>${escapeHtml(spec.codeHtml)}</code></pre>` : ""}
        <div class="quiz-options">${optionsHtml}</div>
        <div style="margin-top:0.85rem;display:flex;gap:0.55rem;align-items:center;flex-wrap:wrap;">
          <button type="button" class="btn btn-primary btn-sm" data-action="submit-quiz">Check Answer</button>
          <button type="button" class="btn btn-sm hidden" data-action="toggle-all-rationales">Show Why Every Option Is Right/Wrong</button>
        </div>
        <div class="quiz-feedback hidden" data-role="quiz-feedback"></div>
      `;

      const optionBtns = card.querySelectorAll(".quiz-option");
      const feedbackEl = card.querySelector("[data-role='quiz-feedback']");
      const toggleAllBtn = card.querySelector("[data-action='toggle-all-rationales']");
      const solvedBadge = card.querySelector("[data-role='quiz-solved-badge']");

      const progress = getProgress();
      if (
        lessonId &&
        progress.lessons[lessonId] &&
        progress.lessons[lessonId].quizzes &&
        progress.lessons[lessonId].quizzes[quizId]
      ) {
        solvedBadge.classList.remove("hidden");
      }

      optionBtns.forEach((btn) => {
        btn.addEventListener("click", () => {
          selectedIndex = parseInt(btn.getAttribute("data-opt-index"), 10);
          optionBtns.forEach((b) => b.classList.remove("selected", "correct", "wrong"));
          btn.classList.add("selected");
        });
      });

      function renderAllRationales() {
        let html = `<div class="quiz-rationale-list">`;
        spec.options.forEach((opt, idx) => {
          html += `
            <div class="quiz-rationale-item ${opt.correct ? "is-correct" : "is-wrong"}">
              <strong>${letters[idx]} — ${opt.correct ? "Correct" : "Incorrect"}:</strong>
              ${opt.whyHtml}
            </div>
          `;
        });
        html += `</div>`;
        return html;
      }

      card.querySelector("[data-action='submit-quiz']").addEventListener("click", () => {
        if (selectedIndex === null) {
          feedbackEl.classList.remove("hidden");
          feedbackEl.innerHTML = `<span style="color:var(--amber);font-weight:600;">Select an option first, then click Check Answer.</span>`;
          return;
        }
        const chosen = spec.options[selectedIndex];
        optionBtns.forEach((b, idx) => {
          b.classList.remove("selected", "correct", "wrong");
          if (idx === selectedIndex) {
            b.classList.add(chosen.correct ? "correct" : "wrong");
          } else if (spec.options[idx].correct) {
            b.classList.add("correct");
          }
        });

        feedbackEl.classList.remove("hidden");
        toggleAllBtn.classList.remove("hidden");
        const correctIdx = spec.options.findIndex((o) => o.correct);

        feedbackEl.innerHTML = `
          <div style="font-weight:700;color:${chosen.correct ? "var(--green)" : "var(--red)"};margin-bottom:0.4rem;">
            ${
              chosen.correct
                ? `Correct (${letters[selectedIndex]})!`
                : `Not quite (${letters[selectedIndex]} is incorrect — Correct answer is ${letters[correctIdx]}).`
            }
          </div>
          <div><strong>Why ${letters[selectedIndex]} is ${chosen.correct ? "right" : "wrong"}:</strong> ${
          chosen.whyHtml
        }</div>
          ${
            !chosen.correct
              ? `<div style="margin-top:0.5rem;"><strong>Why ${letters[correctIdx]} is right:</strong> ${spec.options[correctIdx].whyHtml}</div>`
              : ""
          }
          <div class="hidden" data-role="all-rationales">${renderAllRationales()}</div>
        `;

        if (chosen.correct) {
          solvedBadge.classList.remove("hidden");
          markItemDone(lessonId, "quizzes", quizId);
        }
      });

      toggleAllBtn.addEventListener("click", () => {
        const box = feedbackEl.querySelector("[data-role='all-rationales']");
        if (!box) return;
        const hidden = box.classList.contains("hidden");
        box.classList.toggle("hidden");
        toggleAllBtn.textContent = hidden
          ? "Hide All Option Explanations"
          : "Show Why Every Option Is Right/Wrong";
      });
    });
  }

  document.addEventListener("DOMContentLoaded", () => {
    initTheme();
    initCodeDemos();
    initLabs();
    initQuizzes();
    updateProgressUI();
    if (typeof window.initSqlJs === "function") {
      getSqlEngine().catch(() => {});
    }
  });
})();
