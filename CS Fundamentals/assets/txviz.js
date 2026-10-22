/* CS Fundamentals: two-transaction timeline stepper for isolation anomalies.
 *
 *   <div class="txviz" data-src="tx-1"></div>
 *   <script type="application/json" id="tx-1">{ ...spec... }</script>
 *   <script src="../assets/txviz.js"></script>
 *
 * Spec:
 *   { "levels": ["READ UNCOMMITTED", "READ COMMITTED", ...],      // level buttons, left to right
 *     "modes": [                                                   // one tab per anomaly
 *       { "name": "Dirty read",
 *         "row": "Ada's balance",                                  // heading of the shared-value column
 *         "verdict": { "READ UNCOMMITTED": "happens", "READ COMMITTED": "prevented", ... },
 *         "steps": [ { "t1": "...", "t2": "...", "row": "...", "note": "HTML" } ] } ] }
 * Any step field may instead be an object keyed by level, with "*" as the fallback:
 *   "t2": { "*": "reads 10000", "READ UNCOMMITTED": "reads 0" }
 * An empty string leaves that cell blank for the step.
 */
(function () {
  const css = `
  .txviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 46rem; outline: none; }
  .txviz:focus-visible { border-color: var(--accent); }
  .tx-tabs, .tx-levels { display: flex; gap: .35rem; flex-wrap: wrap; margin-bottom: .5rem; }
  .tx-tabs button, .tx-levels button, .tx-ctl button { font: inherit; font-size: .8rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .75rem; cursor: pointer; }
  .tx-levels button { font-family: var(--mono); font-size: .7rem; border-radius: 5px; }
  .tx-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .tx-levels button[aria-pressed="true"] { border-color: var(--java); color: var(--java); font-weight: 600; }
  .tx-grid { width: 100%; border-collapse: collapse; font-size: .8rem; table-layout: fixed; }
  .tx-grid th { text-align: left; font-family: var(--mono); font-size: .66rem; letter-spacing: .06em; text-transform: uppercase; color: var(--ink-soft); padding: .25rem .4rem; border-bottom: 1px solid var(--rule); }
  .tx-grid th.t1 { color: var(--kotlin); } .tx-grid th.t2 { color: var(--java); }
  .tx-grid th:first-child, .tx-grid td:first-child { width: 1.8rem; color: var(--ink-soft); font-family: var(--mono); }
  .tx-grid td { padding: .3rem .4rem; border-bottom: 1px solid var(--rule); vertical-align: top; font-family: var(--mono); font-size: .74rem; overflow-wrap: anywhere; }
  .tx-grid tr.tx-future td { opacity: 0; }
  .tx-grid tr.tx-now td { background: color-mix(in srgb, var(--accent) 9%, var(--bg)); }
  .tx-grid td.tx-err { color: var(--accent); font-weight: 600; }
  .tx-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .tx-verdict { font-size: .82rem; margin: .2rem 0 .5rem; }
  .tx-verdict b { padding: .05rem .45rem; border-radius: 4px; }
  .tx-verdict .happens { color: var(--accent); border: 1px solid var(--accent); }
  .tx-verdict .prevented { color: var(--ok); border: 1px solid var(--ok); }
  .tx-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .tx-ctl button:disabled { opacity: .4; cursor: default; }
  .tx-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .tx-ctl, .tx-tabs, .tx-levels { display: none; } .tx-grid tr.tx-future td { opacity: 1; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));
  const pick = (v, level) => (v && typeof v === "object") ? (level in v ? v[level] : (v["*"] ?? "")) : (v ?? "");
  const isErr = (s) => /^(ERROR|fails|blocked)/i.test(s);

  document.querySelectorAll(".txviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let mode = 0, level = 0, step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="tx-tabs" role="tablist">${spec.modes.map((m, k) => `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <div class="tx-levels">${spec.levels.map((l, k) => `<button data-level="${k}" aria-pressed="false">${esc(l)}</button>`).join("")}</div>
      <table class="tx-grid"><thead></thead><tbody></tbody></table>
      <div class="tx-note" aria-live="polite"></div>
      <div class="tx-verdict"></div>
      <div class="tx-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="tx-count"></span>
      </div>`;

    function draw() {
      const m = spec.modes[mode], L = spec.levels[level];
      root.querySelector("thead").innerHTML = `<tr><th>#</th><th class="t1">T1</th><th class="t2">T2</th><th>${esc(m.row)}</th></tr>`;
      root.querySelector("tbody").innerHTML = m.steps.map((s, k) => {
        const cls = k > step ? "tx-future" : (k === step ? "tx-now" : "");
        const cell = (f) => { const v = pick(s[f], L); return `<td class="${isErr(v) ? "tx-err" : ""}">${esc(v)}</td>`; };
        return `<tr class="${cls}"><td>${k + 1}</td>${cell("t1")}${cell("t2")}${cell("row")}</tr>`;
      }).join("");
      root.querySelector(".tx-note").innerHTML = pick(m.steps[step].note, L);
      const last = step === m.steps.length - 1;
      const v = m.verdict[L];
      root.querySelector(".tx-verdict").innerHTML = last
        ? `At <code>${esc(L)}</code> on H2: <b class="${v}">${v === "happens" ? "anomaly happens" : "prevented"}</b>`
        : "";
      root.querySelector(".tx-count").textContent = `step ${step + 1}/${m.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = last;
      root.querySelector('[data-act="all"]').disabled = last;
      root.querySelectorAll(".tx-tabs button").forEach((b) => b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
      root.querySelectorAll(".tx-levels button").forEach((b) => b.setAttribute("aria-pressed", String(Number(b.dataset.level) === level)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const lastStep = spec.modes[mode].steps.length - 1;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.level !== undefined) { level = Number(b.dataset.level); }
      else if (b.dataset.act === "next") step = Math.min(lastStep, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = lastStep;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const lastStep = spec.modes[mode].steps.length - 1;
      if (e.key === "ArrowRight") { step = Math.min(lastStep, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
