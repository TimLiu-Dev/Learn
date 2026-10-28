/* CS Fundamentals: step-through system diagrams (load balancer, replication, hash ring, queue).
 *
 *   <div class="scaleviz" data-src="sv-1"></div>
 *   <script type="application/json" id="sv-1">{ ...spec... }</script>
 *   <script src="../assets/scaleviz.js"></script>
 *
 * Spec: { "modes": [ mode, ... ] }, one tab per mode. Two kinds of mode:
 *
 *   Boxes and arrows, for machines talking to each other:
 *   { "name": "Load balancer", "type": "boxes", "h": 230,          // drawing height (width is 520)
 *     "nodes": [ { "id": "lb", "label": "LB", "x": 200, "y": 115 } ],
 *     "steps": [ { "note": "HTML",
 *                  "state": { "b1": "UP · 3" },                     // small text under a box
 *                  "cls":   { "b2": "down" },                       // up (default) | down | dead | hot | stale
 *                  "arrows": [ { "from": "lb", "to": "b1", "label": "GET", "fail": false } ] } ] }
 *
 *   Hash ring, for consistent hashing:
 *   { "name": "Hash ring", "type": "ring", "nodes": ["n1", "n2"],   // colour order
 *     "steps": [ { "note": "HTML",
 *                  "points": [ { "a": 12.5, "n": "n1" } ],          // node positions, degrees clockwise from top
 *                  "keys":   [ { "a": 40.0, "n": "n2", "moved": false } ] } ] }
 */
