/* CS Fundamentals: step-through Dijkstra and Kruskal (union-find) on one weighted graph.
 *
 *   <div class="pathviz" data-src="pv-1"></div>
 *   <script type="application/json" id="pv-1">{ ...spec... }</script>
 *   <script src="../assets/pathviz.js"></script>
 *
 * Spec (one tab per mode):
 *   { "nodes": { "A": [x, y], ... },                 // positions in a 480 x 240 box
 *     "edges": [ ["A", "B", 4], ... ],               // undirected, with weights
 *     "modes": [
 *       { "kind": "dijkstra", "name": "...", "steps": [ { "cur": "A" | null, "stale": bool, "settled": [...],
 *           "dist": { "A": 0, ... }, "pq": [[d, "B"], ...], "tree": [["A","B"], ...],
 *           "hl": [["A","B"], ...], "note": "HTML ok" } ] },
 *       { "kind": "kruskal", "name": "...", "steps": [ { "edge": ["A","B"] | null, "take": bool,
 *           "tree": [["B","C"], ...], "parent": { "A": "A", ... }, "note": "HTML ok" } ] } ] }
 *   dist lists only towns reached so far (the rest are ∞); pq is sorted smallest first;
 *   tree = parent edges (Dijkstra) or edges taken (Kruskal); hl = edges relaxed in this step.
 *
 * Generated from real runs (see .build/l49/viz/PathTrace.java).
 */
