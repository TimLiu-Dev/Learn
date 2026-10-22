/* CS Fundamentals: step-through network diagrams (TCP handshake, DNS lookup, protocol layers).
 *
 *   <div class="netviz" data-src="nv-1"></div>
 *   <script type="application/json" id="nv-1">{ ...spec... }</script>
 *   <script src="../assets/netviz.js"></script>
 *
 * Spec: { "modes": [ mode, ... ] }, one tab per mode. Two kinds of mode:
 *
 *   Sequence diagram, for messages between machines over time:
 *   { "name": "TCP", "type": "seq",
 *     "lanes": ["client", "server"],                   // vertical lifelines, left to right
 *     "steps": [ { "note": "HTML",
 *                  "msg": { "from": 0, "to": 1, "label": "SYN seq=100", "lost": false },  // optional
 *                  "state": ["SYN-SENT", "LISTEN"] } ] }  // optional text under each lane header
 *   Each step adds its message below the previous ones; the newest is highlighted.
 *   "lost": true draws the arrow stopping halfway with a cross.
 *
 *   Layer stack, for headers being added on the way down and removed on the way up:
 *   { "name": "Layers", "type": "layers",
 *     "layers": ["Application", "Transport", "Internet", "Link"],   // top to bottom
 *     "steps": [ { "note": "HTML", "side": "left|right|wire", "layer": 0,
 *                  "parts": ["TCP", "\"hello\""] } ] }  // the packet, outermost header first;
 *                                                        // quoted parts are drawn as data
 */
