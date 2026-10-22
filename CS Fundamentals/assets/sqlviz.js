/* CS Fundamentals: step-through SQL evaluation diagram.
 *
 *   <div class="sqlviz" data-src="sv-1"></div>
 *   <script type="application/json" id="sv-1">{ ...spec... }</script>
 *   <script src="../assets/sqlviz.js"></script>
 *
 * Spec:
 *   { "modes": [
 *       { "name": "Logical order",
 *         "sql": "SELECT …",                          // shown above the grid
 *         "clauses": ["FROM", "WHERE", …],             // the clause bar
 *         "steps": [ { "on": 0,                        // index into clauses (lit up)
 *                      "note": "HTML explanation",
 *                      "cols": ["id", "title", …],
 *                      "rows": [[1, "Kindred", …], …], // null renders as NULL
 *                      "cls":  ["", "drop", "g1", "add", …] } ] } ] }
 * Row classes: "" plain · "drop" row removed by this step · "add" row created by this step
 * (e.g. NULL-filled LEFT JOIN rows) · "g0".."g5" group colours.
 */
(function () {
  const css = `
  .sqlviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .sqlviz:focus-visible { border-color: var(--accent); }
  .sv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .sv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .sv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .sv-sql { font-family: var(--mono); font-size: .74rem; white-space: pre-wrap; background: var(--code-bg); border-radius: 5px; padding: .5rem .7rem; margin: 0 0 .6rem; }
  .sv-bar { display: flex; flex-wrap: wrap; gap: .3rem; margin-bottom: .6rem; }
  .sv-bar span { font-family: var(--mono); font-size: .7rem; padding: .15rem .5rem; border-radius: 4px; border: 1px solid var(--rule); color: var(--ink-soft); }
  .sv-bar span.sv-done { color: var(--ink); }
  .sv-bar span.sv-on { border-color: var(--accent); color: var(--accent); font-weight: 600; background: color-mix(in srgb, var(--accent) 8%, var(--bg)); }
  .sv-wrap { overflow-x: auto; max-height: 26rem; overflow-y: auto; }
  .sv-grid { border-collapse: collapse; font-family: var(--mono); font-size: .72rem; width: 100%; }
  .sv-grid th { position: sticky; top: 0; background: var(--bg); font-weight: 600; color: var(--ink-soft); text-align: left; padding: .2rem .4rem; border-bottom: 1px solid var(--rule); white-space: nowrap; }
  .sv-grid td { padding: .18rem .4rem; border-bottom: 1px dashed var(--rule); white-space: nowrap; }
  .sv-grid td.sv-null { color: var(--ink-soft); font-style: italic; }
  .sv-grid tr.drop td { opacity: .35; text-decoration: line-through; }
  .sv-grid tr.add td { background: color-mix(in srgb, var(--ok) 12%, var(--bg)); }
  .sv-grid tr.g0 td { background: color-mix(in srgb, var(--kotlin) 12%, var(--bg)); }
  .sv-grid tr.g1 td { background: color-mix(in srgb, var(--java) 14%, var(--bg)); }
  .sv-grid tr.g2 td { background: color-mix(in srgb, var(--ok) 13%, var(--bg)); }
  .sv-grid tr.g3 td { background: color-mix(in srgb, var(--accent) 10%, var(--bg)); }
  .sv-grid tr.g4 td { background: color-mix(in srgb, var(--ink-soft) 14%, var(--bg)); }
  .sv-grid tr.g5 td { background: color-mix(in srgb, var(--kotlin) 6%, var(--surface)); }
  .sv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .sv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .sv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .sv-ctl button:disabled { opacity: .4; cursor: default; }
  .sv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .sv-ctl, .sv-tabs { display: none; } .sv-wrap { max-height: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".sqlviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let mode = 0;
    let step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="sv-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <pre class="sv-sql"></pre>
      <div class="sv-bar"></div>
      <div class="sv-wrap"><table class="sv-grid"></table></div>
      <div class="sv-note" aria-live="polite"></div>
      <div class="sv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <span class="sv-count"></span>
      </div>`;

    function draw() {
      const m = spec.modes[mode];
      const st = m.steps[step];
      root.querySelectorAll(".sv-tabs button").forEach((b, k) => b.setAttribute("aria-selected", k === mode));
      root.querySelector(".sv-sql").textContent = m.sql;
      root.querySelector(".sv-bar").innerHTML = m.clauses.map((c, k) =>
        `<span class="${k === st.on ? "sv-on" : k < st.on ? "sv-done" : ""}">${esc(c)}</span>`).join("");
      const head = "<tr>" + st.cols.map((c) => `<th>${esc(c)}</th>`).join("") + "</tr>";
      const body = st.rows.map((r, i) => `<tr class="${(st.cls && st.cls[i]) || ""}">` +
        r.map((v) => v === null ? '<td class="sv-null">NULL</td>' : `<td>${esc(v)}</td>`).join("") + "</tr>").join("");
      root.querySelector(".sv-grid").innerHTML = head + body;
      root.querySelector(".sv-note").innerHTML = st.note;
      root.querySelector(".sv-count").textContent = `step ${step + 1} / ${m.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === m.steps.length - 1;
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.mode !== undefined) { mode = +b.dataset.mode; step = 0; }
      else if (b.dataset.act === "prev" && step > 0) step--;
      else if (b.dataset.act === "next" && step < spec.modes[mode].steps.length - 1) step++;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.modes[mode].steps.length;
      if (e.key === "ArrowRight" && step < n - 1) { step++; draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft" && step > 0) { step--; draw(); e.preventDefault(); }
    });
    draw();
  });
})();
