/* CS Fundamentals: step-through graph search and DP-table diagram.
 *
 *   <div class="graphviz" data-src="gv-1"></div>
 *   <script type="application/json" id="gv-1">{ ...spec... }</script>
 *   <script src="../assets/graphviz.js"></script>
 *
 * Graph spec (one tab per mode, e.g. BFS vs DFS):
 *   { "kind": "graph",
 *     "nodes": { "A": [x, y], ... },              // positions in a 480 x 240 box
 *     "edges": [ ["A", "B"], ... ],               // undirected
 *     "modes": [ { "name": "BFS · queue", "ds": "queue" | "stack",
 *                  "steps": [ { "cur": "A" | null, "seen": [...], "done": [...],
 *                               "ds": [...], "tree": [["A","B"], ...], "note": "HTML ok" } ] } ] }
 *   done = visited, in order; seen = discovered (in the queue/stack or done); ds = queue front-first
 *   or stack bottom-first; tree = edges that discovered a vertex.
 *
 * Table spec (a DP table filling cell by cell):
 *   { "kind": "table", "rows": 4, "cols": 4, "blocked": [[r, c], ...],
 *     "steps": [ { "r": 0, "c": 0, "v": "1", "from": [[r, c], ...], "note": "HTML ok" } ] }
 *
 * Both are generated from real runs (see .build/l21/viz/GraphTrace.java).
 */
