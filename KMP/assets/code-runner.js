/**
 * Universal In-Browser Interactive Code Practice Component for KMP Lessons
 * Supports real execution, stdout capture, automated assertion testing, progressive hints, and final answer review.
 */

class KmpCodeRunner {
  constructor(containerId, config) {
    this.container = document.getElementById(containerId);
    if (!this.container) return;
    this.config = Object.assign({
      title: 'Interactive Code Practice',
      fileName: 'Main.kt',
      instructions: '',
      starterCode: '',
      hints: [],
      finalSolution: '',
      solutionExplanation: '',
      playgroundUrl: null,
      execute: null,
      validate: null
    }, config);

    this.currentHintIndex = 0;
    this.render();
  }

  render() {
    const { title, instructions, starterCode, fileName, hints, finalSolution, solutionExplanation, playgroundUrl } = this.config;

    let html = `
      <div class="code-runner-card">
        <div class="runner-header">
          <div class="runner-title-group">
            <span class="runner-badge">🛠️ Interactive Code Practice</span>
            <h4 class="runner-title">${title}</h4>
          </div>
          <span class="file-name-pill">${fileName}</span>
        </div>

        <div class="runner-instructions">
          ${instructions}
        </div>

        <div class="editor-wrapper">
          <div class="editor-toolbar">
            <span class="editor-lang">Kotlin / KMP</span>
            <div class="editor-actions">
              ${playgroundUrl ? `<a href="${playgroundUrl}" target="_blank" class="editor-btn btn-playground" title="Open in Kotlin Playground">🌐 Open in Playground</a>` : ''}
              <button class="editor-btn btn-reset" title="Reset to Starter Code">↺ Reset Code</button>
            </div>
          </div>
          <textarea class="code-editor-textarea" spellcheck="false" autocomplete="off" autocapitalize="off">${this.escapeHtml(starterCode)}</textarea>
        </div>

        <div class="runner-controls">
          <div class="primary-controls">
            <button class="run-btn">▶ Run & Check Code</button>
            ${hints && hints.length > 0 ? `<button class="hint-btn">💡 Need a Hint? (${hints.length} available)</button>` : ''}
          </div>
          <button class="solution-toggle-btn">🔑 Reveal Final Answer</button>
        </div>

        <!-- Progressive Hint Drawer -->
        <div class="hint-drawer" style="display: none;">
          <div class="hint-content"></div>
        </div>

        <!-- Terminal Console Output -->
        <div class="terminal-console" style="display: none;">
          <div class="terminal-header">
            <span>Execution Console & Test Output</span>
            <span class="terminal-status-tag">Ready</span>
          </div>
          <div class="terminal-body"></div>
        </div>

        <!-- Final Solution Box -->
        <div class="solution-drawer" style="display: none;">
          <div class="solution-header">
            <h5>✅ Official Reference Solution</h5>
          </div>
          <pre class="solution-code"><code>${this.escapeHtml(finalSolution)}</code></pre>
          <div class="solution-notes">
            <h6>Architecture & Design Takeaways:</h6>
            <p>${solutionExplanation}</p>
          </div>
        </div>

      </div>
    `;

    this.container.innerHTML = html;
    this.bindEvents();
  }

  bindEvents() {
    const textarea = this.container.querySelector('.code-editor-textarea');
    const runBtn = this.container.querySelector('.run-btn');
    const resetBtn = this.container.querySelector('.btn-reset');
    const hintBtn = this.container.querySelector('.hint-btn');
    const hintDrawer = this.container.querySelector('.hint-drawer');
    const hintContent = this.container.querySelector('.hint-content');
    const solutionBtn = this.container.querySelector('.solution-toggle-btn');
    const solutionDrawer = this.container.querySelector('.solution-drawer');
    const terminal = this.container.querySelector('.terminal-console');
    const terminalBody = this.container.querySelector('.terminal-body');
    const terminalStatus = this.container.querySelector('.terminal-status-tag');

    // Tab key indentation support
    textarea.addEventListener('keydown', (e) => {
      if (e.key === 'Tab') {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.substring(0, start) + '    ' + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 4;
      }
    });

    // Reset code to starter template
    resetBtn.addEventListener('click', () => {
      if (confirm('Reset code to initial template?')) {
        textarea.value = this.config.starterCode;
        terminal.style.display = 'none';
        hintDrawer.style.display = 'none';
        hintContent.innerHTML = '';
        this.currentHintIndex = 0;
        if (hintBtn) {
          hintBtn.disabled = false;
          hintBtn.textContent = `💡 Need a Hint? (${this.config.hints.length} available)`;
        }
      }
    });

    // Progressive hints disclosure
    if (hintBtn) {
      hintBtn.addEventListener('click', () => {
        hintDrawer.style.display = 'block';
        const hints = this.config.hints;
        if (this.currentHintIndex < hints.length) {
          const currentHint = hints[this.currentHintIndex];
          hintContent.innerHTML += `
            <div class="hint-item">
              <strong>💡 Hint ${this.currentHintIndex + 1}:</strong> ${currentHint}
            </div>
          `;
          this.currentHintIndex++;
          if (this.currentHintIndex >= hints.length) {
            hintBtn.disabled = true;
            hintBtn.textContent = '💡 All hints revealed';
          } else {
            hintBtn.textContent = `💡 Next Hint (${this.currentHintIndex}/${hints.length})`;
          }
        }
      });
    }

    // Toggle final solution
    solutionBtn.addEventListener('click', () => {
      const isVisible = solutionDrawer.style.display === 'block';
      solutionDrawer.style.display = isVisible ? 'none' : 'block';
      solutionBtn.textContent = isVisible ? '🔑 Reveal Final Answer' : '🙈 Hide Final Answer';
    });

    // Run Code & Tests
    runBtn.addEventListener('click', () => {
      const userCode = textarea.value;
      terminal.style.display = 'block';
      terminalBody.innerHTML = '<div class="terminal-line info">Executing code and evaluating assertions...</div>';

      setTimeout(() => {
        this.executeAndValidate(userCode, terminalBody, terminalStatus);
      }, 150);
    });
  }