(function () {
  const css = `
  .pathviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; outline: none; }
  .pathviz:focus-visible { border-color: var(--accent); }
  .pv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .pv-tabs button, .pv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .pv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .pv-ctl button:disabled { opacity: .4; cursor: default; }
  .pv-svg { width: 100%; height: auto; display: block; }
  .pv-svg line { stroke: var(--rule); stroke-width: 2; }
  .pv-svg line.pv-tree { stroke: var(--kotlin); stroke-width: 5; }
  .pv-svg line.pv-hl { stroke: var(--java); stroke-width: 4; stroke-dasharray: 7 4; }
  .pv-svg line.pv-skip { stroke: var(--accent); stroke-width: 4; stroke-dasharray: 3 4; }
  .pv-svg line.pv-take { stroke: var(--ok); stroke-width: 6; }
  .pv-svg .pv-w rect { fill: var(--bg); }
  .pv-svg .pv-w text { font-family: var(--mono); font-size: 13px; fill: var(--ink-soft); text-anchor: middle; dominant-baseline: central; }
  .pv-svg circle { fill: var(--bg); stroke: var(--ink-soft); stroke-width: 2; }
  .pv-svg g.pv-done circle { fill: color-mix(in srgb, var(--ok) 22%, var(--bg)); stroke: var(--ok); }
  .pv-svg g.pv-cur circle { fill: color-mix(in srgb, var(--accent) 25%, var(--bg)); stroke: var(--accent); stroke-width: 4; }
  .pv-svg g.pv-end circle { stroke: var(--accent); stroke-width: 4; }
  .pv-svg text.pv-name { font-family: var(--mono); font-size: 18px; font-weight: 600; fill: var(--ink); text-anchor: middle; dominant-baseline: central; }
  .pv-svg text.pv-d { font-family: var(--mono); font-size: 13px; font-weight: 600; fill: var(--kotlin); text-anchor: middle; }
  .pv-row { display: flex; align-items: center; gap: .35rem; flex-wrap: wrap; font-family: var(--mono); font-size: .78rem; margin: .35rem 0; min-height: 1.7rem; }
  .pv-row b { font-size: .66rem; letter-spacing: .08em; color: var(--ink-soft); min-width: 4.6rem; }
  .pv-row span.pv-box { display: inline-block; text-align: center; padding: .1rem .35rem; border: 1px solid var(--java); border-radius: 4px; color: var(--java); }
  .pv-row span.pv-box.pv-stale { border-style: dashed; opacity: .7; }
  .pv-row span.pv-set { display: inline-block; padding: .1rem .4rem; border: 1px solid var(--ok); border-radius: 4px; color: var(--ok); }
  .pv-row span.pv-par { color: var(--ink); }
  .pv-row span.pv-par.pv-root { color: var(--ok); font-weight: 600; }
  .pv-row em { color: var(--ink-soft); font-style: normal; font-size: .7rem; }
  .pv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .pv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .pv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .pv-ctl, .pv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const NS = "http://www.w3.org/2000/svg";
  const key = (a, b) => (a < b ? `${a}-${b}` : `${b}-${a}`);

  function svg(spec) {
    const lines = spec.edges.map(([a, b]) => {
      const [x1, y1] = spec.nodes[a], [x2, y2] = spec.nodes[b];
      return `<line data-e="${key(a, b)}" x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}"/>`;
    }).join("");
    const weights = spec.edges.map(([a, b, w]) => {
      const [x1, y1] = spec.nodes[a], [x2, y2] = spec.nodes[b];
      const x = (x1 + x2) / 2, y = (y1 + y2) / 2;
      return `<g class="pv-w"><rect x="${x - 11}" y="${y - 9}" width="22" height="18" rx="4"/><text x="${x}" y="${y}">${esc(w)}</text></g>`;
    }).join("");
    const nodes = Object.entries(spec.nodes).map(([id, [x, y]]) =>
      `<g data-n="${id}"><circle cx="${x}" cy="${y}" r="19"/><text class="pv-name" x="${x}" y="${y}">${esc(id)}</text>` +
      `<text class="pv-d" data-d="${id}" x="${x}" y="${y < 120 ? y - 26 : y + 36}"></text></g>`).join("");
    return `<svg class="pv-svg" viewBox="0 0 480 240" xmlns="${NS}" role="img" aria-label="weighted graph">${lines}${weights}${nodes}</svg>`;
  }

  function drawDijkstra(root, st) {
    const settled = new Set(st ? st.settled : []);
    root.querySelectorAll("g[data-n]").forEach((g) => {
      const id = g.dataset.n;
      g.setAttribute("class", st && st.cur === id && !st.stale ? "pv-cur" : settled.has(id) ? "pv-done" : "");
      const d = st && st.dist[id] !== undefined ? st.dist[id] : "∞";
      g.querySelector("[data-d]").textContent = String(d);
    });
    const tree = new Set((st ? st.tree : []).map(([a, b]) => key(a, b)));
    const hl = new Set((st ? st.hl : []).map(([a, b]) => key(a, b)));
    root.querySelectorAll("line[data-e]").forEach((l) =>
      l.setAttribute("class", hl.has(l.dataset.e) ? "pv-hl" : tree.has(l.dataset.e) ? "pv-tree" : ""));
    const pq = st ? st.pq : [];
    root.querySelector(".pv-a").innerHTML = "<b>QUEUE</b>" + (pq.length
      ? "<em>smallest →</em>" + pq.map(([d, v]) =>
        `<span class="pv-box${settled.has(v) ? " pv-stale" : ""}">(${esc(d)}, ${esc(v)})</span>`).join("")
      : "<em>empty</em>");
    root.querySelector(".pv-b").innerHTML = "<b>SETTLED</b>" + ((st && st.settled.length)
      ? st.settled.map((v) => `<span class="pv-par pv-root">${esc(v)}=${esc(st.dist[v])}</span>`).join(" ")
      : "<em>none yet</em>");
  }

  function drawKruskal(root, st) {
    const tree = new Set((st ? st.tree : []).map(([a, b]) => key(a, b)));
    const cur = st && st.edge ? key(st.edge[0], st.edge[1]) : null;
    root.querySelectorAll("line[data-e]").forEach((l) => {
      const e = l.dataset.e;
      l.setAttribute("class", e === cur ? (st.take ? "pv-take" : "pv-skip") : tree.has(e) ? "pv-tree" : "");
    });
    const ends = new Set(st && st.edge ? st.edge : []);
    root.querySelectorAll("g[data-n]").forEach((g) => {
      g.setAttribute("class", ends.has(g.dataset.n) ? "pv-end" : "");
      g.querySelector("[data-d]").textContent = "";
    });
    const parent = st ? st.parent : null;
    if (!parent) { root.querySelector(".pv-a").innerHTML = ""; root.querySelector(".pv-b").innerHTML = ""; return; }
    root.querySelector(".pv-a").innerHTML = "<b>PARENT</b>" + Object.entries(parent).map(([x, p]) =>
      `<span class="pv-par${x === p ? " pv-root" : ""}">${esc(x)}→${esc(p)}</span>`).join(" ");
    const findRoot = (x) => { while (parent[x] !== x) x = parent[x]; return x; };
    const sets = {};
    Object.keys(parent).forEach((x) => { (sets[findRoot(x)] = sets[findRoot(x)] || []).push(x); });
    root.querySelector(".pv-b").innerHTML = "<b>SETS</b>" + Object.values(sets).map((s) =>
      `<span class="pv-set">{${s.map(esc).join(",")}}</span>`).join("");
  }

  document.querySelectorAll(".pathviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const modes = spec.modes;
    let mode = 0, step = 1;
    root.tabIndex = 0;
    root.innerHTML = (modes.length > 1 ? `<div class="pv-tabs" role="tablist">${modes.map((m, k) =>
      `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>` : "") +
      svg(spec) + `<div class="pv-row pv-a"></div><div class="pv-row pv-b"></div>
      <div class="pv-note" aria-live="polite"></div>
      <div class="pv-ctl"><button data-act="prev">◀ Back</button><button data-act="next">Step ▶</button>
      <button data-act="all">Run all</button><span class="pv-count"></span></div>`;

    function draw() {
      const m = modes[mode], n = m.steps.length;
      const st = m.steps[step - 1];
      (m.kind === "kruskal" ? drawKruskal : drawDijkstra)(root, st);
      root.querySelector(".pv-note").innerHTML = st.note + (step === 1 ? " Press <b>Step</b> (or the → key)." : "");
      root.querySelector(".pv-count").textContent = `step ${step}/${n}`;
      root.querySelector('[data-act="prev"]').disabled = step === 1;
      root.querySelector('[data-act="next"]').disabled = step === n;
      root.querySelector('[data-act="all"]').disabled = step === n;
      root.querySelectorAll(".pv-tabs button").forEach((b) => b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }
    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = modes[mode].steps.length;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 1; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(1, step - 1);
      else if (b.dataset.act === "all") step = n;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = modes[mode].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(1, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
