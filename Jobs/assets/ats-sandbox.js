/* ==========================================================================
   Reusable ATS Text-Stream & Boolean Query Lab (./assets/ats-sandbox.js)
   Simulates left-to-right PDF text extraction and Greenhouse-style Boolean
   evaluation (`""`, `()`, `NOT`, `AND`, `OR`, trailing `*` wildcard).
   ========================================================================== */

(function () {
  const SAMPLES = {
    twoColumn: {
      label: "Two-Column Graphic Resume (Broken)",
      visualNote: "Left sidebar: Skills & Contact | Right main: Experience & Projects",
      extractedText: [
        "ALEX RIVERA SKILLS",
        "alex.rivera@mail.com Kotlin, Jetpack",
        "Madison, WI Compose, Hilt, Room",
        "github.com/arivera-dev EXPERIENCE",
        "PROJECTS Android Developer — JHT",
        "SignalApply Backend Nov 2025 – Present",
        "Spring Boot 4 + Postgres Cut BLE dropoffs 31%",
        "EDUCATION",
        "B.S. Computer Science May 2025"
      ].join("\n")
    },
    singleColumn: {
      label: "Single-Column Linear Resume (ATS-Safe)",
      visualNote: "Strictly vertical top-to-bottom sections with standard headers",
      extractedText: [
        "ALEX RIVERA",
        "Madison, WI | alex.rivera@mail.com | github.com/arivera-dev",
        "",
        "EXPERIENCE",
        "Android Developer — Johnson Health Tech (Nov 2025 – Present)",
        "- Reduced BLE workout-sync dropoffs by 31% using Kotlin Coroutines and Room.",
        "- Cut Jetpack Compose frame jank by 38% across connected-fitness screens.",
        "",
        "PROJECTS",
        "SignalApply (Spring Boot 4, Java 21, PostgreSQL, Jetpack Compose)",
        "- Engineered job-pipeline REST API with Testcontainers integration coverage.",
        "",
        "SKILLS",
        "Mobile: Kotlin, Jetpack Compose, Hilt, Room | Backend: Java, Spring Boot, PostgreSQL",
        "",
        "EDUCATION",
        "B.S. Computer Science — May 2025"
      ].join("\n")
    }
  };

  function tokenizeBoolean(query) {
    const tokens = [];
    let i = 0;
    while (i < query.length) {
      const ch = query[i];
      if (/\s/.test(ch)) {
        i++;
        continue;
      }
      if (ch === "(" || ch === ")") {
        tokens.push({ type: ch, value: ch });
        i++;
        continue;
      }
      if (ch === '"') {
        let j = i + 1;
        while (j < query.length && query[j] !== '"') j++;
        tokens.push({ type: "PHRASE", value: query.slice(i + 1, j) });
        i = j + 1;
        continue;
      }
      let j = i;
      while (j < query.length && !/[\s()"]/.test(query[j])) j++;
      const word = query.slice(i, j);
      if (word === "AND" || word === "OR" || word === "NOT") {
        tokens.push({ type: word, value: word });
      } else {
        tokens.push({ type: "TERM", value: word });
      }
      i = j;
    }
    return tokens;
  }

  function insertImplicitAnd(tokens) {
    const out = [];
    const isValEnd = (t) => t && (t.type === "TERM" || t.type === "PHRASE" || t.type === ")");
    const isValStart = (t) => t && (t.type === "TERM" || t.type === "PHRASE" || t.type === "(" || t.type === "NOT");
    for (let i = 0; i < tokens.length; i++) {
      const cur = tokens[i];
      if (out.length > 0 && isValEnd(out[out.length - 1]) && isValStart(cur)) {
        out.push({ type: "AND", value: "AND" });
      }
      out.push(cur);
    }
    return out;
  }

  function matchTerm(textLower, rawTerm, isPhrase) {
    const target = rawTerm.toLowerCase();
    if (!target) return true;
    if (isPhrase) {
      return textLower.includes(target);
    }
    if (target.endsWith("*")) {
      const stem = target.slice(0, -1).replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
      return new RegExp(`\\b${stem}\\w*`, "i").test(textLower);
    }
    const escaped = target.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    return new RegExp(`\\b${escaped}\\b`, "i").test(textLower);
  }

  function evaluateBoolean(query, text) {
    const tokens = insertImplicitAnd(tokenizeBoolean(query));
    const textLower = text.toLowerCase();
    let pos = 0;

    function peek() {
      return tokens[pos];
    }
    function consume() {
      return tokens[pos++];
    }

    // Precedence: OR (lowest) -> AND -> NOT -> Atom (PHRASE, TERM, ())
    function parseOr() {
      let left = parseAnd();
      while (peek() && peek().type === "OR") {
        consume();
        const right = parseAnd();
        left = left || right;
      }
      return left;
    }

    function parseAnd() {
      let left = parseNot();
      while (peek() && peek().type === "AND") {
        consume();
        const right = parseNot();
        left = left && right;
      }
      return left;
    }

    function parseNot() {
      if (peek() && peek().type === "NOT") {
        consume();
        return !parseNot();
      }
      return parseAtom();
    }

    function parseAtom() {
      const t = peek();
      if (!t) return true;
      if (t.type === "(") {
        consume();
        const val = parseOr();
        if (peek() && peek().type === ")") consume();
        return val;
      }
      if (t.type === "PHRASE") {
        consume();
        return matchTerm(textLower, t.value, true);
      }
      if (t.type === "TERM") {
        consume();
        return matchTerm(textLower, t.value, false);
      }
      consume();
      return true;
    }

    if (tokens.length === 0) return { ok: false, error: "Enter a query." };
    try {
      return { ok: true, match: parseOr() };
    } catch (e) {
      return { ok: false, error: e.message };
    }
  }

  function initSandboxes() {
    document.querySelectorAll(".ats-sandbox[data-sandbox]").forEach((root) => {
      let mode = "twoColumn";

      root.innerHTML = `
        <div class="sandbox-panel">
          <div class="sandbox-controls">
            <button type="button" class="sandbox-btn is-active" data-mode="twoColumn">Two-Column Layout</button>
            <button type="button" class="sandbox-btn" data-mode="singleColumn">Single-Column Layout</button>
          </div>
          <label for="ats-bool-input" style="display:block;font-weight:600;margin-bottom:0.35rem;">
            Recruiter Boolean Query (Greenhouse / LinkedIn Precedence):
          </label>
          <input id="ats-bool-input" class="sandbox-input" type="text"
            value='("Android Developer" OR "Android Engineer") AND Kotlin AND "Jetpack Compose" NOT Senior' />
          <div id="ats-sandbox-verdict" style="font-weight:600;margin-bottom:0.75rem;"></div>
          <div class="sandbox-grid">
            <div class="sandbox-box">
              <h4>Extracted Plain-Text Stream</h4>
              <pre style="margin:0;white-space:pre-wrap;"><code id="ats-stream-out"></code></pre>
            </div>
            <div class="sandbox-box">
              <h4>Field Mapping Diagnostics</h4>
              <div id="ats-field-diag" style="font-size:0.84rem;line-height:1.5;"></div>
            </div>
          </div>
        </div>
      `;

      const buttons = root.querySelectorAll(".sandbox-btn[data-mode]");
      const input = root.querySelector("#ats-bool-input");
      const verdictEl = root.querySelector("#ats-sandbox-verdict");
      const streamEl = root.querySelector("#ats-stream-out");
      const diagEl = root.querySelector("#ats-field-diag");

      function render() {
        const sample = SAMPLES[mode];
        streamEl.textContent = sample.extractedText;
        const q = input.value;
        const res = evaluateBoolean(q, sample.extractedText);

        if (!res.ok) {
          verdictEl.textContent = `Query issue: ${res.error}`;
          verdictEl.style.color = "var(--warn)";
        } else if (res.match) {
          verdictEl.textContent = "MATCH — Candidate surfaced in recruiter search results.";
          verdictEl.style.color = "var(--ok)";
        } else {
          verdictEl.textContent = "NO MATCH — Candidate filtered out of recruiter search.";
          verdictEl.style.color = "var(--accent)";
        }

        if (mode === "twoColumn") {
          diagEl.innerHTML = `
            <p style="margin:0 0 0.45rem;"><strong>Status:</strong> <span style="color:var(--accent)">Field Corruption</span></p>
            <ul style="margin:0;padding-left:1.15rem;">
              <li><code>Name</code> merged with header <code>SKILLS</code> -> <code>ALEX RIVERA SKILLS</code>.</li>
              <li><code>Kotlin, Jetpack Compose</code> split across lines 2 and 3, breaking exact-phrase search.</li>
              <li><code>Projects</code> row interleaved with <code>Android Developer — JHT</code>, corrupting structured title/date extraction.</li>
            </ul>
          `;
        } else {
          diagEl.innerHTML = `
            <p style="margin:0 0 0.45rem;"><strong>Status:</strong> <span style="color:var(--ok)">Clean Top-to-Bottom Parse</span></p>
            <ul style="margin:0;padding-left:1.15rem;">
              <li><code>Name</code> & <code>Contact</code> isolated cleanly on lines 1–2.</li>
              <li>Standard headers (<code>EXPERIENCE</code>, <code>PROJECTS</code>, <code>SKILLS</code>, <code>EDUCATION</code>) map accurately.</li>
              <li>Clean phrase boundaries preserve <code>"Android Developer"</code>, <code>"Jetpack Compose"</code>, and <code>"Spring Boot"</code>.</li>
            </ul>
          `;
        }
      }

      buttons.forEach((btn) => {
        btn.addEventListener("click", () => {
          mode = btn.getAttribute("data-mode");
          buttons.forEach((b) => b.classList.toggle("is-active", b === btn));
          render();
        });
      });

      input.addEventListener("input", render);
      render();
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initSandboxes);
  } else {
    initSandboxes();
  }
})();
