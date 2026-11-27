/* Shared Interactive Components for CS Foundations & LeetCode Prerequisites
   Provides:
   1. Polyglot Code Tab Switcher (switchTab)
   2. Standard Retrieval Quiz Engine (checkAnswer + resetQuizCard)
   3. Two-Part Code Complexity Quiz Engine (selectComplexityChoice, checkComplexityQuiz, resetComplexityQuiz)
   4. Spot & Fix the Bug Code Drill Engine (selectBugLine, selectBugFix, checkBugDrill, resetBugDrill)
   5. Active Fill-in-the-Code Evaluator (checkCodeFill, resetCodeFill, revealCodeFill)
   6. Active Fill-in-the-Trace-Table Evaluator (checkTraceTable, revealTraceTable, resetTraceTable)
   7. Hint Toggle Helper (toggleHint)
   8. Shared Progress Sync (localStorage), Drill Tracker & /teach Prompt Copy Bar
*/

const PROGRESS_STORAGE_KEY = 'lc_prereq_checklist_v2';

function readProgressMap() {
  try {
    return JSON.parse(localStorage.getItem(PROGRESS_STORAGE_KEY) || '{}');
  } catch (e) {
    return {};
  }
}

function writeProgressMap(map) {
  try {
    localStorage.setItem(PROGRESS_STORAGE_KEY, JSON.stringify(map));
  } catch (e) {
    console.warn('Could not persist progress', e);
  }
}

function switchTab(evt, tabId) {
  const container = evt.currentTarget.closest('.code-tabs');
  if (!container) return;
  container.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
  container.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('active'));
  evt.currentTarget.classList.add('active');
  const pane = container.querySelector('#' + tabId);
  if (pane) pane.classList.add('active');
}

function checkAnswer(qKey, selectedIdx) {
  const cardId = typeof qKey === 'number' ? 'q' + qKey : String(qKey);
  const card = document.getElementById(cardId);
  if (!card) return;

  const buttons = card.querySelectorAll('.quiz-btn');
  const fb = card.querySelector('.quiz-feedback') || document.getElementById('fb' + qKey);

  let correctIdx = card.dataset.correct !== undefined ? parseInt(card.dataset.correct, 10) : undefined;
  if (correctIdx === undefined && typeof correctAnswers !== 'undefined') {
    correctIdx = correctAnswers[qKey];
  }

  let explanation = card.dataset.explanation || '';
  if (!explanation && typeof explanations !== 'undefined') {
    explanation = explanations[qKey] || '';
  }

  const isCorrect = selectedIdx === correctIdx;

  buttons.forEach((btn, i) => {
    btn.classList.remove('correct', 'incorrect');
    if (i === correctIdx) {
      btn.classList.add('correct');
    } else if (i === selectedIdx) {
      btn.classList.add('incorrect');
    }
  });

  if (fb) {
    fb.style.display = 'block';
    if (isCorrect) {
      card.dataset.solved = '1';
      fb.innerHTML = `<span style="color: var(--success); font-weight: 600;">&check; Correct!</span> ${explanation}`;
    } else {
      fb.innerHTML = `<span style="color: #ef4444; font-weight: 600;">&cross; Not quite.</span> ${explanation}`;
    }
  }
  updateLessonDrillCounter();
}

/* 3. Two-Part Code Complexity Quiz ("What's the Time & Auxiliary Space Big O?") */
function selectComplexityChoice(quizId, dimension, idx) {
  const card = document.getElementById(quizId);
  if (!card) return;
  const group = card.querySelector(`[data-dim="${dimension}"]`);
  if (!group) return;
  group.querySelectorAll('.quiz-btn').forEach((btn, i) => {
    btn.classList.remove('selected', 'correct', 'incorrect');
    if (i === idx) btn.classList.add('selected');
  });
  card.dataset[dimension + 'Selected'] = String(idx);
}

