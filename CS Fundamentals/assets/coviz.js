/* CS Fundamentals: step-through coroutine diagrams.
 *
 *   <div class="coviz" data-src="cv-1"></div>
 *   <script type="application/json" id="cv-1">{ ...spec... }</script>
 *   <script src="../assets/coviz.js"></script>
 *
 * Spec: { "tabs": [ tab, ... ] }. Each tab is one of:
 *   { "name": "…", "kind": "threads", "threads": ["T1", "T2"],
 *     "steps": [ { "clock": "0 ms", "run": { "T1": "B", "T2": null },
 *                  "parked": [["A", "wakes at 100 ms"]], "out": "printed line", "note": "HTML" } ] }
 *       Each thread row shows the coroutine running on it, or idle. "parked" lists
 *       suspended coroutines, which hold no thread at all.
 *   { "name": "…", "kind": "tree", "nodes": [ { "id": "p", "label": "coroutineScope", "parent": null } ],
 *     "steps": [ { "state": { "p": "active" }, "out": "printed line", "note": "HTML" } ] }
 *       States: active | cancelling | cancelled | failed | completed. Nodes not in a step's
 *       state map keep their previous state.
 * "out" lines accumulate in a little console, so the real program output appears
 * as you step.
 */
(function () {
  const css = `
  .coviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .coviz:focus-visible { border-color: var(--accent); }
  .cv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .cv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .cv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .cv-stage { display: grid; grid-template-columns: minmax(0, 1fr); gap: .6rem; }
  @media (min-width: 40rem) { .cv-stage { grid-template-columns: minmax(0, 3fr) minmax(0, 2fr); } }
  .cv-clock { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); margin-bottom: .3rem; }
  .cv-row { display: flex; align-items: center; gap: .5rem; padding: .3rem 0; border-bottom: 1px dashed var(--rule); }
  .cv-row .cv-name { font-family: var(--mono); font-size: .75rem; font-weight: 600; width: 3.2rem; flex: none; }
  .cv-slot { flex: 1; min-height: 1.6rem; display: flex; align-items: center; }
  .cv-chip { display: inline-block; font-family: var(--mono); font-size: .75rem; padding: .1rem .55rem; border-radius: 4px; margin: .1rem .25rem .1rem 0; }
  .cv-run { color: var(--kotlin); background: color-mix(in srgb, var(--kotlin) 14%, var(--bg)); font-weight: 600; }
  .cv-idle { color: var(--ink-soft); font-family: var(--mono); font-size: .72rem; font-style: italic; }
  .cv-parked-box { margin-top: .5rem; padding: .4rem .5rem; border: 1px dashed var(--rule); border-radius: 6px; }
  .cv-parked-box .cv-label { font-family: var(--mono); font-size: .65rem; letter-spacing: .08em; text-transform: uppercase; color: var(--ink-soft); }
  .cv-park { color: var(--java); background: color-mix(in srgb, var(--java) 12%, var(--bg)); }
  .cv-park small { color: var(--ink-soft); font-size: .68rem; }
  .cv-console { font-family: var(--mono); font-size: .72rem; background: var(--code-bg); border-radius: 6px; padding: .5rem .6rem; min-height: 6rem; white-space: pre-wrap; overflow-wrap: anywhere; margin: 0; }
  .cv-console .cv-new { color: var(--accent); font-weight: 600; }
  .cv-tree, .cv-tree ul { list-style: none; margin: 0; padding: 0; }
  .cv-tree ul { padding-left: 1.2rem; border-left: 1px solid var(--rule); margin-left: .6rem; }
  .cv-tree li { margin: .3rem 0; }
  .cv-node { display: inline-block; font-family: var(--mono); font-size: .76rem; padding: .15rem .6rem; border-radius: 4px; border: 1px solid transparent; }
  .cv-node em { font-style: normal; font-size: .66rem; margin-left: .4rem; opacity: .85; }
  .cv-active { color: var(--kotlin); background: color-mix(in srgb, var(--kotlin) 12%, var(--bg)); }
  .cv-completed { color: var(--ok); background: color-mix(in srgb, var(--ok) 12%, var(--bg)); }
  .cv-failed { color: var(--accent); background: color-mix(in srgb, var(--accent) 14%, var(--bg)); font-weight: 600; }
  .cv-cancelling { color: var(--accent); border-color: var(--accent); border-style: dashed; }
  .cv-cancelled { color: var(--ink-soft); background: var(--surface); text-decoration: line-through; }
  .cv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .cv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .cv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .cv-ctl button:disabled { opacity: .4; cursor: default; }
  .cv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .cv-ctl, .cv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  function drawThreads(tab, k) {
    const st = tab.steps[k] || { run: {}, parked: [] };
    const rows = tab.threads.map((t) => {
      const c = st.run && st.run[t];
      return `<div class="cv-row"><span class="cv-name">${esc(t)}</span><span class="cv-slot">${
        c ? `<span class="cv-chip cv-run">${esc(c)}</span>` : `<span class="cv-idle">idle</span>`}</span></div>`;
    }).join("");
    const parked = (st.parked || []).map(([c, why]) =>
      `<span class="cv-chip cv-park">${esc(c)} <small>${esc(why)}</small></span>`).join("") ||
      `<span class="cv-idle">none</span>`;
    return `<div class="cv-clock">${esc(st.clock || "")}</div>${rows}
      <div class="cv-parked-box"><div class="cv-label">suspended: holding no thread</div>${parked}</div>`;
  }

  function drawTree(tab, k) {
    const state = {};
    tab.nodes.forEach((n) => { state[n.id] = "active"; });
    for (let i = 0; i <= k && i < tab.steps.length; i++) Object.assign(state, tab.steps[i].state || {});
    const kids = (pid) => tab.nodes.filter((n) => (n.parent || null) === pid);
    const li = (n) => {
      const ch = kids(n.id);
      return `<li><span class="cv-node cv-${state[n.id]}">${esc(n.label)}<em>${state[n.id]}</em></span>${
        ch.length ? `<ul>${ch.map(li).join("")}</ul>` : ""}</li>`;
    };
    return `<ul class="cv-tree">${kids(null).map(li).join("")}</ul>`;
  }

  document.querySelectorAll(".coviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let tab = 0;
    let step = 0;   // index of the step being shown
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="cv-tabs" role="tablist">${spec.tabs.map((t, i) =>
        `<button role="tab" data-tab="${i}">${esc(t.name)}</button>`).join("")}</div>
      <div class="cv-stage"><div class="cv-main"></div><pre class="cv-console" aria-label="program output"></pre></div>
      <div class="cv-note" aria-live="polite"></div>
      <div class="cv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Run all</button>
        <span class="cv-count"></span>
      </div>`;
    if (spec.tabs.length < 2) root.querySelector(".cv-tabs").style.display = "none";

    function draw() {
      const t = spec.tabs[tab];
      const n = t.steps.length;
      root.querySelector(".cv-main").innerHTML = t.kind === "tree" ? drawTree(t, step) : drawThreads(t, step);
      const lines = [];
      for (let i = 0; i <= step; i++) if (t.steps[i].out) lines.push([i, t.steps[i].out]);
      root.querySelector(".cv-console").innerHTML = lines.length
        ? lines.map(([i, l]) => i === step ? `<span class="cv-new">${esc(l)}</span>` : esc(l)).join("\n")
        : `<span class="cv-idle">(no output yet)</span>`;
      root.querySelector(".cv-note").innerHTML = t.steps[step].note;
      root.querySelector(".cv-count").textContent = `step ${step + 1}/${n}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === n - 1;
      root.querySelector('[data-act="all"]').disabled = step === n - 1;
      root.querySelectorAll(".cv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.tab) === tab)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = spec.tabs[tab].steps.length;
      if (b.dataset.tab !== undefined) { tab = Number(b.dataset.tab); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n - 1, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = n - 1;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.tabs[tab].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n - 1, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
