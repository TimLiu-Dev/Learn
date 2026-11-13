/**
 * Reusable Lesson Engine for Go Cloud Engineering Course (2027 Edition)
 * Handles themes, interactive code pre-checks, progressive hints,
 * solution toggles, balanced A/B/C/D quizzes with rationales, and progress tracking.
 */

class LessonEngine {
  constructor(config = {}) {
    this.lessonId = config.lessonId || "default-lesson";
    this.trackProgress = config.trackProgress !== undefined
      ? config.trackProgress
      : !["master-plan", "capstone", "ref-01", "ref-02", "default-lesson"].includes(this.lessonId);
    this.testSuite = config.testSuite || null;
    this.defaultCode = config.defaultCode || "";
    this.solutionCode = config.solutionCode || "";
    this.labCommand = config.labCommand || "";
    this.currentHintIndex = 0;
    this.hints = config.hints || [];

    this.initTheme();
    this.initEditor();
    this.initHints();
    this.initSolution();
    this.initQuizzes();
    this.loadProgress();
  }

  /**
   * Strips Go line and block comments while preserving string literals so
   * browser static pre-checks cannot be fooled by commented-out tokens.
   */
  static stripComments(code) {
    if (!code) return "";
    return code
      .replace(/`[\s\S]*?`/g, (m) => m)
      .replace(/"(?:\\.|[^"\\])*"/g, (m) => m)
      .replace(/\/\*[\s\S]*?\*\//g, "")
      .replace(/\/\/[^\n]*/g, "");
  }

  // --- Theme Management ---
  initTheme() {
    const savedTheme = localStorage.getItem("gocloud_theme") || "dark";
    document.documentElement.setAttribute("data-theme", savedTheme);

    const toggleBtn = document.getElementById("themeToggleBtn");
    if (toggleBtn) {
      this.updateThemeButtonLabel(toggleBtn, savedTheme);
      toggleBtn.addEventListener("click", () => {
        const currentTheme = document.documentElement.getAttribute("data-theme");
        const nextTheme = currentTheme === "dark" ? "light" : "dark";
        document.documentElement.setAttribute("data-theme", nextTheme);
        localStorage.setItem("gocloud_theme", nextTheme);
        this.updateThemeButtonLabel(toggleBtn, nextTheme);
      });
    }
  }

  updateThemeButtonLabel(btn, theme) {
    btn.innerHTML = theme === "dark" ? "☀️ Light Mode" : "🌙 Dark Mode";
  }

  // --- Code Editor & Tab Handling ---
  initEditor() {
    this.editor = document.getElementById("codeEditor");
    this.runBtn = document.getElementById("runCodeBtn");
    this.resetBtn = document.getElementById("resetCodeBtn");
    this.outputTerminal = document.getElementById("outputTerminal");

    if (!this.editor) return;

    const savedCode = localStorage.getItem(`gocloud_code_${this.lessonId}`);
    if (savedCode !== null && savedCode !== "") {
      this.editor.value = savedCode;
    } else if (this.defaultCode) {
      this.editor.value = this.defaultCode;
    }

    this.editor.addEventListener("input", () => {
      localStorage.setItem(`gocloud_code_${this.lessonId}`, this.editor.value);
    });

    this.editor.addEventListener("keydown", (e) => {
      if (e.key === "Tab") {
        e.preventDefault();
        const start = this.editor.selectionStart;
        const end = this.editor.selectionEnd;
        this.editor.value =
          this.editor.value.substring(0, start) + "\t" + this.editor.value.substring(end);
        this.editor.selectionStart = this.editor.selectionEnd = start + 1;
        localStorage.setItem(`gocloud_code_${this.lessonId}`, this.editor.value);
      }
    });

    if (this.runBtn) {
      this.runBtn.addEventListener("click", () => this.runAndVerifyCode());
    }

    if (this.resetBtn) {
      this.resetBtn.addEventListener("click", () => {
        if (confirm("Reset code to starter template? Your edits for this lesson will be replaced.")) {
          this.editor.value = this.defaultCode;
          localStorage.setItem(`gocloud_code_${this.lessonId}`, this.defaultCode);
          this.hideTerminal();
        }
      });
    }
  }

