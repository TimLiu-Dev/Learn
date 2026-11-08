/**
 * OOD Practice Runner Engine
 * Handles in-browser code execution, test assertions, console redirection,
 * progressive hints, and solution reveals.
 */

class PracticeRunner {
  constructor(config) {
    this.storageKey = config.id || 'ood-lesson-code';
    this.initialCode = config.initialCode.trim();
    this.solutionCode = config.solutionCode.trim();
    this.testSuite = config.testSuite || [];
    this.hints = config.hints || [];
    
    this.editorEl = document.getElementById(config.editorId || 'codeEditor');
    this.consoleEl = document.getElementById(config.consoleId || 'consoleOutput');
    this.testResultsEl = document.getElementById(config.testResultsId || 'testResults');
    this.summaryBadgeEl = document.getElementById(config.summaryBadgeId || 'testSummaryBadge');
    this.runBtn = document.getElementById(config.runBtnId || 'runCodeBtn');
    this.resetBtn = document.getElementById(config.resetBtnId || 'resetCodeBtn');
    this.solutionBtn = document.getElementById(config.solutionBtnId || 'solutionBtn');
    
    this.init();
  }

  init() {
    // Load saved code from localStorage or default to initialCode
    const savedCode = localStorage.getItem(this.storageKey);
    this.editorEl.value = savedCode !== null ? savedCode : this.initialCode;

    // Listen to code changes
    this.editorEl.addEventListener('input', () => {
      localStorage.setItem(this.storageKey, this.editorEl.value);
    });

    // Support Tab key in textarea
    this.editorEl.addEventListener('keydown', (e) => {
      if (e.key === 'Tab') {
        e.preventDefault();
        const start = this.editorEl.selectionStart;
        const end = this.editorEl.selectionEnd;
        this.editorEl.value = this.editorEl.value.substring(0, start) + '  ' + this.editorEl.value.substring(end);
        this.editorEl.selectionStart = this.editorEl.selectionEnd = start + 2;
        localStorage.setItem(this.storageKey, this.editorEl.value);
      }
    });

    // Run Button
    if (this.runBtn) {
      this.runBtn.addEventListener('click', () => this.runTests());
    }

    // Reset Button
    if (this.resetBtn) {
      this.resetBtn.addEventListener('click', () => {
        if (confirm('Reset your code back to the initial template?')) {
          this.editorEl.value = this.initialCode;
          localStorage.setItem(this.storageKey, this.initialCode);
          this.clearConsole();
          this.logToConsole('Code reset to initial state.', 'warn');
        }
      });
    }

    // Solution Reveal Button
    if (this.solutionBtn) {
      this.solutionBtn.addEventListener('click', () => {
        if (confirm('Load the official Senior Solution into your editor? (Your current code will be replaced)')) {
          this.editorEl.value = this.solutionCode;
          localStorage.setItem(this.storageKey, this.solutionCode);
          this.logToConsole('Official solution loaded. Run tests to verify!', 'success');
          this.runTests();
        }
      });
    }

    // Render initial test cases view
    this.renderInitialTestView();
    this.setupHints();
  }

  clearConsole() {
    if (this.consoleEl) {
      this.consoleEl.innerHTML = '';
    }
  }

