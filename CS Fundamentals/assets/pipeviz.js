/* CS Fundamentals: step-through eager vs lazy pipeline diagram.
 *
 *   <div class="pipeviz" data-src="pv-1"></div>
 *   <script type="application/json" id="pv-1">{ ...spec... }</script>
 *   <script src="../assets/pipeviz.js"></script>
 *
 * Spec:
 *   { "items":  ["kiwi", "fig", ...],                 // source elements, one row each
 *     "stages": ["filter { … }", "map { … }", ...],   // pipeline steps, one column each
 *     "modes": [                                       // tabs, e.g. eager vs lazy
 *       { "name": "List · eager",
 *         "steps": [ { "s": 0, "i": 0, "v": "✓", "cls": "ok", "note": "HTML ok", "call": true }, ... ] } ] }
 * Each step fills cell (item i, stage s) with v. cls is one of ok | no | val | out.
 * "call": true counts the step as one lambda call. Untouched cells stay empty, which is the point.
 */
(function () {
  const css = `
  .pipeviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .pipeviz:focus-visible { border-color: var(--accent); }
  .pv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .pv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .pv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .pv-wrap { overflow-x: auto; }
  .pv-grid { border-collapse: collapse; font-family: var(--mono); font-size: .76rem; width: 100%; }
  .pv-grid th { font-weight: 600; color: var(--ink-soft); text-align: center; vertical-align: bottom; padding: .25rem .3rem; border-bottom: 1px solid var(--rule); }
  .pv-grid td { text-align: center; padding: .3rem .3rem; border-bottom: 1px dashed var(--rule); min-width: 3.6rem; height: 1.6rem; }
  .pv-grid td.pv-item { text-align: left; font-weight: 600; min-width: 0; }
  .pv-grid td span { display: inline-block; padding: .05rem .4rem; border-radius: 4px; }
  .pv-ok span  { color: var(--ok); background: color-mix(in srgb, var(--ok) 12%, var(--bg)); }
  .pv-no span  { color: var(--accent); background: color-mix(in srgb, var(--accent) 10%, var(--bg)); }
  .pv-val span { color: var(--kotlin); background: color-mix(in srgb, var(--kotlin) 10%, var(--bg)); }
  .pv-out span { color: var(--java); background: color-mix(in srgb, var(--java) 12%, var(--bg)); font-weight: 600; }
  .pv-grid td.pv-now { outline: 2px solid var(--accent); outline-offset: -2px; border-radius: 4px; }
  .pv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .pv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .pv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .pv-ctl button:disabled { opacity: .4; cursor: default; }
  .pv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .pv-ctl, .pv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".pipeviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let mode = 0;
    let step = 0;   // number of steps applied
    root.tabIndex = 0;

    const head = "<tr><th></th>" + spec.stages.map((s) => `<th>${esc(s)}</th>`).join("") + "</tr>";
    const rows = spec.items.map((it, i) =>
      `<tr><td class="pv-item">${esc(it)}</td>` +
      spec.stages.map((_, s) => `<td data-i="${i}" data-s="${s}"></td>`).join("") + "</tr>").join("");
    root.innerHTML = `
      <div class="pv-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <div class="pv-wrap"><table class="pv-grid">${head}${rows}</table></div>
      <div class="pv-note" aria-live="polite"></div>
      <div class="pv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Run all</button>
        <span class="pv-count"></span>
      </div>`;

    const cell = (i, s) => root.querySelector(`td[data-i="${i}"][data-s="${s}"]`);

    function draw() {
      const steps = spec.modes[mode].steps;
      root.querySelectorAll("td[data-i]").forEach((td) => { td.className = ""; td.innerHTML = ""; });
      let calls = 0;
      for (let k = 0; k < step; k++) {
        const st = steps[k];
        const td = cell(st.i, st.s);
        td.className = "pv-" + (st.cls || "val") + (k === step - 1 ? " pv-now" : "");
        td.innerHTML = `<span>${esc(st.v)}</span>`;
        if (st.call) calls++;
      }
      root.querySelector(".pv-note").innerHTML = step === 0
        ? "Press <b>Step</b> to run the pipeline one operation at a time."
        : steps[step - 1].note;
      root.querySelector(".pv-count").textContent =
        `step ${step}/${steps.length} · lambda calls: ${calls}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length;
      root.querySelector('[data-act="all"]').disabled = step === steps.length;
      root.querySelectorAll(".pv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = spec.modes[mode].steps.length;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = n;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.modes[mode].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
