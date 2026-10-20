/* CS Fundamentals: step-through wait-for graph for locks and threads (deadlock vs lock ordering).
 *
 *   <div class="deadlockviz" data-src="dl-1"></div>
 *   <script type="application/json" id="dl-1">{ ...spec... }</script>
 *   <script src="../assets/deadlockviz.js"></script>
 *
 * Spec:
 *   { "height": 230,                                    // SVG height in viewBox units (width is 520)
 *     "nodes": { "t1": { "x": 20, "y": 20, "label": "t1", "kind": "thread|lock" }, ... },  // fixed positions
 *     "modes": [                                        // tabs
 *       { "name": "Opposite order",
 *         "steps": [                                    // each step is a full snapshot
 *           { "note": "HTML",
 *             "state": { "t1": "run|blocked|done", ... },        // optional, threads only
 *             "tags": { "t1": "holds alice", ... },              // optional text under a node
 *             "edges": [ ["alice", "t1", "holds"], ["t1", "bob", "waits"] ],
 *             "cycle": true } ] } ] }                   // optional: draw every edge as part of a cycle
 * "holds" edges go lock -> thread (solid, "held by"); "waits" edges go thread -> lock (dashed, "waits for").
 */
(function () {
  const W = 520, NW = 130, NH = 34;
  const css = `
  .deadlockviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 46rem; outline: none; }
  .deadlockviz:focus-visible { border-color: var(--accent); }
  .dl-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .dl-tabs button, .dl-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .dl-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .dl-svg { width: 100%; height: auto; display: block; }
  .dl-svg text { font-family: var(--mono); font-size: 13px; fill: var(--ink); }
  .dl-svg .dl-edge { stroke: var(--ink-soft); stroke-width: 1.6; fill: none; }
  .dl-svg .dl-waits { stroke-dasharray: 6 4; }
  .dl-svg .dl-cycle { stroke: var(--accent); stroke-width: 2.6; }
  .dl-svg .dl-elabel { font-size: 11px; fill: var(--ink-soft); paint-order: stroke; stroke: var(--bg); stroke-width: 5px; stroke-linejoin: round; }
  .dl-svg .dl-elabel.dl-cycle { fill: var(--accent); stroke: var(--bg); stroke-width: 5px; font-weight: 600; }
  .dl-svg .dl-node rect { fill: var(--bg); stroke-width: 2; }
  .dl-svg .dl-thread rect { stroke: var(--kotlin); rx: 17; }
  .dl-svg .dl-lock rect { stroke: var(--java); fill: var(--surface); rx: 4; }
  .dl-svg .dl-lock text { fill: var(--java); font-weight: 600; }
  .dl-svg .dl-blocked rect { stroke: var(--accent); fill: color-mix(in srgb, var(--accent) 10%, var(--bg)); }
  .dl-svg .dl-blocked text:not(.dl-tag) { fill: var(--accent); font-weight: 600; }
  .dl-svg .dl-done rect { stroke: var(--ok); fill: color-mix(in srgb, var(--ok) 12%, var(--bg)); }
  .dl-svg .dl-tag { font-size: 11px; fill: var(--ink-soft); font-weight: 400; }
  .dl-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .dl-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .dl-ctl button:disabled { opacity: .4; cursor: default; }
  .dl-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  .dl-key { font-size: .75rem; color: var(--ink-soft); margin-top: .4rem; display: flex; gap: .9rem; flex-wrap: wrap; }
  .dl-key span::before { content: ""; display: inline-block; width: 1.2rem; height: 0; margin-right: .3rem; vertical-align: middle; border-top: 2px solid var(--ink-soft); }
  .dl-key .k-waits::before { border-top-style: dashed; }
  .dl-key .k-cycle::before { border-top-color: var(--accent); border-top-width: 3px; }
  @media print { .dl-ctl, .dl-tabs { display: none; } }`;
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
  document.querySelectorAll(".deadlockviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const id = uid++;
    let mode = 0, step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="dl-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <svg class="dl-svg" viewBox="0 0 ${W} ${spec.height}" role="img" aria-label="wait-for graph"></svg>
      <div class="dl-key"><span>lock held by thread</span><span class="k-waits">thread waits for lock</span><span class="k-cycle">cycle = deadlock</span></div>
      <div class="dl-note" aria-live="polite"></div>
      <div class="dl-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="dl-count"></span>
      </div>`;
    const svg = root.querySelector("svg");

    function draw() {
      const m = spec.modes[mode], st = m.steps[step];
      const arrow = (cls, colour) => `<marker id="dl-${cls}-${id}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="${colour}"/></marker>`;
      let out = `<defs>${arrow("n", "var(--ink-soft)")}${arrow("c", "var(--accent)")}</defs>`;
      let labels = "";
      (st.edges || []).forEach(([f, t, kind]) => {
        const a = spec.nodes[f], b = spec.nodes[t];
        if (!a || !b) return;
        const [x1, y1] = exitPoint(a, b), [x2, y2] = exitPoint(b, a);
        const cyc = st.cycle ? " dl-cycle" : "";
        out += `<line class="dl-edge${kind === "waits" ? " dl-waits" : ""}${cyc}" x1="${x1.toFixed(1)}" y1="${y1.toFixed(1)}" x2="${x2.toFixed(1)}" y2="${y2.toFixed(1)}" marker-end="url(#dl-${st.cycle ? "c" : "n"}-${id})"/>`;
        const mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
        const flat = Math.abs(x2 - x1) >= Math.abs(y2 - y1);       // label beside the line, not on it
        labels += `<text class="dl-elabel${cyc}" x="${(flat ? mx : mx + 8).toFixed(1)}" y="${(flat ? my - 7 : my + 4).toFixed(1)}" text-anchor="${flat ? "middle" : "start"}">${kind === "waits" ? "waits for" : "held by"}</text>`;
      });
      out += labels;
      Object.entries(spec.nodes).forEach(([key, n]) => {
        const state = (st.state || {})[key];
        const cls = n.kind === "lock" ? "dl-lock" : "dl-thread" + (state ? " dl-" + state : "");
        const tag = (st.tags || {})[key];
        const below = n.y + NH / 2 > spec.height / 2;            // put tags on the outer side
        out += `<g class="dl-node ${cls}"><rect x="${n.x}" y="${n.y}" width="${NW}" height="${NH}"/>` +
          `<text x="${n.x + NW / 2}" y="${n.y + 22}" text-anchor="middle">${esc(n.label)}</text>` +
          (tag ? `<text class="dl-tag" x="${n.x + NW / 2}" y="${below ? n.y + NH + 15 : n.y - 7}" text-anchor="middle">${esc(tag)}</text>` : "") + `</g>`;
      });
      svg.innerHTML = out;
      root.querySelector(".dl-note").innerHTML = st.note;
      root.querySelector(".dl-count").textContent = `step ${step + 1}/${m.steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === m.steps.length - 1;
      root.querySelector('[data-act="all"]').disabled = step === m.steps.length - 1;
      root.querySelectorAll(".dl-tabs button").forEach((b) =>
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