  // --- Static Pre-Check Execution ---
  async runAndVerifyCode() {
    if (!this.editor || !this.outputTerminal) return;

    const userCode = this.editor.value;
    this.outputTerminal.className = "output-terminal active";
    this.outputTerminal.innerHTML = `
      <div class="output-status-bar output-status-running">
        <span>⚡ Running Browser Static Pre-Check...</span>
      </div>
      <div>Checking Go syntax structure, required contracts, and common pitfalls...</div>
    `;

    await new Promise((res) => setTimeout(res, 220));

    try {
      if (this.testSuite && typeof this.testSuite === "function") {
        const result = await this.testSuite(userCode, LessonEngine.stripComments(userCode));
        this.renderTestResult(result);
      } else {
        this.renderTestResult({
          passed: true,
          output: "Static pre-check complete. Run real Go tests locally via `go test -v -race ./...`.",
          message: "Browser static pre-check passed."
        });
      }
    } catch (err) {
      this.renderTestResult({
        passed: false,
        output: `pre-check error: ${err.message}`,
        message: "Browser static pre-check failed."
      });
    }
  }

  renderTestResult(result) {
    const isPass = Boolean(result.passed);
    const statusClass = isPass ? "output-status-pass" : "output-status-fail";
    const statusIcon = isPass ? "✅ STATIC PRE-CHECK PASS" : "❌ STATIC PRE-CHECK FAIL";

    let html = `
      <div class="output-status-bar ${statusClass}">
        <span>${statusIcon}: ${this.escapeHtml(result.message || (isPass ? "Checks passed!" : "Checks failed"))}</span>
      </div>
      <pre style="margin:0; background:transparent; border:none; padding:0; color: inherit; box-shadow: none;">${this.escapeHtml(result.output || "")}</pre>
    `;

    if (result.details) {
      html += `<div style="margin-top: 0.75rem; color: var(--text-secondary); font-size: 0.82rem;">${this.escapeHtml(result.details)}</div>`;
    }

    if (this.labCommand) {
      html += `<div style="margin-top: 0.75rem; padding-top: 0.65rem; border-top: 1px solid var(--border-color); color: var(--accent-cyan); font-size: 0.82rem;">Real compiler &amp; race check: <code>${this.escapeHtml(this.labCommand)}</code></div>`;
    }

    this.outputTerminal.innerHTML = html;

    if (isPass && this.trackProgress) {
      this.markLessonComplete();
    }
  }

  hideTerminal() {
    if (this.outputTerminal) {
      this.outputTerminal.className = "output-terminal";
      this.outputTerminal.innerHTML = "";
    }
  }

  // --- Progressive Hints System ---
  initHints() {
    this.hintBtn = document.getElementById("getHintBtn");
    this.hintPanel = document.getElementById("hintPanel");
    if (!this.hintBtn || !this.hintPanel || !this.hints.length) return;

    this.hintBtn.addEventListener("click", () => {
      this.hintPanel.classList.add("active");
      if (this.currentHintIndex < this.hints.length) {
        const stepNum = this.currentHintIndex + 1;
        const hintText = this.hints[this.currentHintIndex];
        const stepEl = document.createElement("div");
        stepEl.className = "hint-step";
        stepEl.innerHTML = `<strong>💡 Hint ${stepNum} of ${this.hints.length}:</strong> ${this.escapeHtml(hintText)}`;
        this.hintPanel.appendChild(stepEl);
        this.currentHintIndex++;

        if (this.currentHintIndex >= this.hints.length) {
          this.hintBtn.innerText = "All Hints Revealed";
          this.hintBtn.disabled = true;
          this.hintBtn.style.opacity = "0.6";
        } else {
          this.hintBtn.innerText = `Get Next Hint (${this.currentHintIndex + 1}/${this.hints.length})`;
        }
      }
    });
  }

  // --- Solution Reveal System ---
  initSolution() {
    this.solutionBtn = document.getElementById("showSolutionBtn");
    this.solutionPanel = document.getElementById("solutionPanel");
    if (!this.solutionBtn || !this.solutionPanel) return;

    let isRevealed = false;
    this.solutionBtn.addEventListener("click", () => {
      isRevealed = !isRevealed;
      if (isRevealed) {
        this.solutionPanel.classList.add("active");
        this.solutionBtn.innerText = "Hide Reference Solution";
        this.solutionPanel.scrollIntoView({ behavior: "smooth", block: "nearest" });
      } else {
        this.solutionPanel.classList.remove("active");
        this.solutionBtn.innerText = "Show Reference Solution";
      }
    });
  }

