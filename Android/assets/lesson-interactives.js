/**
 * Modern Android Curriculum - Universal Interactive Engine v3
 * Built for Junior-to-Senior Android & KMP Mastery (2026/2027 Standards)
 */

document.addEventListener('DOMContentLoaded', () => {
  initLessonProgress();
  initRunnableExamples();
  initCodeEditors();
  initQuizzes();
});

/* ==========================================================================
   HTML & String Safety Utilities
   ========================================================================== */
function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * Escapes raw generic brackets (e.g. <T>, <List<String>>, <reified>) so innerHTML
 * never swallows them as unknown tags, while preserving allowed inline formatting tags.
 */
function formatSafeInlineHtml(str) {
  const placeholders = [];
  const allowedTagRegex = /<\/?(?:code|strong|em|b|i|br)\s*\/?>/gi;
  const tokenized = String(str).replace(allowedTagRegex, (match) => {
    const token = `__SAFE_HTML_TAG_${placeholders.length}__`;
    placeholders.push(match);
    return token;
  });
  let escaped = escapeHtml(tokenized);
  placeholders.forEach((tag, idx) => {
    escaped = escaped.replace(`__SAFE_HTML_TAG_${idx}__`, tag);
  });
  return escaped;
}

/**
 * Strips // single-line and /* block *\/ comments ONLY outside Kotlin string
 * and character literals so URLs like "https://..." or "wss://..." are preserved intact.
 */
function stripCommentsPreservingStrings(code) {
  let out = '';
  let i = 0;
  const n = code.length;

  while (i < n) {
    // 1. Triple-quoted raw string """..."""
    if (code.startsWith('"""', i)) {
      const end = code.indexOf('"""', i + 3);
      if (end === -1) {
        out += code.slice(i);
        break;
      }
      out += code.slice(i, end + 3);
      i = end + 3;
    }
    // 2. Block comment /* ... */
    else if (code.startsWith('/*', i)) {
      const end = code.indexOf('*/', i + 2);
      if (end === -1) break;
      out += ' ';
      i = end + 2;
    }
    // 3. Single-line comment // ...
    else if (code.startsWith('//', i)) {
      const end = code.indexOf('\n', i + 2);
      if (end === -1) break;
      i = end;
    }
    // 4. Single-quoted string "..."
    else if (code[i] === '"') {
      out += code[i++];
      while (i < n && code[i] !== '"' && code[i] !== '\n') {
        if (code[i] === '\\' && i + 1 < n) {
          out += code.slice(i, i + 2);
          i += 2;
        } else {
          out += code[i++];
        }
      }
      if (i < n && code[i] === '"') {
        out += code[i++];
      }
    }
    // 5. Character literal '...'
    else if (code[i] === "'") {
      out += code[i++];
      while (i < n && code[i] !== "'" && code[i] !== '\n') {
        if (code[i] === '\\' && i + 1 < n) {
          out += code.slice(i, i + 2);
          i += 2;
        } else {
          out += code[i++];
        }
      }
      if (i < n && code[i] === "'") {
        out += code[i++];
      }
    }
    // 6. Regular code character
    else {
      out += code[i++];
    }
  }

  return out.trim();
}

/* ==========================================================================
   0. Lesson Completion Progress Tracker (localStorage)
   ========================================================================== */
const PROGRESS_STORAGE_KEY = 'mad_2027_completed_lessons';
const TOTAL_CURRICULUM_LESSONS = 76;

function getCompletedLessons() {
  try {
    const raw = localStorage.getItem(PROGRESS_STORAGE_KEY);
    const arr = raw ? JSON.parse(raw) : [];
    return Array.isArray(arr) ? arr : [];
  } catch (e) {
    return [];
  }
}

function saveCompletedLessons(list) {
  try {
    localStorage.setItem(PROGRESS_STORAGE_KEY, JSON.stringify(Array.from(new Set(list)).sort()));
  } catch (e) {}
}

