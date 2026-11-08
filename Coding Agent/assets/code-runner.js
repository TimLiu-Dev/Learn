/**
 * Interactive Python Code Runner & Test Checker
 * Supports Pyodide (WASM Python) with automatic graceful fallback simulation for instant offline execution.
 */

class InteractiveCodeRunner {
  constructor(options) {
    this.containerId = options.containerId;
    this.container = document.getElementById(this.containerId);
    if (!this.container) return;

    this.starterCode = options.starterCode || "";
    this.solutionCode = options.solutionCode || "";
    this.hints = options.hints || [];
    this.testSuite = options.testSuite || "";
    this.validatorFn = options.validatorFn || null; // fallback JS evaluator

    this.currentHintIndex = 0;
    this.pyodide = null;
    this.isPyodideLoading = false;

    this.initDOM();
    this.initPyodide();
  }

  async initPyodide() {
    // Attempt lazy loading of Pyodide from CDN in background
    if (window.loadPyodide && !this.pyodide && !this.isPyodideLoading) {
      try {
        this.isPyodideLoading = true;
        this.pyodide = await window.loadPyodide();
        this.updateStatus("Python 3.12 (WASM Engine Ready)", "ready");
      } catch (e) {
        console.warn("Pyodide CDN load skipped or network restricted. Using embedded verification engine.", e);
        this.updateStatus("Client Verification Engine Active", "ready");
      } finally {
        this.isPyodideLoading = false;
      }
    }
  }

  updateStatus(text, type) {
    const badge = this.container.querySelector(".runner-status");
    if (badge) {
      badge.textContent = text;
      badge.className = `runner-status tag ${type === 'ready' ? 'time' : 'benchmark'}`;
    }
  }

  initDOM() {
    this.container.innerHTML = `
      <div class="practice-topbar">
        <div class="practice-title">
          <span class="dot"></span>
          <span>Interactive Code Lab</span>
          <span class="runner-status tag time" style="margin-left: 10px;">Python 3.12 Ready</span>
        </div>
        <div class="btn-group">
          <button class="btn btn-subtle btn-reset" title="Reset code to original starter template">↺ Reset</button>
        </div>
      </div>
      <div class="editor-area">
        <textarea class="code-editor" spellcheck="false">${this.escapeHtml(this.starterCode)}</textarea>
      </div>
      <div class="practice-controls">
        <div class="btn-group">
          <button class="btn btn-primary btn-run">▶ Run Code & Test</button>
          <button class="btn btn-secondary btn-hint">💡 Hint (<span class="hint-count">0</span>/${this.hints.length})</button>
          <button class="btn btn-secondary btn-solution">👁 Reveal Solution</button>
        </div>
        <div class="test-indicator tag" style="display:none;"></div>
      </div>
      <div class="hints-drawer"></div>
      <div class="solution-drawer">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
          <strong style="color:var(--accent-emerald); font-size:13.5px;">✓ Reference Implementation</strong>
          <span style="font-size:12px; color:var(--text-dim);">Compare with your solution</span>
        </div>
        <pre><code class="solution-code">${this.escapeHtml(this.solutionCode)}</code></pre>
      </div>
      <div class="terminal-output">// Terminal Output & Assertion Checks will appear here after clicking 'Run Code & Test'</div>
    `;

    // Event listeners
    this.editor = this.container.querySelector(".code-editor");
    this.terminal = this.container.querySelector(".terminal-output");
    this.hintsDrawer = this.container.querySelector(".hints-drawer");
    this.solutionDrawer = this.container.querySelector(".solution-drawer");
    this.testIndicator = this.container.querySelector(".test-indicator");
    this.hintCountSpan = this.container.querySelector(".hint-count");

    // Tab key support in textarea
    this.editor.addEventListener("keydown", (e) => {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = this.editor.selectionStart;
        const end = this.editor.selectionEnd;
        this.editor.value = this.editor.value.substring(0, start) + "    " + this.editor.value.substring(end);
        this.editor.selectionStart = this.editor.selectionEnd = start + 4;
      }
    });