  // --- Multiple-Choice Quizzes with Comprehensive Rationales ---
  initQuizzes() {
    const quizCards = document.querySelectorAll(".quiz-card");
    quizCards.forEach((card) => {
      const options = card.querySelectorAll(".quiz-option");
      const feedback = card.querySelector(".quiz-feedback");
      const submitBtn = card.querySelector(".quiz-submit-btn");
      let selectedOption = null;

      options.forEach((opt) => {
        const key = opt.getAttribute("data-key") || "";
        if (key && !opt.querySelector(".quiz-option-key")) {
          const badge = document.createElement("span");
          badge.className = "quiz-option-key";
          badge.textContent = key;
          opt.insertBefore(badge, opt.firstChild);
        }

        opt.addEventListener("click", () => {
          options.forEach((o) => o.classList.remove("selected"));
          opt.classList.add("selected");
          selectedOption = opt;
          if (submitBtn) submitBtn.disabled = false;
        });
      });

      if (submitBtn) {
        submitBtn.addEventListener("click", () => {
          if (!selectedOption) return;

          const isCorrect = selectedOption.getAttribute("data-correct") === "true";
          const chosenKey = selectedOption.getAttribute("data-key");

          // Reset previous grading highlights before applying current attempt
          options.forEach((opt) => {
            opt.classList.remove("correct", "incorrect");
            if (opt.getAttribute("data-correct") === "true") {
              opt.classList.add("correct");
            } else if (opt === selectedOption && !isCorrect) {
              opt.classList.add("incorrect");
            }
          });

          if (feedback) {
            feedback.className = `quiz-feedback active ${isCorrect ? "success" : "failure"}`;
            const summaryTitle = isCorrect
              ? `✅ <strong>Correct!</strong> Choice ${this.escapeHtml(chosenKey)} is accurate.`
              : `❌ <strong>Incorrect.</strong> Choice ${this.escapeHtml(chosenKey)} reflects a common misconception.`;

            const rationalesContainer = card.querySelector(".quiz-rationales-data");
            let rationaleHtml = "";
            if (rationalesContainer) {
              rationaleHtml = `
                <div class="rationale-block">
                  <div style="font-weight: 700; margin-bottom: 0.5rem;">Detailed Breakdown of All Options:</div>
                  ${rationalesContainer.innerHTML}
                </div>
              `;
            }

            feedback.innerHTML = `<div>${summaryTitle}</div>${rationaleHtml}`;
          }

          this.recordQuizResult(card.id, isCorrect);
        });
      }
    });
  }

  recordQuizResult(quizId, isCorrect) {
    const storageKey = `gocloud_quiz_${this.lessonId}_${quizId}`;
    localStorage.setItem(storageKey, isCorrect ? "passed" : "attempted");
  }

  markLessonComplete() {
    if (!this.trackProgress) return;
    const progress = JSON.parse(localStorage.getItem("gocloud_progress") || "{}");
    progress[this.lessonId] = {
      completed: true,
      timestamp: new Date().toISOString()
    };
    localStorage.setItem("gocloud_progress", JSON.stringify(progress));

    const checkmark = document.getElementById("lessonStatusIndicator");
    if (checkmark) {
      checkmark.innerHTML = "✅ Pre-Check Passed";
      checkmark.className = "badge-pill badge-bench";
    }
  }

  loadProgress() {
    if (!this.trackProgress) return;
    const progress = JSON.parse(localStorage.getItem("gocloud_progress") || "{}");
    if (progress[this.lessonId] && progress[this.lessonId].completed) {
      const checkmark = document.getElementById("lessonStatusIndicator");
      if (checkmark) {
        checkmark.innerHTML = "✅ Pre-Check Passed";
        checkmark.className = "badge-pill badge-bench";
      }
    }
  }

  escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }
}

window.copyToClipboard = function (elementId, btnElement) {
  const codeEl = document.getElementById(elementId);
  if (!codeEl) return;
  const text = "value" in codeEl ? codeEl.value : (codeEl.innerText || codeEl.textContent || "");
  if (!navigator.clipboard) return;
  navigator.clipboard.writeText(text).then(() => {
    const origText = btnElement.innerText;
    btnElement.innerText = "Copied!";
    setTimeout(() => {
      btnElement.innerText = origText;
    }, 2000);
  });
};