function initLessonProgress() {
  const pathMatch = window.location.pathname.match(/(\d{4})-[^/]+\.html$/);
  if (!pathMatch) return;
  const lessonId = pathMatch[1];

  const toggleBtns = document.querySelectorAll('.btn-complete-lesson');
  const progressPills = document.querySelectorAll('.course-progress-pill');

  function updateUi() {
    const completed = getCompletedLessons();
    const isDone = completed.includes(lessonId);
    const pct = Math.round((completed.length / TOTAL_CURRICULUM_LESSONS) * 100);

    toggleBtns.forEach(btn => {
      btn.classList.toggle('completed', isDone);
      btn.innerHTML = isDone ? '✓ Completed' : '○ Mark Lesson Complete';
    });

    progressPills.forEach(pill => {
      pill.textContent = `${completed.length} / ${TOTAL_CURRICULUM_LESSONS} Completed (${pct}%)`;
    });
  }

  toggleBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const completed = getCompletedLessons();
      const idx = completed.indexOf(lessonId);
      if (idx >= 0) {
        completed.splice(idx, 1);
      } else {
        completed.push(lessonId);
      }
      saveCompletedLessons(completed);
      updateUi();
    });
  });

  updateUi();
}

/* ==========================================================================
   1. Runnable Code Examples Runner (Simulated Output Preview)
   ========================================================================== */
function initRunnableExamples() {
  const exampleBoxes = document.querySelectorAll('.runnable-example-box');

  exampleBoxes.forEach(box => {
    const runBtn = box.querySelector('.btn-run-example');
    const terminal = box.querySelector('.runnable-terminal');
    const termOutput = box.querySelector('.terminal-output');
    const expectedOutput = box.getAttribute('data-output') || 'Program executed successfully with exit code 0.';

    if (runBtn && terminal && termOutput) {
      runBtn.addEventListener('click', () => {
        terminal.classList.add('active');
        termOutput.innerHTML = '<span style="color:#93c5fd;">[K2 SIMULATOR] Executing Kotlin 2.x reference snippet...</span>';
        runBtn.disabled = true;
        runBtn.innerHTML = '⏳ Running...';

        setTimeout(() => {
          runBtn.disabled = false;
          runBtn.innerHTML = '▶ Run Again';

          const lines = expectedOutput.split(/\r?\n|\\n/);
          let formattedHtml = '<div style="color:#71717a; margin-bottom:0.4rem;">$ kotlinc -script snippet.kts (simulated runtime output)</div>';
          lines.forEach(line => {
            let color = '#e4e4e7';
            if (line.includes('[SUCCESS]') || line.includes('✔') || line.includes('PASSED') || line.includes('✅')) color = '#4ade80';
            else if (line.includes('[STATE]') || line.includes('-->') || line.includes('->') || line.includes('Emitted:')) color = '#38bdf8';
            else if (line.includes('[LOG]') || line.includes('[AUDIT]') || line.includes('Debug:')) color = '#fbbf24';
            else if (line.includes('[ERROR]') || line.includes('Exception') || line.includes('❌') || line.includes('FAIL')) color = '#f87171';

            formattedHtml += `<div style="color:${color}; margin-bottom:0.2rem;">${escapeHtml(line)}</div>`;
          });

          termOutput.innerHTML = formattedHtml;
        }, 300);
      });
    }
  });
}

/* ==========================================================================
   2. Code Practice IDE & Verification Runner
   ========================================================================== */
