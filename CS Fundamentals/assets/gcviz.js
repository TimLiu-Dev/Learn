/* CS Fundamentals: step-through garbage-collection diagram (roots, objects, marking, generations).
 *
 *   <div class="gcviz" data-src="gc-1"></div>
 *   <script type="application/json" id="gc-1">{ ...spec... }</script>
 *   <script src="../assets/gcviz.js"></script>
 *
 * Spec:
 *   { "height": 300,                                   // SVG height in viewBox units (width is 520)
 *     "modes": [                                       // tabs
 *       { "name": "Mark & sweep",
 *         "lanes": [ { "label": "young · eden", "y": 0, "h": 120, "x": 110 }, ... ], // optional bands (x defaults to 2)
 *         "steps": [                                   // each step is a full snapshot
 *           { "note": "HTML",
 *             "nodes": { "id": { "x": 20, "y": 30, "label": "Order", "cls": "root|live|marked|dead|new", "tag": "age 1" } },
 *             "edges": [ ["fromId", "toId"], ... ] } ] } ] }
 * Nodes are 96×30 boxes placed at (x, y). A node missing from a step's "nodes" is not drawn (it was swept).
 */
(function () {
  const W = 520, NW = 96, NH = 30;
  const css = `
  .gcviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 46rem; outline: none; }
  .gcviz:focus-visible { border-color: var(--accent); }
  .gc-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .gc-tabs button, .gc-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .gc-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .gc-svg { width: 100%; height: auto; display: block; }
  .gc-svg text { font-family: var(--mono); font-size: 12px; fill: var(--ink); }
  .gc-svg .gc-lane rect { fill: var(--surface); stroke: var(--rule); stroke-dasharray: 3 3; }
  .gc-svg .gc-lane text { font-size: 10px; fill: var(--ink-soft); letter-spacing: .06em; text-transform: uppercase; }
  .gc-svg .gc-lane text, .gc-svg .gc-tag { paint-order: stroke; stroke: var(--surface); stroke-width: 4px; stroke-linejoin: round; }
  .gc-svg .gc-edge { stroke: var(--ink-soft); stroke-width: 1.4; fill: none; }
  .gc-svg .gc-node rect { fill: var(--bg); stroke: var(--rule); stroke-width: 1.4; rx: 5; }
  .gc-svg .gc-root rect { fill: var(--surface); stroke: var(--java); stroke-width: 2; }
  .gc-svg .gc-root text { fill: var(--java); font-weight: 600; }
  .gc-svg .gc-marked rect { fill: color-mix(in srgb, var(--ok) 14%, var(--bg)); stroke: var(--ok); stroke-width: 2; }
  .gc-svg .gc-dead rect { fill: color-mix(in srgb, var(--accent) 8%, var(--bg)); stroke: var(--accent); stroke-dasharray: 4 3; }
  .gc-svg .gc-dead text:not(.gc-tag) { fill: var(--accent); text-decoration: line-through; }
  .gc-svg .gc-new rect { stroke: var(--kotlin); stroke-width: 2; }
  .gc-svg .gc-tag { font-size: 10px; fill: var(--ink-soft); }
  .gc-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .gc-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .gc-ctl button:disabled { opacity: .4; cursor: default; }
  .gc-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  .gc-key { font-size: .75rem; color: var(--ink-soft); margin-top: .4rem; display: flex; gap: .9rem; flex-wrap: wrap; }
  .gc-key span::before { content: ""; display: inline-block; width: .8rem; height: .6rem; margin-right: .3rem; border-radius: 2px; vertical-align: middle; border: 2px solid var(--rule); }
  .gc-key .k-root::before { border-color: var(--java); }
  .gc-key .k-marked::before { border-color: var(--ok); background: color-mix(in srgb, var(--ok) 14%, transparent); }
  .gc-key .k-dead::before { border-color: var(--accent); border-style: dashed; }
  .gc-key .k-new::before { border-color: var(--kotlin); }
  @media print { .gc-ctl, .gc-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));

  // Where the segment from box a's centre toward box b's centre leaves box a.
  function exitPoint(a, b) {
    const ax = a.x + NW / 2, ay = a.y + NH / 2, bx = b.x + NW / 2, by = b.y + NH / 2;
    const dx = bx - ax, dy = by - ay;
    if (dx === 0 && dy === 0) return [ax, ay];
    const t = Math.min(dx ? (NW / 2) / Math.abs(dx) : Infinity, dy ? (NH / 2) / Math.abs(dy) : Infinity);
    return [ax + dx * t, ay + dy * t];
  }

  let uid = 0;
  document.querySelectorAll(".gcviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const marker = "gc-arrow-" + (uid++);
    let mode = 0, step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="gc-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <svg class="gc-svg" viewBox="0 0 ${W} ${spec.height}" role="img" aria-label="heap diagram"></svg>
      <div class="gc-key"><span class="k-root">GC root</span><span class="k-marked">marked reachable</span><span class="k-dead">unreachable: garbage</span><span class="k-new">just allocated</span></div>
      <div class="gc-note" aria-live="polite"></div>
      <div class="gc-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="gc-count"></span>
      </div>`;
    const svg = root.querySelector("svg");

    function draw() {
      const m = spec.modes[mode], st = m.steps[step];
      let out = `<defs><marker id="${marker}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="var(--ink-soft)"/></marker></defs>`;
      let labels = "";
      (m.lanes || []).forEach((l) => {
        const x = l.x === undefined ? 2 : l.x;
        out += `<g class="gc-lane"><rect x="${x}" y="${l.y}" width="${W - 2 - x}" height="${l.h}" rx="6"/></g>`;
        labels += `<g class="gc-lane"><text x="${x + 8}" y="${l.y + 13}">${esc(l.label)}</text></g>`;
      });
      (st.edges || []).forEach(([f, t]) => {
        const a = st.nodes[f], b = st.nodes[t];
        if (!a || !b) return;
        const [x1, y1] = exitPoint(a, b), [x2, y2] = exitPoint(b, a);
        out += `<line class="gc-edge" x1="${x1.toFixed(1)}" y1="${y1.toFixed(1)}" x2="${x2.toFixed(1)}" y2="${y2.toFixed(1)}" marker-end="url(#${marker})"/>`;
      });
      out += labels;
      Object.values(st.nodes).forEach((n) => {
        out += `<g class="gc-node gc-${n.cls || "live"}"><rect x="${n.x}" y="${n.y}" width="${NW}" height="${NH}"/>` +
          `<text x="${n.x + NW / 2}" y="${n.y + 19}" text-anchor="middle">${esc(n.label)}</text>` +
          (n.tag ? `<text class="gc-tag" x="${n.x + NW / 2}" y="${n.y + NH + 12}" text-anchor="middle">${esc(n.tag)}</text>` : "") + `</g>`;
      });
      svg.innerHTML = out;
      root.querySelector(".gc-note").innerHTML = st.note;
      root.querySelector(".gc-count").textContent = `step ${step + 1}/${m.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === m.steps.length - 1;
      root.querySelector('[data-act="all"]').disabled = step === m.steps.length - 1;
      root.querySelectorAll(".gc-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const last = spec.modes[mode].steps.length - 1;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(last, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = last;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const last = spec.modes[mode].steps.length - 1;
      if (e.key === "ArrowRight") { step = Math.min(last, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
