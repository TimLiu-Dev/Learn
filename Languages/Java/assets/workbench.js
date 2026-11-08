// Antigravity Senior Java Interactive Workbench Component
// Supports both self-rendering into <div id="ex-1" class="workbench-container"></div>
// and progressive enhancement of pre-existing workbench markup.
// Features:
// - Scaffolded / Bug-Fix initialCode editor with Tab indentation & Cmd/Ctrl+Enter shortcut
// - Multi-check automated verification suite + realistic simulated JVM/test console output
// - Progressive step-by-step Hints button
// - Toggleable "Final Answer" drawer with syntax block, senior trade-off notes, Copy & "Load into Editor" buttons

function escapeWbHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

class JavaExercise {
  constructor(config) {
    this.id = config.id;
    this.filename = config.filename || 'Solution.java';
    this.badge = config.badge || 'Hands-On Practice';
    this.instructions = config.instructions || '';
    this.initialCode = config.initialCode || '';
    this.solution = config.solution || '';
    this.solutionExplanation = config.solutionExplanation || '';
    this.tests = config.tests || [];
    this.hints = Array.isArray(config.hints) ? config.hints : (config.hints ? [config.hints] : []);
    this.revealedHints = 0;
    this.simulatedOutput = config.simulatedOutput || function () {
      return 'Program compiled and executed with exit code 0.';
    };
    this.init();
  }

  init() {
    this.container = document.getElementById(this.id);
    if (!this.container) return;

    // Self-render if container has no .workbench-editor yet
    if (!this.container.querySelector('.workbench-editor')) {
      this.renderScaffold();
    } else {
      this.upgradeExistingMarkup();
    }

    this.textarea = this.container.querySelector('.workbench-editor');
    this.consoleEl = this.container.querySelector('.workbench-console');
    this.resultsEl = this.container.querySelector('.workbench-results');
    this.checkBtn = this.container.querySelector('.btn-check-solution');
    this.resetBtn = this.container.querySelector('.btn-reset-code');
    this.hintBtn = this.container.querySelector('.btn-toggle-hint');
    this.hintBox = this.container.querySelector('.workbench-hints');
    this.solutionBtn = this.container.querySelector('.btn-show-solution');
    this.solutionBox = this.container.querySelector('.workbench-solution');

    if (this.textarea) {
      if (this.initialCode) {
        this.textarea.value = this.initialCode;
      }
      this.enableEditorShortcuts(this.textarea);
    }

    if (this.checkBtn) {
      this.checkBtn.addEventListener('click', () => this.runCheck());
    }

    if (this.resetBtn) {
      this.resetBtn.addEventListener('click', () => {
        if (this.textarea) this.textarea.value = this.initialCode;
        this.revealedHints = 0;
        this.updateHintButtonLabel();
        this.clearFeedback();
      });
    }

    if (this.hintBtn && this.hintBox) {
      this.updateHintButtonLabel();
      this.hintBtn.addEventListener('click', () => this.handleHintClick());
    }

    if (this.solutionBtn && this.solutionBox) {
      this.solutionBtn.addEventListener('click', () => this.toggleSolution());
      const loadBtn = this.solutionBox.querySelector('.btn-load-solution');
      const copyBtn = this.solutionBox.querySelector('.btn-copy-solution');
      if (loadBtn) {
        loadBtn.addEventListener('click', () => {
          if (this.textarea && this.solution) {
            this.textarea.value = this.solution;
            this.runCheck();
          }
        });
      }
      if (copyBtn) {
        copyBtn.addEventListener('click', () => {
          if (navigator.clipboard && this.solution) {
            navigator.clipboard.writeText(this.solution);
            copyBtn.textContent = '✓ Copied!';
            setTimeout(() => { copyBtn.textContent = '📋 Copy Code'; }, 1500);
          }
        });
      }
    }
  }

  renderScaffold() {
    const instructionsBlock = this.instructions
      ? `<div class="workbench-instructions">${this.instructions}</div>`
      : '';
    const hintBtnHtml = this.hints.length > 0
      ? `<button type="button" class="btn-secondary btn-toggle-hint">&#128161; Show Hint (1/${this.hints.length})</button>`
      : '';
    const solBtnHtml = this.solution
      ? `<button type="button" class="btn-secondary btn-show-solution">&#10003; Show Final Answer</button>`
      : '';

    this.container.innerHTML = `
      <div class="workbench-header">
        <div class="workbench-filename"><span>&#128196;</span> ${escapeWbHtml(this.filename)}</div>
        <div><span class="badge badge-current">${escapeWbHtml(this.badge)}</span></div>
      </div>
      <div class="workbench-body">
        ${instructionsBlock}
        <textarea class="workbench-editor" spellcheck="false" aria-label="${escapeWbHtml(this.filename)} code editor"></textarea>
        <div class="workbench-actions">
          <button type="button" class="btn-primary btn-check-solution">&#9654; Run &amp; Check Code</button>
          ${hintBtnHtml}
          ${solBtnHtml}
          <button type="button" class="btn-secondary btn-reset-code">&#8635; Reset</button>
        </div>
        <div class="workbench-hints"></div>
        ${this.buildSolutionBoxHtml()}
        <div class="workbench-results"></div>
        <div class="workbench-console"></div>
      </div>
    `;
  }