function checkComplexityQuiz(quizId) {
  const card = document.getElementById(quizId);
  if (!card) return;
  const expectedTime = parseInt(card.dataset.timeCorrect, 10);
  const expectedSpace = parseInt(card.dataset.spaceCorrect, 10);
  const selTime = card.dataset.timeSelected !== undefined ? parseInt(card.dataset.timeSelected, 10) : -1;
  const selSpace = card.dataset.spaceSelected !== undefined ? parseInt(card.dataset.spaceSelected, 10) : -1;
  const fb = card.querySelector('.quiz-feedback');

  if (selTime === -1 || selSpace === -1) {
    if (fb) {
      fb.style.display = 'block';
      fb.innerHTML = `<span style="color: var(--warning); font-weight: 600;">Select both Time Complexity and Auxiliary Space Complexity before checking.</span>`;
    }
    return;
  }

  const timeBtns = card.querySelectorAll('[data-dim="time"] .quiz-btn');
  const spaceBtns = card.querySelectorAll('[data-dim="space"] .quiz-btn');

  timeBtns.forEach((btn, i) => {
    btn.classList.remove('selected', 'correct', 'incorrect');
    if (i === expectedTime) btn.classList.add('correct');
    else if (i === selTime) btn.classList.add('incorrect');
  });

  spaceBtns.forEach((btn, i) => {
    btn.classList.remove('selected', 'correct', 'incorrect');
    if (i === expectedSpace) btn.classList.add('correct');
    else if (i === selSpace) btn.classList.add('incorrect');
  });

  const bothOk = selTime === expectedTime && selSpace === expectedSpace;
  if (fb) {
    fb.style.display = 'block';
    const exp = card.dataset.explanation || '';
    if (bothOk) {
      card.dataset.solved = '1';
      fb.innerHTML = `<span style="color: var(--success); font-weight: 600;">&check; Spot on! Both Time and Auxiliary Space match.</span> ${exp}`;
    } else {
      fb.innerHTML = `<span style="color: #ef4444; font-weight: 600;">&cross; Check the highlighted bounds above.</span> ${exp}`;
    }
  }
  updateLessonDrillCounter();
}

function resetComplexityQuiz(quizId) {
  const card = document.getElementById(quizId);
  if (!card) return;
  delete card.dataset.timeSelected;
  delete card.dataset.spaceSelected;
  delete card.dataset.solved;
  card.querySelectorAll('.quiz-btn').forEach(btn => btn.classList.remove('selected', 'correct', 'incorrect'));
  const fb = card.querySelector('.quiz-feedback');
  if (fb) fb.style.display = 'none';
  updateLessonDrillCounter();
}

/* 4. Spot & Fix the Bug Code Drill */
function selectBugLine(drillId, lineNum) {
  const card = document.getElementById(drillId);
  if (!card) return;
  card.querySelectorAll('.bug-line').forEach(row => {
    row.classList.remove('selected', 'correct-line', 'wrong-line');
    if (parseInt(row.dataset.line, 10) === lineNum) {
      row.classList.add('selected');
    }
  });
  card.dataset.selectedLine = String(lineNum);
}

function selectBugFix(drillId, idx) {
  const card = document.getElementById(drillId);
  if (!card) return;
  card.querySelectorAll('.fix-options .quiz-btn').forEach((btn, i) => {
    btn.classList.remove('selected', 'correct', 'incorrect');
    if (i === idx) btn.classList.add('selected');
  });
  card.dataset.selectedFix = String(idx);
}

