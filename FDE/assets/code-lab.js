/* ==========================================================================
   Reusable In-Browser Runnable Code Practice Engine
   Supports instant sandboxed JavaScript execution, deterministic assertions,
   captured stdout, progressive hints, and solution reveal + loader.
   ========================================================================== */
(function () {
  const registry = {};

  function formatValue(val) {
    if (typeof val === "string") return val;
    if (val === undefined) return "undefined";
    try {
      return JSON.stringify(val, null, 2);
    } catch (_) {
      return String(val);
    }
  }

  function deepEqual(a, b) {
    if (Object.is(a, b)) return true;
    if (a === null || b === null || typeof a !== "object" || typeof b !== "object") {
      return false;
    }
    if (Array.isArray(a) !== Array.isArray(b)) return false;
    const keysA = Object.keys(a);
    const keysB = Object.keys(b);
    if (keysA.length !== keysB.length) return false;
    for (const k of keysA) {
      if (!Object.prototype.hasOwnProperty.call(b, k)) return false;
      if (!deepEqual(a[k], b[k])) return false;
    }
    return true;
  }

  const assertHelpers = {
    equal(actual, expected, msg) {
      if (!Object.is(actual, expected)) {
        throw new Error(
          `${msg ? msg + " — " : ""}Expected ${formatValue(expected)}, got ${formatValue(actual)}`
        );
      }
    },
    deepEqual(actual, expected, msg) {
      if (!deepEqual(actual, expected)) {
        throw new Error(
          `${msg ? msg + " — " : ""}Expected:\n${formatValue(expected)}\nGot:\n${formatValue(actual)}`
        );
      }
    },
    ok(cond, msg) {
      if (!cond) {
        throw new Error(msg || "Expected condition to be truthy");
      }
    }
  };

  function runJavaScriptLab(userCode, exportNames, tests) {
    const logs = [];
    const mockConsole = {
      log: (...args) => logs.push(args.map(formatValue).join(" ")),
      warn: (...args) => logs.push("[WARN] " + args.map(formatValue).join(" ")),
      error: (...args) => logs.push("[ERR] " + args.map(formatValue).join(" "))
    };

    const epilogue = (exportNames || [])
      .map(
        (name) =>
          `if (typeof ${name} !== "undefined") { __exports__[${JSON.stringify(name)}] = ${name}; }`
      )
      .join("\n");

    let exported = {};
    let compileOrTopError = null;

    try {
      const moduleObj = { exports: {} };
      const runner = new Function(
        "console",
        "module",
        "exports",
        "__exports__",
        `"use strict";\n${userCode}\n${epilogue}\nreturn Object.assign({}, module.exports, __exports__);`
      );
      exported = runner(mockConsole, moduleObj, moduleObj.exports, {});
    } catch (err) {
      compileOrTopError = err;
    }

    const results = tests.map((t) => {
      if (compileOrTopError) {
        return {
          name: t.name,
          passed: false,
          error: `${compileOrTopError.name}: ${compileOrTopError.message}`
        };
      }
      try {
        t.run(exported, assertHelpers);
        return { name: t.name, passed: true, error: null };
      } catch (err) {
        return {
          name: t.name,
          passed: false,
          error: err && err.message ? err.message : String(err)
        };
      }
    });

    return { logs, results };
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function enableEditorKeybindings(textarea, onRun) {
    textarea.addEventListener("keydown", (e) => {
      if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        onRun();
        return;
      }
      if (e.key === "Tab") {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const val = textarea.value;
        if (!e.shiftKey) {
          textarea.value = val.slice(0, start) + "  " + val.slice(end);
          textarea.selectionStart = textarea.selectionEnd = start + 2;
        } else {
          const lineStart = val.lastIndexOf("\n", start - 1) + 1;
          if (val.slice(lineStart, lineStart + 2) === "  ") {
            textarea.value = val.slice(0, lineStart) + val.slice(lineStart + 2);
            textarea.selectionStart = textarea.selectionEnd = Math.max(lineStart, start - 2);
          }
        }
        textarea.dispatchEvent(new Event("input"));
      }
    });
  }

  function mountLab(container, labId, config) {
    const saved = window.FDECourseProgress?.getLabState(config.lessonId, labId);
    const initialCode = saved?.code ? saved.code : config.starterCode;
    let hintIndex = 0;
    let solutionVisible = false;

    container.innerHTML = `
      <div class="lab-header">
        <div class="lab-title-group">
          <span class="lang-pill">${escapeHtml(config.language || "JavaScript")}</span>
          <h3 class="lab-title">${escapeHtml(config.title)}</h3>
        </div>
        <span class="lab-status-pill ${saved?.passed ? "pass" : ""}" data-status-pill>
          ${saved?.passed ? `Passed (${config.tests.length}/${config.tests.length})` : `0 / ${config.tests.length} Checks`}
        </span>
      </div>
      <div class="lab-brief">${config.briefHtml}</div>
      <div class="lab-workbench">
        <div class="lab-editor-pane">
          <div class="lab-editor-bar">
            <span>${escapeHtml(config.filename || "solution.js")}</span>
            <span>Tab indents · Cmd/Ctrl+Enter runs</span>
          </div>
          <textarea class="lab-textarea" spellcheck="false" aria-label="${escapeHtml(config.title)} code editor"></textarea>
          <div class="lab-controls">
            <button type="button" class="btn btn-primary" data-run>Run &amp; Check</button>
            <button type="button" class="btn btn-secondary" data-hint>Hint (0/${config.hints.length})</button>
            <button type="button" class="btn btn-secondary" data-solution>Show Final Answer</button>
            <button type="button" class="btn btn-secondary" data-reset>Reset</button>
          </div>
        </div>
        <div class="lab-results-pane">
          <div class="lab-results-header">
            <span>Automated Verification</span>
            <span data-runtime-ms>Ready</span>
          </div>
          <div class="lab-checks-list" data-checks></div>
          <div class="lab-console">
            <div class="lab-console-title">Console Output (console.log)</div>
            <pre data-console>(Run code to see output)</pre>
          </div>
        </div>
      </div>
      <div class="lab-assist-area" data-assist></div>
    `;

    const textarea = container.querySelector(".lab-textarea");
    const statusPill = container.querySelector("[data-status-pill]");
    const runtimeEl = container.querySelector("[data-runtime-ms]");
    const checksEl = container.querySelector("[data-checks]");
    const consoleEl = container.querySelector("[data-console]");
    const assistEl = container.querySelector("[data-assist]");
    const hintBtn = container.querySelector("[data-hint]");
    const solutionBtn = container.querySelector("[data-solution]");
    const runBtn = container.querySelector("[data-run]");
    const resetBtn = container.querySelector("[data-reset]");

    textarea.value = initialCode;

    function renderPendingChecks() {
      checksEl.innerHTML = config.tests
        .map(
          (t, i) => `
          <div class="check-row">
            <div class="check-row-head">
              <span>${i + 1}. ${escapeHtml(t.name)}</span>
              <span class="check-tag pending">Pending</span>
            </div>
          </div>`
        )
        .join("");
    }

    function renderRunResults(results, logs, elapsedMs) {
      const passedCount = results.filter((r) => r.passed).length;
      const allPassed = passedCount === results.length;

      statusPill.textContent = allPassed
        ? `Passed (${passedCount}/${results.length})`
        : `${passedCount} / ${results.length} Passing`;
      statusPill.className = `lab-status-pill ${allPassed ? "pass" : "fail"}`;
      runtimeEl.textContent = `${elapsedMs.toFixed(1)} ms`;

      checksEl.innerHTML = results
        .map(
          (r, i) => `
          <div class="check-row ${r.passed ? "pass" : "fail"}">
            <div class="check-row-head">
              <span>${i + 1}. ${escapeHtml(r.name)}</span>
              <span class="check-tag ${r.passed ? "pass" : "fail"}">${r.passed ? "PASS" : "FAIL"}</span>
            </div>
            ${r.error ? `<div class="check-detail">${escapeHtml(r.error)}</div>` : ""}
          </div>`
        )
        .join("");

      consoleEl.textContent = logs.length ? logs.join("\n") : "(no console output)";
      window.FDECourseProgress?.markLabPassed(config.lessonId, labId, allPassed, textarea.value);
    }

    function execute() {
      const t0 = performance.now();
      const { logs, results } = runJavaScriptLab(
        textarea.value,
        config.exportNames,
        config.tests
      );
      const elapsed = performance.now() - t0;
      renderRunResults(results, logs, elapsed);
    }

    function renderAssist() {
      let html = "";
      for (let i = 0; i < hintIndex; i++) {
        html += `<div class="hint-box"><strong>Hint ${i + 1}:</strong> ${escapeHtml(config.hints[i])}</div>`;
      }
      if (solutionVisible) {
        html += `
          <div class="solution-box">
            <div class="solution-box-head">
              <span>Reference Final Answer (${escapeHtml(config.filename || "solution.js")})</span>
              <button type="button" class="btn btn-ghost" data-load-solution>Load into Editor &amp; Run</button>
            </div>
            <pre><code>${escapeHtml(config.solutionCode)}</code></pre>
            <div class="solution-why">${config.solutionExplanationHtml}</div>
          </div>`;
      }
      assistEl.innerHTML = html;

      const loadBtn = assistEl.querySelector("[data-load-solution]");
      if (loadBtn) {
        loadBtn.addEventListener("click", () => {
          textarea.value = config.solutionCode;
          execute();
        });
      }
    }

    renderPendingChecks();
    if (saved?.passed) {
      execute();
    }

    textarea.addEventListener("input", () => {
      window.FDECourseProgress?.saveLabDraft(config.lessonId, labId, textarea.value);
    });

    enableEditorKeybindings(textarea, execute);
    runBtn.addEventListener("click", execute);

    hintBtn.addEventListener("click", () => {
      if (hintIndex < config.hints.length) {
        hintIndex += 1;
        hintBtn.textContent = `Hint (${hintIndex}/${config.hints.length})`;
        renderAssist();
      }
    });

    solutionBtn.addEventListener("click", () => {
      solutionVisible = !solutionVisible;
      solutionBtn.textContent = solutionVisible ? "Hide Final Answer" : "Show Final Answer";
      renderAssist();
    });

    resetBtn.addEventListener("click", () => {
      textarea.value = config.starterCode;
      hintIndex = 0;
      solutionVisible = false;
      hintBtn.textContent = `Hint (0/${config.hints.length})`;
      solutionBtn.textContent = "Show Final Answer";
      statusPill.textContent = `0 / ${config.tests.length} Checks`;
      statusPill.className = "lab-status-pill";
      runtimeEl.textContent = "Reset";
      consoleEl.textContent = "(Run code to see output)";
      renderPendingChecks();
      renderAssist();
      window.FDECourseProgress?.markLabPassed(config.lessonId, labId, false, config.starterCode);
    });
  }

  window.FDECodeLabs = {
    register(labId, config) {
      registry[labId] = config;
    },
    mountAll() {
      document.querySelectorAll("[data-lab-id]").forEach((el) => {
        const id = el.getAttribute("data-lab-id");
        if (registry[id]) {
          mountLab(el, id, registry[id]);
        }
      });
    }
  };

  document.addEventListener("DOMContentLoaded", () => {
    window.FDECodeLabs.mountAll();
  });
})();
