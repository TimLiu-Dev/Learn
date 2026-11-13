/**
 * Shared In-Browser Code Practice & Test Runner Component
 * Executes JavaScript/Distributed algorithms client-side with zero external dependencies.
 * Includes automated test execution, console output capture, progressive hints, and solution reveals.
 */

class CodeRunner {
  constructor(containerId, options) {
    this.container = document.getElementById(containerId);
    this.id = options.id || containerId;
    this.initialCode = (options.starterCode || '').trim();
    this.solutionCode = (options.solutionCode || '').trim();
    this.hints = options.hints || [];
    this.testSuite = options.tests || [];
    this.successMessage = options.successMessage || 'Superb work! Your implementation accurately satisfies the concurrency, rolling window, and accuracy invariants.';
    this.storageKey = `coderunner_${this.id}`;

    if (this.container) {
      this.init();
    }
  }

  init() {
    this.render();
    this.attachEvents();
    this.restoreCode();
  }

  render() {
    this.container.innerHTML = `
      <div class="code-practice-widget">
        <div class="widget-header">
          <div class="widget-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="16 18 22 12 16 6"></polyline>
              <polyline points="8 6 2 12 8 18"></polyline>
            </svg>
            <span>Interactive Code Sandbox & Verifier</span>
          </div>
          <div class="widget-actions">
            <button class="btn btn-secondary reset-btn" title="Reset starter code">↺ Reset</button>
            <button class="btn btn-primary run-btn">▶ Run Tests & Verify</button>
          </div>
        </div>

        <div class="widget-tabs">
          <button class="tab-btn active" data-tab="editor">Code Editor</button>
          <button class="tab-btn" data-tab="tests">Test Results (<span class="test-badge-count">0/${this.testSuite.length} Passed</span>)</button>
          <button class="tab-btn" data-tab="console">Console (<span class="log-count">0</span>)</button>
          <button class="tab-btn" data-tab="hints">Hints (${this.hints.length})</button>
          <button class="tab-btn" data-tab="solution">Final Solution</button>
        </div>

        <div class="widget-body">
          <!-- Editor Tab -->
          <div class="tab-content active tab-editor-content">
            <div class="code-editor-area">
              <textarea class="code-textarea" spellcheck="false" placeholder="Write your algorithm implementation here..."></textarea>
            </div>
            <div style="padding: 0.6rem 1rem; background-color: var(--bg-subtle); display: flex; justify-content: space-between; font-size: 0.8rem; color: var(--text-muted);">
              <span>Shortcut: <strong>Ctrl/Cmd + Enter</strong> to run tests. <strong>Tab</strong> inserts 2 spaces.</span>
              <span class="save-status">Auto-saved locally</span>
            </div>
          </div>

          <!-- Tests Tab -->
          <div class="tab-content tab-tests-content">
            <div class="test-summary" style="margin-bottom: 1rem; font-weight: 600;">Click "Run Tests" to evaluate your implementation against the test harness.</div>
            <div class="test-list-container">
              ${this.testSuite.map((t, idx) => `
                <div class="test-suite-card" id="${this.id}_test_${idx}" style="border: 1px solid var(--border-subtle); background: var(--bg-subtle);">
                  <span><strong>Test ${idx + 1}:</strong> ${t.name}</span>
                  <span class="test-status" style="font-weight: 700; color: var(--text-muted);">Pending</span>
                </div>
              `).join('')}
            </div>
          </div>

          <!-- Console Tab -->
          <div class="tab-content tab-console-content">
            <pre class="console-output">Logs will appear here when you run your code...</pre>
          </div>

          <!-- Hints Tab -->
          <div class="tab-content tab-hints-content">
            <p style="font-size: 0.9rem; margin-bottom: 1rem;">Stuck? Reveal progressive hints one level at a time before looking at the solution.</p>
            ${this.hints.map((h, idx) => `
              <div class="hint-box">
                <div class="hint-header">
                  <span>💡 Hint ${idx + 1}: ${h.title}</span>
                  <span style="font-size: 0.8rem; color: var(--accent-primary);">Click to reveal ▼</span>
                </div>
                <div class="hint-body">
                  <p style="margin: 0.5rem 0 0 0;">${h.content}</p>
                </div>
              </div>
            `).join('')}
          </div>

          <!-- Solution Tab -->
          <div class="tab-content tab-solution-content">
            <div class="callout callout-warning" style="margin-top: 0;">
              <div class="callout-title">⚠️ Benchmark Solution Warning</div>
              <p>Try implementing and passing tests yourself first to build storage strength. View the benchmark solution when finished or after exhausting hints.</p>
            </div>
            <div style="display: flex; justify-content: flex-end; margin-bottom: 0.5rem;">
              <button class="btn btn-secondary copy-solution-btn">Copy Solution to Editor</button>
            </div>
            <pre><code class="solution-code-block">${this.escapeHtml(this.solutionCode)}</code></pre>
          </div>
        </div>
      </div>
    `;
  }

