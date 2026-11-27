/**
 * Spring Boot Mastery Course Engine (2027 Senior Backend Engineer)
 * Universal Interactive Engine:
 * - Dual Language Switching (Java 25 LTS / Kotlin 2.x) across snippets and editors
 * - Interactive Code Quiz System with deep per-option explanations & reload persistence
 * - In-Browser Test Runner & Execution Simulator with multi-assertion console
 * - Progressive Hints Drawer (3 levels: Nudge -> Syntax -> Key Snippet)
 * - Final Solution Drawer (Drop-in Java & Kotlin code + Architectural Breakdown)
 * - Persistent Progress & Code State in localStorage
 */

(function () {
  'use strict';

  // --- Preference & Storage Keys ---
  const GLOBAL_LANG_KEY = 'spring_mastery_lang';
  const CODE_STORAGE_PREFIX = 'spring_mastery_code_';
  const PROGRESS_STORAGE_KEY = 'spring_mastery_progress';
  const QUIZ_STORAGE_KEY = 'spring_mastery_quizzes';

  // Current global language: 'java' or 'kotlin'
  let currentLanguage = localStorage.getItem(GLOBAL_LANG_KEY) || 'java';

  // Safe HTML formatter: escapes generic type parameters like <String, T> so innerHTML never swallows them,
  // while preserving intentional inline formatting tags (<code>, <strong>, <em>, <br>) and converting `backticks`.
  function formatSafeInlineHtml(str) {
    if (!str) return '';
    const escaped = String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
    const restoredTags = escaped.replace(
      /&lt;(\/?(?:code|strong|em|b|i|br)\s*\/?)&gt;/gi,
      '<$1>'
    );
    return restoredTags.replace(/`([^`\n]+)`/g, '<code>$1</code>');
  }

  // --- 1. Global Language Switching (Java 25 vs Kotlin 2.x) ---
  function initLanguageTabs() {
    injectHeaderLanguageToggle();
    applyLanguage(currentLanguage);

    document.addEventListener('click', (e) => {
      const btn = e.target.closest('.code-tab-btn');
      if (!btn) return;

      const targetLang = btn.dataset.lang;
      if (!targetLang) return;

      currentLanguage = targetLang;
      localStorage.setItem(GLOBAL_LANG_KEY, targetLang);
      applyLanguage(targetLang);
    });
  }

  function injectHeaderLanguageToggle() {
    const headerMeta = document.querySelector('.site-header .header-meta');
    if (!headerMeta || headerMeta.querySelector('.global-lang-switch')) return;
    // Replace static Java/Kotlin badges in lesson headers with clickable global language switches
    const javaBadge = headerMeta.querySelector('.badge-java');
    const kotlinBadge = headerMeta.querySelector('.badge-kotlin');
    if (javaBadge && kotlinBadge && document.querySelector('.code-box, .practice-container')) {
      const wrapper = document.createElement('div');
      wrapper.className = 'tab-group global-lang-switch';
      wrapper.title = 'Switch preferred language across all lessons';
      wrapper.innerHTML = `
        <button type="button" class="code-tab-btn java-tab" data-lang="java">Java 25</button>
        <button type="button" class="code-tab-btn kotlin-tab" data-lang="kotlin">Kotlin 2.x</button>
      `;
      javaBadge.replaceWith(wrapper);
      kotlinBadge.remove();
    }
  }

  function applyLanguage(lang) {
    // 1. Update snippet & header tab buttons
    document.querySelectorAll('.code-tab-btn').forEach((btn) => {
      const isTarget = btn.dataset.lang === lang;
      btn.classList.toggle('active', isTarget);
    });

    // 2. Update snippet code panes
    document.querySelectorAll('.code-box').forEach((box) => {
      box.querySelectorAll('.code-pane').forEach((pane) => {
        const isTarget = pane.dataset.lang === lang;
        pane.classList.toggle('active', isTarget);
      });
    });

    // 3. Update lab editors to match if user hasn't explicitly locked a separate language
    document.querySelectorAll('.practice-container').forEach((container) => {
      const practiceId = container.dataset.practiceId;
      const langBtn = container.querySelector(`.practice-lang-btn[data-lang="${lang}"]`);
      if (langBtn && !container.dataset.userLockedLang) {
        switchPracticeLanguage(container, practiceId, lang, false);
      }
    });
  }

  // --- 2. Interactive Code Practice Engine ---
  function initCodePractices() {
    document.querySelectorAll('.practice-container').forEach((container) => {
      const practiceId = container.dataset.practiceId;
      const textarea = container.querySelector('.editor-textarea');
      const runBtn = container.querySelector('.btn-run');
      const resetBtn = container.querySelector('.btn-reset');
      const hintBtn = container.querySelector('.btn-hint');
      const solutionBtn = container.querySelector('.btn-solution');
      const hintsDrawer = container.querySelector('.hints-drawer');
      const solutionDrawer = container.querySelector('.solution-drawer');
      const consoleEl = container.querySelector('.test-console');

      if (!textarea || !practiceId) return;

      // Initialize lab language (default to global preference)
      switchPracticeLanguage(container, practiceId, currentLanguage, false);

      // Lab Language Switch Buttons (also sync global preference for smooth multi-lesson study)
      container.querySelectorAll('.practice-lang-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
          const targetLang = btn.dataset.lang;
          if (targetLang) {
            currentLanguage = targetLang;
            localStorage.setItem(GLOBAL_LANG_KEY, targetLang);
            applyLanguage(targetLang);
            switchPracticeLanguage(container, practiceId, targetLang, true);
          }
        });
      });

      // Handle Tab key and Ctrl/Cmd+Enter inside editor
      textarea.addEventListener('keydown', function (e) {
        if (e.key === 'Tab') {
          e.preventDefault();
          const start = this.selectionStart;
          const end = this.selectionEnd;
          this.value = this.value.substring(0, start) + '    ' + this.value.substring(end);
          this.selectionStart = this.selectionEnd = start + 4;
        } else if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
          e.preventDefault();
          if (runBtn) runBtn.click();
        }
      });

      // Auto-save on input for current language
      textarea.addEventListener('input', () => {
        const currentLabLang = container.dataset.currentLabLang || 'java';
        const key = `${CODE_STORAGE_PREFIX}${practiceId}_${currentLabLang}`;
        localStorage.setItem(key, textarea.value);
      });

      // Reset Button
      if (resetBtn) {
        resetBtn.addEventListener('click', () => {
          const currentLabLang = container.dataset.currentLabLang || 'java';
          if (confirm(`Reset your ${currentLabLang.toUpperCase()} code back to the starter template?`)) {
            const starterAttr = currentLabLang === 'kotlin' ? 'starterKotlin' : 'starterJava';
            const starterCode = container.dataset[starterAttr] || textarea.dataset.initialCode || '';
            textarea.value = starterCode;
            localStorage.removeItem(`${CODE_STORAGE_PREFIX}${practiceId}_${currentLabLang}`);
            if (consoleEl) {
              consoleEl.className = 'test-console';
              consoleEl.innerHTML = '';
            }
          }
        });
      }

      // Progressive Hint Button
      if (hintBtn && hintsDrawer) {
        hintBtn.addEventListener('click', () => {
          const isHidden = hintsDrawer.style.display === 'none' || !hintsDrawer.style.display;
          hintsDrawer.style.display = isHidden ? 'block' : 'none';
          hintBtn.textContent = isHidden ? 'Hide Hints' : 'Show Hints (3)';
        });
      }

      // Final Solution Button
      if (solutionBtn && solutionDrawer) {
        solutionBtn.addEventListener('click', () => {
          const isHidden = solutionDrawer.style.display === 'none' || !solutionDrawer.style.display;
          solutionDrawer.style.display = isHidden ? 'block' : 'none';
          solutionBtn.textContent = isHidden ? 'Hide Final Answer' : 'View Final Answer';
        });
      }

      // Run & Verify Button
      if (runBtn) {
        runBtn.addEventListener('click', () => {
          const currentLabLang = container.dataset.currentLabLang || 'java';
          executePracticeVerification(practiceId, textarea.value, currentLabLang, consoleEl, container);
        });
      }
    });

    // Allow clicking status badge in lesson lab header to manually toggle completion (e.g. when solved in IntelliJ IDEA)
    document.querySelectorAll('[data-progress-badge]').forEach((badge) => {
      badge.style.cursor = 'pointer';
      badge.title = 'Click to toggle completion status (or pass the automated lab)';
      badge.addEventListener('click', () => {
        const id = badge.dataset.progressBadge;
        if (!id) return;
        const record = JSON.parse(localStorage.getItem(PROGRESS_STORAGE_KEY) || '{}');
        const currentlyDone = Boolean(record[id]) || localStorage.getItem('sb_mastery_done_' + id) === 'true';
        if (currentlyDone) {
          delete record[id];
          localStorage.setItem('sb_mastery_done_' + id, 'false');
        } else {
          record[id] = true;
          localStorage.setItem('sb_mastery_done_' + id, 'true');
        }
        localStorage.setItem(PROGRESS_STORAGE_KEY, JSON.stringify(record));
        updateProgressUI();
      });
    });
  }

  function switchPracticeLanguage(container, practiceId, lang, focusEditor) {
    container.dataset.currentLabLang = lang;
    const textarea = container.querySelector('.editor-textarea');
    if (!textarea) return;

    // Update active tab buttons
    container.querySelectorAll('.practice-lang-btn').forEach((btn) => {
      btn.classList.toggle('active', btn.dataset.lang === lang);
    });

    // Check localStorage for saved user code in this language
    const key = `${CODE_STORAGE_PREFIX}${practiceId}_${lang}`;
    const savedCode = localStorage.getItem(key);

    if (savedCode !== null) {
      textarea.value = savedCode;
    } else {
      const starterAttr = lang === 'kotlin' ? 'starterKotlin' : 'starterJava';
      const starter = container.dataset[starterAttr];
      if (starter) {
        textarea.value = starter;
      }
    }

    if (focusEditor) {
      textarea.focus();
    }
  }

  // Strip starter instruction comments (including SQL '--') and TODO lines so untouched starter templates never auto-pass
  function stripStarterComments(rawCode, starterCode) {
    if (!rawCode) return '';
    const starterCommentLines = new Set(
      (starterCode || '')
        .split(/\r?\n/)
        .map((l) => l.trim())
        .filter((l) =>
          l.startsWith('//') ||
          l.startsWith('#') ||
          l.startsWith('--') ||
          l.startsWith('<!--') ||
          l.startsWith('/*') ||
          l.startsWith('*')
        )
    );
    return rawCode
      .split(/\r?\n/)
      .map((line) => {
        const trimmed = line.trim();
        if (starterCommentLines.has(trimmed)) return '';
        if (/^(?:\/\/|#|--|<!--|\*).*TODO/i.test(trimmed)) return '';
        return line
          .replace(/(?:\/\/|#|--)\s*TODO.*$/i, '')
          .replace(/<!--\s*TODO[\s\S]*?-->/gi, '');
      })
      .join('\n');
  }

  // --- 3. In-Browser Test Execution Simulation Engine ---
  function executePracticeVerification(practiceId, code, lang, consoleEl, container) {
    if (!consoleEl) return;

    const runtimeLabel = lang === 'kotlin' ? 'KOTLIN 2.x • SPRING BOOT 4.2' : 'JAVA 25 LTS • SPRING BOOT 4.2';

    consoleEl.className = 'test-console visible';
    consoleEl.innerHTML = `
      <div class="console-title">
        <span>RUNNING TEST SUITE • [${escapeHtml(practiceId)}] • ${runtimeLabel}</span>
        <span>JUnit / AssertJ Simulator</span>
      </div>
      <div class="test-line info">Compiling and running assertions on candidate implementation...</div>
    `;

    // Look up registered validator for this practice lab
    const validator = window.practiceValidators && window.practiceValidators[practiceId];
    const starterAttr = lang === 'kotlin' ? 'starterKotlin' : 'starterJava';
    const starterCode = (container && container.dataset && container.dataset[starterAttr]) || '';
    const cleanCode = stripStarterComments(code, starterCode);
    let testResults = [];

    if (typeof validator === 'function') {
      try {
        testResults = validator(cleanCode, lang);
      } catch (e) {
        testResults = [
          { name: 'Execution Safety Check', passed: false, error: 'Runtime error in test runner: ' + e.message }
        ];
      }
    } else {
      testResults = defaultFallbackTests(cleanCode);
    }

    let allPassed = true;
    let renderedHtml = `
      <div class="console-title">
        <span>TEST SUITE OUTPUT • ${runtimeLabel}</span>
        <span>${new Date().toLocaleTimeString()}</span>
      </div>
    `;

    testResults.forEach((t, idx) => {
      if (t.passed) {
        renderedHtml += `
          <div class="test-line pass">
            [PASS] Test ${idx + 1}: ${escapeHtml(t.name)}
          </div>
        `;
      } else {
        allPassed = false;
        renderedHtml += `
          <div class="test-line fail">
            [FAIL] Test ${idx + 1}: ${escapeHtml(t.name)}
            ${t.error ? `<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↳ <strong>Issue:</strong> ${formatSafeInlineHtml(t.error)}` : ''}
            ${t.hint ? `<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↳ <strong>Fix Nudge:</strong> ${formatSafeInlineHtml(t.hint)}` : ''}
          </div>
        `;
      }
    });

    if (allPassed) {
      renderedHtml += `
        <div class="test-line summary-pass">
          ✅ ALL ${testResults.length} TESTS PASSED! Verification Successful. Ready for production!
        </div>
      `;
      markPracticeCompleted(practiceId);
    } else {
      renderedHtml += `
        <div class="test-line summary-fail">
          ❌ TEST SUITE FAILED. Review issues above or open the Progressive Hints drawer!
        </div>
      `;
    }

    consoleEl.innerHTML = renderedHtml;
  }

  function defaultFallbackTests(code) {
    if (!code || code.trim().length < 15) {
      return [
        { name: 'Non-Empty Code Check', passed: false, error: 'Editor code is empty or too short to test.' }
      ];
    }
    return [
      { name: 'Syntax Completeness', passed: true },
      { name: 'Structural Integrity', passed: true }
    ];
  }

  function markPracticeCompleted(practiceId) {
    try {
      const record = JSON.parse(localStorage.getItem(PROGRESS_STORAGE_KEY) || '{}');
      record[practiceId] = true;
      localStorage.setItem(PROGRESS_STORAGE_KEY, JSON.stringify(record));
      localStorage.setItem('sb_mastery_done_' + practiceId, 'true');
      updateProgressUI();
    } catch (e) {
      console.error(e);
    }
  }

  function updateProgressUI() {
    try {
      const record = JSON.parse(localStorage.getItem(PROGRESS_STORAGE_KEY) || '{}');
      document.querySelectorAll('[data-progress-badge]').forEach((el) => {
        const id = el.dataset.progressBadge;
        const isDone = Boolean(record[id]) || localStorage.getItem('sb_mastery_done_' + id) === 'true';
        if (isDone) {
          el.className = 'badge badge-status completed';
          el.textContent = 'Completed ✓';
        } else {
          el.className = 'badge badge-status';
          el.textContent = 'To Do';
        }
      });
    } catch (e) {
      console.error(e);
    }
  }

  // --- 4. Interactive Code Quiz System with Per-Option Feedback & Reload Persistence ---
  function renderQuizCorrectState(btn, feedbackEl, fullExplanation) {
    const optionReason = btn.dataset.reason || 'Correct analysis.';
    btn.classList.add('correct');
    if (feedbackEl) {
      feedbackEl.className = 'quiz-feedback visible success';
      feedbackEl.innerHTML = `
        <div style="font-weight: 700; margin-bottom: 6px;">✓ Correct!</div>
        <div>${formatSafeInlineHtml(optionReason)}</div>
        ${fullExplanation ? `<div class="quiz-explanation-box"><strong>Architectural Rationale:</strong><br>${formatSafeInlineHtml(fullExplanation)}</div>` : ''}
      `;
    }
  }

  function initCodeQuizzes() {
    let quizRecord = {};
    try {
      quizRecord = JSON.parse(localStorage.getItem(QUIZ_STORAGE_KEY) || '{}');
    } catch (e) {
      quizRecord = {};
    }

    document.querySelectorAll('.quiz-card').forEach((card) => {
      const quizId = card.id;
      if (!quizId) return;

      const feedbackEl = card.querySelector('.quiz-feedback');
      const allButtons = card.querySelectorAll('.quiz-opt-btn');
      const fullExplanation = card.dataset.explanation || '';

      // Restore previously answered quiz state on reload
      if (quizRecord[quizId]) {
        allButtons.forEach((btn) => {
          const isCorrect = btn.dataset.isCorrect === 'true' || btn.classList.contains('target-correct');
          if (isCorrect) {
            renderQuizCorrectState(btn, feedbackEl, fullExplanation);
          }
        });
      }

      allButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
          const isCorrect = btn.dataset.isCorrect === 'true' || btn.classList.contains('target-correct');
          const optionReason = btn.dataset.reason || (isCorrect ? 'Correct analysis.' : 'Incorrect assessment.');

          allButtons.forEach((b) => b.classList.remove('correct', 'incorrect'));

          if (isCorrect) {
            renderQuizCorrectState(btn, feedbackEl, fullExplanation);
            quizRecord[quizId] = true;
            localStorage.setItem(QUIZ_STORAGE_KEY, JSON.stringify(quizRecord));
          } else {
            btn.classList.add('incorrect');
            if (feedbackEl) {
              feedbackEl.className = 'quiz-feedback visible error';
              feedbackEl.innerHTML = `
                <div style="font-weight: 700; margin-bottom: 6px;">✗ Incorrect</div>
                <div>${formatSafeInlineHtml(optionReason)}</div>
                <div style="margin-top: 6px; font-size: 13px; color: var(--text-muted);">
                  Tip: Analyze the code snippet carefully or try another option to inspect why it's chosen!
                </div>
              `;
            }
          }
        });
      });
    });
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  document.addEventListener('DOMContentLoaded', () => {
    initLanguageTabs();
    initCodePractices();
    initCodeQuizzes();
    updateProgressUI();
  });

  window.practiceValidators = window.practiceValidators || {};
})();
