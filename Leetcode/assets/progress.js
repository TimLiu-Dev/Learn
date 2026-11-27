// Shared workspace component: session completion tracker, spaced-review state,
// countdown interview timer, explicit light/dark theme toggle, and 1-click code copy.
(function () {
  "use strict";

  const STORAGE_KEY = "leetcode_2027_progress_v1";
  const THEME_KEY = "leetcode_2027_theme";
  const TOTAL_SESSIONS = 39;

  function loadProgress() {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY) || "{}") || {};
    } catch (e) {
      return {};
    }
  }

  function saveProgress(data) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } catch (e) {}
  }

  // Apply persisted theme immediately
  function getSavedTheme() {
    try {
      return localStorage.getItem(THEME_KEY) || "auto";
    } catch (e) {
      return "auto";
    }
  }

  function applyTheme(theme) {
    if (theme === "dark" || theme === "light") {
      document.documentElement.setAttribute("data-theme", theme);
    } else {
      document.documentElement.removeAttribute("data-theme");
    }
    try {
      localStorage.setItem(THEME_KEY, theme);
    } catch (e) {}
  }

  applyTheme(getSavedTheme());

  document.addEventListener("DOMContentLoaded", function () {
    const main = document.querySelector("main");
    if (!main) return;

    const path = window.location.pathname || "";
    const fileMatch = path.match(/([^/]+\.html)$/);
    const fileName = fileMatch ? fileMatch[1] : "index.html";
    const isLesson = /\/lessons\/00\d\d-/.test(path) || /^00\d\d-/.test(fileName);
    const isReference = /\/reference\//.test(path);
    const prefix = isLesson || isReference ? "../" : "";

    const progress = loadProgress();
    const doneCount = Object.keys(progress).filter(function (k) {
      return progress[k] && progress[k].done;
    }).length;

    // 1. Build top utility bar
    const bar = document.createElement("nav");
    bar.className = "session-bar";
    bar.setAttribute("aria-label", "Workspace navigation and session controls");

    const left = document.createElement("div");
    left.className = "session-bar-left";
    left.innerHTML =
      `<a href="${prefix}index.html"><strong>⌂ Hub</strong></a> · ` +
      `<a href="${prefix}reference/roadmap.html">Roadmap</a> · ` +
      `<a href="${prefix}reference/patterns.html">Patterns</a> · ` +
      `<a href="${prefix}reference/kotlin-toolkit.html">Kotlin</a> · ` +
      `<a href="${prefix}reference/glossary.html">Glossary</a> ` +
      `<span class="progress-summary" style="margin-left:.25rem;">(${doneCount}/${TOTAL_SESSIONS} done)</span>`;

    const right = document.createElement("div");
    right.className = "session-bar-right";

    // Lesson-specific controls: countdown timer + Mark Complete toggle
    if (isLesson) {
      const defaultMins =
        fileName.indexOf("0037-") === 0 ? 35 :
        fileName.indexOf("0038-") === 0 || fileName.indexOf("0039-") === 0 ? 45 : 25;

      let totalSecs = defaultMins * 60;
      let remSecs = totalSecs;
      let timerId = null;

      const timerBtn = document.createElement("button");
      timerBtn.type = "button";
      timerBtn.className = "bar-btn";

      function fmtTime(s) {
        const m = Math.floor(s / 60);
        const sec = s % 60;
        return `${m}:${sec < 10 ? "0" : ""}${sec}`;
      }

      function updateTimerLabel() {
        if (remSecs <= 0) {
          timerBtn.textContent = "⏱ Time Up (Reset)";
          timerBtn.className = "bar-btn timer-expired";
        } else if (timerId) {
          timerBtn.textContent = `⏸ ${fmtTime(remSecs)}`;
          timerBtn.className = "bar-btn timer-active";
        } else if (remSecs < totalSecs) {
          timerBtn.textContent = `▶ ${fmtTime(remSecs)}`;
          timerBtn.className = "bar-btn";
        } else {
          timerBtn.textContent = `⏱ ${defaultMins}m Timer`;
          timerBtn.className = "bar-btn";
        }
      }

      timerBtn.title = "Click to start/pause timed interview drill; double-click to reset";
      timerBtn.addEventListener("click", function () {
        if (remSecs <= 0) {
          remSecs = totalSecs;
          updateTimerLabel();
          return;
        }
        if (timerId) {
          clearInterval(timerId);
          timerId = null;
        } else {
          timerId = setInterval(function () {
            remSecs = Math.max(0, remSecs - 1);
            if (remSecs === 0) {
              clearInterval(timerId);
              timerId = null;
            }
            updateTimerLabel();
          }, 1000);
        }
        updateTimerLabel();
      });

      timerBtn.addEventListener("dblclick", function () {
        if (timerId) {
          clearInterval(timerId);
          timerId = null;
        }
        remSecs = totalSecs;
        updateTimerLabel();
      });

      updateTimerLabel();
      right.appendChild(timerBtn);

      const doneBtn = document.createElement("button");
      doneBtn.type = "button";

      function syncDoneBtn() {
        const cur = loadProgress();
        const isDone = Boolean(cur[fileName] && cur[fileName].done);
        doneBtn.className = isDone ? "bar-btn done" : "bar-btn";
        doneBtn.textContent = isDone ? "✓ Session Completed" : "○ Mark Complete";
        const countNow = Object.keys(cur).filter(function (k) {
          return cur[k] && cur[k].done;
        }).length;
        const sumEl = left.querySelector(".progress-summary");
        if (sumEl) sumEl.textContent = `(${countNow}/${TOTAL_SESSIONS} done)`;
      }

      doneBtn.addEventListener("click", function () {
        const cur = loadProgress();
        const wasDone = Boolean(cur[fileName] && cur[fileName].done);
        if (wasDone) {
          delete cur[fileName];
        } else {
          cur[fileName] = { done: true, completedAt: new Date().toISOString() };
        }
        saveProgress(cur);
        syncDoneBtn();
      });

      syncDoneBtn();
      right.appendChild(doneBtn);
    }

    // Theme cycler button: Auto -> Dark -> Light
    const themeBtn = document.createElement("button");
    themeBtn.type = "button";
    themeBtn.className = "bar-btn";

    function updateThemeLabel() {
      const t = getSavedTheme();
      themeBtn.textContent = t === "dark" ? "☾ Dark" : t === "light" ? "☀ Light" : "◐ Auto";
      themeBtn.title = "Switch color theme (Auto / Dark / Light)";
    }

    themeBtn.addEventListener("click", function () {
      const cur = getSavedTheme();
      const next = cur === "auto" ? "dark" : cur === "dark" ? "light" : "auto";
      applyTheme(next);
      updateThemeLabel();
    });

    updateThemeLabel();
    right.appendChild(themeBtn);

    bar.appendChild(left);
    bar.appendChild(right);
    main.insertBefore(bar, main.firstChild);

    // 2. Decorate session links on Roadmap / Masterlist / Hub with completion checkmarks
    document.querySelectorAll('a[href*="00"]').forEach(function (link) {
      const href = link.getAttribute("href") || "";
      const m = href.match(/(00\d\d-[a-z0-9-]+\.html)$/);
      if (!m) return;
      const targetFile = m[1];
      if (!progress[targetFile] || !progress[targetFile].done) return;
      // Add badge inside roadmap/hub .session h3 or masterlist td strong
      const parentH3 = link.closest(".session h3");
      const parentTd = link.closest(".legend-table td");
      if (parentH3 && !parentH3.querySelector(".done-pill")) {
        const pill = document.createElement("span");
        pill.className = "done-pill";
        pill.textContent = "✓ Done";
        parentH3.appendChild(pill);
      } else if (parentTd && !parentTd.querySelector(".done-pill")) {
        const pill = document.createElement("span");
        pill.className = "done-pill";
        pill.style.marginLeft = ".35rem";
        pill.textContent = "✓";
        link.insertAdjacentElement("afterend", pill);
      }
    });

    // 3. Add 1-click Copy button to code blocks (excluding inline console outputs)
    document.querySelectorAll("pre > code").forEach(function (codeEl) {
      const pre = codeEl.parentElement;
      if (!pre || pre.classList.contains("stdout-pre") || pre.classList.contains("error-pre")) return;
      pre.classList.add("has-copy");
      const btn = document.createElement("button");
      btn.type = "button";
      btn.className = "code-copy-btn";
      btn.textContent = "Copy";
      btn.addEventListener("click", function () {
        const txt = codeEl.textContent || "";
        if (navigator.clipboard && navigator.clipboard.writeText) {
          navigator.clipboard.writeText(txt).then(function () {
            btn.textContent = "✓ Copied";
            setTimeout(function () {
              btn.textContent = "Copy";
            }, 1300);
          });
        }
      });
      pre.appendChild(btn);
    });
  });
})();
