/* CS Fundamentals: step-through Git commit-graph diagram.
 *
 *   <div class="gitgraph" data-src="gg-1"></div>
 *   <script type="application/json" id="gg-1">{ ...spec... }</script>
 *   <script src="../assets/gitgraph.js"></script>
 *
 * Spec:
 *   { "steps": [
 *       { "cmd": "git switch -c greeting",               // command shown for this step
 *         "note": "What happened (HTML ok)",
 *         "commits": [ { "id": "55834c9", "msg": "Add Hello", "lane": 0, "parents": [] }, ... ],
 *         "refs": { "main": "55834c9", "greeting": "55834c9" },   // branch -> commit id
 *         "head": "greeting" }                            // a branch name, or a commit id when detached
 *   ] }
 * Commits are drawn left to right in array order; "lane" picks the row. New commits and moved
 * branch labels are highlighted relative to the previous step.
 */
(function () {
  const NS = "http://www.w3.org/2000/svg";
  const css = `
  .gitgraph { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .gitgraph:focus-visible { border-color: var(--accent); }
  .gg-cmd { font-family: var(--mono); font-size: .8rem; background: #1e1d1a; color: #ecead9; border-left: 3px solid var(--accent); border-radius: 5px; padding: .45rem .8rem; margin-bottom: .5rem; overflow-x: auto; white-space: pre; }
  .gg-cmd::before { content: "$ "; color: #8f8b7e; }
  .gg-note { font-size: .9rem; min-height: 2.4rem; margin-bottom: .5rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .gg-svgwrap { overflow-x: auto; }
  .gg-svgwrap svg { display: block; }
  .gg-edge { fill: none; stroke: var(--ink-soft); stroke-width: 2; }
  .gg-commit { fill: var(--bg); stroke: var(--ink); stroke-width: 2; }
  .gg-commit.gg-new { fill: var(--accent); stroke: var(--accent); }
  .gg-id { font-family: var(--mono); font-size: 11px; fill: var(--ink-soft); }
  .gg-msg { font-family: var(--serif); font-size: 11px; fill: var(--ink-soft); font-style: italic; }
  .gg-ref rect { fill: var(--surface); stroke: var(--java); stroke-width: 1.2; }
  .gg-ref text { font-family: var(--mono); font-size: 11px; fill: var(--java); font-weight: 600; }
  .gg-ref.gg-head rect { stroke: var(--kotlin); stroke-width: 2; }
  .gg-ref.gg-head text { fill: var(--kotlin); }
  .gg-ref.gg-moved rect { fill: color-mix(in srgb, var(--accent) 14%, var(--bg)); }
  .gg-ctl { display: flex; align-items: center; gap: .5rem; margin-top: .4rem; }
  .gg-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .gg-ctl button:disabled { opacity: .4; cursor: default; }
  .gg-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .gg-ctl { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const el = (name, attrs, text) => {
    const n = document.createElementNS(NS, name);
    for (const [k, v] of Object.entries(attrs || {})) n.setAttribute(k, v);
    if (text !== undefined) n.textContent = text;
    return n;
  };

  document.querySelectorAll(".gitgraph").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="gg-cmd"></div>
      <div class="gg-note" aria-live="polite"></div>
      <div class="gg-svgwrap"></div>
      <div class="gg-ctl"><button data-go="first" title="First step">⏮</button><button data-go="prev">◀ Prev</button><span class="gg-count"></span><button data-go="next">Next ▶</button></div>`;

    const go = (where) => {
      if (where === "first") step = 0;
      if (where === "prev") step = Math.max(0, step - 1);
      if (where === "next") step = Math.min(spec.steps.length - 1, step + 1);
      render();
    };
    root.querySelector(".gg-ctl").addEventListener("click", (e) => e.target.dataset.go && go(e.target.dataset.go));
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") { go("next"); e.preventDefault(); }
      if (e.key === "ArrowLeft") { go("prev"); e.preventDefault(); }
    });

    function render() {
      const s = spec.steps[step];
      const prev = step > 0 ? spec.steps[step - 1] : null;
      const prevIds = new Set(prev ? prev.commits.map((c) => c.id) : []);
      root.querySelector(".gg-cmd").textContent = s.cmd || "";
      root.querySelector(".gg-note").innerHTML = s.note || "";
      root.querySelector(".gg-count").textContent = `step ${step + 1} / ${spec.steps.length}`;
      root.querySelector("[data-go=prev]").disabled = step === 0;
      root.querySelector("[data-go=first]").disabled = step === 0;
      root.querySelector("[data-go=next]").disabled = step === spec.steps.length - 1;

      // Layout: x by order, y by lane; leave room above each commit for branch labels
      const pos = {};
      const maxLane = Math.max(0, ...spec.steps.flatMap((st) => st.commits.map((c) => c.lane)));
      const maxCommits = Math.max(...spec.steps.map((st) => st.commits.length));
      const refsPer = {};
      for (const [name, id] of Object.entries(s.refs || {})) (refsPer[id] ||= []).push(name);
      const maxStack = Math.max(1, ...spec.steps.map((st) => {
        const c = {};
        Object.values(st.refs || {}).forEach((id) => (c[id] = (c[id] || 0) + 1));
        return Math.max(0, ...Object.values(c)) + (typeof st.head === "string" && !(st.refs || {})[st.head] ? 1 : 0);
      }));
      const top = 18 + maxStack * 22;
      const laneH = 74 + maxStack * 22;
      s.commits.forEach((c, i) => { pos[c.id] = { x: 50 + i * 110, y: top + c.lane * laneH }; });
      const width = 50 + Math.max(1, maxCommits) * 110;
      const height = top + maxLane * laneH + 46;

      const svg = el("svg", { width, height, viewBox: `0 0 ${width} ${height}`, role: "img", "aria-label": "commit graph" });
      // edges first, so circles sit on top
      s.commits.forEach((c) => (c.parents || []).forEach((p) => {
        const a = pos[p], b = pos[c.id];
        if (!a || !b) return;
        const d = a.y === b.y ? `M${a.x},${a.y} L${b.x},${b.y}` : `M${a.x},${a.y} C${a.x + 55},${a.y} ${b.x - 55},${b.y} ${b.x},${b.y}`;
        svg.appendChild(el("path", { d, class: "gg-edge" }));
      }));
      s.commits.forEach((c) => {
        const { x, y } = pos[c.id];
        const circle = el("circle", { cx: x, cy: y, r: 10, class: "gg-commit" + (prev && !prevIds.has(c.id) ? " gg-new" : "") });
        circle.appendChild(el("title", {}, `${c.id} ${c.msg || ""}`));
        svg.appendChild(circle);
        svg.appendChild(el("text", { x, y: y + 26, "text-anchor": "middle", class: "gg-id" }, c.id));
        if (c.msg) svg.appendChild(el("text", { x, y: y + 40, "text-anchor": "middle", class: "gg-msg" }, c.msg.length > 20 ? c.msg.slice(0, 19) + "…" : c.msg));
      });
      // branch labels stacked above their commit; HEAD marks the branch it points to
      const labelsAt = {};
      for (const [name, id] of Object.entries(s.refs || {})) (labelsAt[id] ||= []).push({ text: name, head: s.head === name, moved: !prev || (prev.refs || {})[name] !== id });
      if (s.head && !(s.refs || {})[s.head] && pos[s.head]) (labelsAt[s.head] ||= []).push({ text: "HEAD (detached)", head: true, moved: false });
      for (const [id, labels] of Object.entries(labelsAt)) {
        if (!pos[id]) continue;
        labels.forEach((lab, k) => {
          const text = lab.head && !lab.text.startsWith("HEAD") ? `HEAD → ${lab.text}` : lab.text;
          const w = 12 + text.length * 7;
          const { x, y } = pos[id];
          const ly = y - 22 - k * 22;
          const g = el("g", { class: "gg-ref" + (lab.head ? " gg-head" : "") + (lab.moved && prev ? " gg-moved" : "") });
          g.appendChild(el("rect", { x: x - w / 2, y: ly - 13, width: w, height: 18, rx: 4 }));
          g.appendChild(el("text", { x, y: ly, "text-anchor": "middle" }, text));
          svg.appendChild(g);
        });
      }
      const wrap = root.querySelector(".gg-svgwrap");
      wrap.innerHTML = "";
      wrap.appendChild(svg);
    }
    render();
  });
})();