  executeAndValidate(userCode, terminalBody, terminalStatus) {
    terminalBody.innerHTML = '';
    const stdoutLogs = [];

    // Capture stdout
    const customPrintln = (...args) => {
      stdoutLogs.push(args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' '));
    };

    let executionError = null;
    let testResults = [];

    // Execute logic if provided
    if (typeof this.config.execute === 'function') {
      try {
        const execOutput = this.config.execute(userCode, customPrintln);
        if (execOutput && Array.isArray(execOutput.tests)) {
          testResults = execOutput.tests;
        }
      } catch (err) {
        executionError = err.message || String(err);
      }
    } else if (typeof this.config.validate === 'function') {
      try {
        testResults = this.config.validate(userCode, customPrintln);
      } catch (err) {
        executionError = err.message || String(err);
      }
    }

    // 1. Render Program Output (stdout)
    if (stdoutLogs.length > 0) {
      const stdoutBlock = document.createElement('div');
      stdoutBlock.className = 'stdout-block';
      stdoutBlock.innerHTML = `
        <div class="stdout-title">Program Output (stdout):</div>
        <pre class="stdout-content">${this.escapeHtml(stdoutLogs.join('\n'))}</pre>
      `;
      terminalBody.appendChild(stdoutBlock);
    }

    // 2. Render Execution Error if any
    if (executionError) {
      terminalStatus.className = 'terminal-status-tag status-fail';
      terminalStatus.textContent = 'EXECUTION ERROR';
      const errBox = document.createElement('div');
      errBox.className = 'terminal-summary error';
      errBox.innerHTML = `<strong>Runtime / Compilation Error:</strong> ${this.escapeHtml(executionError)}`;
      terminalBody.appendChild(errBox);
      return;
    }

    // 3. Render Assertion Results
    let allPassed = testResults && testResults.length > 0;
    const testList = document.createElement('div');
    testList.className = 'test-suite-list';
    testList.innerHTML = '<div class="test-suite-title">Automated Verification Checks:</div>';

    (testResults || []).forEach((test, idx) => {
      const line = document.createElement('div');
      line.className = `test-result-line ${test.passed ? 'pass' : 'fail'}`;
      line.innerHTML = `
        <div>
          <span class="test-icon">${test.passed ? '✔ PASS' : '✖ FAIL'}</span>
          <span class="test-name">Check ${idx + 1}: ${test.name}</span>
        </div>
      `;
      if (!test.passed) {
        allPassed = false;
        const diag = document.createElement('div');
        diag.className = 'test-diagnostic';
        diag.innerHTML = `<strong>Diagnostic:</strong> ${test.message}`;
        line.appendChild(diag);
      }
      testList.appendChild(line);
    });

    terminalBody.appendChild(testList);

    // 4. Summary Status
    if (allPassed) {
      terminalStatus.className = 'terminal-status-tag status-pass';
      terminalStatus.textContent = 'ALL CHECKS PASSED';
      const summary = document.createElement('div');
      summary.className = 'terminal-summary success';
      summary.innerHTML = '🎉 <strong>Success!</strong> All requirements satisfied and verified.';
      terminalBody.appendChild(summary);
    } else {
      terminalStatus.className = 'terminal-status-tag status-fail';
      terminalStatus.textContent = 'ASSERTION FAILED';
      const summary = document.createElement('div');
      summary.className = 'terminal-summary error';
      summary.innerHTML = '⚠️ Some checks failed. Check the diagnostics above or click <strong>Need a Hint?</strong>.';
      terminalBody.appendChild(summary);
    }
  }

  escapeHtml(str) {
    if (!str) return '';
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }
}