(function () {
  const W = 520;
  const css = `
  .scaleviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 46rem; outline: none; }
  .scaleviz:focus-visible { border-color: var(--accent); }
  .sv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .sv-tabs button, .sv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .sv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .sv-svg { width: 100%; height: auto; display: block; }
  .sv-svg text { font-family: var(--mono); font-size: 12px; fill: var(--ink); }
  .sv-node rect { fill: var(--surface); stroke: var(--java); stroke-width: 1.6; }
  .sv-node .sv-label { font-weight: 600; fill: var(--java); }
  .sv-node .sv-state { font-size: 10.5px; fill: var(--ink-soft); }
  .sv-node.hot rect { stroke: var(--accent); stroke-width: 2.4; }
  .sv-node.hot .sv-label { fill: var(--accent); }
  .sv-node.stale rect { stroke: var(--kotlin); stroke-dasharray: 5 3; }
  .sv-node.stale .sv-label { fill: var(--kotlin); }
  .sv-node.down rect { stroke: var(--accent); stroke-dasharray: 4 4; }
  .sv-node.down .sv-label, .sv-node.down .sv-state { fill: var(--accent); }
  .sv-node.dead rect { stroke: var(--rule); fill: var(--bg); }
  .sv-node.dead .sv-label, .sv-node.dead .sv-state { fill: var(--ink-soft); text-decoration: line-through; }
  .sv-arrow line { stroke: var(--ok); stroke-width: 2; }
  .sv-arrow text { font-size: 11px; fill: var(--ok); font-weight: 600; paint-order: stroke; stroke: var(--bg); stroke-width: 5px; stroke-linejoin: round; }
  .sv-arrow.fail line { stroke: var(--accent); stroke-dasharray: 5 4; }
  .sv-arrow.fail text { fill: var(--accent); }
  .sv-ring { fill: none; stroke: var(--rule); stroke-width: 10; }
  .sv-key { stroke: var(--bg); stroke-width: 1.5; }
  .sv-key.moved { stroke: var(--ink); stroke-width: 2.4; }
  .sv-legend { font-size: 11px; }
  .sv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .sv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .sv-ctl button:disabled { opacity: .4; cursor: default; }
  .sv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .sv-ctl, .sv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));
  const BW = 92, BH = 34;
  const palette = ["var(--kotlin)", "var(--java)", "var(--ok)", "var(--accent)", "var(--ink-soft)"];

  // Boxes and arrows. Arrows start and end at the box edges, so labels never sit inside a box.
  function drawBoxes(m, s, id) {
    const at = Object.fromEntries(m.nodes.map((n) => [n.id, n]));
    let out = `<defs>` + ["ok", "fail"].map((k) =>
      `<marker id="sv-${k}-${id}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">` +
      `<path d="M0,0 L10,5 L0,10 z" fill="var(--${k === "ok" ? "ok" : "accent"})"/></marker>`).join("") + `</defs>`;
    const arrows = s.arrows || [];
    arrows.forEach((a, i) => {
      const p = at[a.from], q = at[a.to];
      const dx = q.x - p.x, dy = q.y - p.y, len = Math.hypot(dx, dy) || 1;
      // shrink each end to the box border along the line
      const cut = () => Math.min(Math.abs(BW / 2 / (dx / len || 1e-9)), Math.abs(BH / 2 / (dy / len || 1e-9))) + 3;
      const c = cut();
      // a request and its reply between the same two boxes: draw them side by side, labels apart
      const back = arrows.findIndex((b) => b.from === a.to && b.to === a.from);
      const side = back < 0 ? 0 : (i < back ? -1 : 1);
      const sgn = a.from < a.to ? 1 : -1;                // same perpendicular for both arrows of a pair
      const ox = -dy / len * 7 * side * sgn, oy = dx / len * 7 * side * sgn;
      const x1 = p.x + dx / len * c + ox, y1 = p.y + dy / len * c + oy, x2 = q.x - dx / len * c + ox, y2 = q.y - dy / len * c + oy;
      const ly = (y1 + y2) / 2 + (oy > 0 ? 15 : -5);      // label on the outer side of its own arrow
      out += `<g class="sv-arrow${a.fail ? " fail" : ""}"><line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" marker-end="url(#sv-${a.fail ? "fail" : "ok"}-${id})"/>` +
        (a.label ? `<text x="${(x1 + x2) / 2}" y="${ly}" text-anchor="middle">${esc(a.label)}</text>` : "") + `</g>`;
    });
    m.nodes.forEach((n) => {
      const cls = (s.cls || {})[n.id] || "up";
      const st = (s.state || {})[n.id];
      out += `<g class="sv-node ${cls}"><rect x="${n.x - BW / 2}" y="${n.y - BH / 2}" width="${BW}" height="${BH}" rx="5"/>` +
        `<text class="sv-label" x="${n.x}" y="${n.y + 4}" text-anchor="middle">${esc(n.label)}</text>` +
        (st ? `<text class="sv-state" x="${n.x}" y="${n.y + BH / 2 + 14}" text-anchor="middle">${esc(st)}</text>` : "") + `</g>`;
    });
    return { h: m.h || 230, out };
  }

  // Hash ring: node positions as coloured ticks on the circle, keys as dots just inside it.
  function drawRing(m, s) {
    const cx = 170, cy = 150, r = 112, h = 300;
    const colour = (n) => palette[m.nodes.indexOf(n) % palette.length];
    const pos = (a, rr) => [cx + rr * Math.sin(a * Math.PI / 180), cy - rr * Math.cos(a * Math.PI / 180)];
    let out = `<circle class="sv-ring" cx="${cx}" cy="${cy}" r="${r}"/>`;
    const labelled = [];                                 // skip a label that would overlap a neighbour's
    s.points.forEach((p) => {
      const [x1, y1] = pos(p.a, r - 14), [x2, y2] = pos(p.a, r + 14), [tx, ty] = pos(p.a, r + 26);
      out += `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="${colour(p.n)}" stroke-width="4"/>`;
      if (labelled.some((a) => Math.min(Math.abs(a - p.a), 360 - Math.abs(a - p.a)) < 14)) return;
      labelled.push(p.a);
      out += `<text x="${tx}" y="${ty + 4}" text-anchor="middle" style="fill:${colour(p.n)};font-weight:600;font-size:11px">${esc(p.n)}</text>`;
    });
    s.keys.forEach((k) => {
      const [x, y] = pos(k.a, r - 30);
      out += `<circle class="sv-key${k.moved ? " moved" : ""}" cx="${x}" cy="${y}" r="${k.moved ? 6.5 : 5}" fill="${colour(k.n)}"/>`;
    });
    // legend: keys per node
    const counts = {};
    s.keys.forEach((k) => { counts[k.n] = (counts[k.n] || 0) + 1; });
    m.nodes.forEach((n, i) => {
      if (!s.points.some((p) => p.n === n)) return;
      out += `<circle cx="352" cy="${70 + i * 26}" r="7" fill="${colour(n)}"/>` +
        `<text class="sv-legend" x="368" y="${74 + i * 26}">${esc(n)}: ${counts[n] || 0} keys</text>`;
    });
    const moved = s.keys.filter((k) => k.moved).length;
    if (moved) out += `<text class="sv-legend" x="344" y="${82 + m.nodes.length * 26}">ringed dots: ${moved} moved</text>`;
    return { h, out };
  }

  let uid = 0;
  document.querySelectorAll(".scaleviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const id = uid++;
    let mode = 0, step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="sv-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <svg class="sv-svg" role="img" aria-label="system diagram"></svg>
      <div class="sv-note" aria-live="polite"></div>
      <div class="sv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="sv-count"></span>
      </div>`;
    const svg = root.querySelector("svg");

    function draw() {
      const m = spec.modes[mode], s = m.steps[step];
      const { h, out } = m.type === "ring" ? drawRing(m, s) : drawBoxes(m, s, id);
      svg.setAttribute("viewBox", `0 0 ${W} ${h}`);
      svg.innerHTML = out;
      root.querySelector(".sv-note").innerHTML = s.note;
      root.querySelector(".sv-count").textContent = `step ${step + 1}/${m.steps.length}`;
      const last = m.steps.length - 1;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === last;
      root.querySelector('[data-act="all"]').disabled = step === last;
      root.querySelectorAll(".sv-tabs button").forEach((b) =>
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
