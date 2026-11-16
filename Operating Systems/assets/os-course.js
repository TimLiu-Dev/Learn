(function () {
  const THEME_KEY = "os2027_theme";
  const STORAGE_KEY = "os2027_progress_v1";

  function applyTheme(t) {
    document.documentElement.setAttribute("data-theme", t);
    try { localStorage.setItem(THEME_KEY, t); } catch (_) {}
  }

  function loadProgress() {
    try {
      const parsed = JSON.parse(localStorage.getItem(STORAGE_KEY)) || {};
      return {
        completedLessons: parsed.completedLessons || {},
        lessonScores: parsed.lessonScores || {},
        passedLabs: parsed.passedLabs || {},
        quizSelections: parsed.quizSelections || {}
      };
    } catch (_) {
      return { completedLessons: {}, lessonScores: {}, passedLabs: {}, quizSelections: {} };
    }
  }

  function saveProgress(data) {
    try { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)); } catch (_) {}
  }

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");
  }

  window.initOsCourse = function initOsCourse(cfg) {
    const lessonNum = cfg.num;
    const LABS = cfg.labs || {};
    const QUIZZES = cfg.quizzes || [];
    const labIds = Object.keys(LABS);

    const savedTheme = (function () {
      try { return localStorage.getItem(THEME_KEY) || "dark"; } catch (_) { return "dark"; }
    })();
    applyTheme(savedTheme);

    const themeBtn = document.getElementById("themeToggle");
    if (themeBtn) {
      themeBtn.addEventListener("click", function () {
        applyTheme(document.documentElement.getAttribute("data-theme") === "dark" ? "light" : "dark");
      });
    }

    const initialProgress = loadProgress();
    const savedLabList = Array.isArray(initialProgress.passedLabs[lessonNum]) ? initialProgress.passedLabs[lessonNum] : [];
    const passedLabs = new Set(savedLabList.map(String));
    const savedQuizMap = initialProgress.quizSelections[lessonNum] && typeof initialProgress.quizSelections[lessonNum] === "object"
      ? initialProgress.quizSelections[lessonNum]
      : {};
    const quizAnswered = {};
    QUIZZES.forEach((item, idx) => {
      if (Object.prototype.hasOwnProperty.call(savedQuizMap, idx)) {
        quizAnswered[idx] = (Number(savedQuizMap[idx]) === item.correct);
      }
    });
    const hintState = {};
    labIds.forEach((id) => {
      hintState[id] = 0;
      if (passedLabs.has(String(id))) {
        const statusEl = document.getElementById(`lab${id}-status`);
        if (statusEl) {
          statusEl.textContent = "PASS (Saved)";
          statusEl.className = "lab-status pass";
        }
      }
    });

    function persistLabState() {
      const data = loadProgress();
      data.passedLabs[lessonNum] = Array.from(passedLabs);
      saveProgress(data);
    }

    function persistQuizSelection(qIdx, oIdx) {
      const data = loadProgress();
      if (!data.quizSelections[lessonNum] || typeof data.quizSelections[lessonNum] !== "object") {
        data.quizSelections[lessonNum] = {};
      }
      data.quizSelections[lessonNum][qIdx] = oIdx;
      saveProgress(data);
    }

    function refreshProgressPills() {
      const labPill = document.getElementById("labProgressPill");
      if (labPill) labPill.textContent = `Labs Passed: ${passedLabs.size} / ${labIds.length}`;

      const correctCount = Object.values(quizAnswered).filter(Boolean).length;
      const quizPill = document.getElementById("quizProgressPill");
      if (quizPill) quizPill.textContent = `Quiz Score: ${correctCount} / ${QUIZZES.length}`;

      const data = loadProgress();
      if (QUIZZES.length > 0 && (Object.keys(quizAnswered).length > 0 || !data.lessonScores[lessonNum])) {
        data.lessonScores[lessonNum] = `${correctCount}/${QUIZZES.length}`;
        saveProgress(data);
      }

      const completeBtn = document.getElementById("markLessonComplete");
      if (completeBtn) {
        completeBtn.textContent = data.completedLessons[lessonNum]
          ? `Lesson ${lessonNum} Completed ✓`
          : `Mark Lesson ${lessonNum} Complete`;
      }
    }

    const completeBtn = document.getElementById("markLessonComplete");
    if (completeBtn) {
      completeBtn.addEventListener("click", function () {
        const data = loadProgress();
        data.completedLessons[lessonNum] = !data.completedLessons[lessonNum];
        saveProgress(data);
        refreshProgressPills();
      });
    }

    document.querySelectorAll(".copy-btn").forEach((btn) => {
      btn.addEventListener("click", async function () {
        const el = document.getElementById(btn.dataset.copy);
        if (!el) return;
        try {
          await navigator.clipboard.writeText(el.textContent);
          const orig = btn.textContent;
          btn.textContent = "Copied!";
          setTimeout(() => { btn.textContent = orig; }, 1200);
        } catch (_) {
          btn.textContent = "Select & Copy";
        }
      });
    });

    window.runLab = function runLab(id) {
      const lab = LABS[id];
      const codeEl = document.getElementById(`lab${id}-code`);
      const outEl = document.getElementById(`lab${id}-output`);
      const statusEl = document.getElementById(`lab${id}-status`);
      if (!lab || !codeEl || !outEl || !statusEl) return;

      outEl.classList.add("visible");
      try {
        const factory = new Function(`"use strict";\n${codeEl.value}\nreturn ${lab.fnName};`);
        const userFn = factory();
        if (typeof userFn !== "function") {
          throw new Error(`Function "${lab.fnName}" was not defined.`);
        }
        const testResults = lab.tests(userFn);
        const allPass = testResults.every((t) => t.pass);

        outEl.innerHTML = testResults.map((t) => `
          <div class="test-row ${t.pass ? "test-pass" : "test-fail"}">
            <span><strong>${t.pass ? "PASS" : "FAIL"}</strong> — ${escapeHtml(t.name)}</span>
            <span>${escapeHtml(t.detail)}</span>
          </div>
        `).join("");

        if (allPass) {
          statusEl.textContent = `PASS (${testResults.length}/${testResults.length})`;
          statusEl.className = "lab-status pass";
          passedLabs.add(String(id));
        } else {
          const passed = testResults.filter((t) => t.pass).length;
          statusEl.textContent = `FAIL (${passed}/${testResults.length})`;
          statusEl.className = "lab-status fail";
          passedLabs.delete(String(id));
        }
        persistLabState();
        refreshProgressPills();
      } catch (err) {
        statusEl.textContent = "Runtime / Syntax Error";
        statusEl.className = "lab-status fail";
        outEl.innerHTML = `<div class="test-row test-fail"><span><strong>ERROR:</strong> ${escapeHtml(err.message)}</span></div>`;
        passedLabs.delete(String(id));
        persistLabState();
        refreshProgressPills();
      }
    };

    window.revealNextHint = function revealNextHint(id) {
      const box = document.getElementById(`lab${id}-hints`);
      const countEl = document.getElementById(`lab${id}-hint-count`);
      const hints = LABS[id].hints;
      if (hintState[id] < hints.length) hintState[id]++;
      if (countEl) countEl.textContent = `${hintState[id]}/${hints.length}`;
      if (box) {
        box.classList.add("visible");
        box.innerHTML = hints.slice(0, hintState[id]).map((h) => `<p style="margin:0.3rem 0;">${escapeHtml(h)}</p>`).join("");
      }
    };

    window.toggleSolution = function toggleSolution(id) {
      const box = document.getElementById(`lab${id}-solution`);
      if (!box) return;
      if (box.classList.contains("visible")) {
        box.classList.remove("visible");
        return;
      }
      box.classList.add("visible");
      box.innerHTML = `
        <strong>Reference Solution:</strong>
        <pre class="code" style="background:var(--bg-code); border:1px solid var(--border); border-radius:8px; margin:0.5rem 0;">${escapeHtml(LABS[id].solution)}</pre>
        <p style="margin:0.35rem 0 0; color:var(--text-muted);">${escapeHtml(LABS[id].solutionExplain)}</p>
      `;
    };

    window.resetLab = function resetLab(id) {
      const ta = document.getElementById(`lab${id}-code`);
      if (ta) ta.value = LABS[id].starter;
      ["output", "hints", "solution"].forEach((s) => {
        const el = document.getElementById(`lab${id}-${s}`);
        if (el) el.classList.remove("visible");
      });
      hintState[id] = 0;
      const countEl = document.getElementById(`lab${id}-hint-count`);
      if (countEl) countEl.textContent = `0/${LABS[id].hints.length}`;
      const statusEl = document.getElementById(`lab${id}-status`);
      if (statusEl) {
        statusEl.textContent = "Not Run";
        statusEl.className = "lab-status";
      }
      passedLabs.delete(String(id));
      persistLabState();
      refreshProgressPills();
    };

    labIds.forEach((id) => {
      const ta = document.getElementById(`lab${id}-code`);
      if (!ta) return;
      ta.value = LABS[id].starter;
      ta.addEventListener("keydown", function (e) {
        if (e.key === "Tab") {
          e.preventDefault();
          const start = ta.selectionStart;
          const end = ta.selectionEnd;
          ta.value = ta.value.substring(0, start) + "  " + ta.value.substring(end);
          ta.selectionStart = ta.selectionEnd = start + 2;
        } else if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
          e.preventDefault();
          window.runLab(id);
        }
      });
    });

    const quizContainer = document.getElementById("quizContainer");
    if (quizContainer) {
      quizContainer.innerHTML = "";
      QUIZZES.forEach((item, qIdx) => {
        const card = document.createElement("div");
        card.className = "quiz-card";
        card.innerHTML = `
          <p class="quiz-q">${escapeHtml(item.q).replace(/\n/g, "<br/>")}</p>
          <div class="quiz-options">
            ${item.options.map((opt, oIdx) => `
              <button class="quiz-opt" type="button" data-q="${qIdx}" data-o="${oIdx}">${escapeHtml(opt)}</button>
            `).join("")}
          </div>
          <div class="quiz-feedback" id="qfb-${qIdx}"></div>
        `;
        quizContainer.appendChild(card);
      });

      function applyQuizSelection(card, qIdx, oIdx, shouldPersist) {
        const item = QUIZZES[qIdx];
        if (!item || !card) return;
        card.querySelectorAll(".quiz-opt").forEach((b, idx) => {
          b.classList.remove("selected-correct", "selected-wrong", "reveal-correct");
          if (idx === item.correct) {
            b.classList.add(idx === oIdx ? "selected-correct" : "reveal-correct");
          } else if (idx === oIdx) {
            b.classList.add("selected-wrong");
          }
        });
        quizAnswered[qIdx] = (oIdx === item.correct);
        if (shouldPersist) {
          persistQuizSelection(qIdx, oIdx);
        }
        refreshProgressPills();

        const fb = document.getElementById(`qfb-${qIdx}`);
        if (fb) {
          fb.classList.add("visible");
          fb.innerHTML = `
            <div style="font-weight:700; margin-bottom:0.45rem; color:${oIdx === item.correct ? "var(--green)" : "var(--red)"};">
              ${oIdx === item.correct ? "Correct!" : "Not quite — here is why each choice is right or wrong:"}
            </div>
            ${item.why.map((line, idx) => `
              <div class="opt-explain ${idx === item.correct ? "ok" : "bad"}">${escapeHtml(line)}</div>
            `).join("")}
          `;
        }
      }

      quizContainer.querySelectorAll(".quiz-opt").forEach((btn) => {
        btn.addEventListener("click", function () {
          applyQuizSelection(btn.closest(".quiz-card"), Number(btn.dataset.q), Number(btn.dataset.o), true);
        });
      });

      const cards = quizContainer.querySelectorAll(".quiz-card");
      Object.keys(savedQuizMap).forEach((qKey) => {
        const qIdx = Number(qKey);
        const oIdx = Number(savedQuizMap[qKey]);
        if (cards[qIdx]) applyQuizSelection(cards[qIdx], qIdx, oIdx, false);
      });
    }

    refreshProgressPills();
  };
})();