  logToConsole(msg, type = 'log') {
    if (!this.consoleEl) return;
    const line = document.createElement('div');
    line.className = `console-line console-${type}`;
    const timestamp = new Date().toLocaleTimeString([], { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' });
    line.textContent = `[${timestamp}] ${typeof msg === 'object' ? JSON.stringify(msg, null, 2) : msg}`;
    this.consoleEl.appendChild(line);
    this.consoleEl.scrollTop = this.consoleEl.scrollHeight;
  }

  renderInitialTestView() {
    if (!this.testResultsEl) return;
    this.testResultsEl.innerHTML = '';
    this.testSuite.forEach((t, i) => {
      const item = document.createElement('div');
      item.className = 'test-case-item';
      item.id = `test-item-${i}`;
      item.innerHTML = `
        <div class="test-case-header">
          <span>Test ${i + 1}: ${t.name}</span>
          <span class="badge" style="background: rgba(255,255,255,0.08); color: var(--text-muted);">Ready</span>
        </div>
      `;
      this.testResultsEl.appendChild(item);
    });
    if (this.summaryBadgeEl) {
      this.summaryBadgeEl.className = 'test-summary-badge';
      this.summaryBadgeEl.style.background = 'rgba(255,255,255,0.08)';
      this.summaryBadgeEl.textContent = `0 / ${this.testSuite.length} Passed`;
    }
  }

  async runTests() {
    this.clearConsole();
    this.logToConsole('Starting test execution...', 'log');

    const userCode = this.editorEl.value;

    // Sandbox console output
    const originalConsole = {
      log: console.log,
      warn: console.warn,
      error: console.error
    };

    const capturedLogs = [];
    console.log = (...args) => {
      capturedLogs.push({ type: 'log', text: args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' ') });
      this.logToConsole(args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' '), 'log');
      originalConsole.log(...args);
    };
    console.warn = (...args) => {
      capturedLogs.push({ type: 'warn', text: args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' ') });
      this.logToConsole(args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' '), 'warn');
      originalConsole.warn(...args);
    };
    console.error = (...args) => {
      capturedLogs.push({ type: 'error', text: args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' ') });
      this.logToConsole(args.map(a => typeof a === 'object' ? JSON.stringify(a) : a).join(' '), 'error');
      originalConsole.error(...args);
    };

    let passCount = 0;
    const results = [];

    const startTime = performance.now();

    try {
      // Evaluate user code in a function context and expose declared classes/functions
      const sandboxFn = new Function(`
        ${userCode}
        return {
          ${this.getExportList(userCode)}
        };
      `);

      let exportedScope;
      try {
        exportedScope = sandboxFn();
      } catch (err) {
        throw new Error(`Syntax/Runtime error in your code: ${err.message}`);
      }

      // Run each test
      for (let i = 0; i < this.testSuite.length; i++) {
        const testDef = this.testSuite[i];
        let passed = false;
        let errorMessage = null;

        try {
          // Custom assertion helper
          const assert = {
            strictEqual: (actual, expected, msg) => {
              if (actual !== expected) {
                throw new Error(`${msg || 'Assertion failed'}: Expected ${JSON.stringify(expected)}, got ${JSON.stringify(actual)}`);
              }
            },
            deepEqual: (actual, expected, msg) => {
              if (JSON.stringify(actual) !== JSON.stringify(expected)) {
                throw new Error(`${msg || 'Deep equal failed'}: Expected ${JSON.stringify(expected)}, got ${JSON.stringify(actual)}`);
              }
            },
            isTrue: (value, msg) => {
              if (value !== true) throw new Error(msg || `Expected true, got ${value}`);
            },
            throws: (fn, expectedPattern, msg) => {
              let didThrow = false;
              let caughtErr;
              try {
                fn();
              } catch (e) {
                didThrow = true;
                caughtErr = e;
              }
              if (!didThrow) {
                throw new Error(msg || 'Expected function to throw an error, but it succeeded.');
              }
              if (expectedPattern && caughtErr) {
                const errStr = caughtErr.message || String(caughtErr);
                if (typeof expectedPattern === 'string' && !errStr.includes(expectedPattern)) {
                  throw new Error(`Expected error containing "${expectedPattern}", got "${errStr}"`);
                }
              }
            }
          };

          await testDef.fn(exportedScope, assert);
          passed = true;
          passCount++;
        } catch (testErr) {
          passed = false;
          errorMessage = testErr.message;
        }

        results.push({ name: testDef.name, passed, errorMessage });
      }

    } catch (topLevelErr) {
      this.logToConsole(`Execution Fatal Error: ${topLevelErr.message}`, 'error');
      results.push({ name: 'Compilation & Setup', passed: false, errorMessage: topLevelErr.message });
    } finally {
      // Restore standard console
      console.log = originalConsole.log;
      console.warn = originalConsole.warn;
      console.error = originalConsole.error;
    }

    const duration = (performance.now() - startTime).toFixed(1);

    // Update UI
    this.renderTestResults(results, passCount, duration);
  }

  getExportList(code) {
    // Automatically find declared classes, functions, and consts to export
    const exports = [];
    const classMatches = code.matchAll(/class\s+([A-Za-z0-9_$]+)/g);
    for (const match of classMatches) exports.push(match[1]);

    const funcMatches = code.matchAll(/function\s+([A-Za-z0-9_$]+)/g);
    for (const match of funcMatches) exports.push(match[1]);

    const constMatches = code.matchAll(/(?:const|let|var)\s+([A-Za-z0-9_$]+)\s*=/g);
    for (const match of constMatches) exports.push(match[1]);

    const unique = [...new Set(exports)];
    return unique.map(name => `"${name}": typeof ${name} !== 'undefined' ? ${name} : undefined`).join(',\n');
  }

  renderTestResults(results, passCount, duration) {
    if (!this.testResultsEl) return;
    this.testResultsEl.innerHTML = '';

    results.forEach((res, i) => {
      const item = document.createElement('div');
      item.className = 'test-case-item';
      item.style.borderColor = res.passed ? 'rgba(16, 185, 129, 0.4)' : 'rgba(244, 63, 94, 0.4)';
      item.innerHTML = `
        <div class="test-case-header">
          <span>${res.name}</span>
          <span class="badge ${res.passed ? 'status-live' : 'badge-midtech'}" style="${res.passed ? 'background: rgba(16, 185, 129, 0.2); color: var(--accent-emerald)' : 'background: rgba(244, 63, 94, 0.2); color: var(--accent-rose)'}">
            ${res.passed ? '✓ PASSED' : '✕ FAILED'}
          </span>
        </div>
        ${res.errorMessage ? `<div class="test-case-error">${res.errorMessage}</div>` : ''}
      `;
      this.testResultsEl.appendChild(item);
    });

    const total = this.testSuite.length;
    const allPass = passCount === total;

    if (this.summaryBadgeEl) {
      this.summaryBadgeEl.textContent = `${passCount} / ${total} Passed (${duration}ms)`;
      this.summaryBadgeEl.className = `test-summary-badge ${allPass ? 'all-pass' : 'has-fail'}`;
    }

    if (allPass) {
      this.logToConsole(`🎉 All ${total} tests passed in ${duration}ms! Clean, production-grade implementation.`, 'success');
    } else {
      this.logToConsole(`⚠️ ${total - passCount} test(s) failed. Check details in the test runner pane.`, 'warn');
    }
  }

  setupHints() {
    const hintContainer = document.getElementById('hintButtonsRow');
    const hintContent = document.getElementById('hintContentBox');
    if (!hintContainer || !hintContent || !this.hints.length) return;

    hintContainer.innerHTML = '';
    this.hints.forEach((hintText, index) => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'hint-btn';
      btn.textContent = `Hint ${index + 1}`;
      btn.addEventListener('click', () => {
        hintContainer.querySelectorAll('.hint-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        hintContent.innerHTML = `<strong>💡 Hint ${index + 1}:</strong> ${hintText}`;
        hintContent.classList.add('show');
      });
      hintContainer.appendChild(btn);
    });
  }
}

window.PracticeRunner = PracticeRunner;
