/* CS Fundamentals: step-through binary tree and heap diagram.
 *
 *   <div class="treeviz" data-src="tv-1"></div>
 *   <script type="application/json" id="tv-1">{ ...spec... }</script>
 *   <script src="../assets/treeviz.js"></script>
 *
 * Spec:
 *   { "modes": [                                   // tabs, e.g. "contains 60" / "insert 65"
 *       { "name": "contains 60",
 *         "steps": [ { "t": [50, [30, null, null], [70, null, null]],   // tree: [key, left, right], null = empty
 *                      "cur": [50], "seen": [], "ok": [], "del": [],     // node ids to highlight
 *                      "out": "50 30",                                   // optional output line
 *                      "note": "HTML ok" }, ... ] } ] }
 * A heap step uses "h": [1, 2, 8, 5] (the backing array) instead of "t". It is drawn as a
 * complete tree plus the array underneath, and the highlight ids are array INDICES, not values.
 * In a tree step the ids are the keys themselves (keys are unique in a BST).
 * Every step is a full snapshot, so Back works without undo logic.
 */
(function () {
  const css = `
  .treeviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .treeviz:focus-visible { border-color: var(--accent); }
  .tv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .tv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .tv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .tv-stage { overflow-x: auto; text-align: center; }
  .tv-stage svg { display: block; margin: 0 auto; height: auto; }
  .tv-stage line { stroke: var(--ink-soft); stroke-width: 1.4; }
  .tv-stage circle { fill: var(--bg); stroke: var(--ink-soft); stroke-width: 1.4; }
  .tv-stage text { font-family: var(--mono); font-size: 12px; fill: var(--ink); text-anchor: middle; dominant-baseline: central; }
  .tv-stage text.tv-idx { font-size: 9px; fill: var(--ink-soft); }
  .tv-stage .tv-seen circle { fill: color-mix(in srgb, var(--kotlin) 16%, var(--bg)); stroke: var(--kotlin); }
  .tv-stage .tv-ok circle { fill: color-mix(in srgb, var(--ok) 20%, var(--bg)); stroke: var(--ok); stroke-width: 2; }
  .tv-stage .tv-del circle { fill: color-mix(in srgb, var(--accent) 16%, var(--bg)); stroke: var(--accent); stroke-dasharray: 3 2; stroke-width: 2; }
  .tv-stage .tv-cur circle { stroke: var(--accent); stroke-width: 3; }
  .tv-arr { display: flex; justify-content: center; flex-wrap: wrap; gap: 0; margin: .5rem 0 0; font-family: var(--mono); font-size: .78rem; }
  .tv-arr div { border: 1px solid var(--rule); min-width: 2.2rem; padding: .15rem .3rem; text-align: center; margin-left: -1px; }
  .tv-arr div small { display: block; font-size: .6rem; color: var(--ink-soft); }
  .tv-arr .tv-seen { background: color-mix(in srgb, var(--kotlin) 16%, var(--bg)); }
  .tv-arr .tv-ok { background: color-mix(in srgb, var(--ok) 20%, var(--bg)); }
  .tv-arr .tv-del { background: color-mix(in srgb, var(--accent) 16%, var(--bg)); }
  .tv-arr .tv-cur { outline: 2px solid var(--accent); outline-offset: -2px; position: relative; }
  .tv-out { font-family: var(--mono); font-size: .8rem; margin: .5rem 0 0; min-height: 1.2rem; }
  .tv-out b { color: var(--ink-soft); font-weight: 400; }
  .tv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .tv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .tv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .tv-ctl button:disabled { opacity: .4; cursor: default; }
  .tv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  .tv-key { font-size: .72rem; color: var(--ink-soft); margin-top: .4rem; }
  .tv-key span { display: inline-block; width: .7rem; height: .7rem; border-radius: 50%; vertical-align: -1px; margin: 0 .2rem 0 .6rem; border: 2px solid; }
  @media print { .tv-ctl, .tv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const NS = "http://www.w3.org/2000/svg";
  const DX = 44, DY = 54, R = 15, PAD = 22;

  // Heap array -> nested [id, left, right] tree where id = array index.
  function heapToTree(h, i) {
    if (i >= h.length) return null;
    return [i, heapToTree(h, 2 * i + 1), heapToTree(h, 2 * i + 2)];
  }

  // In-order position gives x, depth gives y: subtrees never overlap.
  function layout(tree) {
    const nodes = [], edges = [];
    let x = 0;
    (function walk(t, depth, parent) {
      if (!t) return;
      const me = { id: t[0], depth };
      walk(t[1], depth + 1, me);
      me.x = x++;
      nodes.push(me);
      if (parent) edges.push([parent, me]);
      walk(t[2], depth + 1, me);
    })(tree, 0, null);
    return { nodes, edges, width: x, depth: nodes.reduce((m, n) => Math.max(m, n.depth), 0) };
  }

  function classFor(step, id) {
    const has = (k) => (step[k] || []).includes(id);
    return [has("seen") && "tv-seen", has("ok") && "tv-ok", has("del") && "tv-del", has("cur") && "tv-cur"]
      .filter(Boolean).join(" ");
  }

  function svgFor(step) {
    const isHeap = Array.isArray(step.h);
    const tree = isHeap ? heapToTree(step.h, 0) : step.t;
    const { nodes, edges, width, depth } = layout(tree);
    const w = Math.max(1, width) * DX + PAD, h = (depth + 1) * DY + (isHeap ? 10 : 0);
    const svg = document.createElementNS(NS, "svg");
    svg.setAttribute("viewBox", `0 0 ${w} ${h}`);
    svg.setAttribute("width", w);
    svg.style.maxWidth = "100%";
    svg.setAttribute("role", "img");
    const pos = (n) => [PAD / 2 + n.x * DX + DX / 2, n.depth * DY + R + 6];
    for (const [a, b] of edges) {
      const [x1, y1] = pos(a), [x2, y2] = pos(b);
      const l = document.createElementNS(NS, "line");
      l.setAttribute("x1", x1); l.setAttribute("y1", y1); l.setAttribute("x2", x2); l.setAttribute("y2", y2);
      svg.appendChild(l);
    }
    const labels = [];
    for (const n of nodes) {
      const [cx, cy] = pos(n);
      const g = document.createElementNS(NS, "g");
      g.setAttribute("class", classFor(step, n.id));
      const c = document.createElementNS(NS, "circle");
      c.setAttribute("cx", cx); c.setAttribute("cy", cy); c.setAttribute("r", R);
      const t = document.createElementNS(NS, "text");
      t.setAttribute("x", cx); t.setAttribute("y", cy);
      t.textContent = isHeap ? step.h[n.id] : n.id;
      g.appendChild(c); g.appendChild(t);
      if (isHeap) {
        const ix = document.createElementNS(NS, "text");
        ix.setAttribute("class", "tv-idx");
        ix.setAttribute("x", cx); ix.setAttribute("y", cy + R + 7);
        ix.textContent = "[" + n.id + "]";
        g.appendChild(ix);
      }
      svg.appendChild(g);
      labels.push(isHeap ? step.h[n.id] : n.id);
    }
    svg.setAttribute("aria-label", nodes.length ? "tree with nodes " + labels.join(", ") : "empty tree");
    return svg;
  }

  document.querySelectorAll(".treeviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let mode = 0, step = 0;
    root.tabIndex = 0;
    const tabs = spec.modes.length > 1
      ? `<div class="tv-tabs" role="tablist">${spec.modes.map((m, k) =>
          `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>` : "";
    root.innerHTML = `${tabs}
      <div class="tv-stage"></div>
      <div class="tv-out"></div>
      <div class="tv-note" aria-live="polite"></div>
      <div class="tv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="tv-count"></span>
      </div>
      <div class="tv-key"><span style="border-color:var(--accent)"></span>current<span style="border-color:var(--kotlin)"></span>visited<span style="border-color:var(--ok)"></span>found / done<span style="border-color:var(--accent);border-style:dashed"></span>to remove</div>`;

    function draw() {
      const steps = spec.modes[mode].steps;
      const st = steps[step];
      const stage = root.querySelector(".tv-stage");
      stage.innerHTML = "";
      stage.appendChild(svgFor(st));
      if (Array.isArray(st.h)) {
        const arr = document.createElement("div");
        arr.className = "tv-arr";
        arr.innerHTML = st.h.length
          ? st.h.map((v, i) => `<div class="${classFor(st, i)}">${esc(v)}<small>${i}</small></div>`).join("")
          : "<div>empty<small>&nbsp;</small></div>";
        stage.appendChild(arr);
      }
      root.querySelector(".tv-out").innerHTML = st.out !== undefined ? `<b>output:</b> ${esc(st.out)}` : "";
      root.querySelector(".tv-note").innerHTML = st.note || "";
      root.querySelector(".tv-count").textContent = `step ${step + 1}/${steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length - 1;
      root.querySelector('[data-act="all"]').disabled = step === steps.length - 1;
      root.querySelectorAll(".tv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = spec.modes[mode].steps.length;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n - 1, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = n - 1;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.modes[mode].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n - 1, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
