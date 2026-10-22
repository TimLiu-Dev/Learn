/* CS Fundamentals: design-pattern structure diagrams, Java (GoF) shape vs idiomatic Kotlin shape.
 *
 *   <div class="patternviz" data-src="pz-1"></div>
 *   <script type="application/json" id="pz-1">{ ...spec... }</script>
 *   <script src="../assets/patternviz.js"></script>
 *
 * Spec:
 *   { "patterns": [
 *       { "name": "Strategy",
 *         "java":   { "boxes": [ { "id": "p", "label": "LateFeePolicy", "kind": "interface", "x": 1, "y": 0 }, ... ],
 *                     "edges": [ { "from": "a", "to": "p", "type": "impl" }, ... ],
 *                     "count": "1 interface + 3 classes" },
 *         "kotlin": { ...same shape... },
 *         "note": "HTML ok" } ] }
 * Box kinds: interface | class | func | object. x is a column (0-3), y a row (0-3).
 * Edge types: impl (dashed: implements), has (solid: holds a reference / wraps), makes (dotted: creates).
 */
(function () {
  const css = `
  .patternviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .pz-tabs, .pz-lang { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .pz-tabs button, .pz-lang button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .pz-tabs button[aria-selected="true"] { border-color: var(--accent); color: var(--accent); font-weight: 600; }
  .pz-lang button[aria-pressed="true"].pz-java { border-color: var(--java); color: var(--java); font-weight: 600; }
  .pz-lang button[aria-pressed="true"].pz-kotlin { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .pz-svg { width: 100%; height: auto; display: block; }
  .pz-svg text { font-family: var(--mono); font-size: 11px; fill: var(--ink); }
  .pz-svg .pz-kind { font-size: 9px; fill: var(--ink-soft); }
  .pz-svg rect { stroke-width: 1.4; }
  .pz-svg .k-class rect { fill: var(--surface); stroke: var(--ink-soft); }
  .pz-svg .k-interface rect { fill: var(--bg); stroke: var(--ink-soft); stroke-dasharray: 4 3; }
  .pz-svg .k-interface text.pz-label { font-style: italic; }
  .pz-svg .k-func rect { fill: color-mix(in srgb, var(--kotlin) 12%, var(--bg)); stroke: var(--kotlin); }
  .pz-svg .k-object rect { fill: color-mix(in srgb, var(--java) 12%, var(--bg)); stroke: var(--java); }
  .pz-svg line { stroke: var(--ink-soft); stroke-width: 1.3; }
  .pz-svg line.e-impl { stroke-dasharray: 5 3; }
  .pz-svg line.e-makes { stroke-dasharray: 1.5 3; }
  .pz-svg marker path { fill: var(--ink-soft); }
  .pz-count { font-family: var(--mono); font-size: .76rem; color: var(--ink-soft); margin: .3rem 0 0; }
  .pz-note { font-size: .9rem; margin: .5rem 0 0; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .pz-legend { font-size: .72rem; color: var(--ink-soft); margin-top: .4rem; }
  @media print { .pz-tabs, .pz-lang { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));
  const W = 150, H = 40, GX = 26, GY = 34, PAD = 10;   // box size and gaps, in SVG units
  const kindLabel = { interface: "«interface»", class: "class", func: "function type", object: "object" };

  function centre(b) { return { x: PAD + b.x * (W + GX) + W / 2, y: PAD + b.y * (H + GY) + H / 2 }; }

  // Clip a line from a's centre to b's centre at b's box edge, so the arrowhead touches the border
  function edgePoint(from, to) {
    const dx = to.x - from.x, dy = to.y - from.y;
    const sx = dx === 0 ? Infinity : (W / 2) / Math.abs(dx);
    const sy = dy === 0 ? Infinity : (H / 2) / Math.abs(dy);
    const s = Math.min(sx, sy);
    return { x: to.x - dx * s, y: to.y - dy * s };
  }

  function draw(shape, uid) {
    const cols = Math.max(...shape.boxes.map((b) => b.x)) + 1;
    const rows = Math.max(...shape.boxes.map((b) => b.y)) + 1;
    const vw = PAD * 2 + cols * W + (cols - 1) * GX, vh = PAD * 2 + rows * H + (rows - 1) * GY;
    const byId = Object.fromEntries(shape.boxes.map((b) => [b.id, b]));
    const edges = shape.edges.map((e) => {
      const a = centre(byId[e.from]), b = centre(byId[e.to]);
      const start = edgePoint(b, a), end = edgePoint(a, b);
      return `<line class="e-${e.type}" x1="${start.x}" y1="${start.y}" x2="${end.x}" y2="${end.y}" marker-end="url(#pz-arrow-${uid})"/>`;
    }).join("");
    const boxes = shape.boxes.map((b) => {
      const x = PAD + b.x * (W + GX), y = PAD + b.y * (H + GY);
      return `<g class="k-${b.kind}"><rect x="${x}" y="${y}" width="${W}" height="${H}" rx="${b.kind === "func" ? 14 : 4}"/>` +
        `<text class="pz-kind" x="${x + W / 2}" y="${y + 13}" text-anchor="middle">${esc(kindLabel[b.kind] || b.kind)}</text>` +
        `<text class="pz-label" x="${x + W / 2}" y="${y + 29}" text-anchor="middle">${esc(b.label)}</text></g>`;
    }).join("");
    return `<svg class="pz-svg" viewBox="0 0 ${vw} ${vh}" style="max-width:${vw}px" role="img">
      <defs><marker id="pz-arrow-${uid}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z"/></marker></defs>
      ${edges}${boxes}</svg>`;
  }

  let counter = 0;
  document.querySelectorAll(".patternviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const uid = ++counter;
    let p = 0, lang = "java";
    root.innerHTML = `
      <div class="pz-tabs" role="tablist">${spec.patterns.map((pt, k) => `<button role="tab" data-p="${k}">${esc(pt.name)}</button>`).join("")}</div>
      <div class="pz-lang"><button class="pz-java" data-lang="java">Java (GoF shape)</button><button class="pz-kotlin" data-lang="kotlin">Idiomatic Kotlin</button></div>
      <div class="pz-diagram"></div>
      <p class="pz-count"></p>
      <p class="pz-note"></p>
      <p class="pz-legend">dashed box = interface · rounded purple = Kotlin function type · orange = singleton object · dashed line = implements · solid line = holds / wraps · dotted line = creates</p>`;

    function render() {
      const pt = spec.patterns[p], shape = pt[lang];
      root.querySelector(".pz-diagram").innerHTML = draw(shape, uid);
      root.querySelector(".pz-count").textContent = `${lang === "java" ? "Java" : "Kotlin"}: ${shape.count}`;
      root.querySelector(".pz-note").innerHTML = pt.note;
      root.querySelectorAll(".pz-tabs button").forEach((b) => b.setAttribute("aria-selected", String(+b.dataset.p === p)));
      root.querySelectorAll(".pz-lang button").forEach((b) => b.setAttribute("aria-pressed", String(b.dataset.lang === lang)));
    }
    root.querySelectorAll(".pz-tabs button").forEach((b) => b.addEventListener("click", () => { p = +b.dataset.p; render(); }));
    root.querySelectorAll(".pz-lang button").forEach((b) => b.addEventListener("click", () => { lang = b.dataset.lang; render(); }));
    render();
  });
})();