    this.container.querySelector(".btn-run").addEventListener("click", () => this.runCodeAndTests());
    this.container.querySelector(".btn-hint").addEventListener("click", () => this.showNextHint());
    this.container.querySelector(".btn-solution").addEventListener("click", () => this.toggleSolution());
    this.container.querySelector(".btn-reset").addEventListener("click", () => this.resetCode());
  }

  escapeHtml(str) {
    return str
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");
  }

  showNextHint() {
    if (this.currentHintIndex < this.hints.length) {
      this.currentHintIndex++;
      this.renderHints();
    } else {
      // Toggle visibility if all are shown
      this.hintsDrawer.classList.toggle("open");
    }
  }

  renderHints() {
    this.hintsDrawer.classList.add("open");
    this.hintCountSpan.textContent = this.currentHintIndex;
    
    let html = `<div style="margin-bottom:8px; font-weight:600; font-size:13px; color:var(--accent-amber);">💡 Progressive Hints</div>`;
    for (let i = 0; i < this.currentHintIndex; i++) {
      html += `
        <div class="hint-step">
          <strong>Hint ${i + 1}:</strong> ${this.hints[i]}
        </div>
      `;
    }
    this.hintsDrawer.innerHTML = html;
  }

  toggleSolution() {
    this.solutionDrawer.classList.toggle("open");
  }

  resetCode() {
    if (confirm("Reset editor to initial starter code? Any unsaved edits will be lost.")) {
      this.editor.value = this.starterCode;
      this.terminal.textContent = "// Editor reset to default starter template.";
      this.terminal.className = "terminal-output";
      this.testIndicator.style.display = "none";
    }
  }

  async runCodeAndTests() {
    const userCode = this.editor.value;
    this.terminal.textContent = "⏳ Running code and verifying assertions...\n";
    this.terminal.className = "terminal-output";
    this.testIndicator.style.display = "inline-flex";
    this.testIndicator.className = "test-indicator tag benchmark";
    this.testIndicator.textContent = "Running...";

    // 1. Try Pyodide if available
    if (this.pyodide) {
      try {
        let stdout = "";
        let stderr = "";

        this.pyodide.setStdout({ batched: (str) => { stdout += str + "\n"; } });
        this.pyodide.setStderr({ batched: (str) => { stderr += str + "\n"; } });

        const fullScript = `
import sys
${userCode}

${this.testSuite}
`;
        await this.pyodide.runPythonAsync(fullScript);

        this.terminal.className = "terminal-output success";
        this.terminal.textContent = (stdout ? stdout + "\n" : "") + "✅ ALL TEST ASSERTIONS PASSED!\nExecution finished successfully with exit code 0.";
        this.testIndicator.className = "test-indicator tag time";
        this.testIndicator.textContent = "✓ Passed (100%)";
        return;
      } catch (err) {
        this.terminal.className = "terminal-output error";
        this.terminal.textContent = `❌ TEST FAILED OR RUNTIME ERROR:\n\n${err.message}`;
        this.testIndicator.className = "test-indicator tag benchmark";
        this.testIndicator.textContent = "✗ Test Failed";
        return;
      }
    }

    // 2. Fallback Verification Engine (Client-side AST and semantic simulation)
    if (this.validatorFn) {
      try {
        const result = this.validatorFn(userCode);
        if (result.passed) {
          this.terminal.className = "terminal-output success";
          this.terminal.textContent = `${result.output}\n\n✅ [VERIFICATION HARNESS] All state machine transitions and tool dispatches verified!`;
          this.testIndicator.className = "test-indicator tag time";
          this.testIndicator.textContent = "✓ Passed (100%)";
        } else {
          this.terminal.className = "terminal-output error";
          this.terminal.textContent = `❌ VERIFICATION CHECK FAILED:\n\n${result.error}\n\nCheck Hint ${this.currentHintIndex + 1} for guidance.`;
          this.testIndicator.className = "test-indicator tag benchmark";
          this.testIndicator.textContent = "✗ Needs Attention";
        }
      } catch (e) {
        this.terminal.className = "terminal-output error";
        this.terminal.textContent = `❌ Execution Error: ${e.message}`;
      }
      return;
    }

    // Default basic output
    this.terminal.textContent = "Code executed. Ensure pyodide.js is loaded or provide a custom validatorFn.";
  }
}

window.InteractiveCodeRunner = InteractiveCodeRunner;
