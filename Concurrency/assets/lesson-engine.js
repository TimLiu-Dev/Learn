(function (global) {
  'use strict';

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function assertEq(actual, expected, msg) {
    if (actual !== expected) {
      throw new Error(msg || `Expected ${expected}, got ${actual}`);
    }
  }

  function assertDeepEq(actual, expected, msg) {
    const a = JSON.stringify(actual);
    const b = JSON.stringify(expected);
    if (a !== b) {
      throw new Error(msg || `Expected ${b}, got ${a}`);
    }
  }

  async function runTestCases(cases) {
    const results = [];
    for (const tc of cases) {
      try {
        await tc.run();
        results.push({ name: tc.name, passed: true });
      } catch (err) {
        results.push({ name: tc.name, passed: false, error: err && err.message ? err.message : String(err) });
      }
    }
    return results;
  }

  function initLessonPage(config) {
    const STORAGE_KEY = config.storageKey;
    const LABS = config.labs || {};
    const QUIZZES = config.quizzes || [];
    const labIds = Object.keys(LABS).map(Number);
    const totalLabs = labIds.length;
    const hintCounters = {};
    labIds.forEach((id) => { hintCounters[id] = 0; });

    let lessonState = {
      labsPassed: [],
      quizAnswers: {},
      quizCorrect: 0,
      completed: false
    };

    function loadState() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (raw) {
          lessonState = { ...lessonState, ...JSON.parse(raw) };
        }
      } catch (e) {}
    }

    function updateHeaderPills() {
      const pillLabs = document.getElementById('pill-labs');
      const pillQuiz = document.getElementById('pill-quiz');
      const pillDone = document.getElementById('pill-complete');
      const labsCount = lessonState.labsPassed.length;
      const quizCorrect = lessonState.quizCorrect;

      if (pillLabs) {
        pillLabs.textContent = `Labs: ${labsCount} / ${totalLabs}`;
        pillLabs.classList.toggle('done', labsCount === totalLabs && totalLabs > 0);
      }
      if (pillQuiz) {
        pillQuiz.textContent = `Quiz: ${quizCorrect} / ${QUIZZES.length}`;
        pillQuiz.classList.toggle('done', quizCorrect === QUIZZES.length && QUIZZES.length > 0);
      }
      if (pillDone) {
        pillDone.textContent = lessonState.completed ? '✓ Lesson Complete' : 'In Progress';
        pillDone.classList.toggle('done', !!lessonState.completed);
      }
    }

    function saveState() {
      const labsCount = lessonState.labsPassed.length;
      const quizCorrect = Object.keys(lessonState.quizAnswers).reduce((acc, qIdx) => {
        const q = QUIZZES[Number(qIdx)];
        return acc + (q && lessonState.quizAnswers[qIdx] === q.correct ? 1 : 0);
      }, 0);
      lessonState.quizCorrect = quizCorrect;
      lessonState.completed = (labsCount === totalLabs && quizCorrect === QUIZZES.length);
      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(lessonState));
      } catch (e) {}
      updateHeaderPills();
    }

    function markLabStatus(id, passed, label) {
      const card = document.getElementById(`lab-${id}`);
      const badge = document.getElementById(`status-lab-${id}`);
      if (badge) {
        badge.textContent = label;
        badge.classList.remove('pass', 'fail');
        badge.classList.add(passed ? 'pass' : 'fail');
      }
      if (card) card.classList.toggle('passed', passed);
    }

    function initLabs() {
      labIds.forEach((id) => {
        const ta = document.getElementById(`code-lab-${id}`);
        if (!ta) return;
        ta.value = LABS[id].starter;
        ta.addEventListener('keydown', (e) => {
          if (e.key === 'Tab') {
            e.preventDefault();
            const start = ta.selectionStart;
            const end = ta.selectionEnd;
            ta.value = ta.value.substring(0, start) + '  ' + ta.value.substring(end);
            ta.selectionStart = ta.selectionEnd = start + 2;
          } else if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') {
            e.preventDefault();
            runLab(id);
          }
        });
        if (lessonState.labsPassed.includes(id)) {
          markLabStatus(id, true, '✓ Passed');
        }
      });
    }

    async function runLab(id) {
      const lab = LABS[id];
      const ta = document.getElementById(`code-lab-${id}`);
      const resultsEl = document.getElementById(`results-lab-${id}`);
      if (!lab || !ta || !resultsEl) return;

      resultsEl.classList.add('visible');
      resultsEl.innerHTML = '<div style="color:var(--text-muted);">Running tests...</div>';

      try {
        const factory = new Function(
          `"use strict";\n${ta.value}\nif (typeof ${lab.fnName} !== 'function') { throw new Error('Symbol ${lab.fnName} is not defined.'); }\nreturn ${lab.fnName};`
        );
        const userSymbol = factory();
        const outcomes = await lab.tests(userSymbol);
        const allPassed = outcomes.every((o) => o.passed);

        resultsEl.innerHTML = outcomes.map((o) => {
          if (o.passed) {
            return `<div class="test-case test-pass">✓ PASS: ${escapeHtml(o.name)}</div>`;
          }
          return `<div class="test-case test-fail">✗ FAIL: ${escapeHtml(o.name)}<br/>&nbsp;&nbsp;↳ ${escapeHtml(o.error)}</div>`;
        }).join('');

        markLabStatus(id, allPassed, allPassed ? '✓ All Tests Passed' : '✗ Tests Failing');

        if (allPassed && !lessonState.labsPassed.includes(id)) {
          lessonState.labsPassed.push(id);
          saveState();
        }
      } catch (err) {
        resultsEl.innerHTML = `<div class="test-case test-fail">✗ Syntax / Runtime Error: ${escapeHtml(err.message)}</div>`;
        markLabStatus(id, false, '✗ Error');
      }
    }

    function cycleHint(id) {
      const lab = LABS[id];
      const drawer = document.getElementById(`hints-lab-${id}`);
      const counterEl = document.getElementById(`hint-count-${id}`);
      if (!lab || !drawer) return;
      if (hintCounters[id] < lab.hints.length) {
        hintCounters[id]++;
      }
      if (counterEl) counterEl.textContent = hintCounters[id];
      drawer.classList.add('visible');
      drawer.innerHTML = lab.hints.slice(0, hintCounters[id]).map((h, i) =>
        `<div class="hint-item"><strong>Hint ${i + 1}:</strong> ${escapeHtml(h)}</div>`
      ).join('');
    }

    function toggleAnswer(id) {
      const lab = LABS[id];
      const drawer = document.getElementById(`answer-lab-${id}`);
      if (!lab || !drawer) return;
      if (drawer.classList.contains('visible')) {
        drawer.classList.remove('visible');
        return;
      }
      drawer.classList.add('visible');
      const prodCode = lab.javaEquivalent || lab.kotlinEquivalent;
      const prodLabel = lab.productionLabel || (lab.javaEquivalent ? 'Equivalent Production Java / Spring Boot Code:' : 'Equivalent Production Kotlin Code:');
      drawer.innerHTML = `
        <div style="font-weight:700; margin-bottom:8px; color:var(--green);">Final Reference Answer (JS Sandbox):</div>
        <pre>${escapeHtml(lab.solution)}</pre>
        <div style="font-weight:700; margin:14px 0 8px; color:var(--accent);">${escapeHtml(prodLabel)}</div>
        <pre>${escapeHtml(prodCode)}</pre>
      `;
    }

    function resetLab(id) {
      const lab = LABS[id];
      const ta = document.getElementById(`code-lab-${id}`);
      const resultsEl = document.getElementById(`results-lab-${id}`);
      if (ta && lab) ta.value = lab.starter;
      if (resultsEl) resultsEl.classList.remove('visible');
    }

    function renderBreakdown(q, chosen) {
      const isRight = chosen === q.correct;
      const verdict = isRight
        ? `<div class="breakdown-verdict" style="color:var(--green);">✓ Correct! Full breakdown for every option:</div>`
        : `<div class="breakdown-verdict" style="color:var(--rose);">✗ Incorrect. Review why the correct option works and why each wrong option fails:</div>`;
      const rows = q.why.map((text, idx) => {
        const cls = idx === q.correct ? 'breakdown-row is-right' : 'breakdown-row is-wrong';
        return `<div class="${cls}">${escapeHtml(text)}</div>`;
      }).join('');
      return verdict + rows;
    }

    function renderQuizzes() {
      const container = document.getElementById('quiz-container');
      if (!container) return;
      const letters = ['A', 'B', 'C', 'D'];
      container.innerHTML = QUIZZES.map((q, qIdx) => {
        const chosen = lessonState.quizAnswers[qIdx];
        const answered = typeof chosen === 'number';
        return `
          <div class="quiz-card" id="quiz-card-${qIdx}">
            <div class="quiz-qnum">QUESTION ${qIdx + 1} OF ${QUIZZES.length}</div>
            <p class="quiz-stem">${escapeHtml(q.stem)}</p>
            ${q.code ? `<pre>${escapeHtml(q.code)}</pre>` : ''}
            <div class="options-list">
              ${q.options.map((opt, oIdx) => {
                let cls = 'opt-btn';
                if (answered) {
                  if (oIdx === chosen && oIdx === q.correct) cls += ' selected-correct';
                  else if (oIdx === chosen && oIdx !== q.correct) cls += ' selected-wrong';
                  else if (oIdx === q.correct) cls += ' reveal-correct';
                }
                return `
                  <button class="${cls}" ${answered ? 'disabled' : ''} onclick="answerQuiz(${qIdx}, ${oIdx})">
                    <span class="opt-letter">${letters[oIdx]}</span>
                    <span>${escapeHtml(opt)}</span>
                  </button>
                `;
              }).join('')}
            </div>
            <div class="quiz-breakdown ${answered ? 'visible' : ''}">
              ${answered ? renderBreakdown(q, chosen) : ''}
            </div>
          </div>
        `;
      }).join('');
    }

    function answerQuiz(qIdx, oIdx) {
      lessonState.quizAnswers[qIdx] = oIdx;
      saveState();
      renderQuizzes();
    }

    function resetQuizzes() {
      lessonState.quizAnswers = {};
      saveState();
      renderQuizzes();
    }

    loadState();
    initLabs();
    renderQuizzes();
    saveState();

    global.runLab = runLab;
    global.cycleHint = cycleHint;
    global.toggleAnswer = toggleAnswer;
    global.resetLab = resetLab;
    global.answerQuiz = answerQuiz;
    global.resetQuizzes = resetQuizzes;
  }

  global.ConcurrencyEngine = {
    escapeHtml,
    assertEq,
    assertDeepEq,
    runTestCases,
    initLessonPage
  };
  global.escapeHtml = escapeHtml;
  global.assertEq = assertEq;
  global.assertDeepEq = assertDeepEq;
  global.runTestCases = runTestCases;
})(window);
