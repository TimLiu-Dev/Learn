(function (global) {
  "use strict";

  const STORAGE_KEY = "fe2027_progress_v1";

  function loadProgress() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      const parsed = raw ? JSON.parse(raw) : {};
      return {
        completed: parsed.completed || {},
        lessons: parsed.lessons || {}
      };
    } catch {
      return { completed: {}, lessons: {} };
    }
  }

  function saveProgress(state) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch {
      // Ignore storage errors in restricted environments
    }
  }

  function initThemeToggle(btnId) {
    const saved = (() => { try { return localStorage.getItem("fe2027_theme"); } catch { return null; } })();
    if (saved === "light" || saved === "dark") {
      document.documentElement.setAttribute("data-theme", saved);
    }
    const btn = document.getElementById(btnId || "themeToggle");
    if (!btn) return;
    btn.addEventListener("click", () => {
      const curr = document.documentElement.getAttribute("data-theme") || "dark";
      const next = curr === "dark" ? "light" : "dark";
      document.documentElement.setAttribute("data-theme", next);
      try { localStorage.setItem("fe2027_theme", next); } catch {}
    });
  }

  function enableTabSupport(textarea) {
    textarea.addEventListener("keydown", (e) => {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.slice(0, start) + "  " + textarea.value.slice(end);
        textarea.selectionStart = textarea.selectionEnd = start + 2;
      }
    });
  }

  function formatValue(val) {
    if (typeof val === "string") return val;
    if (val instanceof Error) return `${val.name}: ${val.message}`;
    try { return JSON.stringify(val, null, 2); } catch { return String(val); }
  }

  /**
   * Renders a runnable HTML/CSS/JS or JS-console code example.
   */
  function initLiveExample(mountId, cfg) {
    const mount = document.getElementById(mountId);
    if (!mount) return;

    const mode = cfg.mode || "iframe"; // "iframe" or "js"
    mount.innerHTML = `
      <div class="lab-shell">
        <div class="lab-header">
          <div>
            <span class="tag tag-core">Live Example</span>
            <span class="lab-title" style="margin-left:0.45rem;">${cfg.title}</span>
          </div>
          <div class="lab-btn-group">
            <button type="button" class="btn btn-primary" data-act="run">Run Example</button>
            <button type="button" class="btn btn-ghost" data-act="reset">Reset</button>
          </div>
        </div>
        ${cfg.description ? `<div class="lab-prompt">${cfg.description}</div>` : ""}
        <div class="lab-body">
          <div class="lab-editor-pane">
            <div class="pane-bar"><span>Editable Code (${mode === "iframe" ? "HTML/CSS/JS" : "JavaScript"})</span><span>Tab = 2 spaces</span></div>
            <textarea class="code-editor" spellcheck="false"></textarea>
          </div>
          <div class="lab-output-pane">
            <div class="pane-bar"><span>${mode === "iframe" ? "Sandboxed Browser Preview" : "Console Output"}</span><span data-act="status">Ready</span></div>
            ${mode === "iframe"
              ? `<iframe class="lab-iframe" sandbox="allow-scripts"></iframe>`
              : `<div class="lab-console">Click "Run Example" to execute.</div>`
            }
          </div>
        </div>
      </div>
    `;

    const editor = mount.querySelector(".code-editor");
    editor.value = cfg.initialCode;
    enableTabSupport(editor);

    const statusEl = mount.querySelector('[data-act="status"]');
    const runBtn = mount.querySelector('[data-act="run"]');
    const resetBtn = mount.querySelector('[data-act="reset"]');

    function run() {
      const code = editor.value;
      if (mode === "iframe") {
        const iframe = mount.querySelector("iframe");
        iframe.srcdoc = code;
        statusEl.textContent = "Rendered";
      } else {
        const consoleEl = mount.querySelector(".lab-console");
        const logs = [];
        const fakeConsole = {
          log: (...args) => logs.push(args.map(formatValue).join(" ")),
          warn: (...args) => logs.push("[WARN] " + args.map(formatValue).join(" ")),
          error: (...args) => logs.push("[ERROR] " + args.map(formatValue).join(" "))
        };
        try {
          const fn = new Function("console", code);
          const ret = fn(fakeConsole);
          if (ret !== undefined) logs.push("=> " + formatValue(ret));
          consoleEl.textContent = logs.length ? logs.join("\n") : "(Executed cleanly with no console output)";
          statusEl.textContent = "Ran cleanly";
        } catch (err) {
          consoleEl.textContent = `Runtime Error: ${err.message}`;
          statusEl.textContent = "Error";
        }
      }
    }

    runBtn.addEventListener("click", run);
    resetBtn.addEventListener("click", () => {
      editor.value = cfg.initialCode;
      run();
    });

    if (cfg.autoRun !== false) run();
  }

  /**
   * Renders an interactive coding practice lab with Run, Automated Checks, Progressive Hints, and Final Answer.
   */
  function initCodeLab(mountId, cfg, onPassCallback) {
    const mount = document.getElementById(mountId);
    if (!mount) return;

    let hintIdx = 0;
    let answerVisible = false;
    let activeRunId = 0;

    mount.innerHTML = `
      <div class="lab-shell">
        <div class="lab-header">
          <div>
            <span class="tag tag-senior">Practice Lab</span>
            <span class="lab-title" style="margin-left:0.45rem;">${cfg.title}</span>
          </div>
          <span class="tag tag-core" data-act="badge">Not checked</span>
        </div>
        <div class="lab-prompt">${cfg.prompt}</div>
        <div class="lab-body">
          <div class="lab-editor-pane">
            <div class="pane-bar"><span>Your Solution (${cfg.languageLabel || "JavaScript"})</span><span>Cmd/Ctrl + Enter to check</span></div>
            <textarea class="code-editor" spellcheck="false"></textarea>
          </div>
          <div class="lab-output-pane">
            <div class="pane-bar"><span>Execution &amp; Test Runner Output</span><span data-act="summary">Awaiting run</span></div>
            <div class="lab-console">Write your solution on the left, then click "Run Code" or "Check Solution".</div>
          </div>
        </div>
        <div class="test-results" data-act="tests"></div>
        <div class="hint-drawer" data-act="hints"></div>
        <div class="answer-drawer" data-act="answer"></div>
        <div class="lab-actions">
          <div class="lab-btn-group">
            <button type="button" class="btn" data-act="run">Run Code</button>
            <button type="button" class="btn btn-primary" data-act="check">Check Solution</button>
            <button type="button" class="btn btn-ghost" data-act="reset">Reset</button>
          </div>
          <div class="lab-btn-group">
            <button type="button" class="btn btn-amber" data-act="hint">Hint (0/${(cfg.hints || []).length})</button>
            <button type="button" class="btn" data-act="toggleAnswer">Show Final Answer</button>
          </div>
        </div>
      </div>
    `;

    const editor = mount.querySelector(".code-editor");
    editor.value = cfg.starterCode;
    enableTabSupport(editor);

    const consoleEl = mount.querySelector(".lab-console");
    const summaryEl = mount.querySelector('[data-act="summary"]');
    const badgeEl = mount.querySelector('[data-act="badge"]');
    const testsEl = mount.querySelector('[data-act="tests"]');
    const hintsEl = mount.querySelector('[data-act="hints"]');
    const answerEl = mount.querySelector('[data-act="answer"]');
    const hintBtn = mount.querySelector('[data-act="hint"]');
    const answerBtn = mount.querySelector('[data-act="toggleAnswer"]');

    async function runOnly() {
      const runId = ++activeRunId;
      const logs = [];
      const fakeConsole = {
        log: (...args) => {
          logs.push(args.map(formatValue).join(" "));
          if (runId === activeRunId) consoleEl.textContent = logs.join("\n");
        },
        warn: (...args) => {
          logs.push("[WARN] " + args.map(formatValue).join(" "));
          if (runId === activeRunId) consoleEl.textContent = logs.join("\n");
        },
        error: (...args) => {
          logs.push("[ERROR] " + args.map(formatValue).join(" "));
          if (runId === activeRunId) consoleEl.textContent = logs.join("\n");
        }
      };
      if (cfg.isAsync) {
        summaryEl.textContent = "Running...";
        consoleEl.textContent = "Executing async code...";
      }
      try {
        const res = await cfg.run(editor.value, fakeConsole);
        if (runId !== activeRunId) return;
        if (res && res.output) logs.push(res.output);
        consoleEl.textContent = logs.length ? logs.join("\n") : "(Executed cleanly. Click 'Check Solution' to verify assertions.)";
        summaryEl.textContent = "Ran";
      } catch (err) {
        if (runId !== activeRunId) return;
        consoleEl.textContent = `Error: ${err && err.message ? err.message : String(err)}`;
        summaryEl.textContent = "Syntax / Runtime Error";
      }
    }

    async function checkSolution() {
      const runId = ++activeRunId;
      const logs = [];
      const fakeConsole = {
        log: (...args) => logs.push(args.map(formatValue).join(" ")),
        warn: (...args) => logs.push("[WARN] " + args.map(formatValue).join(" ")),
        error: (...args) => logs.push("[ERROR] " + args.map(formatValue).join(" "))
      };
      if (cfg.isAsync) {
        summaryEl.textContent = "Checking...";
        badgeEl.className = "tag tag-core";
        badgeEl.textContent = "Running...";
      }
      let results = [];
      try {
        results = await cfg.check(editor.value, fakeConsole);
      } catch (err) {
        results = [{ name: "Code compilation / execution", pass: false, detail: err && err.message ? err.message : String(err) }];
      }
      if (runId !== activeRunId) return;

      consoleEl.textContent = logs.length ? logs.join("\n") : "(No console logs emitted during test run.)";

      const passed = results.filter(r => r.pass).length;
      const total = results.length;
      summaryEl.textContent = `${passed} / ${total} checks passed`;

      testsEl.classList.add("visible");
      testsEl.innerHTML = results.map(r => `
        <div class="test-row">
          <span class="test-badge ${r.pass ? "test-pass" : "test-fail"}">${r.pass ? "PASS" : "FAIL"}</span>
          <div>
            <strong>${r.name}</strong>
            ${r.detail ? `<div style="color:var(--text-muted); font-size:0.82rem;">${r.detail}</div>` : ""}
          </div>
        </div>
      `).join("");

      if (passed === total && total > 0) {
        badgeEl.className = "tag tag-ready";
        badgeEl.textContent = "Passed";
        if (typeof onPassCallback === "function") onPassCallback(cfg.id);
      } else {
        badgeEl.className = "tag tag-interview";
        badgeEl.textContent = `${passed}/${total} passed`;
      }
    }

    mount.querySelector('[data-act="run"]').addEventListener("click", runOnly);
    mount.querySelector('[data-act="check"]').addEventListener("click", checkSolution);
    mount.querySelector('[data-act="reset"]').addEventListener("click", () => {
      activeRunId++;
      editor.value = cfg.starterCode;
      testsEl.classList.remove("visible");
      badgeEl.className = "tag tag-core";
      badgeEl.textContent = "Not checked";
      summaryEl.textContent = "Reset";
      consoleEl.textContent = "Editor reset to starter code.";
    });

    editor.addEventListener("keydown", (e) => {
      if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        checkSolution();
      }
    });

    hintBtn.addEventListener("click", () => {
      const hints = cfg.hints || [];
      if (!hints.length) return;
      if (hintIdx < hints.length) hintIdx += 1;
      hintsEl.classList.add("visible");
      hintsEl.innerHTML = `<strong>Hints (${hintIdx}/${hints.length}):</strong>` +
        hints.slice(0, hintIdx).map((h, i) => `<div class="hint-item">${i + 1}. ${h}</div>`).join("");
      hintBtn.textContent = hintIdx < hints.length ? `Hint (${hintIdx}/${hints.length})` : `All Hints Shown`;
    });

    answerBtn.addEventListener("click", () => {
      answerVisible = !answerVisible;
      answerEl.classList.toggle("visible", answerVisible);
      answerBtn.textContent = answerVisible ? "Hide Final Answer" : "Show Final Answer";
      if (answerVisible && !answerEl.dataset.rendered) {
        answerEl.dataset.rendered = "1";
        answerEl.innerHTML = `
          <div style="display:flex; justify-content:space-between; align-items:center; gap:0.5rem; flex-wrap:wrap; margin-bottom:0.45rem;">
            <strong>Final Answer &amp; Senior Explanation</strong>
            <button type="button" class="btn" data-act="loadAnswer" style="padding:0.25rem 0.6rem; font-size:0.78rem;">Load into Editor</button>
          </div>
          <p style="margin:0 0 0.55rem; color:var(--text);">${cfg.answerExplanation}</p>
          <pre style="margin:0;"><code></code></pre>
        `;
        answerEl.querySelector("code").textContent = cfg.solutionCode;
        answerEl.querySelector('[data-act="loadAnswer"]').addEventListener("click", () => {
          editor.value = cfg.solutionCode;
          checkSolution();
        });
      }
    });
  }

  /**
   * Renders diagnostic multiple-choice quiz cards with full per-option right/wrong explanations.
   */
  function initQuiz(mountId, questions, onAnswerCallback) {
    const mount = document.getElementById(mountId);
    if (!mount) return;

    const LETTERS = ["A", "B", "C", "D"];
    mount.innerHTML = "";

    questions.forEach((q, qIndex) => {
      const card = document.createElement("div");
      card.className = "quiz-card";
      card.innerHTML = `
        <div class="quiz-header">
          <span class="tag tag-interview">Question ${qIndex + 1} of ${questions.length}</span>
          <span class="tag tag-core" data-q-status>Unanswered</span>
        </div>
        <p class="quiz-q">${q.question}</p>
        ${q.code ? `<pre><code></code></pre>` : ""}
        <div class="quiz-options"></div>
        <div class="quiz-feedback" data-q-feedback></div>
      `;

      if (q.code) {
        card.querySelector("code").textContent = q.code;
      }

      const optsWrap = card.querySelector(".quiz-options");
      const statusEl = card.querySelector("[data-q-status]");
      const feedbackEl = card.querySelector("[data-q-feedback]");

      q.options.forEach((opt, optIdx) => {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "quiz-opt";
        btn.innerHTML = `<span class="quiz-opt-letter">${LETTERS[optIdx]}.</span><span>${opt.text}</span>`;
        btn.addEventListener("click", () => {
          const allBtns = optsWrap.querySelectorAll(".quiz-opt");
          allBtns.forEach((b, idx) => {
            b.disabled = true;
            b.classList.remove("is-selected");
            if (idx === q.correctIndex) b.classList.add("is-correct");
            else if (idx === optIdx && optIdx !== q.correctIndex) b.classList.add("is-wrong");
          });

          const isCorrect = optIdx === q.correctIndex;
          statusEl.className = `tag ${isCorrect ? "tag-ready" : "tag-interview"}`;
          statusEl.textContent = isCorrect ? "Correct" : "Review Explanation";

          feedbackEl.classList.add("visible");
          feedbackEl.innerHTML = `
            <div style="font-weight:700; margin-bottom:0.5rem; color:${isCorrect ? "var(--green)" : "var(--rose)"};">
              ${isCorrect ? "Correct!" : `Not quite — you picked ${LETTERS[optIdx]}, correct answer is ${LETTERS[q.correctIndex]}.`}
            </div>
            ${q.options.map((o, idx) => `
              <div class="quiz-why-row">
                <strong style="color:${idx === q.correctIndex ? "var(--green)" : "var(--rose)"};">
                  ${LETTERS[idx]} (${idx === q.correctIndex ? "Why this is RIGHT" : "Why this is WRONG"}):
                </strong>
                <span>${o.why}</span>
              </div>
            `).join("")}
            ${q.source ? `<div style="margin-top:0.55rem; font-size:0.8rem; color:var(--text-dim);">Reference: <a href="${q.source.url}" target="_blank" rel="noopener">${q.source.label}</a></div>` : ""}
          `;

          if (typeof onAnswerCallback === "function") {
            onAnswerCallback(q.id, isCorrect);
          }
        });
        optsWrap.appendChild(btn);
      });

      mount.appendChild(card);
    });
  }

  global.FE2027 = {
    loadProgress,
    saveProgress,
    initThemeToggle,
    initLiveExample,
    initCodeLab,
    initQuiz
  };
})(window);
