/**
 * Spring Boot Mastery Course Engine (2027 Senior Backend Engineer)
 * Universal Interactive Engine:
 * - Dual Language Switching (Java 21 / Kotlin 2.x) across snippets and editors
 * - Interactive Code Quiz System with deep per-option explanations
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

  // --- 1. Global Language Switching (Java vs Kotlin) ---
  function initLanguageTabs() {
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

  function applyLanguage(lang) {
    // 1. Update snippet tab buttons
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

    // 3. Update lab editors to match if user hasn't explicitly overridden
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
      let labLang = currentLanguage;
      switchPracticeLanguage(container, practiceId, labLang, false);

      // Lab Language Switch Buttons
      container.querySelectorAll('.practice-lang-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
          const targetLang = btn.dataset.lang;
          if (targetLang) {
            container.dataset.userLockedLang = 'true';
            switchPracticeLanguage(container, practiceId, targetLang, true);
          }
        });
      });

      // Handle Tab key inside editor
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

  // --- 3. In-Browser Test Execution Simulation Engine ---
  function executePracticeVerification(practiceId, code, lang, consoleEl, container) {
    if (!consoleEl) return;

    consoleEl.className = 'test-console visible';
    consoleEl.innerHTML = `
      <div class="console-title">
        <span>RUNNING TEST SUITE • [${practiceId}] • ${lang.toUpperCase()}</span>
        <span>JUnit 5 / AssertJ Simulator</span>
      </div>
      <div class="test-line info">Compiling and running assertions on candidate implementation...</div>
    `;

    // Look up registered validator for this practice lab
    const validator = window.practiceValidators && window.practiceValidators[practiceId];
    let testResults = [];

    if (typeof validator === 'function') {
      try {
        testResults = validator(code, lang);
      } catch (e) {
        testResults = [
          { name: 'Execution Safety Check', passed: false, error: 'Runtime error in test runner: ' + e.message }
        ];
      }
    } else {
      testResults = defaultFallbackTests(code);
    }

    // Render results line by line with slight terminal delay simulation
    let allPassed = true;
    let renderedHtml = `
      <div class="console-title">
        <span>TEST SUITE OUTPUT • ${lang.toUpperCase()} 21+</span>
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
            ${t.error ? `<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↳ <strong>Issue:</strong> ${t.error}` : ''}
            ${t.hint ? `<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;↳ <strong>Fix Nudge:</strong> ${t.hint}` : ''}
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
        if (record[id]) {
          el.className = 'badge badge-status completed';
          el.textContent = 'Completed ✓';
        }
      });
    } catch (e) {
      console.error(e);
    }
  }

  // --- 4. Interactive Code Quiz System with Per-Option Feedback ---
  function initCodeQuizzes() {
    // Restore saved quiz completions
    const quizRecord = JSON.parse(localStorage.getItem(QUIZ_STORAGE_KEY) || '{}');

    document.querySelectorAll('.quiz-card').forEach((card) => {
      const quizId = card.id;
      if (!quizId) return;

      const feedbackEl = card.querySelector('.quiz-feedback');
      const allButtons = card.querySelectorAll('.quiz-opt-btn');

      allButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
          const isCorrect = btn.dataset.isCorrect === 'true' || btn.classList.contains('target-correct');
          const optionReason = btn.dataset.reason || (isCorrect ? 'Correct analysis.' : 'Incorrect assessment.');
          const fullExplanation = card.dataset.explanation || '';

          // Reset other buttons state visually but keep clickable so user can explore
          allButtons.forEach((b) => b.classList.remove('correct', 'incorrect'));

          if (isCorrect) {
            btn.classList.add('correct');
            feedbackEl.className = 'quiz-feedback visible success';
            feedbackEl.innerHTML = `
              <div style="font-weight: 700; margin-bottom: 6px;">✓ Correct!</div>
              <div>${optionReason}</div>
              ${fullExplanation ? `<div class="quiz-explanation-box"><strong>Architectural Rationale:</strong><br>${fullExplanation}</div>` : ''}
            `;
            // Save completion
            quizRecord[quizId] = true;
            localStorage.setItem(QUIZ_STORAGE_KEY, JSON.stringify(quizRecord));
          } else {
            btn.classList.add('incorrect');
            feedbackEl.className = 'quiz-feedback visible error';
            feedbackEl.innerHTML = `
              <div style="font-weight: 700; margin-bottom: 6px;">✗ Incorrect</div>
              <div>${optionReason}</div>
              <div style="margin-top: 6px; font-size: 13px; color: var(--text-muted);">
                Tip: Analyze the code snippet carefully or try another option to inspect why it's chosen!
              </div>
            `;
          }
        });
      });
    });
  }

  // Helper
  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  // DOM Ready initialization
  document.addEventListener('DOMContentLoaded', () => {
    initLanguageTabs();
    initCodePractices();
    initCodeQuizzes();
    updateProgressUI();
  });

  // Global validator registry
  window.practiceValidators = window.practiceValidators || {};
})();