(function () {
  const W = 520;
  const css = `
  .netviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 46rem; outline: none; }
  .netviz:focus-visible { border-color: var(--accent); }
  .nv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .nv-tabs button, .nv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .nv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .nv-svg { width: 100%; height: auto; display: block; }
  .nv-svg text { font-family: var(--mono); font-size: 12px; fill: var(--ink); }
  .nv-svg .nv-head rect { fill: var(--surface); stroke: var(--java); stroke-width: 1.6; }
  .nv-svg .nv-head text { font-weight: 600; fill: var(--java); }
  .nv-svg .nv-state { font-size: 10.5px; fill: var(--kotlin); font-weight: 600; }
  .nv-svg .nv-life { stroke: var(--rule); stroke-width: 1.4; stroke-dasharray: 4 4; }
  .nv-svg .nv-msg { stroke: var(--ink-soft); stroke-width: 1.5; fill: none; }
  .nv-svg .nv-mlabel { font-size: 11px; fill: var(--ink-soft); paint-order: stroke; stroke: var(--bg); stroke-width: 5px; stroke-linejoin: round; }
  .nv-svg .nv-now .nv-msg { stroke: var(--accent); stroke-width: 2.4; }
  .nv-svg .nv-now .nv-mlabel { fill: var(--accent); font-weight: 600; }
  .nv-svg .nv-x { stroke: var(--accent); stroke-width: 2.4; }
  .nv-svg .nv-layer { fill: none; stroke: var(--rule); stroke-width: 1.2; }
  .nv-svg .nv-lname { font-size: 11px; fill: var(--ink-soft); }
  .nv-svg .nv-side { font-weight: 600; fill: var(--java); }
  .nv-svg .nv-part rect { stroke-width: 1.4; }
  .nv-svg .nv-hdr rect { fill: color-mix(in srgb, var(--kotlin) 14%, var(--bg)); stroke: var(--kotlin); }
  .nv-svg .nv-hdr text { fill: var(--kotlin); font-weight: 600; font-size: 11px; }
  .nv-svg .nv-data rect { fill: color-mix(in srgb, var(--ok) 14%, var(--bg)); stroke: var(--ok); }
  .nv-svg .nv-data text { fill: var(--ok); font-weight: 600; font-size: 11px; }
  .nv-svg .nv-wire { stroke: var(--ink-soft); stroke-width: 2; }
  .nv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .nv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .nv-ctl button:disabled { opacity: .4; cursor: default; }
  .nv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .nv-ctl, .nv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));

  // Sequence diagram: lane headers, dashed lifelines, one arrow per step so far.
  function drawSeq(m, step, id) {
    const n = m.lanes.length, pad = 50, gap = (W - 2 * pad) / (n - 1);
    const x = (i) => pad + i * gap, top = 62, row = 34;
    const shown = m.steps.slice(0, step + 1).filter((s) => s.msg);
    const h = top + Math.max(1, m.steps.filter((s) => s.msg).length) * row + 16;
    const boxW = Math.min(96, gap - 8);
    const st = m.steps[step].state || [];
    let out = `<defs><marker id="nv-a-${id}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="var(--ink-soft)"/></marker>` +
      `<marker id="nv-n-${id}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="var(--accent)"/></marker></defs>`;
    m.lanes.forEach((name, i) => {
      out += `<line class="nv-life" x1="${x(i)}" y1="34" x2="${x(i)}" y2="${h - 6}"/>` +
        `<g class="nv-head"><rect x="${x(i) - boxW / 2}" y="6" width="${boxW}" height="26" rx="4"/>` +
        `<text x="${x(i)}" y="23" text-anchor="middle">${esc(name)}</text></g>` +
        (st[i] ? `<text class="nv-state" x="${x(i)}" y="48" text-anchor="middle">${esc(st[i])}</text>` : "");
    });
    shown.forEach((s, k) => {
      const now = k === shown.length - 1 && m.steps[step].msg ? " nv-now" : "";
      const y = top + k * row + 14, x1 = x(s.msg.from), x2full = x(s.msg.to);
      const dir = Math.sign(x2full - x1);
      const x2 = s.msg.lost ? x1 + (x2full - x1) * 0.55 : x2full - dir * 3;
      const mid = s.msg.lost ? (x1 + x2) / 2 : (x1 + x2full) / 2;   // keep a lost label clear of the cross
      out += `<g class="${now.trim()}"><line class="nv-msg" x1="${x1 + dir * 3}" y1="${y}" x2="${x2}" y2="${y}"` +
        (s.msg.lost ? "" : ` marker-end="url(#nv-${now ? "n" : "a"}-${id})"`) + `/>` +
        `<text class="nv-mlabel" x="${mid}" y="${y - 6}" text-anchor="middle">${esc(s.msg.label)}</text>` +
        (s.msg.lost ? `<path class="nv-x" d="M${x2 - 6},${y - 6} L${x2 + 6},${y + 6} M${x2 - 6},${y + 6} L${x2 + 6},${y - 6}"/>` : "") +
        `</g>`;
    });
    return { h, out };
  }

  // Layer stack: sender column, receiver column, the packet drawn in its current layer.
  function drawLayers(m, step) {
    const s = m.steps[step], rows = m.layers.length, top = 34, rh = 48, h = top + rows * rh + 40;
    const colW = 222, lx = 8, rx = W - 8 - colW;
    let out = `<text class="nv-side" x="${lx + colW / 2}" y="20" text-anchor="middle">sender</text>` +
      `<text class="nv-side" x="${rx + colW / 2}" y="20" text-anchor="middle">receiver</text>`;
    m.layers.forEach((name, i) => {
      const y = top + i * rh;
      [lx, rx].forEach((cx) => {
        out += `<rect class="nv-layer" x="${cx}" y="${y}" width="${colW}" height="${rh - 6}" rx="4"/>` +
          `<text class="nv-lname" x="${cx + 6}" y="${y + 13}">${esc(name)}</text>`;
      });
    });
    const wy = top + rows * rh + 14;
    out += `<line class="nv-wire" x1="${lx + colW / 2}" y1="${wy}" x2="${rx + colW / 2}" y2="${wy}"/>` +
      `<line class="nv-wire" x1="${lx + colW / 2}" y1="${top + rows * rh - 6}" x2="${lx + colW / 2}" y2="${wy}"/>` +
      `<line class="nv-wire" x1="${rx + colW / 2}" y1="${top + rows * rh - 6}" x2="${rx + colW / 2}" y2="${wy}"/>` +
      `<text class="nv-lname" x="${W / 2}" y="${wy + 16}" text-anchor="middle">the wire</text>`;
    // packet geometry: headers are narrow boxes, the quoted data is wider
    const widths = s.parts.map((p) => /"/.test(p) ? 60 : 38);
    const total = widths.reduce((a, b) => a + b, 0);
    let px, py;
    if (s.side === "wire") { px = W / 2 - total / 2; py = wy - 30; }
    else { const cx = s.side === "left" ? lx : rx; px = cx + colW - total - 6; py = top + s.layer * rh + 18; }
    s.parts.forEach((p, i) => {
      const isData = /"/.test(p);
      out += `<g class="nv-part ${isData ? "nv-data" : "nv-hdr"}"><rect x="${px}" y="${py}" width="${widths[i] - 2}" height="20" rx="3"/>` +
        `<text x="${px + (widths[i] - 2) / 2}" y="${py + 14}" text-anchor="middle">${esc(p)}</text></g>`;
      px += widths[i];
    });
    return { h, out };
  }

  let uid = 0;
  document.querySelectorAll(".netviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const id = uid++;
    let mode = 0, step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="nv-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <svg class="nv-svg" role="img" aria-label="network diagram"></svg>
      <div class="nv-note" aria-live="polite"></div>
      <div class="nv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="nv-count"></span>
      </div>`;
    const svg = root.querySelector("svg");

    function draw() {
      const m = spec.modes[mode];
      const { h, out } = m.type === "layers" ? drawLayers(m, step) : drawSeq(m, step, id);
      svg.setAttribute("viewBox", `0 0 ${W} ${h}`);
      svg.innerHTML = out;
      root.querySelector(".nv-note").innerHTML = m.steps[step].note;
      root.querySelector(".nv-count").textContent = `step ${step + 1}/${m.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === m.steps.length - 1;
      root.querySelector('[data-act="all"]').disabled = step === m.steps.length - 1;
      root.querySelectorAll(".nv-tabs button").forEach((b) =>
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
