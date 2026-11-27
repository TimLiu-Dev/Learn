// Antigravity Senior Java Interactive Workbench & Progress Engine
// Features:
// - Scaffolded / Bug-Fix initialCode editor with Tab indentation, Cmd/Ctrl+Enter shortcut & localStorage draft autosave
// - Multi-check automated verification suite + accurate JDK 25 / Gradle 9 simulated console output
// - Progressive step-by-step Hints & toggleable "Final Answer" drawer (idempotent against duplicate init)
// - Automatic & manual Lesson Completion tracking synced with Master Plan Hub (Lesson 0001)

function escapeWbHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function getWbLessonId() {
  const m = (window.location.pathname || '').match(/\/(\d{4})-[^/]+\.html$/);
  return m ? m[1] : null;
}

function getProgressStore() {
  try {
    const raw = window.localStorage.getItem('agy_java_progress_v1');
    return raw ? JSON.parse(raw) : {};
  } catch (_) {
    return {};
  }
}

function saveProgressStore(store) {
  try {
    window.localStorage.setItem('agy_java_progress_v1', JSON.stringify(store));
  } catch (_) {
    // Ignore sandbox storage errors
  }
}

function getDraftKey(lessonId, exId) {
  return `agy_java_draft_v1_${lessonId}_${exId}`;
}

function readDraft(lessonId, exId) {
  if (!lessonId) return null;
  try {
    return window.localStorage.getItem(getDraftKey(lessonId, exId));
  } catch (_) {
    return null;
  }
}

function writeDraft(lessonId, exId, value, initialCode) {
  if (!lessonId) return;
  try {
    const key = getDraftKey(lessonId, exId);
    if (!value || value === initialCode) {
      window.localStorage.removeItem(key);
    } else {
      window.localStorage.setItem(key, value);
    }
  } catch (_) {
    // Ignore storage errors
  }
}