  upgradeExistingMarkup() {
    const actions = this.container.querySelector('.workbench-actions');
    const body = this.container.querySelector('.workbench-body');
    if (this.solution && actions && !this.container.querySelector('.btn-show-solution')) {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'btn-secondary btn-show-solution';
      btn.innerHTML = '&#10003; Show Final Answer';
      actions.appendChild(btn);
    }
    if (this.solution && body && !this.container.querySelector('.workbench-solution')) {
      const wrapper = document.createElement('div');
      wrapper.innerHTML = this.buildSolutionBoxHtml();
      const resultsEl = body.querySelector('.workbench-results');
      if (resultsEl) {
        body.insertBefore(wrapper.firstElementChild, resultsEl);
      } else {
        body.appendChild(wrapper.firstElementChild);
      }
    }
  }

  buildSolutionBoxHtml() {
    if (!this.solution) return '';
    const expHtml = this.solutionExplanation
      ? `<div class="workbench-solution-explanation"><strong>Why this final answer works:</strong> ${this.solutionExplanation}</div>`
      : '';
    return `
      <div class="workbench-solution">
        <div class="workbench-solution-header">
          <span class="workbench-solution-title">&check; Reference Final Answer (${escapeWbHtml(this.filename)})</span>
          <div style="display:flex; gap:0.45rem; flex-wrap:wrap;">
            <button type="button" class="btn-inline btn-load-solution">&#10515; Load into Editor &amp; Run</button>
            <button type="button" class="btn-inline btn-copy-solution">&#128203; Copy Code</button>
          </div>
        </div>
        <pre><code>${escapeWbHtml(this.solution)}</code></pre>
        ${expHtml}
      </div>
    `;
  }

