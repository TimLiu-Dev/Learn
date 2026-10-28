/* CS Fundamentals: step-through system-architecture diagram that grows one layer at a time.
 *
 *   <div class="designviz" data-src="dv-1"></div>
 *   <script type="application/json" id="dv-1">{ ...spec... }</script>
 *   <script src="../assets/designviz.js"></script>
 *
 * Spec:
 *   { "w": 600, "h": 320,                                   // SVG viewBox size
 *     "nodes": [ { "id": "db", "label": "SQLite", "sub": "primary", "x": 470, "y": 120,
 *                  "kind": "db", "from": 0, "until": 9 } ],  // visible for steps from..until-1 (until optional)
 *     "edges": [ { "a": "app", "b": "db", "from": 0, "until": 2, "dashed": false, "label": "SQL" } ],
 *     "steps": [ { "title": "1 · One server", "why": "HTML: the estimate or bottleneck behind this step" } ] }
 * kind is one of client | edge | server | cache | db | queue (only changes colours).
 * Nodes and edges added at the current step are highlighted.
 */
(function () {
  const css = `
  .designviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .designviz:focus-visible { border-color: var(--accent); }
  .dv-title { font-weight: 600; margin: 0 0 .4rem; }
  .dv-svg { width: 100%; height: auto; display: block; }
  .dv-svg text { font-family: var(--mono); font-size: 12px; fill: var(--ink); text-anchor: middle; dominant-baseline: middle; }
  .dv-svg text.dv-sub { font-size: 9.5px; fill: var(--ink-soft); }
  .dv-svg text.dv-elabel { font-size: 9.5px; fill: var(--ink-soft); }
  .dv-svg rect { fill: var(--surface); stroke: var(--ink-soft); stroke-width: 1.2; }
  .dv-svg line { stroke: var(--ink-soft); stroke-width: 1.4; }
  .dv-svg line.dv-dash { stroke-dasharray: 5 4; }
  .dv-svg .k-client rect { fill: var(--bg); }
  .dv-svg .k-cache rect { stroke: var(--kotlin); }
  .dv-svg .k-db rect { stroke: var(--java); }
  .dv-svg .k-queue rect { stroke: var(--ok); }
  .dv-svg .k-edge rect { stroke: var(--accent); }
  .dv-svg .dv-new rect { stroke: var(--accent); stroke-width: 2.6; fill: color-mix(in srgb, var(--accent) 10%, var(--surface)); }
  .dv-svg line.dv-new { stroke: var(--accent); stroke-width: 2.2; }
  .dv-note { font-size: .9rem; min-height: 3.2rem; margin: .6rem 0 .4rem; padding: .5rem .75rem; background: var(--surface); border-radius: 5px; }
  .dv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .dv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .dv-ctl button:disabled { opacity: .4; cursor: default; }
  .dv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .dv-ctl { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const NS = "http://www.w3.org/2000/svg";
  const BW = 104, BH = 38;   // box size in viewBox units
  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const visible = (o, k) => k >= o.from && (o.until === undefined || k < o.until);

  function el(name, attrs, parent) {
    const e = document.createElementNS(NS, name);
    for (const [a, v] of Object.entries(attrs)) e.setAttribute(a, v);
    if (parent) parent.appendChild(e);
    return e;
  }

  document.querySelectorAll(".designviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const byId = Object.fromEntries(spec.nodes.map((n) => [n.id, n]));
    let step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <p class="dv-title"></p>
      <svg class="dv-svg" viewBox="0 0 ${spec.w} ${spec.h}" role="img"></svg>
      <div class="dv-note" aria-live="polite"></div>
      <div class="dv-ctl">
        <button data-act="back">◀ Back</button>
        <button data-act="next">Next layer ▶</button>
        <span class="dv-count"></span>
      </div>`;
    const svg = root.querySelector("svg");
    const title = root.querySelector(".dv-title");
    const note = root.querySelector(".dv-note");
    const count = root.querySelector(".dv-count");
    const back = root.querySelector('[data-act="back"]');
    const next = root.querySelector('[data-act="next"]');

    function draw() {
      svg.textContent = "";
      const lines = el("g", {}, svg);
      const boxes = el("g", {}, svg);
      for (const e of spec.edges) {
        if (!visible(e, step)) continue;
        const a = byId[e.a], b = byId[e.b];
        const ln = el("line", { x1: a.x + BW / 2, y1: a.y + BH / 2, x2: b.x + BW / 2, y2: b.y + BH / 2 }, lines);
        const cls = [e.dashed ? "dv-dash" : "", e.from === step && step > 0 ? "dv-new" : ""].join(" ").trim();
        if (cls) ln.setAttribute("class", cls);
        if (e.label) {
          const t = el("text", { x: (a.x + b.x) / 2 + BW / 2, y: (a.y + b.y) / 2 + BH / 2 - 7, class: "dv-elabel" }, lines);
          t.textContent = e.label;
        }
      }
      for (const n of spec.nodes) {
        if (!visible(n, step)) continue;
        const g = el("g", { class: `k-${n.kind || "server"}${n.from === step && step > 0 ? " dv-new" : ""}` }, boxes);
        el("rect", { x: n.x, y: n.y, width: BW, height: BH, rx: n.kind === "db" ? 10 : 4 }, g);
        const label = (n.labels && n.labels[step] !== undefined) ? n.labels[step] : n.label;
        const t = el("text", { x: n.x + BW / 2, y: n.y + (n.sub ? 14 : BH / 2) }, g);
        t.textContent = label;
        if (n.sub) {
          const s = el("text", { x: n.x + BW / 2, y: n.y + 28, class: "dv-sub" }, g);
          s.textContent = n.sub;
        }
      }
      const st = spec.steps[step];
      title.textContent = st.title;
      svg.setAttribute("aria-label", st.title);
      note.innerHTML = st.why;
      count.textContent = `layer ${step + 1} / ${spec.steps.length}`;
      back.disabled = step === 0;
      next.disabled = step === spec.steps.length - 1;
    }

    back.addEventListener("click", () => { if (step > 0) { step--; draw(); } });
    next.addEventListener("click", () => { if (step < spec.steps.length - 1) { step++; draw(); } });
    root.addEventListener("keydown", (ev) => {
      if (ev.key === "ArrowRight") { next.click(); ev.preventDefault(); }
      if (ev.key === "ArrowLeft") { back.click(); ev.preventDefault(); }
    });
    draw();
  });
})();
