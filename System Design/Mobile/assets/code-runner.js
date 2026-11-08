// Mobile System Design 2027 - In-Browser Interactive Code Practice Runner
document.addEventListener('DOMContentLoaded', () => {
  const playgrounds = document.querySelectorAll('.playground-box');

  playgrounds.forEach((box, index) => {
    const boxId = box.dataset.exerciseId || `exercise-${index}`;
    const editor = box.querySelector('.code-editor');
    const runBtn = box.querySelector('.btn-run');
    const hintBtn = box.querySelector('.btn-hint');
    const solutionBtn = box.querySelector('.btn-solution');
    const resetBtn = box.querySelector('.btn-reset');
    const consoleOutput = box.querySelector('.console-output');
    const hintBox = box.querySelector('.hint-box');
    const solutionBox = box.querySelector('.solution-box');

    // Enable Tab indentation inside textarea
    if (editor) {
      editor.addEventListener('keydown', (e) => {
        if (e.key === 'Tab') {
          e.preventDefault();
          const start = editor.selectionStart;
          const end = editor.selectionEnd;
          editor.value = editor.value.substring(0, start) + "  " + editor.value.substring(end);
          editor.selectionStart = editor.selectionEnd = start + 2;
        }
      });

      // Restore user code from storage
      const savedCode = localStorage.getItem(`code_${boxId}`);
      if (savedCode) {
        editor.value = savedCode;
      }

      editor.addEventListener('input', () => {
        localStorage.setItem(`code_${boxId}`, editor.value);
      });
    }

    // Hint toggle
    let hintLevel = 0;
    if (hintBtn && hintBox) {
      hintBtn.addEventListener('click', () => {
        const hints = hintBox.querySelectorAll('.hint-step');
        if (hints.length > 0) {
          hintBox.classList.add('show');
          if (hintLevel < hints.length) {
            hints[hintLevel].style.display = 'block';
            hintLevel++;
            hintBtn.textContent = hintLevel < hints.length ? `Next Hint (${hintLevel + 1}/${hints.length})` : 'All Hints Shown';
          }
        } else {
          hintBox.classList.toggle('show');
        }
      });
    }

    // Solution toggle & apply
    if (solutionBtn && solutionBox) {
      solutionBtn.addEventListener('click', () => {
        const isShown = solutionBox.classList.toggle('show');
        solutionBtn.textContent = isShown ? 'Hide Solution' : 'Reveal Solution';
      });

      const copySolutionBtn = solutionBox.querySelector('.btn-load-solution');
      if (copySolutionBtn) {
        copySolutionBtn.addEventListener('click', () => {
          const solutionCode = solutionBox.querySelector('pre code').textContent.trim();
          editor.value = solutionCode;
          localStorage.setItem(`code_${boxId}`, editor.value);
          logToConsole(consoleOutput, "Loaded canonical solution into editor. Click 'Run & Check' to verify.", "info");
        });
      }
    }

    // Reset starter
    if (resetBtn && editor) {
      resetBtn.addEventListener('click', () => {
        const starter = box.dataset.starterCode;
        if (starter) {
          editor.value = starter.trim();
          localStorage.setItem(`code_${boxId}`, editor.value);
          logToConsole(consoleOutput, "Editor reset to starter template.", "info");
        }
      });
    }

    // Execution & Verification
    if (runBtn && editor && consoleOutput) {
      runBtn.addEventListener('click', () => {
        executeExercise(box, editor.value, consoleOutput);
      });
    }
  });

  function logToConsole(targetEl, message, type = 'normal') {
    targetEl.className = 'console-output';
    if (type === 'success') targetEl.classList.add('success');
    if (type === 'failure') targetEl.classList.add('failure');
    targetEl.textContent = message;
  }

  function executeExercise(box, userCode, consoleEl) {
    const testSuiteFnName = box.dataset.testRunner;
    logToConsole(consoleEl, "Executing test suite in isolated runtime...\n");

    const logs = [];
    const customConsole = {
      log: (...args) => logs.push(args.map(a => typeof a === 'object' ? JSON.stringify(a) : String(a)).join(' ')),
      warn: (...args) => logs.push("[WARN] " + args.join(' ')),
      error: (...args) => logs.push("[ERROR] " + args.join(' '))
    };

    try {
      // Evaluate user code and test runner
      const testRunnerFn = window[testSuiteFnName];
      if (typeof testRunnerFn !== 'function') {
        throw new Error(`Test runner '${testSuiteFnName}' is not defined.`);
      }

      // Execute in sandbox function with custom console
      const userEnv = new Function('console', `${userCode}\nreturn this;`);
      const context = {};
      userEnv.call(context, customConsole);

      // Run assertion tests against the created context
      const testResults = testRunnerFn(context, customConsole);

      let output = "";
      if (logs.length > 0) {
        output += "[Logs]\n" + logs.join("\n") + "\n\n";
      }

      if (testResults.passed) {
        output += `✓ ALL TESTS PASSED (${testResults.total} assertions)\n`;
        output += `Summary: ${testResults.message || 'Architectural invariants satisfied!'}`;
        logToConsole(consoleEl, output, 'success');
      } else {
        output += `✗ TEST SUITE FAILED: ${testResults.failedAssertion}\n`;
        output += `Detail: ${testResults.details || ''}\n`;
        output += `Hint: Inspect edge-cases or verify method contracts.`;
        logToConsole(consoleEl, output, 'failure');
      }
    } catch (err) {
      let output = "";
      if (logs.length > 0) {
        output += "[Logs]\n" + logs.join("\n") + "\n\n";
      }
      output += `RUNTIME ERROR: ${err.message}\n${err.stack || ''}`;
      logToConsole(consoleEl, output, 'failure');
    }
  }
});
