/* ==========================================================================
   Reusable Interactive Salary & Negotiation Practice Lab Widget
   Supports numeric offer calculations, scenario dropdowns, automated checks,
   progressive hints, final answer reveal, and localStorage persistence.
   ========================================================================== */

(function (global) {
  'use strict';

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function parseNumeric(raw) {
    if (typeof raw === 'number') return raw;
    const cleaned = String(raw || '').replace(/[$,%\s,]/g, '');
    if (cleaned === '') return NaN;
    return Number(cleaned);
  }

  function mount(containerId, config) {
    const root = typeof containerId === 'string'
      ? document.getElementById(containerId)
      : containerId;
    if (!root) return;

    const storageKey = 'salary_practice_' + config.id;
    let saved = {};
    try {
      const raw = localStorage.getItem(storageKey);
      if (raw) saved = JSON.parse(raw) || {};
    } catch (_) { /* ignore */ }

    const hints = config.hints || [];
    const fields = config.fields || [];
    let revealedHints = 0;

    root.classList.add('code-lab');

    const fieldsHtml = fields.map(function (f) {
      const val = Object.prototype.hasOwnProperty.call(saved, f.name) ? saved[f.name] : (f.initial || '');
      const fieldId = escapeHtml(config.id + '_' + f.name);
      let inputHtml = '';
      if (f.type === 'select') {
        const opts = (f.options || []).map(function (opt) {
          const optVal = typeof opt === 'object' ? opt.value : opt;
          const optLabel = typeof opt === 'object' ? opt.label : opt;
          return '<option value="' + escapeHtml(optVal) + '" ' + (String(val) === String(optVal) ? 'selected' : '') + '>' + escapeHtml(optLabel) + '</option>';
        }).join('');
        inputHtml = '<select id="' + fieldId + '" class="practice-input" data-field="' + escapeHtml(f.name) + '">' +
          '<option value="">-- Choose an answer --</option>' + opts + '</select>';
      } else {
        inputHtml = '<input id="' + fieldId + '" type="text" class="practice-input" data-field="' + escapeHtml(f.name) + '" ' +
          'placeholder="' + escapeHtml(f.placeholder || '') + '" value="' + escapeHtml(val) + '" />';
      }

      return `
        <div class="practice-field" data-wrap="${escapeHtml(f.name)}">
          <label class="practice-label" for="${fieldId}">${f.label}</label>
          ${f.help ? '<div class="practice-help">' + f.help + '</div>' : ''}
          ${inputHtml}
        </div>
      `;
    }).join('');

    root.innerHTML = `
      <div class="code-lab-header">
        <h3 class="code-lab-title">${escapeHtml(config.title)}</h3>
        <span class="code-lab-status" data-role="status">Not checked yet</span>
      </div>
      <div class="code-lab-prompt">${config.promptHtml}</div>
      <div class="practice-grid">${fieldsHtml}</div>
      <div class="code-lab-toolbar">
        <button type="button" class="btn btn-primary" data-action="check">Check My Work</button>
        <button type="button" class="btn" data-action="hint">Hint (0/${hints.length})</button>
        <button type="button" class="btn" data-action="solution">Show Final Answer</button>
        <button type="button" class="btn" data-action="reset">Reset</button>
      </div>
      <div class="code-lab-panels">
        <div class="lab-results hidden" data-role="results"></div>
        <div class="lab-hints hidden" data-role="hints"></div>
        <div class="lab-solution hidden" data-role="solution"></div>
      </div>
    `;

    const statusBadge = root.querySelector('[data-role="status"]');
    const resultsBox = root.querySelector('[data-role="results"]');
    const hintsBox = root.querySelector('[data-role="hints"]');
    const solutionBox = root.querySelector('[data-role="solution"]');
    const hintBtn = root.querySelector('[data-action="hint"]');
    const solutionBtn = root.querySelector('[data-action="solution"]');

    if (hints.length === 0) hintBtn.classList.add('hidden');

    function getValues() {
      const out = {};
      fields.forEach(function (f) {
        const el = root.querySelector('[data-field="' + f.name + '"]');
        out[f.name] = el ? el.value.trim() : '';
      });
      return out;
    }

    function persist() {
      try {
        localStorage.setItem(storageKey, JSON.stringify(getValues()));
      } catch (_) { /* ignore */ }
    }

    root.querySelectorAll('.practice-input').forEach(function (el) {
      el.addEventListener('input', persist);
      el.addEventListener('change', persist);
    });

    function runCheck() {
      const values = getValues();
      let passCount = 0;

      const checksHtml = fields.map(function (f, idx) {
        const raw = values[f.name];
        let ok = false;
        let detail = '';

        if (f.type === 'number') {
          const num = parseNumeric(raw);
          const tol = typeof f.tolerance === 'number' ? f.tolerance : 1;
          ok = !isNaN(num) && Math.abs(num - f.answer) <= tol;
          detail = raw === ''
            ? 'Blank — enter a number'
            : (ok ? 'Your answer: ' + raw : 'Your answer: ' + raw + ' (recheck the formula or use a Hint)');
        } else {
          ok = String(raw) === String(f.answer);
          detail = raw === ''
            ? 'Not selected yet'
            : (ok ? 'Correct choice!' : 'Not quite — try again or inspect a Hint');
        }

        if (ok) passCount += 1;
        return `
          <div class="test-case ${ok ? 'pass' : 'fail'}">
            <div><strong>${ok ? 'PASS' : 'CHECK'} · Step ${idx + 1}:</strong> ${f.label}</div>
            <div class="test-case-detail">${escapeHtml(detail)}</div>
          </div>
        `;
      }).join('');

      const allPassed = passCount === fields.length;
      statusBadge.textContent = allPassed
        ? 'All ' + fields.length + ' checks passed'
        : passCount + '/' + fields.length + ' checks passed';
      statusBadge.className = 'code-lab-status ' + (allPassed ? 'passed' : 'failed');

      resultsBox.classList.remove('hidden');
      resultsBox.innerHTML =
        '<div><strong>Check Summary:</strong> ' + passCount + ' of ' + fields.length + ' items verified.</div>' +
        checksHtml;
    }

    root.querySelector('[data-action="check"]').addEventListener('click', runCheck);

    hintBtn.addEventListener('click', function () {
      if (revealedHints < hints.length) revealedHints += 1;
      hintBtn.textContent = 'Hint (' + revealedHints + '/' + hints.length + ')';
      hintsBox.classList.remove('hidden');
      hintsBox.innerHTML = hints.slice(0, revealedHints).map(function (h, i) {
        return '<div class="lab-hint-item"><strong>Hint ' + (i + 1) + ':</strong> ' + h + '</div>';
      }).join('');
    });

    let solutionVisible = false;
    solutionBtn.addEventListener('click', function () {
      solutionVisible = !solutionVisible;
      solutionBtn.textContent = solutionVisible ? 'Hide Final Answer' : 'Show Final Answer';
      if (!solutionVisible) {
        solutionBox.classList.add('hidden');
        return;
      }
      solutionBox.classList.remove('hidden');
      const answerLines = fields.map(function (f) {
        const displayAns = f.answerDisplay || f.answer;
        return '<li><strong>' + f.label + ':</strong> <code>' + escapeHtml(displayAns) + '</code>' +
          (f.why ? ' — ' + f.why : '') + '</li>';
      }).join('');

      solutionBox.innerHTML = `
        <div style="display:flex;justify-content:space-between;align-items:center;gap:0.5rem;margin-bottom:0.5rem;">
          <strong>Final Answer Breakdown</strong>
          <button type="button" class="btn" data-action="fill-answers">Fill Answers &amp; Verify</button>
        </div>
        ${config.solutionExplanation ? '<p style="margin-bottom:0.65rem;">' + config.solutionExplanation + '</p>' : ''}
        <ul style="margin-bottom:0;">${answerLines}</ul>
      `;

      solutionBox.querySelector('[data-action="fill-answers"]').addEventListener('click', function () {
        fields.forEach(function (f) {
          const el = root.querySelector('[data-field="' + f.name + '"]');
          if (el) el.value = f.answer;
        });
        persist();
        runCheck();
      });
    });

    root.querySelector('[data-action="reset"]').addEventListener('click', function () {
      fields.forEach(function (f) {
        const el = root.querySelector('[data-field="' + f.name + '"]');
        if (el) el.value = f.initial || '';
      });
      revealedHints = 0;
      hintBtn.textContent = 'Hint (0/' + hints.length + ')';
      hintsBox.classList.add('hidden');
      hintsBox.innerHTML = '';
      solutionVisible = false;
      solutionBtn.textContent = 'Show Final Answer';
      solutionBox.classList.add('hidden');
      solutionBox.innerHTML = '';
      resultsBox.classList.add('hidden');
      statusBadge.textContent = 'Not checked yet';
      statusBadge.className = 'code-lab-status';
      try {
        localStorage.removeItem(storageKey);
      } catch (_) { /* ignore */ }
    });
  }

  global.PracticeLab = {
    mount: mount
  };
})(window);
