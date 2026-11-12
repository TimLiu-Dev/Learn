/* ==========================================================================
   Shared Course Progress, Navigation Badges, and Copy Helpers
   Persists progress across Master Plan & Lessons via localStorage
   ========================================================================== */
(function () {
  const STORAGE_KEY = "fde_senior_2027_progress_v1";

  function loadState() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) return { lessons: {} };
      const parsed = JSON.parse(raw);
      return parsed && typeof parsed === "object" ? parsed : { lessons: {} };
    } catch (_) {
      return { lessons: {} };
    }
  }

  function saveState(state) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (_) {
      // Ignore storage quota / privacy mode blocks gracefully
    }
  }

  function ensureLesson(state, lessonId) {
    if (!state.lessons[lessonId]) {
      state.lessons[lessonId] = {
        labs: {},
        quizzes: {},
        targets: { labs: 0, quizzes: 0 }
      };
    }
    return state.lessons[lessonId];
  }

  const ProgressAPI = {
    registerLessonTargets(lessonId, totalLabs, totalQuizzes) {
      const state = loadState();
      const lesson = ensureLesson(state, lessonId);
      lesson.targets = { labs: totalLabs, quizzes: totalQuizzes };
      saveState(state);
      this.refreshHeaderBadge(lessonId);
    },

    saveLabDraft(lessonId, labId, code) {
      const state = loadState();
      const lesson = ensureLesson(state, lessonId);
      const prev = lesson.labs[labId] || {};
      lesson.labs[labId] = {
        ...prev,
        code,
        updatedAt: Date.now()
      };
      saveState(state);
    },

    markLabPassed(lessonId, labId, passed, code) {
      const state = loadState();
      const lesson = ensureLesson(state, lessonId);
      lesson.labs[labId] = {
        passed: Boolean(passed),
        code,
        updatedAt: Date.now()
      };
      saveState(state);
      this.refreshHeaderBadge(lessonId);
    },

    getLabState(lessonId, labId) {
      const state = loadState();
      return state.lessons?.[lessonId]?.labs?.[labId] || null;
    },

    markQuizAnswered(lessonId, quizId, itemId, chosenIndex, isCorrect) {
      const state = loadState();
      const lesson = ensureLesson(state, lessonId);
      if (!lesson.quizzes[quizId]) lesson.quizzes[quizId] = {};
      lesson.quizzes[quizId][itemId] = {
        chosenIndex,
        isCorrect: Boolean(isCorrect),
        updatedAt: Date.now()
      };
      saveState(state);
      this.refreshHeaderBadge(lessonId);
    },

    getQuizState(lessonId, quizId) {
      const state = loadState();
      return state.lessons?.[lessonId]?.quizzes?.[quizId] || {};
    },

    getLessonSummary(lessonId) {
      const state = loadState();
      const lesson = state.lessons?.[lessonId];
      if (!lesson) {
        return { labsPassed: 0, labsTotal: 0, quizzesCorrect: 0, quizzesTotal: 0, complete: false };
      }
      const labsPassed = Object.values(lesson.labs || {}).filter((l) => l && l.passed).length;
      let quizzesCorrect = 0;
      Object.values(lesson.quizzes || {}).forEach((suite) => {
        Object.values(suite || {}).forEach((item) => {
          if (item && item.isCorrect) quizzesCorrect += 1;
        });
      });
      const labsTotal = lesson.targets?.labs || 0;
      const quizzesTotal = lesson.targets?.quizzes || 0;
      const complete =
        labsTotal + quizzesTotal > 0 &&
        labsPassed >= labsTotal &&
        quizzesCorrect >= quizzesTotal;
      return { labsPassed, labsTotal, quizzesCorrect, quizzesTotal, complete };
    },

    refreshHeaderBadge(lessonId) {
      const el = document.querySelector(`[data-lesson-progress="${lessonId}"]`);
      if (!el) return;
      const s = this.getLessonSummary(lessonId);
      const dot = el.querySelector(".progress-dot");
      const label = el.querySelector(".progress-label");
      if (label) {
        label.textContent = `Labs ${s.labsPassed}/${s.labsTotal} · Quiz ${s.quizzesCorrect}/${s.quizzesTotal}`;
      }
      if (dot) {
        dot.classList.toggle("done", s.complete);
      }
    },

    hydrateMasterPlanBadges() {
      const badges = document.querySelectorAll("[data-master-lesson-status]");
      let completedCount = 0;
      badges.forEach((el) => {
        const lessonId = el.getAttribute("data-master-lesson-status");
        const s = this.getLessonSummary(lessonId);
        if (s.complete) completedCount += 1;
        if (s.labsTotal === 0 && s.quizzesTotal === 0) return;
        if (s.complete) {
          el.className = "badge badge-done";
          el.textContent = "Completed";
        } else if (s.labsPassed > 0 || s.quizzesCorrect > 0) {
          el.className = "badge badge-ready";
          el.textContent = `In Progress (${s.labsPassed}/${s.labsTotal} Labs · ${s.quizzesCorrect}/${s.quizzesTotal} Quiz)`;
        }
      });
      const overallEl = document.querySelector("[data-overall-progress]");
      if (overallEl && badges.length > 0) {
        overallEl.textContent = `${completedCount} / ${badges.length} Units Verified`;
      }

      document.querySelectorAll("[data-skill-lessons]").forEach((node) => {
        const ids = (node.getAttribute("data-skill-lessons") || "")
          .split(",")
          .map((s) => s.trim())
          .filter(Boolean);
        if (!ids.length) return;
        const doneCount = ids.filter((id) => this.getLessonSummary(id).complete).length;
        const meta = node.querySelector(".skill-node-meta");
        if (doneCount === ids.length) {
          node.classList.add("done");
          if (meta) meta.textContent = `Done (${ids.join(", ")})`;
        } else if (doneCount > 0) {
          if (meta) meta.textContent = `${doneCount}/${ids.length} (${ids.join(", ")})`;
        }
      });
    },

    resetAll() {
      try {
        localStorage.removeItem(STORAGE_KEY);
      } catch (_) {}
      window.location.reload();
    }
  };

  function installCopyButtons() {
    document.querySelectorAll(".code-example").forEach((block) => {
      const btn = block.querySelector(".copy-btn");
      const code = block.querySelector("pre code");
      if (!btn || !code) return;
      btn.addEventListener("click", async () => {
        try {
          await navigator.clipboard.writeText(code.textContent);
          const orig = btn.textContent;
          btn.textContent = "Copied";
          setTimeout(() => {
            btn.textContent = orig;
          }, 1400);
        } catch (_) {
          btn.textContent = "Select & Copy";
        }
      });
    });
  }

  window.FDECourseProgress = ProgressAPI;

  document.addEventListener("DOMContentLoaded", () => {
    installCopyButtons();
    ProgressAPI.hydrateMasterPlanBadges();
    document.querySelectorAll("[data-lesson-progress]").forEach((el) => {
      const id = el.getAttribute("data-lesson-progress");
      ProgressAPI.refreshHeaderBadge(id);
    });
  });
})();