(function () {
  const css = `
  .graphviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; outline: none; }
  .graphviz:focus-visible { border-color: var(--accent); }
  .gv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .gv-tabs button, .gv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .gv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .gv-ctl button:disabled { opacity: .4; cursor: default; }
  .gv-svg { width: 100%; height: auto; display: block; }
  .gv-svg line { stroke: var(--rule); stroke-width: 2; }
  .gv-svg line.gv-tree { stroke: var(--kotlin); stroke-width: 4; }
  .gv-svg circle { fill: var(--bg); stroke: var(--ink-soft); stroke-width: 2; }
  .gv-svg g.gv-seen circle { stroke: var(--java); stroke-dasharray: 5 3; stroke-width: 3; }
  .gv-svg g.gv-done circle { fill: color-mix(in srgb, var(--ok) 22%, var(--bg)); stroke: var(--ok); stroke-dasharray: none; }
  .gv-svg g.gv-cur circle { fill: color-mix(in srgb, var(--accent) 25%, var(--bg)); stroke: var(--accent); stroke-width: 4; stroke-dasharray: none; }
  .gv-svg text { font-family: var(--mono); font-size: 18px; font-weight: 600; fill: var(--ink); text-anchor: middle; dominant-baseline: central; }
  .gv-svg text.gv-idx { font-size: 12px; font-weight: 400; fill: var(--ink-soft); }
  .gv-row { display: flex; align-items: center; gap: .35rem; flex-wrap: wrap; font-family: var(--mono); font-size: .78rem; margin: .35rem 0; min-height: 1.7rem; }
  .gv-row b { font-size: .66rem; letter-spacing: .08em; color: var(--ink-soft); min-width: 4.2rem; }
  .gv-row span.gv-box { display: inline-block; min-width: 1.6rem; text-align: center; padding: .1rem .35rem; border: 1px solid var(--java); border-radius: 4px; color: var(--java); }
  .gv-row span.gv-ord { color: var(--ok); }
  .gv-row em { color: var(--ink-soft); font-style: normal; font-size: .7rem; }
  .gv-table { border-collapse: collapse; font-family: var(--mono); font-size: .95rem; margin: .2rem auto; }
  .gv-table td { width: 2.6rem; height: 2.3rem; text-align: center; border: 1px solid var(--rule); }
  .gv-table th { font-size: .66rem; color: var(--ink-soft); font-weight: 400; padding: 0 .3rem; }
  .gv-table td.gv-wall { background: repeating-linear-gradient(45deg, var(--surface), var(--surface) 4px, var(--rule) 4px, var(--rule) 6px); color: var(--ink-soft); }
  .gv-table td.gv-filled { color: var(--ink); }
  .gv-table td.gv-from { background: color-mix(in srgb, var(--kotlin) 16%, var(--bg)); }
  .gv-table td.gv-now { outline: 3px solid var(--accent); outline-offset: -3px; font-weight: 700; }
  .gv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .gv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .gv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .gv-ctl, .gv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const NS = "http://www.w3.org/2000/svg";

  function setupGraph(root, spec) {
    const ids = Object.keys(spec.nodes);
    const lines = spec.edges.map(([a, b]) => {
      const [x1, y1] = spec.nodes[a], [x2, y2] = spec.nodes[b];
      return `<line data-e="${a}-${b}" x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}"/>`;
    }).join("");
    const circles = ids.map((id) => {
      const [x, y] = spec.nodes[id];
      return `<g data-n="${id}"><circle cx="${x}" cy="${y}" r="20"/><text x="${x}" y="${y}">${esc(id)}</text>` +
        `<text class="gv-idx" data-ord="${id}" x="${x + 24}" y="${y - 22}"></text></g>`;
    }).join("");
    return {
      body: `<svg class="gv-svg" viewBox="0 0 480 240" xmlns="${NS}" role="img" aria-label="graph">${lines}${circles}</svg>
        <div class="gv-row gv-ds"></div><div class="gv-row gv-order"></div>`,
      draw(mode, st) {
        const done = new Set(st ? st.done : []), seen = new Set(st ? st.seen : []);
        root.querySelectorAll("g[data-n]").forEach((g) => {
          const id = g.dataset.n;
          g.setAttribute("class", st && st.cur === id ? "gv-cur" : done.has(id) ? "gv-done" : seen.has(id) ? "gv-seen" : "");
          const k = st ? st.done.indexOf(id) : -1;
          g.querySelector("[data-ord]").textContent = k >= 0 ? String(k + 1) : "";
        });
        const tree = new Set((st ? st.tree : []).flatMap(([a, b]) => [`${a}-${b}`, `${b}-${a}`]));
        root.querySelectorAll("line[data-e]").forEach((l) => l.setAttribute("class", tree.has(l.dataset.e) ? "gv-tree" : ""));
        const isQueue = mode.ds === "queue";
        const items = st ? st.ds : [];
        root.querySelector(".gv-ds").innerHTML = `<b>${isQueue ? "QUEUE" : "STACK"}</b>` +
          (items.length ? (isQueue ? "<em>front →</em>" : "<em>bottom →</em>") +
            items.map((x) => `<span class="gv-box">${esc(x)}</span>`).join("") + (isQueue ? "<em>← back</em>" : "<em>← top</em>")
            : "<em>empty</em>");
        root.querySelector(".gv-order").innerHTML = "<b>VISITED</b>" +
          ((st && st.done.length) ? st.done.map((x) => `<span class="gv-ord">${esc(x)}</span>`).join(" ") : "<em>none yet</em>");
      },
    };
  }

  function setupTable(root, spec) {
    let rows = "<tr><th></th>" + Array.from({ length: spec.cols }, (_, c) => `<th>c${c}</th>`).join("") + "</tr>";
    for (let r = 0; r < spec.rows; r++) {
      rows += `<tr><th>r${r}</th>` + Array.from({ length: spec.cols }, (_, c) => `<td data-r="${r}" data-c="${c}"></td>`).join("") + "</tr>";
    }
    const wall = new Set(spec.blocked.map(([r, c]) => `${r},${c}`));
    return {
      body: `<table class="gv-table">${rows}</table>`,
      draw(mode, st, k) {
        root.querySelectorAll("td[data-r]").forEach((td) => {
          td.className = wall.has(`${td.dataset.r},${td.dataset.c}`) ? "gv-wall" : "";
          td.textContent = wall.has(`${td.dataset.r},${td.dataset.c}`) ? "#" : "";
        });
        for (let i = 0; i < k; i++) {
          const s = spec.steps[i];
          const td = root.querySelector(`td[data-r="${s.r}"][data-c="${s.c}"]`);
          td.textContent = s.v;
          td.classList.add("gv-filled");
          if (i === k - 1) td.classList.add("gv-now");
        }
        if (st) st.from.forEach(([r, c]) => root.querySelector(`td[data-r="${r}"][data-c="${c}"]`).classList.add("gv-from"));
      },
    };
  }

  document.querySelectorAll(".graphviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const modes = spec.kind === "table" ? [{ name: "DP table", steps: spec.steps }] : spec.modes;
    let mode = 0, step = 0;
    root.tabIndex = 0;
    const view = spec.kind === "table" ? setupTable(root, spec) : setupGraph(root, spec);
    root.innerHTML = (modes.length > 1 ? `<div class="gv-tabs" role="tablist">${modes.map((m, k) =>
      `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>` : "") +
      view.body +
      `<div class="gv-note" aria-live="polite"></div>
       <div class="gv-ctl"><button data-act="prev">◀ Back</button><button data-act="next">Step ▶</button>
       <button data-act="all">Run all</button><span class="gv-count"></span></div>`;

    function draw() {
      const m = modes[mode], n = m.steps.length;
      const st = step > 0 ? m.steps[step - 1] : null;
      view.draw(m, st, step);
      root.querySelector(".gv-note").innerHTML = st ? st.note : "Press <b>Step</b> (or the → key) to run one step at a time.";
      root.querySelector(".gv-count").textContent = `step ${step}/${n}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === n;
      root.querySelector('[data-act="all"]').disabled = step === n;
      root.querySelectorAll(".gv-tabs button").forEach((b) => b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }
    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = modes[mode].steps.length;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = n;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = modes[mode].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