function checkBugDrill(drillId) {
  const card = document.getElementById(drillId);
  if (!card) return;
  const expectedLine = parseInt(card.dataset.bugLine, 10);
  const expectedFix = parseInt(card.dataset.fixCorrect, 10);
  const selLine = card.dataset.selectedLine !== undefined ? parseInt(card.dataset.selectedLine, 10) : -1;
  const selFix = card.dataset.selectedFix !== undefined ? parseInt(card.dataset.selectedFix, 10) : -1;
  const fb = card.querySelector('.quiz-feedback');

  if (selLine === -1 || selFix === -1) {
    if (fb) {
      fb.style.display = 'block';
      fb.innerHTML = `<span style="color: var(--warning); font-weight: 600;">Click the buggy code line AND select the fix option first.</span>`;
    }
    return;
  }

  card.querySelectorAll('.bug-line').forEach(row => {
    const ln = parseInt(row.dataset.line, 10);
    row.classList.remove('selected', 'correct-line', 'wrong-line');
    if (ln === expectedLine) row.classList.add('correct-line');
    else if (ln === selLine) row.classList.add('wrong-line');
  });

  card.querySelectorAll('.fix-options .quiz-btn').forEach((btn, i) => {
    btn.classList.remove('selected', 'correct', 'incorrect');
    if (i === expectedFix) btn.classList.add('correct');
    else if (i === selFix) btn.classList.add('incorrect');
  });

  const ok = selLine === expectedLine && selFix === expectedFix;
  if (fb) {
    fb.style.display = 'block';
    const exp = card.dataset.explanation || '';
    if (ok) {
      card.dataset.solved = '1';
      fb.innerHTML = `<span style="color: var(--success); font-weight: 600;">&check; Bug isolated and patched! (Line ${expectedLine})</span> ${exp}`;
    } else {
      fb.innerHTML = `<span style="color: #ef4444; font-weight: 600;">&cross; Review Line ${expectedLine} and the highlighted fix.</span> ${exp}`;
    }
  }
  updateLessonDrillCounter();
}

function resetBugDrill(drillId) {
  const card = document.getElementById(drillId);
  if (!card) return;
  delete card.dataset.selectedLine;
  delete card.dataset.selectedFix;
  delete card.dataset.solved;
  card.querySelectorAll('.bug-line').forEach(r => r.classList.remove('selected', 'correct-line', 'wrong-line'));
  card.querySelectorAll('.fix-options .quiz-btn').forEach(b => b.classList.remove('selected', 'correct', 'incorrect'));
  const fb = card.querySelector('.quiz-feedback');
  if (fb) fb.style.display = 'none';
  updateLessonDrillCounter();
}

/* 5. Active Fill-in-the-Code Evaluator */
function normalizeCode(str) {
  return String(str || '')
    .trim()
    .replace(/;$/, '')
    .replace(/\s+/g, ' ')
    .replace(/\s*([^a-zA-Z0-9_\s])\s*/g, '$1');
}

function checkCodeFill(labId) {
  const card = document.getElementById(labId);
  if (!card) return;
  const blanks = card.querySelectorAll('.code-blank');
  const fb = card.querySelector('.quiz-feedback');
  let allPass = true;
  let passedCount = 0;

  blanks.forEach(input => {
    const accepted = (input.dataset.answers || '')
      .split('|')
      .map(normalizeCode)
      .filter(Boolean);
    const userVal = normalizeCode(input.value);
    const ok = accepted.includes(userVal);
    input.classList.remove('pass', 'fail');
    input.classList.add(ok ? 'pass' : 'fail');
    if (ok) passedCount++;
    else allPass = false;
  });

  if (fb) {
    fb.style.display = 'block';
    const successMsg = card.dataset.success || 'All test cases passed!';
    const failHint = card.dataset.fail || 'One or more blanks do not match expected logic. Check red fields or toggle Hint.';
    if (allPass) {
      card.dataset.solved = '1';
      fb.innerHTML = `<span style="color: var(--success); font-weight: 600;">&check; ACCEPTED (${passedCount}/${blanks.length} blanks verified)!</span> ${successMsg}`;
    } else {
      fb.innerHTML = `<span style="color: #ef4444; font-weight: 600;">&cross; Wrong Answer / Compile Check (${passedCount}/${blanks.length} blanks valid).</span> ${failHint}`;
    }
  }
  updateLessonDrillCounter();
}

function resetCodeFill(labId) {
  const card = document.getElementById(labId);
  if (!card) return;
  delete card.dataset.solved;
  card.querySelectorAll('.code-blank').forEach(input => {
    input.value = '';
    input.classList.remove('pass', 'fail');
  });
  const fb = card.querySelector('.quiz-feedback');
  if (fb) fb.style.display = 'none';
  updateLessonDrillCounter();
}

function decodeHtmlEntityText(str) {
  const txt = document.createElement('textarea');
  txt.innerHTML = str;
  return txt.value;
}