  enableEditorShortcuts(textarea) {
    textarea.addEventListener('keydown', (e) => {
      if (e.key === 'Tab') {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.substring(0, start) + '    ' + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 4;
      } else if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
        e.preventDefault();
        this.runCheck();
      }
    });
  }

  updateHintButtonLabel() {
    if (!this.hintBtn) return;
    if (this.hints.length === 0) return;
    if (this.revealedHints === 0) {
      this.hintBtn.innerHTML = `&#128161; Show Hint (1/${this.hints.length})`;
    } else if (this.revealedHints < this.hints.length) {
      this.hintBtn.innerHTML = `&#128161; Next Hint (${this.revealedHints + 1}/${this.hints.length})`;
    } else {
      const hidden = this.hintBox && (this.hintBox.style.display === 'none' || !this.hintBox.style.display);
      this.hintBtn.innerHTML = hidden ? '&#128161; Show Hints' : '&#128161; Hide Hints';
    }
  }

  handleHintClick() {
    if (!this.hintBox) return;
    if (this.hints.length === 0) {
      const isHidden = this.hintBox.style.display === 'none' || !this.hintBox.style.display;
      this.hintBox.style.display = isHidden ? 'block' : 'none';
      this.hintBtn.textContent = isHidden ? 'Hide Hint' : 'Show Hint';
      return;
    }

    if (this.revealedHints < this.hints.length) {
      this.revealedHints += 1;
      const items = this.hints.slice(0, this.revealedHints)
        .map((h) => `<li>${h}</li>`)
        .join('');
      this.hintBox.innerHTML = `<strong>Progressive Hints (${this.revealedHints}/${this.hints.length}):</strong><ol>${items}</ol>`;
      this.hintBox.style.display = 'block';
    } else {
      const isHidden = this.hintBox.style.display === 'none';
      this.hintBox.style.display = isHidden ? 'block' : 'none';
    }
    this.updateHintButtonLabel();
  }

  toggleSolution() {
    if (!this.solutionBox || !this.solutionBtn) return;
    const isHidden = this.solutionBox.style.display === 'none' || !this.solutionBox.style.display;
    this.solutionBox.style.display = isHidden ? 'block' : 'none';
    this.solutionBtn.innerHTML = isHidden ? '&#10003; Hide Final Answer' : '&#10003; Show Final Answer';
  }

  clearFeedback() {
    if (this.consoleEl) {
      this.consoleEl.style.display = 'none';
      this.consoleEl.textContent = '';
    }
    if (this.resultsEl) {
      this.resultsEl.style.display = 'none';
      this.resultsEl.innerHTML = '';
    }
    if (this.hintBox && this.hints.length > 0) {
      this.hintBox.style.display = 'none';
      this.hintBox.innerHTML = '';
    }
    if (this.solutionBox && this.solutionBtn) {
      this.solutionBox.style.display = 'none';
      this.solutionBtn.innerHTML = '&#10003; Show Final Answer';
    }
  }

  runCheck() {
    const code = this.textarea ? this.textarea.value : '';
    let passedCount = 0;
    const testResults = [];

    for (const t of this.tests) {
      let res;
      if (typeof t.test === 'function') {
        res = t.test(code);
      } else {
        const rx = t.regex instanceof RegExp ? t.regex : new RegExp(t.regex, t.flags || 'm');
        const anti = t.mustNotMatch
          ? (t.mustNotMatch instanceof RegExp ? t.mustNotMatch : new RegExp(t.mustNotMatch, t.antiFlags || 'm'))
          : null;
        const pass = rx.test(code) && (!anti || !anti.test(code));
        res = {
          pass: pass,
          message: pass ? (t.passMessage || 'Requirement satisfied.') : (t.failMessage || 'Requirement not yet satisfied.')
        };
      }
      if (res.pass) passedCount += 1;
      testResults.push({ name: t.name, pass: res.pass, message: res.message });
    }

    const allPassed = testResults.length > 0 && passedCount === testResults.length;

    if (this.resultsEl) {
      this.resultsEl.style.display = 'block';
      let html = `<div class="results-header ${allPassed ? 'all-passed' : 'has-failures'}">`;
      html += allPassed
        ? `<strong>&check; All ${testResults.length} Verification Checks Passed!</strong>`
        : `<strong>&cross; ${passedCount} / ${testResults.length} Checks Passed &mdash; Complete the remaining items:</strong>`;
      html += `</div><ul class="test-list">`;

      for (const r of testResults) {
        html += `<li class="${r.pass ? 'test-pass' : 'test-fail'}">`;
        html += `<span class="test-icon">${r.pass ? '&check;' : '&cross;'}</span> `;
        html += `<div><strong>${escapeWbHtml(r.name)}:</strong> ${escapeWbHtml(r.message)}</div>`;
        html += `</li>`;
      }
      html += `</ul>`;
      this.resultsEl.innerHTML = html;
    }

    if (this.consoleEl) {
      this.consoleEl.style.display = 'block';
      if (allPassed) {
        const out = typeof this.simulatedOutput === 'function' ? this.simulatedOutput(code) : this.simulatedOutput;
        this.consoleEl.innerHTML = `<span class="console-prefix">&gt; javac ${escapeWbHtml(this.filename)} &amp;&amp; java ${escapeWbHtml(this.filename.replace(/\.java$/, ''))}</span>\n${escapeWbHtml(out)}`;
        this.consoleEl.className = 'workbench-console success';
      } else {
        const firstFailures = testResults.filter((r) => !r.pass).slice(0, 3)
          .map((r, idx) => `  [${idx + 1}] ${r.name}: ${r.message}`)
          .join('\n');
        this.consoleEl.innerHTML = `<span class="console-prefix">&gt; Verification Diagnostic (${escapeWbHtml(this.filename)}):</span>\n${escapeWbHtml(firstFailures)}\nTip: Click "Show Hint" for step-by-step guidance or "Show Final Answer" to compare.`;
        this.consoleEl.className = 'workbench-console error';
      }
    }
  }
}

// Interactive Terminal Simulator for javac, java, javap, jcmd, jfr
function runTerminalCommand(termId, command) {
  const container = document.getElementById(termId);
  if (!container) return;
  const screen = container.querySelector('.terminal-screen');
  const input = container.querySelector('.terminal-input');
  const cmd = command || (input ? input.value.trim() : '');

  if (!cmd) return;

  const responses = container.dataset.responses ? JSON.parse(container.dataset.responses) : {};
  let out = responses[cmd];

  if (!out) {
    if (cmd === 'clear') {
      screen.innerHTML = '';
      if (input) input.value = '';
      return;
    }
    out = `command not recognized: ${cmd}\nTry clicking one of the sample command buttons above.`;
  }

  const promptLine = `<div class="term-line"><span class="term-prompt">$</span> <span class="term-cmd">${escapeWbHtml(cmd)}</span></div>`;
  const outputLine = `<div class="term-output">${escapeWbHtml(out)}</div>`;

  screen.innerHTML += promptLine + outputLine;
  screen.scrollTop = screen.scrollHeight;
  if (input) input.value = '';
}

// Auto-hydrate interactive exercises and quizzes from <script type="application/json" id="lesson-data">
(function hydrateLessonData() {
  const dataEl = document.getElementById('lesson-data');
  if (!dataEl) return;
  try {
    const payload = JSON.parse(dataEl.textContent);
    if (Array.isArray(payload.exercises)) {
      payload.exercises.forEach((exCfg) => new JavaExercise(exCfg));
    }
    if (payload.quiz && typeof JavaQuiz !== 'undefined') {
      new JavaQuiz(payload.quiz);
    }
  } catch (err) {
    console.error('Failed to parse #lesson-data JSON:', err);
  }
})();