  escapeHtml(str) {
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  attachEvents() {
    const textarea = this.container.querySelector('.code-textarea');
    const runBtn = this.container.querySelector('.run-btn');
    const resetBtn = this.container.querySelector('.reset-btn');
    const copySolutionBtn = this.container.querySelector('.copy-solution-btn');
    const tabBtns = this.container.querySelectorAll('.tab-btn');
    const tabContents = this.container.querySelectorAll('.tab-content');
    const hintHeaders = this.container.querySelectorAll('.hint-header');

    // Tab switching
    tabBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        tabBtns.forEach(b => b.classList.remove('active'));
        tabContents.forEach(c => c.classList.remove('active'));
        btn.classList.add('active');

        const tabTarget = btn.getAttribute('data-tab');
        const activeContent = this.container.querySelector(`.tab-${tabTarget}-content`);
        if (activeContent) activeContent.classList.add('active');
      });
    });

    // Expand hints
    hintHeaders.forEach(hh => {
      hh.addEventListener('click', () => {
        const body = hh.nextElementSibling;
        body.classList.toggle('open');
      });
    });

    // Tab indentation inside textarea
    textarea.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        this.runTests();
        return;
      }

      if (e.key === 'Tab') {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.substring(0, start) + '  ' + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 2;
        this.saveCode();
      }
    });

    textarea.addEventListener('input', () => {
      this.saveCode();
    });

    runBtn.addEventListener('click', () => {
      this.runTests();
    });

    resetBtn.addEventListener('click', () => {
      if (confirm('Reset your code to the starting template?')) {
        textarea.value = this.initialCode;
        this.saveCode();
      }
    });

    copySolutionBtn.addEventListener('click', () => {
      if (confirm('Replace your current code with the benchmark solution?')) {
        textarea.value = this.solutionCode;
        this.saveCode();
        // Switch to editor tab
        this.container.querySelector('[data-tab="editor"]').click();
      }
    });
  }

  saveCode() {
    const textarea = this.container.querySelector('.code-textarea');
    localStorage.setItem(this.storageKey, textarea.value);
  }

  restoreCode() {
    const textarea = this.container.querySelector('.code-textarea');
    const saved = localStorage.getItem(this.storageKey);
    textarea.value = saved !== null ? saved : this.initialCode;
  }

  runTests() {
    const textarea = this.container.querySelector('.code-textarea');
    const userCode = textarea.value;
    const consoleBox = this.container.querySelector('.console-output');
    const testCountBadge = this.container.querySelector('.test-badge-count');
    const logCountBadge = this.container.querySelector('.log-count');

    // Switch to tests tab
    this.container.querySelector('[data-tab="tests"]').click();

    // Hijack logs
    const logs = [];
    const originalLog = console.log;
    const originalError = console.error;
    const originalWarn = console.warn;

    console.log = (...args) => {
      logs.push('[LOG] ' + args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' '));
    };
    console.error = (...args) => {
      logs.push('[ERROR] ' + args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' '));
    };
    console.warn = (...args) => {
      logs.push('[WARN] ' + args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' '));
    };

    let passedCount = 0;
    const startTime = performance.now();

    try {
      // Execute user code in a scope
      const executionFn = new Function(userCode + `\n; return {
        ${this.testSuite.map(t => t.exportSymbol).filter(Boolean).filter((v, i, a) => a.indexOf(v) === i).join(', ')}
      };`);

      const exports = executionFn();

      // Run each test
      this.testSuite.forEach((test, idx) => {
        const card = document.getElementById(`${this.id}_test_${idx}`);
        const statusSpan = card.querySelector('.test-status');

        try {
          test.testFn(exports);
          card.className = 'test-suite-card test-pass';
          statusSpan.innerHTML = '✓ Passed';
          passedCount++;
        } catch (err) {
          card.className = 'test-suite-card test-fail';
          statusSpan.innerHTML = `✗ Failed: ${err.message}`;
          logs.push(`[TEST FAILURE] Test ${idx + 1} (${test.name}): ${err.message}`);
        }
      });

    } catch (syntaxErr) {
      this.testSuite.forEach((test, idx) => {
        const card = document.getElementById(`${this.id}_test_${idx}`);
        card.className = 'test-suite-card test-fail';
        card.querySelector('.test-status').innerHTML = '✗ Syntax / Runtime Error';
      });
      logs.push('[RUNTIME FATAL] ' + syntaxErr.stack || syntaxErr.message);
    } finally {
      // Restore console
      console.log = originalLog;
      console.error = originalError;
      console.warn = originalWarn;
    }

    const duration = (performance.now() - startTime).toFixed(1);
    testCountBadge.innerText = `${passedCount}/${this.testSuite.length} Passed (${duration}ms)`;
    logCountBadge.innerText = `${logs.length}`;

    if (logs.length > 0) {
      consoleBox.innerText = logs.join('\n');
    } else {
      consoleBox.innerText = 'Execution completed with no console logs.';
    }

    const summaryEl = this.container.querySelector('.test-summary');
    if (passedCount === this.testSuite.length) {
      summaryEl.innerHTML = `
        <div class="callout callout-tip" style="margin: 0;">
          <div class="callout-title">🎉 All ${passedCount} Tests Passing!</div>
          <p>${this.successMessage}</p>
        </div>
      `;
    } else {
      summaryEl.innerHTML = `
        <div class="callout callout-danger" style="margin: 0;">
          <div class="callout-title">⚠️ ${this.testSuite.length - passedCount} Tests Failing</div>
          <p>Check the failed assertions below or review the "Hints" tab to resolve edge cases.</p>
        </div>
      `;
    }
  }
}

window.CodeRunner = CodeRunner;