function revealCodeFill(labId) {
  const card = document.getElementById(labId);
  if (!card) return;
  card.querySelectorAll('.code-blank').forEach(input => {
    const first = (input.dataset.answers || '').split('|')[0] || '';
    input.value = decodeHtmlEntityText(first);
    input.classList.remove('fail');
    input.classList.add('pass');
  });
  checkCodeFill(labId);
}

/* 6. Active Fill-in-the-Trace-Table Evaluator */
function normalizeTrace(str) {
  return String(str || '')
    .trim()
    .toLowerCase()
    .replace(/\s+/g, '');
}

function checkTraceTable(tableId) {
  const card = document.getElementById(tableId);
  if (!card) return;
  const inputs = card.querySelectorAll('.trace-input');
  const fb = card.querySelector('.quiz-feedback');
  let passed = 0;

  inputs.forEach(inp => {
    const accepted = (inp.dataset.answers || '')
      .split('|')
      .map(normalizeTrace)
      .filter(Boolean);
    const val = normalizeTrace(inp.value);
    const ok = accepted.includes(val);
    inp.classList.remove('pass', 'fail');
    inp.classList.add(ok ? 'pass' : 'fail');
    if (ok) passed++;
  });

  if (fb) {
    fb.style.display = 'block';
    if (passed === inputs.length) {
      card.dataset.solved = '1';
      fb.innerHTML = `<span style="color: var(--success); font-weight: 600;">&check; Perfect Dry-Run! (${passed}/${inputs.length} state cells verified).</span> ${card.dataset.explanation || ''}`;
    } else {
      fb.innerHTML = `<span style="color: #ef4444; font-weight: 600;">&cross; ${passed}/${inputs.length} cells match.</span> Re-trace the red cells step-by-step before checking again.`;
    }
  }
  updateLessonDrillCounter();
}

function revealTraceTable(tableId) {
  const card = document.getElementById(tableId);
  if (!card) return;
  card.querySelectorAll('.trace-input').forEach(inp => {
    const first = (inp.dataset.answers || '').split('|')[0] || '';
    inp.value = first;
    inp.classList.remove('fail');
    inp.classList.add('pass');
  });
  checkTraceTable(tableId);
}

function resetTraceTable(tableId) {
  const card = document.getElementById(tableId);
  if (!card) return;
  delete card.dataset.solved;
  card.querySelectorAll('.trace-input').forEach(inp => {
    inp.value = '';
    inp.classList.remove('pass', 'fail');
  });
  const fb = card.querySelector('.quiz-feedback');
  if (fb) fb.style.display = 'none';
  updateLessonDrillCounter();
}

function toggleHint(hintId) {
  const el = document.getElementById(hintId);
  if (!el) return;
  el.style.display = (el.style.display === 'block') ? 'none' : 'block';
}

/* 8. Spaced-Review Reset Enhancer, Drill Counter & Lesson Completion Bar */
function getCurrentLessonNum() {
  const path = (typeof window !== 'undefined' && window.location && window.location.pathname) || '';
  const m = path.match(/\/lessons\/00(\d{2})-/);
  return m ? m[1] : null;
}

function updateLessonDrillCounter() {
  const counterEl = document.getElementById('lesson-drill-counter');
  if (!counterEl) return;
  const allCards = document.querySelectorAll('.practice-card, .quiz-card');
  let solved = 0;
  allCards.forEach(c => { if (c.dataset.solved === '1') solved++; });
  counterEl.innerText = `${solved} / ${allCards.length} drills verified in this session`;
}