function initCodeEditors() {
  const ideWidgets = document.querySelectorAll('.interactive-ide');

  ideWidgets.forEach(widget => {
    const textarea = widget.querySelector('.code-textarea');
    const runBtn = widget.querySelector('.btn-run');
    const hintBtn = widget.querySelector('.btn-hint');
    const solutionBtn = widget.querySelector('.btn-solution');
    const resetBtn = widget.querySelector('.btn-reset');
    const terminal = widget.querySelector('.ide-terminal');
    const termOutput = widget.querySelector('.terminal-output');
    const hintsContainer = widget.querySelector('.hints-container');
    const hintBody = widget.querySelector('.hint-body');
    const solutionContainer = widget.querySelector('.solution-container');
    const loadSolutionBtn = widget.querySelector('.btn-load-solution');
    const solutionCodeEl = widget.querySelector('.solution-code code');

    const defaultCode = textarea.value;
    let hintIndex = 0;
    const hints = JSON.parse(widget.getAttribute('data-hints') || '[]');
    const testCases = JSON.parse(widget.getAttribute('data-tests') || '[]');

    textarea.addEventListener('keydown', (e) => {
      if (e.key === 'Tab') {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.substring(0, start) + '    ' + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 4;
      }
    });

    if (resetBtn) {
      resetBtn.addEventListener('click', () => {
        if (confirm("Reset editor back to starter code?")) {
          textarea.value = defaultCode;
          if (terminal) terminal.classList.remove('active');
        }
      });
    }

    if (hintBtn && hints.length > 0) {
      hintBtn.innerHTML = `💡 Hint (0/${hints.length})`;
      hintBtn.addEventListener('click', () => {
        hintsContainer.classList.add('active');
        const currentNum = hintIndex + 1;
        const levelTag = currentNum === 1 ? 'Concept Guidance' : currentNum === 2 ? 'API Signature' : 'Guided Structure';
        hintBody.innerHTML = `<strong>Hint ${currentNum} of ${hints.length} [${levelTag}]:</strong><br/><span style="display:inline-block; margin-top:0.35rem;">${formatSafeInlineHtml(hints[hintIndex])}</span>`;
        hintIndex = (hintIndex + 1) % hints.length;
        hintBtn.innerHTML = `💡 Hint (${hintIndex === 0 ? hints.length : hintIndex}/${hints.length})`;
      });
    }

    if (solutionBtn) {
      solutionBtn.addEventListener('click', () => {
        const isShown = solutionContainer.classList.toggle('active');
        solutionBtn.textContent = isShown ? "Hide Answer" : "👁️ Reveal Answer";
      });
    }

    if (loadSolutionBtn && solutionCodeEl) {
      loadSolutionBtn.addEventListener('click', () => {
        textarea.value = solutionCodeEl.textContent.trim();
        solutionContainer.classList.remove('active');
        if (solutionBtn) solutionBtn.textContent = "👁️ Reveal Answer";
        textarea.focus();
      });
    }

    if (runBtn) {
      runBtn.addEventListener('click', () => {
        const rawCode = textarea.value.trim();
        terminal.classList.add('active');
        termOutput.innerHTML = '<div class="term-line term-info">[STATIC-VERIFIER] Checking Kotlin 2.x structural patterns (run ./gradlew test in playground/ for full compiler verification)...</div>';

        setTimeout(() => {
          const codeWithoutComments = stripCommentsPreservingStrings(rawCode);

          if (codeWithoutComments.length === 0) {
            termOutput.innerHTML = `
              <div class="term-line term-fail">✖ Verification Error: Source code is empty!</div>
              <div class="term-subtext">↪ Please implement the required function or component before running checks.</div>
            `;
            return;
          }

          let allPassed = true;
          let resultsHtml = '';

          testCases.forEach((test, idx) => {
            let passed = false;
            try {
              const flags = test.flags ? test.flags : 'i';
              const regex = new RegExp(test.pattern, flags.includes('s') ? flags : flags + 's');
              passed = regex.test(codeWithoutComments);
            } catch (err) {
              const fallbackPattern = test.pattern.replace(/\\\./g, '__DOT__').replace(/\./g, '[\\s\\S]').replace(/__DOT__/g, '\\.');
              const fallbackRegex = new RegExp(fallbackPattern, test.flags || 'i');
              passed = fallbackRegex.test(codeWithoutComments);
            }

            if (passed) {
              resultsHtml += `<div class="term-line term-success">✔ Test ${idx + 1}: ${escapeHtml(test.name)} - PASSED</div>`;
            } else {
              allPassed = false;
              resultsHtml += `<div class="term-line term-fail">✖ Test ${idx + 1}: ${escapeHtml(test.name)} - FAILED</div>`;
              if (test.message) {
                resultsHtml += `<div class="term-subtext">↪ Recommendation: ${formatSafeInlineHtml(test.message)}</div>`;
              }
            }
          });

          if (allPassed) {
            resultsHtml += '<div class="term-line term-success" style="margin-top:0.6rem; font-weight:bold; font-size:0.9rem;">🎉 All structural checks passed! Tip: Paste into ./playground/ to compile &amp; test with real K2!</div>';
          } else {
            resultsHtml += '<div class="term-line term-fail" style="margin-top:0.6rem; font-weight:600;">⚠️ Verification failed. Review the suggestions above, click 💡 Hint, or inspect the reference solution.</div>';
          }

          termOutput.innerHTML = resultsHtml;
        }, 220);
      });
    }
  });
}

