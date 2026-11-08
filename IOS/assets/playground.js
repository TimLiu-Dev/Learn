/**
 * Interactive In-Browser Swift Practice Playground
 * Connects to local serve.py (http://127.0.0.1:8000/api/run) for real Swift 6 compiler execution.
 * Includes progressive hints, reset functionality, and offline fallback.
 */
document.addEventListener('DOMContentLoaded', () => {
  const playgrounds = document.querySelectorAll('.practice-playground');

  playgrounds.forEach((pg, pgIdx) => {
    const editor = pg.querySelector('.code-editor');
    const runBtn = pg.querySelector('.run-code-btn');
    const resetBtn = pg.querySelector('.reset-code-btn');
    const terminalOutput = pg.querySelector('.terminal-output');
    const statusIndicator = pg.querySelector('.status-indicator');
    const hintButtons = pg.querySelectorAll('.hint-btn');
    const hintItems = pg.querySelectorAll('.hint-item');
    const exercisePath = pg.getAttribute('data-exercise-path') || '';

    if (!editor) return;

    // Cache initial starter code
    const initialCode = editor.value;

    // Support Tab key inside the editor
    editor.addEventListener('keydown', (e) => {
      if (e.key === 'Tab') {
        e.preventDefault();
        const start = editor.selectionStart;
        const end = editor.selectionEnd;
        editor.value = editor.value.substring(0, start) + '    ' + editor.value.substring(end);
        editor.selectionStart = editor.selectionEnd = start + 4;
      }
    });

    // Reset code to initial state
    if (resetBtn) {
      resetBtn.addEventListener('click', () => {
        if (confirm('Reset your code to the original starter challenge?')) {
          editor.value = initialCode;
          if (statusIndicator) {
            statusIndicator.className = 'status-indicator waiting';
            statusIndicator.innerText = 'Reset to Starter';
          }
          if (terminalOutput) {
            terminalOutput.className = 'terminal-output';
            terminalOutput.innerText = 'Code reset. Click "▶ Run & Check" to compile and run tests...';
          }
        }
      });
    }

    // Progressive Hints toggle
    hintButtons.forEach((btn) => {
      btn.addEventListener('click', () => {
        const hintNum = btn.getAttribute('data-hint');
        const targetHint = pg.querySelector(`.hint-item.hint-${hintNum}`);

        if (targetHint) {
          const isCurrentlyVisible = targetHint.style.display === 'block';
          if (isCurrentlyVisible) {
            targetHint.style.display = 'none';
            btn.classList.remove('active');
          } else {
            targetHint.style.display = 'block';
            btn.classList.add('active');
          }
        }
      });
    });

    // Run & Check execution
    if (runBtn) {
      runBtn.addEventListener('click', async () => {
        const userCode = editor.value;

        // UI state update
        runBtn.disabled = true;
        const originalBtnText = runBtn.innerHTML;
        runBtn.innerHTML = '⏳ Compiling...';
        if (statusIndicator) {
          statusIndicator.className = 'status-indicator running';
          statusIndicator.innerText = 'Running Swift 6...';
        }
        if (terminalOutput) {
          terminalOutput.className = 'terminal-output';
          terminalOutput.innerText = 'Compiling and running assertions against Swift 6 compiler...\n';
        }

        // Try local compiler server first
        try {
          const controller = new AbortController();
          const timeoutId = setTimeout(() => controller.abort(), 14000);

          const response = await fetch('http://127.0.0.1:8000/api/run', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ code: userCode }),
            signal: controller.signal
          });
          clearTimeout(timeoutId);

          if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
          }

          const result = await response.json();

          if (result.success) {
            if (statusIndicator) {
              statusIndicator.className = 'status-indicator success';
              statusIndicator.innerText = '✅ Tests Passed';
            }
            if (terminalOutput) {
              terminalOutput.className = 'terminal-output success';
              terminalOutput.innerText = result.output;
            }
          } else {
            if (statusIndicator) {
              statusIndicator.className = 'status-indicator error';
              statusIndicator.innerText = '❌ Failed / Compilation Error';
            }
            if (terminalOutput) {
              terminalOutput.className = 'terminal-output error';
              terminalOutput.innerText = result.output;
            }
          }
        } catch (err) {
          // Fallback if local server is unreachable
          handleOfflineExecution(userCode, terminalOutput, statusIndicator, exercisePath);
        } finally {
          runBtn.disabled = false;
          runBtn.innerHTML = originalBtnText;
        }
      });
    }
  });

  function handleOfflineExecution(userCode, terminalOutput, statusIndicator, exercisePath) {
    let msg = `⚠️ Local Swift Compiler Server not connected.\n\n`;
    msg += `To enable live in-browser Swift 6 compilation:\n`;
    msg += `  1. In your terminal, run: python3 serve.py\n`;
    msg += `  2. Open lessons via: http://localhost:8000/lessons/...\n\n`;
    msg += `Or verify this exercise directly in your terminal:\n`;
    msg += `  swift ${exercisePath || 'exercises/.../practice.swift'}\n\n`;
    msg += `─────────────────────────────────────────────────────────────\n`;
    msg += `🔍 Quick Static Code Inspection:\n`;

    const hasTodos = /TODO/i.test(userCode);
    const hasFatalError = /fatalError/i.test(userCode);

    if (hasTodos || hasFatalError) {
      if (statusIndicator) {
        statusIndicator.className = 'status-indicator warning';
        statusIndicator.innerText = '⚠️ Incomplete';
      }
      msg += `• Detected unfinished TODOs or fatalError stubs in your code.\n`;
      msg += `• Make sure to replace all TODO sections before running assertions!`;
    } else {
      if (statusIndicator) {
        statusIndicator.className = 'status-indicator waiting';
        statusIndicator.innerText = 'Ready in Terminal';
      }
      msg += `• No obvious TODO markers found!\n`;
      msg += `• Execute in terminal to verify assertions with the Swift 6 compiler.`;
    }

    if (terminalOutput) {
      terminalOutput.className = 'terminal-output warning';
      terminalOutput.innerText = msg;
    }
  }
});