function mountSpacedReviewButtons() {
  document.querySelectorAll('.practice-card').forEach(card => {
    if (!card.id) return;
    if (card.dataset.timeCorrect !== undefined) {
      const btnWrap = card.querySelector('button.btn-action')?.parentElement;
      if (btnWrap && !btnWrap.querySelector('.auto-reset-btn')) {
        const b = document.createElement('button');
        b.className = 'btn-secondary auto-reset-btn';
        b.style.marginLeft = '0.5rem';
        b.innerText = 'Reset';
        b.onclick = () => resetComplexityQuiz(card.id);
        btnWrap.appendChild(b);
      }
    } else if (card.dataset.bugLine !== undefined) {
      const btnWrap = card.querySelector('button.btn-action')?.parentElement;
      if (btnWrap && !btnWrap.querySelector('.auto-reset-btn')) {
        const b = document.createElement('button');
        b.className = 'btn-secondary auto-reset-btn';
        b.style.marginLeft = '0.5rem';
        b.innerText = 'Reset';
        b.onclick = () => resetBugDrill(card.id);
        btnWrap.appendChild(b);
      }
    } else if (card.querySelector('.trace-input')) {
      const btnWrap = card.querySelector('button.btn-action')?.parentElement;
      if (btnWrap && !btnWrap.querySelector('.auto-reset-btn')) {
        const b = document.createElement('button');
        b.className = 'btn-secondary auto-reset-btn';
        b.innerText = 'Reset';
        b.onclick = () => resetTraceTable(card.id);
        btnWrap.appendChild(b);
      }
    }
  });
}

function mountLessonCheckinBar() {
  const lessonNum = getCurrentLessonNum();
  if (!lessonNum) return;
  const footer = document.querySelector('footer');
  if (!footer) return;

  const checkId = 'c' + lessonNum;
  const bar = document.createElement('div');
  bar.className = 'lesson-checkin-bar';
  bar.id = 'lesson-checkin-bar';

  function renderBar() {
    const state = readProgressMap();
    const done = !!state[checkId];
    bar.classList.toggle('is-complete', done);
    const h1Text = (document.querySelector('h1')?.innerText || '').trim();
    bar.innerHTML = `
      <div>
        <div style="font-weight: 700; font-size: 0.96rem;">
          ${done ? '&#10003; Lesson ' + lessonNum + ' Completed (Synced to Master Plan)' : 'Lesson ' + lessonNum + ' Mastery Gate'}
        </div>
        <div id="lesson-drill-counter" style="font-size: 0.84rem; color: var(--text-secondary); margin-top: 0.15rem;"></div>
      </div>
      <div style="display: flex; gap: 0.6rem; flex-wrap: wrap;">
        <button type="button" class="btn-secondary" id="copy-teach-btn" title="Copy check-in message for your AI teacher">
          Copy /teach Check-In
        </button>
        <button type="button" class="btn-complete ${done ? 'done' : ''}" id="toggle-complete-btn">
          ${done ? '&#10003; Completed' : 'Mark Lesson ' + lessonNum + ' Complete'}
        </button>
      </div>
    `;
    updateLessonDrillCounter();

    bar.querySelector('#toggle-complete-btn').onclick = () => {
      const cur = readProgressMap();
      cur[checkId] = !cur[checkId];
      writeProgressMap(cur);
      renderBar();
    };

    bar.querySelector('#copy-teach-btn').onclick = (ev) => {
      const msg = `/teach Finished Lesson ${lessonNum} (${h1Text}) — quiz me with a 2-minute 2027 oral follow-up and log my learning record!`;
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(msg).then(() => {
          ev.currentTarget.innerText = 'Copied to Clipboard!';
          setTimeout(() => { ev.currentTarget.innerText = 'Copy /teach Check-In'; }, 2000);
        });
      }
    };
  }

  footer.parentNode.insertBefore(bar, footer);
  renderBar();
}

document.addEventListener('DOMContentLoaded', () => {
  mountSpacedReviewButtons();
  mountLessonCheckinBar();

  document.addEventListener('keydown', (evt) => {
    if (evt.key === 'Enter') {
      const active = document.activeElement;
      if (active && active.classList.contains('code-blank')) {
        const card = active.closest('.practice-card');
        if (card && card.id) {
          evt.preventDefault();
          checkCodeFill(card.id);
        }
      } else if (active && active.classList.contains('trace-input')) {
        const card = active.closest('.practice-card, .card');
        if (card && card.id) {
          evt.preventDefault();
          checkTraceTable(card.id);
        }
      }
    }
  });
});
