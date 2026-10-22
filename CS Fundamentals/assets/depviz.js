/* CS Fundamentals: dependency-graph views (L38, SOLID & dependency injection).
 *
 *   <div class="depviz" data-src="dv-1"></div>
 *   <script type="application/json" id="dv-1">{ ...spec... }</script>
 *   <script src="../assets/depviz.js"></script>
 *
 * Spec:
 *   { "views": [
 *       { "tab": "Before DIP",                       // tab label
 *         "note": "What to notice (HTML ok)",
 *         "nodes": [ { "id": "svc", "label": "LendingRules", "sub": "policy", "x": 40, "y": 40,
 *                      "kind": "policy" } ],          // kind: policy | detail | abstract | root | test
 *         "edges": [ { "from": "svc", "to": "db", "kind": "uses" } ] }   // kind: uses | implements | creates
 *   ] }
 * Coordinates are in a 560 x 250 box that scales to the page width. Boxes are 150 x 46.
 */
(function () {
  const NS = "http://www.w3.org/2000/svg";
  const W = 560, H = 250, BW = 150, BH = 46;
  const css = `
  .depviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .dv-tabs { display: flex; flex-wrap: wrap; gap: .4rem; margin-bottom: .6rem; }
  .dv-tabs button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .dv-tabs button[aria-selected="true"] { border-color: var(--accent); color: var(--accent); font-weight: 600; }
  .depviz svg { display: block; width: 100%; height: auto; }
  .dv-box rect { fill: var(--bg); stroke: var(--ink); stroke-width: 1.6; rx: 6; }
  .dv-box.dv-policy rect { stroke: var(--kotlin); stroke-width: 2.2; }
  .dv-box.dv-detail rect { stroke: var(--java); }
  .dv-box.dv-abstract rect { stroke: var(--ok); stroke-dasharray: 5 3; fill: var(--surface); }
  .dv-box.dv-root rect { stroke: var(--accent); fill: color-mix(in srgb, var(--accent) 10%, var(--bg)); }
  .dv-box.dv-test rect { stroke: var(--ink-soft); stroke-dasharray: 2 3; }
  .dv-label { font-family: var(--mono); font-size: 12.5px; fill: var(--ink); font-weight: 600; }
  .dv-sub { font-family: var(--serif); font-size: 11px; fill: var(--ink-soft); font-style: italic; }
  .dv-edge { fill: none; stroke-width: 1.8; }
  .dv-uses { stroke: var(--ink); }
  .dv-implements { stroke: var(--ok); stroke-dasharray: 6 4; }
  .dv-creates { stroke: var(--accent); stroke-dasharray: 2 4; }
  .dv-note { font-size: .9rem; margin-top: .5rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .dv-key { display: flex; flex-wrap: wrap; gap: .9rem; font-size: .75rem; color: var(--ink-soft); margin-top: .4rem; font-family: var(--mono); }
  .dv-key span::before { content: ""; display: inline-block; width: 1.6rem; height: 0; vertical-align: middle; margin-right: .35rem; border-top: 2px solid var(--ink); }
  .dv-key .k-impl::before { border-top: 2px dashed var(--ok); }
  .dv-key .k-creates::before { border-top: 2px dotted var(--accent); }
  @media print { .dv-tabs button:not([aria-selected="true"]) { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const el = (name, attrs, text) => {
    const n = document.createElementNS(NS, name);
    for (const [k, v] of Object.entries(attrs || {})) n.setAttribute(k, v);
    if (text !== undefined) n.textContent = text;
    return n;
  };

  // Point where the line from a box centre towards (tx, ty) leaves the box.
  function exitPoint(n, tx, ty) {
    const cx = n.x + BW / 2, cy = n.y + BH / 2;
    const dx = tx - cx, dy = ty - cy;
    if (dx === 0 && dy === 0) return [cx, cy];
    const sx = dx === 0 ? Infinity : (BW / 2) / Math.abs(dx);
    const sy = dy === 0 ? Infinity : (BH / 2) / Math.abs(dy);
    const s = Math.min(sx, sy);
    return [cx + dx * s, cy + dy * s];
  }

  let uid = 0;
  document.querySelectorAll(".depviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const id = "dv" + (uid++);
    root.innerHTML = `<div class="dv-tabs" role="tablist"></div><div class="dv-svg"></div>
      <div class="dv-note" aria-live="polite"></div>
      <div class="dv-key"><span>uses (depends on)</span><span class="k-impl">implements</span><span class="k-creates">creates / wires</span></div>`;
    const tabs = root.querySelector(".dv-tabs");
    const holder = root.querySelector(".dv-svg");
    const note = root.querySelector(".dv-note");

    function draw(i) {
      const v = spec.views[i];
      [...tabs.children].forEach((b, j) => b.setAttribute("aria-selected", String(i === j)));
      const svg = el("svg", { viewBox: `0 0 ${W} ${H}`, role: "img", "aria-label": v.tab });
      const defs = el("defs");
      for (const [k, color, hollow] of [["uses", "var(--ink)", false], ["implements", "var(--ok)", true], ["creates", "var(--accent)", false]]) {
        const m = el("marker", { id: `${id}-${k}`, viewBox: "0 0 10 10", refX: 9, refY: 5, markerWidth: 9, markerHeight: 9, orient: "auto-start-reverse" });
        m.appendChild(el("path", { d: "M0,0 L10,5 L0,10 z", style: `fill:${hollow ? "var(--bg)" : color};stroke:${color};stroke-width:1.2` }));
        defs.appendChild(m);
      }
      svg.appendChild(defs);
      const byId = Object.fromEntries(v.nodes.map((n) => [n.id, n]));
      for (const e of v.edges) {
        const a = byId[e.from], b = byId[e.to];
        const [x1, y1] = exitPoint(a, b.x + BW / 2, b.y + BH / 2);
        const [x2, y2] = exitPoint(b, a.x + BW / 2, a.y + BH / 2);
        svg.appendChild(el("path", { class: `dv-edge dv-${e.kind}`, d: `M${x1},${y1} L${x2},${y2}`, "marker-end": `url(#${id}-${e.kind})` }));
      }
      for (const n of v.nodes) {
        const g = el("g", { class: `dv-box dv-${n.kind}` });
        g.appendChild(el("rect", { x: n.x, y: n.y, width: BW, height: BH, rx: 6 }));
        g.appendChild(el("text", { class: "dv-label", x: n.x + BW / 2, y: n.y + (n.sub ? 20 : 28), "text-anchor": "middle" }, n.label));
        if (n.sub) g.appendChild(el("text", { class: "dv-sub", x: n.x + BW / 2, y: n.y + 36, "text-anchor": "middle" }, n.sub));
        svg.appendChild(g);
      }
      holder.replaceChildren(svg);
      note.innerHTML = v.note;
    }

    spec.views.forEach((v, i) => {
      const b = document.createElement("button");
      b.type = "button";
      b.setAttribute("role", "tab");
      b.textContent = v.tab;
      b.addEventListener("click", () => draw(i));
      tabs.appendChild(b);
    });
    draw(0);
  });
})();
