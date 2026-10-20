/* CS Fundamentals: step-through thread pool / virtual thread diagram.
 *
 *   <div class="poolviz" data-src="pool-1"></div>
 *   <script type="application/json" id="pool-1">{ ...spec... }</script>
 *   <script src="../assets/poolviz.js"></script>
 *
 * Spec:
 *   { "modes": [                                   // tabs
 *       { "name": "Fixed pool",
 *         "laneLabel": "worker",                   // one box per lane: worker threads or carrier threads
 *         "lanes": 2,
 *         "queueLabel": "queue", "queueCap": 2,    // queueCap draws that many slots (omit = grows freely)
 *         "sideLabel": "rejected", "sideCls": "rej",   // rej (red) or park (grey): rejected or parked tasks
 *         "steps": [ { "note": "HTML ok",
 *                      "lanes": ["task 1", null],  // what each lane is running right now
 *                      "queue": ["task 3"], "side": ["task 5"], "done": ["task 2"],
 *                      "hot": "task 3" } ] } ] }    // hot = the item that just moved (outlined)
 * Each step is a full snapshot, so Back is just "draw the previous snapshot".
 */
(function () {
  const css = `
  .poolviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .poolviz:focus-visible { border-color: var(--accent); }
  .plv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .plv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .plv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .plv-row { display: flex; gap: .5rem; align-items: flex-start; margin: .35rem 0; flex-wrap: wrap; }
  .plv-label { font-family: var(--mono); font-size: .68rem; text-transform: uppercase; letter-spacing: .06em; color: var(--ink-soft); width: 6.2rem; flex: none; padding-top: .35rem; }
  .plv-items { display: flex; gap: .4rem; flex-wrap: wrap; flex: 1; min-width: 0; min-height: 1.9rem; }
  .plv-lane { border: 1px solid var(--rule); border-radius: 6px; padding: .25rem .4rem; min-width: 7.2rem; background: var(--surface); }
  .plv-lane small { display: block; font-family: var(--mono); font-size: .64rem; color: var(--ink-soft); }
  .plv-chip { display: inline-block; font-family: var(--mono); font-size: .74rem; padding: .12rem .45rem; border-radius: 4px; white-space: nowrap; }
  .plv-run  { color: var(--kotlin); background: color-mix(in srgb, var(--kotlin) 12%, var(--bg)); font-weight: 600; }
  .plv-idle { color: var(--ink-soft); }
  .plv-q    { color: var(--java); background: color-mix(in srgb, var(--java) 12%, var(--bg)); }
  .plv-slot { display: inline-block; font-family: var(--mono); font-size: .74rem; padding: .12rem .45rem; border: 1px dashed var(--rule); border-radius: 4px; color: var(--ink-soft); }
  .plv-rej  { color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--bg)); text-decoration: line-through; }
  .plv-park { color: var(--ink-soft); background: var(--surface); border: 1px solid var(--rule); }
  .plv-done { color: var(--ok); background: color-mix(in srgb, var(--ok) 12%, var(--bg)); }
  .plv-hot  { outline: 2px solid var(--accent); outline-offset: 1px; }
  .plv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .plv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .plv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .plv-ctl button:disabled { opacity: .4; cursor: default; }
  .plv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media (max-width: 30rem) { .plv-label { width: 100%; padding-top: 0; } .plv-lane { min-width: 6rem; } }
  @media print { .plv-ctl, .plv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".poolviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let mode = 0;
    let step = 0;   // 0 = before the first snapshot
    root.tabIndex = 0;

    root.innerHTML = `
      <div class="plv-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <div class="plv-body"></div>
      <div class="plv-note" aria-live="polite"></div>
      <div class="plv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="reset">Reset</button>
        <span class="plv-count"></span>
      </div>`;

    const chip = (text, cls, hot) =>
      `<span class="plv-chip plv-${cls}${text === hot ? " plv-hot" : ""}">${esc(text)}</span>`;

    function draw() {
      const m = spec.modes[mode];
      const snap = step === 0
        ? { lanes: Array(m.lanes).fill(null), queue: [], side: [], done: [], note: null }
        : m.steps[step - 1];
      const hot = snap.hot;
      const lanes = snap.lanes.map((t, k) =>
        `<div class="plv-lane"><small>${esc(m.laneLabel)}-${k + 1}</small>${
          t ? chip(t, "run", hot) : '<span class="plv-chip plv-idle">idle</span>'}</div>`).join("");
      let queue = snap.queue.map((t) => chip(t, "q", hot)).join("");
      if (m.queueCap) {
        for (let k = snap.queue.length; k < m.queueCap; k++) queue += '<span class="plv-slot">empty</span>';
      }
      const side = snap.side.map((t) => chip(t, m.sideCls || "rej", hot)).join("");
      const done = snap.done.map((t) => chip(t, "done", hot)).join("");
      root.querySelector(".plv-body").innerHTML = `
        <div class="plv-row"><div class="plv-label">${esc(m.laneLabel)}s</div><div class="plv-items">${lanes}</div></div>
        <div class="plv-row"><div class="plv-label">${esc(m.queueLabel)}</div><div class="plv-items">${queue}</div></div>
        <div class="plv-row"><div class="plv-label">${esc(m.sideLabel)}</div><div class="plv-items">${side}</div></div>
        <div class="plv-row"><div class="plv-label">done</div><div class="plv-items">${done}</div></div>`;
      root.querySelector(".plv-note").innerHTML = snap.note || "Press <b>Step</b> (or the → key) to submit work one event at a time.";
      root.querySelector(".plv-count").textContent = `step ${step}/${m.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === m.steps.length;
      root.querySelectorAll(".plv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = spec.modes[mode].steps.length;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "reset") step = 0;
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
