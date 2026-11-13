// Shared interactive engine for all Kotlin lessons and reference pages.
//
// Capabilities:
//   1. Runnable Code Examples (<code class="kt-run"> outside exercises):
//      - Editable code area with Tab / Shift+Tab / Enter auto-indent & Cmd/Ctrl+Enter shortcut
//      - In-page "▶ Run" button executing via local /api/run proxy or JetBrains Kotlin Compiler API
//      - Inline console displaying stdout, compiler warnings, and line-numbered compile/runtime errors
//   2. Runnable Code Practice (<section class="exercise">):
//      - "Your Code" tab + "Final Answer" tab (both runnable in-page!)
//      - "▶ Run & Check" verifies compile status, runtime exceptions, check(...) assertions, and expected output
//      - Progressive step-by-step Hint reveal ("💡 Reveal Hint 1 of N") right below the editor
//   3. Multi-Choice Code Quizzes (.quiz):
//      - Instant per-option explanations showing BOTH why the correct answer is right AND why each distractor is wrong
//      - Live score tracker + per-quiz "↻ Retry" button

(function () {
  const REMOTE_ENDPOINTS = [
    "https://api.kotlinlang.org/api/2.1.20/compiler/run",
    "https://api.kotlinlang.org/api/2.0.21/compiler/run",
    "https://api.kotlinlang.org/api/2.4.21/compiler/run",
    "https://api.kotlinlang.org/api/compiler/run"
  ];
  let cachedEndpoint = null;
  let versionChecked = false;

  function escapeHtml(str) {
    return String(str)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");
  }

  function formatInlineCode(str) {
    return escapeHtml(str).replace(/`([^`]+)`/g, "<code>$1</code>");
  }

  function decodeEntities(str) {
    const txt = document.createElement("textarea");
    txt.innerHTML = str;
    return txt.value;
  }

  function stripSampleMarkers(code) {
    return code
      .split("\n")
      .filter((line) => {
        const t = line.trim();
        return t !== "//sampleStart" && t !== "//sampleEnd";
      })
      .join("\n")
      .trim();
  }

  function loadAnswersScript(callback) {
    if (window.EXERCISE_ANSWERS) {
      if (callback) callback();
      return;
    }
    const script = document.createElement("script");
    script.src = (location.pathname.includes("/lessons/") || location.pathname.includes("/reference/")) ? "../assets/answers.js" : "assets/answers.js";
    script.onload = () => { if (callback) callback(); };
    script.onerror = () => { if (callback) callback(); };
    document.head.appendChild(script);
  }

  function getLessonNumber() {
    const match = location.pathname.match(/(\d{4})/);
    return match ? match[1] : "";
  }

  function autoResize(textarea) {
    textarea.style.height = "auto";
    const newHeight = Math.max(150, textarea.scrollHeight + 10);
    textarea.style.height = newHeight + "px";
  }

  function bindCodeShortcuts(textarea, onRun) {
    textarea.addEventListener("keydown", function (e) {
      if ((e.metaKey || e.ctrlKey) && e.key === "Enter") {
        e.preventDefault();
        if (onRun) onRun();
        return;
      }

      const start = this.selectionStart;
      const end = this.selectionEnd;
      const value = this.value;

      if (e.key === "Tab") {
        e.preventDefault();
        if (e.shiftKey) {
          const lineStart = value.lastIndexOf("\n", start - 1) + 1;
          if (value.substr(lineStart, 4) === "    ") {
            this.value = value.substring(0, lineStart) + value.substring(lineStart + 4);
            this.selectionStart = this.selectionEnd = Math.max(lineStart, start - 4);
          }
        } else {
          this.value = value.substring(0, start) + "    " + value.substring(end);
          this.selectionStart = this.selectionEnd = start + 4;
        }
        autoResize(this);
      } else if (e.key === "Enter") {
        const currentLine = value.substring(value.lastIndexOf("\n", start - 1) + 1, start);
        const indentMatch = currentLine.match(/^[ ]+/);
        const extraIndent = currentLine.trim().endsWith("{") ? "    " : "";
        if (indentMatch || extraIndent) {
          e.preventDefault();
          const indent = indentMatch ? indentMatch[0] : "";
          const inserted = "\n" + indent + extraIndent;
          this.value = value.substring(0, start) + inserted + value.substring(end);
          this.selectionStart = this.selectionEnd = start + inserted.length;
          autoResize(this);
        }
      }
    });

    textarea.addEventListener("input", function () {
      autoResize(this);
    });

    setTimeout(() => autoResize(textarea), 40);
  }

  function setupCopyButton(btn, textGetter) {
    btn.addEventListener("click", () => {
      const original = btn.textContent;
      navigator.clipboard.writeText(textGetter()).then(() => {
        btn.textContent = "Copied!";
        btn.classList.add("copied");
        setTimeout(() => {
          btn.textContent = original;
          btn.classList.remove("copied");
        }, 1500);
      });
    });
  }

  // ---------- Kotlin Compiler Execution Engine ----------
  async function discoverLatestEndpoint() {
    if (versionChecked) return;
    versionChecked = true;
    try {
      const resp = await fetch("https://api.kotlinlang.org/versions", { method: "GET" });
      if (resp.ok) {
        const versions = await resp.json();
        const latest = Array.isArray(versions)
          ? versions.find((v) => v.latestStable) || versions[versions.length - 1]
          : null;
        if (latest && latest.version) {
          REMOTE_ENDPOINTS.unshift(`https://api.kotlinlang.org/api/${latest.version}/compiler/run`);
        }
      }
    } catch (_) {
      // Ignore discovery errors; fallback list handles execution
    }
  }

  async function postCompileRequest(code) {
    const body = JSON.stringify({
      args: "",
      files: [{ name: "File.kt", text: code, publicId: "" }],
      confType: "java"
    });

    if (cachedEndpoint) {
      const r = await fetch(cachedEndpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body
      });
      if (r.ok) return await r.json();
      cachedEndpoint = null;
    }

    const candidates = [];
    if (location.protocol === "http:" || location.protocol === "https:") {
      candidates.push("/api/run");
    }
    await discoverLatestEndpoint();
    for (const url of REMOTE_ENDPOINTS) {
      if (!candidates.includes(url)) candidates.push(url);
    }

    let lastErr = null;
    for (const url of candidates) {
      try {
        const resp = await fetch(url, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body
        });
        if (resp.ok) {
          const json = await resp.json();
          if (!json.proxyError) {
            cachedEndpoint = url;
            return json;
          }
          lastErr = new Error(json.proxyError);
        } else {
          lastErr = new Error(`HTTP ${resp.status}`);
        }
      } catch (err) {
        lastErr = err;
      }
    }
    throw lastErr || new Error("Unable to reach Kotlin compiler service.");
  }

  function parseRunOutput(rawText) {
    if (!rawText) return { out: "", err: "" };
    const outParts = [];
    const errParts = [];
    const outRegex = /<outStream>([\s\S]*?)<\/outStream>/g;
    const errRegex = /<errStream>([\s\S]*?)<\/errStream>/g;
    let m;
    while ((m = outRegex.exec(rawText)) !== null) {
      outParts.push(decodeEntities(m[1]));
    }
    while ((m = errRegex.exec(rawText)) !== null) {
      errParts.push(decodeEntities(m[1]));
    }
    if (outParts.length === 0 && errParts.length === 0) {
      outParts.push(decodeEntities(rawText.replace(/<\/?(outStream|errStream)>/g, "")));
    }
    return {
      out: outParts.join("").trimEnd(),
      err: errParts.join("").trimEnd()
    };
  }

  async function executeInPanel(code, consoleEl, btnRun, opts) {
    const isCheckMode = opts && opts.mode === "check";
    const expectedOutput = opts && opts.expectedOutput ? opts.expectedOutput.trim() : null;
    const originalLabel = btnRun.textContent;

    btnRun.disabled = true;
    btnRun.textContent = "Running…";
    consoleEl.className = "kt-console visible running";
    consoleEl.innerHTML = `<div class="console-header"><span class="status-pill running">⏳ Compiling &amp; running Kotlin…</span></div>`;

    try {
      const result = await postCompileRequest(code);
      const fileErrors = (result.errors && (result.errors["File.kt"] || Object.values(result.errors)[0])) || [];
      const compileErrors = fileErrors.filter((e) => e.severity === "ERROR");
      const warnings = fileErrors.filter((e) => e.severity === "WARNING");
      const streams = parseRunOutput(result.text || result.output || "");
      const exc = result.exception;

      let statusClass = "good";
      let statusLabel = isCheckMode ? "✅ All checks passed!" : "✅ Executed cleanly";
      const detailsHtml = [];

      if (compileErrors.length > 0) {
        statusClass = "bad";
        statusLabel = `❌ Compilation failed (${compileErrors.length} error${compileErrors.length > 1 ? "s" : ""})`;
        const list = compileErrors.map((e) => {
          const line = e.interval && e.interval.start ? e.interval.start.line + 1 : "?";
          return `<li><strong>Line ${line}:</strong> ${escapeHtml(e.message)}</li>`;
        }).join("");
        detailsHtml.push(`<ul class="console-diagnostics bad">${list}</ul>`);
      } else if (exc) {
        const fullName = exc.fullName || "Exception";
        const shortName = fullName.split(".").pop();
        const msg = exc.message ? decodeEntities(exc.message) : "(no message)";
        const fileFrame = Array.isArray(exc.stackTrace)
          ? exc.stackTrace.find((f) => f.fileName === "File.kt")
          : null;
        const lineNote = fileFrame && fileFrame.lineNumber ? ` (line ${fileFrame.lineNumber})` : "";

        if (fullName === "kotlin.NotImplementedError") {
          statusClass = "warn";
          statusLabel = "🚧 Incomplete — TODO() reached";
          detailsHtml.push(`<p class="console-exc warn"><strong>TODO${lineNote}:</strong> ${escapeHtml(msg)}</p>`);
        } else if (isCheckMode && (shortName === "IllegalStateException" || shortName === "AssertionError" || shortName === "IllegalArgumentException")) {
          statusClass = "bad";
          statusLabel = "❌ Check failed";
          detailsHtml.push(`<p class="console-exc bad"><strong>${escapeHtml(shortName)}${lineNote}:</strong> ${escapeHtml(msg)}</p>`);
        } else {
          statusClass = "bad";
          statusLabel = `❌ Runtime ${escapeHtml(shortName)}`;
          detailsHtml.push(`<p class="console-exc bad"><strong>${escapeHtml(fullName)}${lineNote}:</strong> ${escapeHtml(msg)}</p>`);
        }
      } else if (isCheckMode) {
        if (expectedOutput && streams.out.trim() !== expectedOutput) {
          statusClass = "bad";
          statusLabel = "❌ Output mismatch";
          detailsHtml.push(
            `<p class="console-exc bad"><strong>Expected output:</strong>\n<code>${escapeHtml(expectedOutput)}</code>\n<strong>Actual output:</strong>\n<code>${escapeHtml(streams.out.trim() || "(empty)")}</code></p>`
          );
        } else if (!expectedOutput && !/\bcheck\s*\(|\brequire\s*\(/.test(code)) {
          statusClass = "good";
          statusLabel = "✅ Program ran (compare output with checklist)";
        }
      }

      if (warnings.length > 0) {
        const wList = warnings.map((w) => {
          const line = w.interval && w.interval.start ? w.interval.start.line + 1 : "?";
          return `<li><strong>Warning (line ${line}):</strong> ${escapeHtml(w.message)}</li>`;
        }).join("");
        detailsHtml.push(`<ul class="console-diagnostics warn">${wList}</ul>`);
      }

      if (streams.out) {
        detailsHtml.push(`<div class="console-stdout-label">Program output</div><pre class="console-stdout">${escapeHtml(streams.out)}</pre>`);
      } else if (compileErrors.length === 0 && !exc) {
        detailsHtml.push(`<div class="console-stdout muted">(No stdout printed)</div>`);
      }

      if (streams.err) {
        detailsHtml.push(`<pre class="console-stderr">${escapeHtml(streams.err)}</pre>`);
      }

      consoleEl.className = `kt-console visible ${statusClass}`;
      consoleEl.innerHTML =
        `<div class="console-header"><span class="status-pill ${statusClass}">${statusLabel}</span><button type="button" class="console-close" title="Hide output">✕</button></div>` +
        detailsHtml.join("");
      const closeBtn = consoleEl.querySelector(".console-close");
      if (closeBtn) closeBtn.addEventListener("click", () => { consoleEl.className = "kt-console"; });
    } catch (err) {
      const fileHint = location.protocol === "file:"
        ? ` If your browser blocks direct <code>file://</code> requests, run <code>python3 serve.sh</code> in the course folder and open <code>http://localhost:8000</code>.`
        : "";
      consoleEl.className = "kt-console visible warn";
      consoleEl.innerHTML =
        `<div class="console-header"><span class="status-pill warn">⚠️ Compiler unreachable (offline or blocked)</span></div>` +
        `<p class="console-exc warn">Could not reach the Kotlin compiler service (${escapeHtml(err.message || "network error")}). Check your internet connection or use <strong>Copy</strong> to run in IntelliJ / Android Studio.${fileHint}</p>`;
    } finally {
      btnRun.disabled = false;
      btnRun.textContent = originalLabel;
    }
  }

  // ---------- Progressive Hints Builder ----------
  function initHintsDrawer(section) {
    const hintEls = Array.from(section.querySelectorAll("details.hint"));
    if (hintEls.length === 0) return;

    const hintsData = hintEls.map((el, idx) => {
      const summary = el.querySelector("summary");
      const title = summary ? summary.textContent.trim() : `Hint ${idx + 1}`;
      const clone = el.cloneNode(true);
      const cloneSum = clone.querySelector("summary");
      if (cloneSum) cloneSum.remove();
      return { title, html: clone.innerHTML.trim() };
    });

    const wrapper = document.createElement("div");
    wrapper.className = "hints-drawer";

    const controls = document.createElement("div");
    controls.className = "hints-controls";

    const btnReveal = document.createElement("button");
    btnReveal.type = "button";
    btnReveal.className = "hint-reveal-btn";
    btnReveal.textContent = `💡 Show Hint 1 of ${hintsData.length}`;

    const list = document.createElement("div");
    list.className = "hints-list";

    let revealed = 0;
    btnReveal.addEventListener("click", () => {
      if (revealed >= hintsData.length) return;
      const item = hintsData[revealed];
      const card = document.createElement("div");
      card.className = "hint-step";
      card.innerHTML = `<span class="hint-step-label">${escapeHtml(item.title)}</span><div class="hint-step-body">${item.html}</div>`;
      list.appendChild(card);
      revealed += 1;
      if (revealed < hintsData.length) {
        btnReveal.textContent = `💡 Show Hint ${revealed + 1} of ${hintsData.length}`;
      } else {
        btnReveal.textContent = `✓ All ${hintsData.length} hints shown`;
        btnReveal.disabled = true;
      }
    });

    controls.appendChild(btnReveal);
    wrapper.appendChild(controls);
    wrapper.appendChild(list);

    hintEls[0].parentNode.insertBefore(wrapper, hintEls[0]);
    hintEls.forEach((el) => el.remove());
  }

  // ---------- Practice Exercises (Run & Check + Hints + Final Answer) ----------
  function initExerciseEditors() {
    const lessonNum = getLessonNumber();
    const exercises = Array.from(document.querySelectorAll("section.exercise"));

    exercises.forEach((section, index) => {
      const exIndex = index + 1;
      const exKey = `${lessonNum}-ex${exIndex}`;

      const codeEl = section.querySelector("code.kt-run") || section.querySelector("code.kt-show");
      const rawStarter = codeEl ? stripSampleMarkers(codeEl.textContent) : "fun main() {\n    // Write your solution here\n}";
      const expectedOutput = section.dataset.expectedOutput || (codeEl && codeEl.dataset.expectedOutput) || null;

      const answerData = (window.EXERCISE_ANSWERS && window.EXERCISE_ANSWERS[exKey]) || null;
      const answerCode = answerData ? stripSampleMarkers(answerData.code) : "";
      const answerWhy = answerData ? answerData.why : "";
      const answerExpected = (answerData && answerData.expectedOutput) || expectedOutput;

      const container = document.createElement("div");
      container.className = "exercise-tabs";

      const tabBar = document.createElement("div");
      tabBar.className = "exercise-tab-bar";

      const btnCode = document.createElement("button");
      btnCode.type = "button";
      btnCode.className = "tab-btn active";
      btnCode.textContent = "Your Code";

      const btnAnswer = document.createElement("button");
      btnAnswer.type = "button";
      btnAnswer.className = "tab-btn";
      btnAnswer.textContent = "Final Answer";

      const actions = document.createElement("div");
      actions.className = "tab-actions";

      const btnRun = document.createElement("button");
      btnRun.type = "button";
      btnRun.className = "action-btn run-btn";
      btnRun.textContent = "▶ Run & Check";
      btnRun.title = "Compile, run, and verify checks (Cmd/Ctrl + Enter)";

      const btnCopy = document.createElement("button");
      btnCopy.type = "button";
      btnCopy.className = "action-btn copy-btn";
      btnCopy.textContent = "Copy";

      const btnReset = document.createElement("button");
      btnReset.type = "button";
      btnReset.className = "action-btn reset-btn";
      btnReset.textContent = "Reset";

      actions.appendChild(btnRun);
      actions.appendChild(btnCopy);
      actions.appendChild(btnReset);

      tabBar.appendChild(btnCode);
      tabBar.appendChild(btnAnswer);
      tabBar.appendChild(actions);

      const contentCode = document.createElement("div");
      contentCode.className = "tab-content tab-code active";

      const textarea = document.createElement("textarea");
      textarea.className = "kt-editor";
      textarea.value = rawStarter;
      textarea.setAttribute("spellcheck", "false");
      textarea.setAttribute("autocomplete", "off");
      textarea.setAttribute("autocapitalize", "off");

      const consoleEl = document.createElement("div");
      consoleEl.className = "kt-console";

      const triggerRun = () => {
        const activeCode = contentCode.classList.contains("active") ? textarea.value : (answerCode || textarea.value);
        executeInPanel(activeCode, consoleEl, btnRun, { mode: "check", expectedOutput: answerExpected });
      };

      bindCodeShortcuts(textarea, triggerRun);
      contentCode.appendChild(textarea);

      const contentAnswer = document.createElement("div");
      contentAnswer.className = "tab-content tab-answer";
      const answerBody = document.createElement("div");
      answerBody.className = "answer-body";

      if (answerCode) {
        const answerPre = document.createElement("pre");
        const answerCodeTag = document.createElement("code");
        answerCodeTag.textContent = answerCode;
        answerPre.appendChild(answerCodeTag);
        answerBody.appendChild(answerPre);

        if (answerWhy) {
          const whyP = document.createElement("p");
          whyP.className = "why-idiomatic";
          whyP.innerHTML = `<strong>Why this final answer works:</strong> ${formatInlineCode(answerWhy)}`;
          answerBody.appendChild(whyP);
        }
      } else {
        const noAnswer = document.createElement("p");
        noAnswer.style.color = "var(--muted)";
        noAnswer.textContent = "Use the hints below, or ask your teacher in the workspace to review your solution.";
        answerBody.appendChild(noAnswer);
      }
      contentAnswer.appendChild(answerBody);

      btnCode.addEventListener("click", () => {
        btnCode.classList.add("active");
        btnAnswer.classList.remove("active");
        contentCode.classList.add("active");
        contentAnswer.classList.remove("active");
        btnRun.textContent = "▶ Run & Check";
        btnReset.style.display = "";
        autoResize(textarea);
      });

      btnAnswer.addEventListener("click", () => {
        btnAnswer.classList.add("active");
        btnCode.classList.remove("active");
        contentAnswer.classList.add("active");
        contentCode.classList.remove("active");
        btnRun.textContent = "▶ Run Answer";
        btnReset.style.display = "none";
      });

      btnRun.addEventListener("click", triggerRun);
      setupCopyButton(btnCopy, () => contentCode.classList.contains("active") ? textarea.value : (answerCode || ""));
      btnReset.addEventListener("click", () => {
        textarea.value = rawStarter;
        consoleEl.className = "kt-console";
        autoResize(textarea);
      });

      container.appendChild(tabBar);
      container.appendChild(contentCode);
      container.appendChild(contentAnswer);
      container.appendChild(consoleEl);

      if (codeEl) {
        codeEl.parentNode.replaceChild(container, codeEl);
      } else {
        section.appendChild(container);
      }

      initHintsDrawer(section);
    });
  }

  // ---------- Standalone Runnable Code Examples ----------
  function initStandaloneEditors() {
    const standaloneBlocks = Array.from(document.querySelectorAll("code.kt-run")).filter(
      (el) => !el.closest("section.exercise")
    );

    standaloneBlocks.forEach((codeEl) => {
      const starter = stripSampleMarkers(codeEl.textContent);

      const container = document.createElement("div");
      container.className = "exercise-tabs example-runner";

      const tabBar = document.createElement("div");
      tabBar.className = "exercise-tab-bar";

      const label = document.createElement("span");
      label.className = "tab-btn active";
      label.textContent = "Interactive Example";

      const actions = document.createElement("div");
      actions.className = "tab-actions";

      const btnRun = document.createElement("button");
      btnRun.type = "button";
      btnRun.className = "action-btn run-btn";
      btnRun.textContent = "▶ Run";
      btnRun.title = "Compile and run example (Cmd/Ctrl + Enter)";

      const btnCopy = document.createElement("button");
      btnCopy.type = "button";
      btnCopy.className = "action-btn copy-btn";
      btnCopy.textContent = "Copy";

      const btnReset = document.createElement("button");
      btnReset.type = "button";
      btnReset.className = "action-btn reset-btn";
      btnReset.textContent = "Reset";

      actions.appendChild(btnRun);
      actions.appendChild(btnCopy);
      actions.appendChild(btnReset);
      tabBar.appendChild(label);
      tabBar.appendChild(actions);

      const content = document.createElement("div");
      content.className = "tab-content active";

      const textarea = document.createElement("textarea");
      textarea.className = "kt-editor example-editor";
      textarea.value = starter;
      textarea.setAttribute("spellcheck", "false");

      const consoleEl = document.createElement("div");
      consoleEl.className = "kt-console";

      const triggerRun = () => executeInPanel(textarea.value, consoleEl, btnRun, { mode: "example" });

      bindCodeShortcuts(textarea, triggerRun);
      btnRun.addEventListener("click", triggerRun);
      setupCopyButton(btnCopy, () => textarea.value);
      btnReset.addEventListener("click", () => {
        textarea.value = starter;
        consoleEl.className = "kt-console";
        autoResize(textarea);
      });

      content.appendChild(textarea);
      container.appendChild(tabBar);
      container.appendChild(content);
      container.appendChild(consoleEl);

      codeEl.parentNode.replaceChild(container, codeEl);
    });
  }

  // ---------- Multi-Choice Quizzes with Per-Option Correct/Wrong Explanations ----------
  function initQuizzes() {
    const quizzes = Array.from(document.querySelectorAll(".quiz"));
    const scoreEl = document.querySelector(".quiz-score");
    let answered = 0;
    let correct = 0;

    function updateScore() {
      if (!scoreEl) return;
      scoreEl.textContent = `${correct} / ${answered} correct · ${quizzes.length - answered} left`;
    }

    quizzes.forEach((quiz) => {
      const answer = (quiz.dataset.answer || "").toLowerCase();
      const items = Array.from(quiz.querySelectorAll(".options li"));
      const buttons = Array.from(quiz.querySelectorAll(".options button"));
      const explain = quiz.querySelector(".explain");
      const baseExplainHtml = explain ? explain.innerHTML : "";

      const headerRow = document.createElement("div");
      headerRow.className = "quiz-footer-actions";
      const btnRetry = document.createElement("button");
      btnRetry.type = "button";
      btnRetry.className = "quiz-retry-btn";
      btnRetry.textContent = "↻ Retry question";
      btnRetry.style.display = "none";
      headerRow.appendChild(btnRetry);
      quiz.appendChild(headerRow);

      let wasCorrect = false;

      buttons.forEach((btn) => {
        btn.type = "button";
        btn.addEventListener("click", () => {
          if (quiz.classList.contains("answered")) return;
          quiz.classList.add("answered");

          const pickedKey = (btn.dataset.key || "").toLowerCase();
          const right = pickedKey === answer;
          wasCorrect = right;
          answered += 1;
          if (right) correct += 1;

          buttons.forEach((b) => {
            b.disabled = true;
            const k = (b.dataset.key || "").toLowerCase();
            const isAnswer = k === answer;
            const isPicked = b === btn;

            if (isAnswer) b.classList.add("correct");
            else if (isPicked) b.classList.add("wrong");

            const whyText = b.dataset.why || (b.parentElement && b.parentElement.dataset.why) || "";
            if (whyText) {
              const whyBox = document.createElement("div");
              const tone = isAnswer ? "good" : (isPicked ? "bad" : "other");
              const prefix = isAnswer
                ? "✓ Why correct: "
                : (isPicked ? "✗ Why your choice is wrong: " : "• Why wrong: ");
              whyBox.className = `opt-why ${tone}`;
              const cleanedWhy = whyText.replace(/^(Correct|Wrong|Incorrect)\s*:\s*/i, "");
              whyBox.innerHTML = `<strong>${prefix}</strong>${formatInlineCode(cleanedWhy)}`;
              b.parentElement.appendChild(whyBox);
            }
          });

          if (explain && baseExplainHtml.trim()) {
            const verdict = document.createElement("span");
            verdict.className = "verdict " + (right ? "good" : "bad");
            verdict.textContent = right ? "Correct — Takeaway: " : "Not quite — Key rule: ";
            explain.innerHTML = baseExplainHtml;
            explain.insertBefore(verdict, explain.firstChild);
          }

          btnRetry.style.display = "inline-block";
          updateScore();
        });
      });

      btnRetry.addEventListener("click", () => {
        if (!quiz.classList.contains("answered")) return;
        quiz.classList.remove("answered");
        answered -= 1;
        if (wasCorrect) correct -= 1;

        items.forEach((li) => {
          li.querySelectorAll(".opt-why").forEach((el) => el.remove());
        });
        buttons.forEach((b) => {
          b.disabled = false;
          b.classList.remove("correct", "wrong");
        });
        if (explain) explain.innerHTML = baseExplainHtml;
        btnRetry.style.display = "none";
        updateScore();
      });
    });

    updateScore();
  }

  document.addEventListener("DOMContentLoaded", () => {
    initQuizzes();
    loadAnswersScript(() => {
      initExerciseEditors();
      initStandaloneEditors();
    });
  });
})();
