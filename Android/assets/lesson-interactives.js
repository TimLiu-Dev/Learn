/**
 * Modern Android Curriculum - Universal Interactive Engine v2
 * Built for Junior-to-Senior Android Mastery (2026/2027 Standards)
 */

document.addEventListener('DOMContentLoaded', () => {
  initRunnableExamples();
  initCodeEditors();
  initQuizzes();
});

/* ==========================================================================
   1. Runnable Code Examples Runner
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
        termOutput.innerHTML = '<span style="color:#93c5fd;">[K2 COMPILER] Compiling and executing Kotlin 2.x runtime snippet...</span>';
        runBtn.disabled = true;
        runBtn.innerHTML = '⏳ Running...';

        setTimeout(() => {
          runBtn.disabled = false;
          runBtn.innerHTML = '▶ Run Again';

          // Format simulated terminal output lines
          const lines = expectedOutput.split('\\n');
          let formattedHtml = '<div style="color:#71717a; margin-bottom:0.4rem;">$ kotlinc -script snippet.kts</div>';
          lines.forEach(line => {
            let color = '#e4e4e7';
            if (line.includes('[SUCCESS]') || line.includes('✔') || line.includes('PASSED')) color = '#4ade80';
            else if (line.includes('[STATE]') || line.includes('-->') || line.includes('Emitted:')) color = '#38bdf8';
            else if (line.includes('[LOG]') || line.includes('Debug:')) color = '#fbbf24';
            else if (line.includes('[ERROR]') || line.includes('Exception')) color = '#f87171';

            formattedHtml += `<div style="color:${color}; margin-bottom:0.2rem;">${line}</div>`;
          });

          termOutput.innerHTML = formattedHtml;
        }, 350);
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

    // Tab key handling
    textarea.addEventListener('keydown', (e) => {
      if (e.key === 'Tab') {
        e.preventDefault();
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        textarea.value = textarea.value.substring(0, start) + '    ' + textarea.value.substring(end);
        textarea.selectionStart = textarea.selectionEnd = start + 4;
      }
    });

    // Reset code button
    if (resetBtn) {
      resetBtn.addEventListener('click', () => {
        if (confirm("Reset editor back to starter code?")) {
          textarea.value = defaultCode;
          if (terminal) terminal.classList.remove('active');
        }
      });
    }

    // Hint button (progressive 3-level cycling)
    if (hintBtn && hints.length > 0) {
      hintBtn.innerHTML = `💡 Hint (0/${hints.length})`;
      hintBtn.addEventListener('click', () => {
        hintsContainer.classList.add('active');
        const currentNum = hintIndex + 1;
        const levelTag = currentNum === 1 ? 'Concept Guidance' : currentNum === 2 ? 'API Signature' : 'Guided Structure';
        hintBody.innerHTML = `<strong>Hint ${currentNum} of ${hints.length} [${levelTag}]:</strong><br/><span style="display:inline-block; margin-top:0.35rem;">${hints[hintIndex]}</span>`;
        hintIndex = (hintIndex + 1) % hints.length;
        hintBtn.innerHTML = `💡 Hint (${hintIndex === 0 ? hints.length : hintIndex}/${hints.length})`;
      });
    }

    // Solution button
    if (solutionBtn) {
      solutionBtn.addEventListener('click', () => {
        const isShown = solutionContainer.classList.toggle('active');
        solutionBtn.textContent = isShown ? "Hide Answer" : "👁️ Reveal Answer";
      });
    }

    // Load solution directly into editor
    if (loadSolutionBtn && solutionCodeEl) {
      loadSolutionBtn.addEventListener('click', () => {
        textarea.value = solutionCodeEl.textContent.trim();
        solutionContainer.classList.remove('active');
        if (solutionBtn) solutionBtn.textContent = "👁️ Reveal Answer";
        textarea.focus();
      });
    }

    // Run & Check test verification runner
    if (runBtn) {
      runBtn.addEventListener('click', () => {
        const rawCode = textarea.value.trim();
        terminal.classList.add('active');
        termOutput.innerHTML = '<div class="term-line term-info">[TEST-HARNESS] Parsing and verifying Kotlin 2.x implementation...</div>';

        setTimeout(() => {
          // 1. Strip comments before validation to eliminate false positives on // TODO: comments!
          const codeWithoutComments = rawCode
            .replace(/\/\*[\s\S]*?\*\//g, '')  // block comments
            .replace(/\/\/[^\n\r]*/g, '')      // single-line comments
            .trim();

          if (codeWithoutComments.length === 0) {
            termOutput.innerHTML = `
              <div class="term-line term-fail">✖ Compilation Error: Source code is empty!</div>
              <div class="term-subtext">↪ Please implement the required function or component before running checks.</div>
            `;
            return;
          }

          let allPassed = true;
          let resultsHtml = '';

          testCases.forEach((test, idx) => {
            let passed = false;
            try {
              // Convert pattern to RegExp with 's' flag (dotAll) support
              const flags = test.flags ? test.flags : 'i';
              // Check against code without comments
              const regex = new RegExp(test.pattern, flags.includes('s') ? flags : flags + 's');
              passed = regex.test(codeWithoutComments);
            } catch (err) {
              // Fallback for older JS engines without 's' flag: replace . with [\s\S]
              const fallbackPattern = test.pattern.replace(/\\\./g, '__DOT__').replace(/\./g, '[\\s\\S]').replace(/__DOT__/g, '\\.');
              const fallbackRegex = new RegExp(fallbackPattern, test.flags || 'i');
              passed = fallbackRegex.test(codeWithoutComments);
            }

            if (passed) {
              resultsHtml += `<div class="term-line term-success">✔ Test ${idx + 1}: ${test.name} - PASSED</div>`;
            } else {
              allPassed = false;
              resultsHtml += `<div class="term-line term-fail">✖ Test ${idx + 1}: ${test.name} - FAILED</div>`;
              if (test.message) {
                resultsHtml += `<div class="term-subtext">↪ Recommendation: ${test.message}</div>`;
              }
            }
          });

          if (allPassed) {
            resultsHtml += '<div class="term-line term-success" style="margin-top:0.6rem; font-weight:bold; font-size:0.9rem;">🎉 All verification checks passed! Outstanding work!</div>';
          } else {
            resultsHtml += '<div class="term-line term-fail" style="margin-top:0.6rem; font-weight:600;">⚠️ Verification failed. Review the suggestions above, click 💡 Hint, or inspect the solution.</div>';
          }

          termOutput.innerHTML = resultsHtml;
        }, 250);
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

        // Highlight choices
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

        // Build comprehensive breakdown explaining WHY each answer is right/wrong
        let breakdownHtml = '';
        optionsData.forEach((optData, idx) => {
          const isSelected = (idx === selectedIdx);
          const typeClass = optData.isCorrect ? 'correct' : 'incorrect';
          const label = optData.isCorrect ? '✓ Correct Solution' : '✗ Incorrect Option';
          const selectedMarker = isSelected ? ' <span style="font-weight:700; color:var(--accent-color);">(Your Choice)</span>' : '';

          breakdownHtml += `
            <div class="breakdown-item ${typeClass}">
              <span class="breakdown-tag">[${label}]</span>
              <strong>${optData.text}</strong>${selectedMarker}<br/>
              <span style="display:inline-block; margin-top:0.35rem; line-height:1.45;">${optData.explanation}</span>
            </div>
          `;
        });

        if (breakdownList) breakdownList.innerHTML = breakdownHtml;
        if (breakdownCard) breakdownCard.classList.add('active');
      });
    });
  });
}
