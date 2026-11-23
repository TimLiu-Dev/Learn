/**
 * LocalStorage progress tracker for Master Plan & Capstone Sprint Log.
 */
(function () {
  "use strict";

  const PROGRESS_KEY = "dating_mastery_completed_v1";
  const SPRINT_KEY = "dating_mastery_capstone_sprint_v1";

  function loadJson(key, fallback) {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : fallback;
    } catch (_) {
      return fallback;
    }
  }

  function saveJson(key, val) {
    try {
      localStorage.setItem(key, JSON.stringify(val));
    } catch (_) {}
  }

  function initMasterPlanProgress() {
    const summaryEl = document.getElementById("progress-summary");
    const fillEl = document.getElementById("progress-fill");
    const checkboxes = Array.from(document.querySelectorAll(".lesson-check[data-lesson-id]"));
    if (!checkboxes.length) return;

    const doneMap = loadJson(PROGRESS_KEY, {});

    function refresh() {
      let doneCount = 0;
      checkboxes.forEach((cb) => {
        const id = cb.getAttribute("data-lesson-id");
        const checked = Boolean(doneMap[id]);
        cb.checked = checked;
        const row = cb.closest(".lesson-item");
        if (row) row.classList.toggle("is-done", checked);
        if (checked) doneCount += 1;
      });
      const total = checkboxes.length;
      const pct = Math.round((doneCount / total) * 100);
      if (summaryEl) {
        summaryEl.textContent = `${doneCount} / ${total} modules completed (${pct}%)`;
      }
      if (fillEl) {
        fillEl.style.width = `${pct}%`;
      }
    }

    checkboxes.forEach((cb) => {
      cb.addEventListener("change", function () {
        const id = this.getAttribute("data-lesson-id");
        doneMap[id] = this.checked;
        saveJson(PROGRESS_KEY, doneMap);
        refresh();
      });
    });

    const resetBtn = document.getElementById("progress-reset-btn");
    if (resetBtn) {
      resetBtn.addEventListener("click", function () {
        Object.keys(doneMap).forEach((k) => delete doneMap[k]);
        saveJson(PROGRESS_KEY, doneMap);
        refresh();
      });
    }

    refresh();
  }

  function initCapstoneSprint() {
    const inputs = Array.from(document.querySelectorAll(".sprint-input[data-sprint-key]"));
    if (!inputs.length) return;

    const saved = loadJson(SPRINT_KEY, {});
    const totalEl = document.getElementById("sprint-rubric-total");

    function updateRubricTotal() {
      if (!totalEl) return;
      let sum = 0;
      inputs.forEach((el) => {
        if (el.getAttribute("data-sprint-score") === "true") {
          const val = Number(el.value) || 0;
          const max = Number(el.getAttribute("max")) || 0;
          sum += Math.max(0, Math.min(max, val));
        }
      });
      totalEl.textContent = `${sum} / 100 pts`;
    }

    inputs.forEach((el) => {
      const k = el.getAttribute("data-sprint-key");
      if (el.type === "checkbox") {
        el.checked = Boolean(saved[k]);
      } else if (saved[k] !== undefined) {
        el.value = saved[k];
      }
      el.addEventListener("input", function () {
        saved[k] = el.type === "checkbox" ? el.checked : el.value;
        saveJson(SPRINT_KEY, saved);
        updateRubricTotal();
      });
      el.addEventListener("change", function () {
        saved[k] = el.type === "checkbox" ? el.checked : el.value;
        saveJson(SPRINT_KEY, saved);
        updateRubricTotal();
      });
    });

    const clearBtn = document.getElementById("sprint-reset-btn");
    if (clearBtn) {
      clearBtn.addEventListener("click", function () {
        Object.keys(saved).forEach((k) => delete saved[k]);
        saveJson(SPRINT_KEY, saved);
        inputs.forEach((el) => {
          if (el.type === "checkbox") el.checked = false;
          else el.value = "";
        });
        updateRubricTotal();
      });
    }

    updateRubricTotal();
  }

  function init() {
    initMasterPlanProgress();
    initCapstoneSprint();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