/* ==========================================================================
   3. Enhanced Quiz Engine: Explanations for Correct AND Wrong + Retry
   ========================================================================== */
function initQuizzes() {
  const quizWidgets = document.querySelectorAll('.interactive-quiz');

  quizWidgets.forEach(widget => {
    const options = widget.querySelectorAll('.quiz-option');
    const breakdownCard = widget.querySelector('.quiz-breakdown-card');
    const breakdownList = widget.querySelector('.breakdown-list');
    const retryBtn = widget.querySelector('.btn-retry-quiz');
    let answered = false;

    const quizData = JSON.parse(widget.getAttribute('data-quiz') || '{}');
    const optionsData = quizData.options || [];

    const card = widget.querySelector('.quiz-card');
    const questionEl = widget.querySelector('.quiz-question');
    if (quizData.code && card && questionEl && !card.querySelector('.quiz-code-block')) {
      const pre = document.createElement('pre');
      pre.className = 'quiz-code-block';
      const codeEl = document.createElement('code');
      codeEl.textContent = quizData.code;
      pre.appendChild(codeEl);
      questionEl.insertAdjacentElement('afterend', pre);
    }

    function resetQuiz() {
      answered = false;
      options.forEach(opt => {
        opt.classList.remove('correct', 'incorrect', 'locked');
        opt.style.cursor = 'pointer';
      });
      if (breakdownCard) breakdownCard.classList.remove('active');
      if (breakdownList) breakdownList.innerHTML = '';
    }

    if (retryBtn) {
      retryBtn.addEventListener('click', resetQuiz);
    }

    options.forEach(opt => {
      opt.addEventListener('click', () => {
        if (answered) return;
        answered = true;

        const selectedIdx = parseInt(opt.getAttribute('data-idx'));

        options.forEach((o, idx) => {
          o.classList.add('locked');
          o.style.cursor = 'default';
          const isThisCorrect = optionsData[idx] && optionsData[idx].isCorrect;
          if (isThisCorrect) {
            o.classList.add('correct');
          } else if (idx === selectedIdx) {
            o.classList.add('incorrect');
          }
        });

        let breakdownHtml = '';
        optionsData.forEach((optData, idx) => {
          const isSelected = (idx === selectedIdx);
          const typeClass = optData.isCorrect ? 'correct' : 'incorrect';
          const label = optData.isCorrect ? '✓ Correct Solution' : '✗ Incorrect Option';
          const selectedMarker = isSelected ? ' <span style="font-weight:700; color:var(--accent-color);">(Your Choice)</span>' : '';

          breakdownHtml += `
            <div class="breakdown-item ${typeClass}">
              <span class="breakdown-tag">[${label}]</span>
              <strong>${formatSafeInlineHtml(optData.text)}</strong>${selectedMarker}<br/>
              <span style="display:inline-block; margin-top:0.35rem; line-height:1.45;">${formatSafeInlineHtml(optData.explanation)}</span>
            </div>
          `;
        });

        if (breakdownList) breakdownList.innerHTML = breakdownHtml;
        if (breakdownCard) breakdownCard.classList.add('active');
      });
    });
  });
}