const EXTERNAL_FRAMEWORK_RE = /^\s*import\s+(?:static\s+)?(?:org\.springframework|jakarta\.|javax\.servlet|org\.hibernate|org\.junit|org\.assertj|org\.mockito|io\.resilience4j|com\.github\.benmanes|org\.apache\.|io\.micrometer|io\.opentelemetry|org\.slf4j|tools\.jackson|com\.fasterxml\.jackson|org\.jspecify|io\.grpc|graphql\.|net\.javacrumbs|com\.tngtech|io\.restassured|org\.testcontainers)/m;

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
    this.lessonId = getWbLessonId();
    this.simulatedOutput = config.simulatedOutput || function () {
      return 'Program compiled and executed with exit code 0.';
    };
    this.init();
  }

  init() {
    this.container = document.getElementById(this.id);
    if (!this.container) return;
    if (this.container.dataset.wbHydrated === 'true') return;
    this.container.dataset.wbHydrated = 'true';

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
    this.statusEl = this.container.querySelector('.workbench-save-status');

    if (this.textarea) {
      const savedDraft = readDraft(this.lessonId, this.id);
      if (savedDraft !== null && savedDraft !== this.initialCode) {
        this.textarea.value = savedDraft;
        this.setSaveStatus('Draft restored');
      } else if (this.initialCode) {
        this.textarea.value = this.initialCode;
      }
      if (this.isExercisePassed()) {
        this.setSaveStatus('✓ Passed');
      }
      this.enableEditorShortcuts(this.textarea);
      this.textarea.addEventListener('input', () => {
        writeDraft(this.lessonId, this.id, this.textarea.value, this.initialCode);
        this.setSaveStatus(this.textarea.value === this.initialCode ? '' : 'Draft saved');
      });
    }

    if (this.checkBtn) {
      this.checkBtn.addEventListener('click', () => this.runCheck());
    }

    if (this.resetBtn) {
      this.resetBtn.addEventListener('click', () => {
        if (this.textarea) this.textarea.value = this.initialCode;
        writeDraft(this.lessonId, this.id, '', this.initialCode);
        this.revealedHints = 0;
        this.updateHintButtonLabel();
        this.clearFeedback();
        this.setSaveStatus(this.isExercisePassed() ? '✓ Passed' : '');
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
            writeDraft(this.lessonId, this.id, this.solution, this.initialCode);
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

  setSaveStatus(msg) {
    if (!this.statusEl) return;
    this.statusEl.textContent = msg || '';
    this.statusEl.style.display = msg ? 'inline-block' : 'none';
  }

  isExercisePassed() {
    if (!this.lessonId) return false;
    const store = getProgressStore();
    const entry = store[this.lessonId];
    return Boolean(entry && Array.isArray(entry.exPassed) && entry.exPassed.includes(this.id));
  }

  recordExercisePassed() {
    if (!this.lessonId || this.lessonId === '0001') return;
    const store = getProgressStore();
    const entry = store[this.lessonId] || { exPassed: [], quizPassed: [], completed: false };
    if (!Array.isArray(entry.exPassed)) entry.exPassed = [];
    if (!entry.exPassed.includes(this.id)) {
      entry.exPassed.push(this.id);
    }
    entry.updatedAt = Date.now();
    store[this.lessonId] = entry;
    saveProgressStore(store);
    this.setSaveStatus('✓ Passed');
    if (typeof window.syncLessonCompletionState === 'function') {
      window.syncLessonCompletionState(true);
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
        <div style="display:flex; align-items:center; gap:0.5rem;">
          <span class="workbench-save-status" style="display:none;"></span>
          <span class="badge badge-current">${escapeWbHtml(this.badge)}</span>
        </div>
      </div>
      <div class="workbench-body">
        ${instructionsBlock}
        <textarea class="workbench-editor" spellcheck="false" aria-label="${escapeWbHtml(this.filename)} code editor"></textarea>
        <div class="workbench-actions">
          <button type="button" class="btn-primary btn-check-solution" title="Shortcut: Cmd/Ctrl + Enter">&#9654; Run &amp; Check Code</button>
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
        writeDraft(this.lessonId, this.id, textarea.value, this.initialCode);
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

  buildRunCommandLabel(code) {
    const clsName = this.filename.replace(/\.java$/, '');
    const combined = (code || '') + '\n' + (this.solution || '');
    if (/\bStructuredTaskScope\b/.test(combined)) {
      return `javac --enable-preview --release 25 ${this.filename} && java --enable-preview ${clsName}`;
    }
    if (EXTERNAL_FRAMEWORK_RE.test(combined)) {
      return `./gradlew compileJava && ./gradlew test --tests *${clsName}*`;
    }
    return `javac --release 25 ${this.filename} && java ${clsName}`;
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
    if (allPassed) {
      this.recordExercisePassed();
    }

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
        const cmdLabel = this.buildRunCommandLabel(code);
        this.consoleEl.innerHTML = `<span class="console-prefix">&gt; ${escapeWbHtml(cmdLabel)}</span>\n${escapeWbHtml(out)}`;
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

// Lesson completion toggle pill & automatic completion sync
window.syncLessonCompletionState = function syncLessonCompletionState(allowAutoPromote) {
  const lessonId = getWbLessonId();
  if (!lessonId || lessonId === '0001') return;

  const dataEl = document.getElementById('lesson-data');
  let expectedEx = 0;
  let expectedQuiz = 0;
  if (dataEl) {
    try {
      const parsed = JSON.parse(dataEl.textContent);
      expectedEx = Array.isArray(parsed.exercises) ? parsed.exercises.length : 0;
      expectedQuiz = (parsed.quiz && Array.isArray(parsed.quiz.questions)) ? parsed.quiz.questions.length : 0;
    } catch (_) {}
  }

  const store = getProgressStore();
  const entry = store[lessonId] || { exPassed: [], quizPassed: [], completed: false };
  const exDone = Array.isArray(entry.exPassed) ? entry.exPassed.length : 0;
  const qDone = Array.isArray(entry.quizPassed) ? entry.quizPassed.length : 0;

  if (allowAutoPromote && !entry.completed && expectedEx > 0 && exDone >= expectedEx && qDone >= expectedQuiz) {
    entry.completed = true;
    entry.updatedAt = Date.now();
    store[lessonId] = entry;
    saveProgressStore(store);
  }

  const btns = document.querySelectorAll('.btn-lesson-complete-toggle');
  btns.forEach((btn) => {
    if (entry.completed) {
      btn.className = 'btn-lesson-complete-toggle is-completed';
      btn.innerHTML = '&check; Lesson Completed';
      btn.title = 'Click to mark lesson incomplete';
    } else {
      const sub = (exDone > 0 || qDone > 0) ? ` (${exDone}/${expectedEx} drills, ${qDone}/${expectedQuiz} quiz)` : '';
      btn.className = 'btn-lesson-complete-toggle';
      btn.innerHTML = `&#9744; Mark Complete${escapeWbHtml(sub)}`;
      btn.title = 'Completing all drills & quiz questions marks this automatically, or click to toggle';
    }
  });
};

function mountLessonCompletionToggle() {
  const lessonId = getWbLessonId();
  if (!lessonId || lessonId === '0001') return;
  if (!document.getElementById('lesson-data')) return;

  const navCenter = document.querySelector('.lesson-nav .lesson-nav-center');
  if (navCenter && !navCenter.querySelector('.btn-lesson-complete-toggle')) {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'btn-lesson-complete-toggle';
    btn.addEventListener('click', () => {
      const store = getProgressStore();
      const entry = store[lessonId] || { exPassed: [], quizPassed: [], completed: false };
      entry.completed = !entry.completed;
      entry.updatedAt = Date.now();
      store[lessonId] = entry;
      saveProgressStore(store);
      window.syncLessonCompletionState(false);
    });
    navCenter.appendChild(btn);
  }
  window.syncLessonCompletionState(false);
}

// Master Plan Hub (Lesson 0001) Live Progress Dashboard & Filters
function hydrateMasterPlanHub() {
  const lessonId = getWbLessonId();
  if (lessonId !== '0001') return;
  const items = Array.from(document.querySelectorAll('.curriculum-item'));
  if (items.length === 0) return;

  const renderHubState = (activeFilter) => {
    const store = getProgressStore();
    let completedLessons = 0;
    let totalDrillsDone = 0;
    let totalQuizDone = 0;
    let mainCompleted = 0;
    let mainTotal = 0;
    let nextRecommended = null;

    items.forEach((item) => {
      const idEl = item.querySelector('.curriculum-id');
      const linkEl = item.querySelector('.curriculum-name a');
      const statusEl = item.querySelector('.curriculum-status');
      const stageEl = item.closest('.stage-section');
      if (!idEl || !linkEl || !statusEl) return;

      const id = idEl.textContent.trim();
      const isOptional = Boolean(stageEl && /Optional Track/i.test(stageEl.textContent.slice(0, 160)));
      if (!isOptional) mainTotal += 1;

      if (!statusEl.dataset.originalHtml) {
        statusEl.dataset.originalHtml = statusEl.innerHTML;
      }

      const entry = store[id] || { exPassed: [], quizPassed: [], completed: false };
      const exCount = Array.isArray(entry.exPassed) ? entry.exPassed.length : 0;
      const qCount = Array.isArray(entry.quizPassed) ? entry.quizPassed.length : 0;
      totalDrillsDone += Math.min(2, exCount);
      totalQuizDone += Math.min(3, qCount);

      if (entry.completed) {
        completedLessons += 1;
        if (!isOptional) mainCompleted += 1;
        item.classList.add('is-lesson-done');
        statusEl.innerHTML = '<span class="badge badge-next">&check; Completed</span>';
      } else {
        item.classList.remove('is-lesson-done');
        if (!nextRecommended && !isOptional) {
          nextRecommended = { id, title: linkEl.textContent.trim(), href: linkEl.getAttribute('href') };
        }
        if (exCount > 0 || qCount > 0) {
          statusEl.innerHTML = `<span class="badge badge-current">In Progress (${exCount}/2 &bull; ${qCount}/3)</span>`;
        } else {
          statusEl.innerHTML = statusEl.dataset.originalHtml;
        }
      }

      let visible = true;
      if (activeFilter === 'main') visible = !isOptional;
      else if (activeFilter === 'todo') visible = !entry.completed;
      else if (activeFilter === 'done') visible = Boolean(entry.completed);
      item.style.display = visible ? '' : 'none';
    });

    if (!nextRecommended) {
      const firstTodo = items.find((it) => {
        const id = it.querySelector('.curriculum-id')?.textContent.trim();
        return id && !(store[id] && store[id].completed);
      });
      if (firstTodo) {
        const id = firstTodo.querySelector('.curriculum-id').textContent.trim();
        const a = firstTodo.querySelector('.curriculum-name a');
        nextRecommended = { id, title: a.textContent.trim(), href: a.getAttribute('href') };
      }
    }

    let banner = document.getElementById('hub-progress-dashboard');
    if (!banner) {
      banner = document.createElement('div');
      banner.id = 'hub-progress-dashboard';
      banner.className = 'hub-progress-card';
      const anchor = document.querySelector('.metric-grid');
      if (anchor && anchor.parentNode) {
        anchor.parentNode.insertBefore(banner, anchor.nextSibling);
      }
    }

    const pct = Math.round((mainCompleted / Math.max(1, mainTotal)) * 100);
    const resumeHtml = nextRecommended
      ? `<a class="btn-primary hub-resume-btn" href="${escapeWbHtml(nextRecommended.href)}">&#9654; ${completedLessons === 0 ? 'Start' : 'Resume'} Lesson ${escapeWbHtml(nextRecommended.id)}: ${escapeWbHtml(nextRecommended.title)}</a>`
      : `<span class="badge badge-next" style="font-size:0.88rem; padding:0.45rem 0.85rem;">&#127881; All 90 Coding Lessons Completed!</span>`;

    banner.innerHTML = `
      <div class="hub-progress-top">
        <div>
          <div class="hub-progress-title">Your Local Study Progress</div>
          <div class="hub-progress-stats">
            <strong>${mainCompleted} / ${mainTotal}</strong> Main Track Lessons (${pct}%) &bull;
            <strong>${completedLessons} / ${items.length}</strong> Total &bull;
            <strong>${totalDrillsDone}</strong> Drills &bull;
            <strong>${totalQuizDone}</strong> Quiz Checks
          </div>
        </div>
        <div>${resumeHtml}</div>
      </div>
      <div class="hub-progress-bar-track" role="progressbar" aria-valuenow="${pct}" aria-valuemin="0" aria-valuemax="100">
        <div class="hub-progress-bar-fill" style="width:${pct}%"></div>
      </div>
      <div class="hub-filter-bar">
        <div class="hub-filter-group">
          <button type="button" class="btn-inline ${activeFilter === 'all' ? 'is-active-filter' : ''}" data-hub-filter="all">All (${items.length})</button>
          <button type="button" class="btn-inline ${activeFilter === 'main' ? 'is-active-filter' : ''}" data-hub-filter="main">Main Track (${mainTotal})</button>
          <button type="button" class="btn-inline ${activeFilter === 'todo' ? 'is-active-filter' : ''}" data-hub-filter="todo">Remaining (${items.length - completedLessons})</button>
          <button type="button" class="btn-inline ${activeFilter === 'done' ? 'is-active-filter' : ''}" data-hub-filter="done">Completed (${completedLessons})</button>
        </div>
        <button type="button" class="btn-inline hub-reset-progress">Reset Progress</button>
      </div>
    `;

    banner.querySelectorAll('[data-hub-filter]').forEach((btn) => {
      btn.addEventListener('click', () => renderHubState(btn.dataset.hubFilter));
    });
    const resetBtn = banner.querySelector('.hub-reset-progress');
    if (resetBtn) {
      resetBtn.addEventListener('click', () => {
        if (window.confirm('Reset all saved lesson checkmarks and quiz progress in this browser?')) {
          saveProgressStore({});
          renderHubState('all');
        }
      });
    }
  };

  renderHubState('all');
}

// Auto-hydrate interactive exercises, quizzes, completion toggle, and Hub 0001 dashboard
(function hydrateLessonData() {
  const dataEl = document.getElementById('lesson-data');
  if (dataEl) {
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
    mountLessonCompletionToggle();
  }
  hydrateMasterPlanHub();
})();
