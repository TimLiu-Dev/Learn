/* CS Fundamentals: step-through B+-tree index diagram.
 *
 *   <div class="btreeviz" data-src="bt-1"></div>
 *   <script type="application/json" id="bt-1">{ ...spec... }</script>
 *   <script src="../assets/btreeviz.js"></script>
 *
 * Spec:
 *   { "root": "N2_0",
 *     "nodes": { "N2_0": { "keys": ["≤6·r23"], "kids": ["N1_0", "N1_1"] },     // interior page
 *                "L0":   { "entries": [{ "k": "1·r12", "r": "r12" }, ...], "next": "L1" } },  // leaf page
 *     "table": { "label": "...", "pages": [[{ "r": "r1", "t": "r1 m=8" }, ...], ...] },   // optional heap pages
 *     "modes": [ { "name": "Lookup", "steps": [
 *        { "nodes": [...visited], "now": [...current], "entries": ["L4:0", ...], "rows": ["r6"],
 *          "pages": [0, 2], "nowPage": 2, "reads": 3, "note": "html" } ] } ] }
 * Each step is a full snapshot, so Back is just "show the previous snapshot".
 */
(function () {
  const css = `
  .btreeviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; outline: none; }
  .btreeviz:focus-visible { border-color: var(--accent); }
  .bt-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .bt-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .bt-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .bt-wrap { overflow-x: auto; padding-bottom: .3rem; } /* levels wrap, so the current page is always in view */
  .bt-level { display: flex; flex-wrap: wrap; justify-content: center; align-items: center; gap: .45rem .5rem; margin: 0 auto .55rem; }
  .bt-lab { font-family: var(--mono); font-size: .62rem; letter-spacing: .06em; text-transform: uppercase; color: var(--ink-soft); text-align: center; margin: .2rem 0 .2rem; }
  .bt-node { font-family: var(--mono); font-size: .72rem; border: 1px solid var(--rule); border-radius: 5px; background: var(--surface); padding: .25rem .4rem; display: flex; gap: .35rem; white-space: nowrap; }
  .bt-node.bt-int span { color: var(--ink-soft); }
  .bt-ent { padding: 0 .15rem; border-radius: 3px; }
  .bt-arrow { color: var(--ink-soft); font-size: .8rem; }
  .bt-hl { border-color: var(--kotlin); background: color-mix(in srgb, var(--kotlin) 10%, var(--surface)); }
  .bt-now { outline: 2px solid var(--accent); outline-offset: 1px; }
  .bt-hit { color: var(--ok) !important; background: color-mix(in srgb, var(--ok) 16%, var(--bg)); font-weight: 600; }
  .bt-pages { display: flex; flex-wrap: wrap; gap: .4rem; justify-content: center; margin-top: .3rem; }
  .bt-page { font-family: var(--mono); font-size: .68rem; border: 1px dashed var(--rule); border-radius: 5px; padding: .2rem .35rem; display: grid; gap: .05rem; }
  .bt-page.bt-hl { border-style: solid; }
  .bt-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .bt-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .bt-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .bt-ctl button:disabled { opacity: .4; cursor: default; }
  .bt-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .bt-ctl, .bt-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".btreeviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const nodes = spec.nodes;
    let mode = 0, step = 0;
    root.tabIndex = 0;

    // Breadth-first levels from the root.
    const levels = [];
    for (let lvl = [spec.root]; lvl.length; lvl = lvl.flatMap((id) => nodes[id].kids || [])) levels.push(lvl);

    const nodeHtml = (id) => {
      const n = nodes[id];
      if (n.kids) return `<div class="bt-node bt-int" data-node="${id}">${n.keys.map((k) => `<span>${esc(k)}</span>`).join("")}</div>`;
      return `<div class="bt-node" data-node="${id}">${n.entries.map((e, i) =>
        `<span class="bt-ent" data-ent="${id}:${i}">${esc(e.k)}</span>`).join("")}</div>`;
    };
    const levelName = (i) => i === 0 ? "root page" : i === levels.length - 1 ? "leaf pages (linked →)" : "interior pages";
    const treeHtml = levels.map((lvl, i) => `<div class="bt-lab">${levelName(i)}</div><div class="bt-level">` +
      lvl.map((id, j) => nodeHtml(id) + (i === levels.length - 1 && j < lvl.length - 1 ? '<span class="bt-arrow">→</span>' : "")).join("") +
      "</div>").join("");
    const tableHtml = spec.table ? `<div class="bt-lab">${esc(spec.table.label)}</div><div class="bt-pages">` +
      spec.table.pages.map((pg, p) => `<div class="bt-page" data-page="${p}">${pg.map((c) =>
        `<span class="bt-ent" data-row="${c.r}">${esc(c.t)}</span>`).join("")}</div>`).join("") + "</div>" : "";

    root.innerHTML = `
      <div class="bt-tabs" role="tablist">${spec.modes.map((m, k) =>
        `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>
      <div class="bt-wrap">${treeHtml}${tableHtml}</div>
      <div class="bt-note" aria-live="polite"></div>
      <div class="bt-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Run all</button>
        <span class="bt-count"></span>
      </div>`;

    function draw() {
      const steps = spec.modes[mode].steps;
      const st = step === 0 ? {} : steps[step - 1];
      root.querySelectorAll(".bt-hl, .bt-now, .bt-hit").forEach((el) => el.classList.remove("bt-hl", "bt-now", "bt-hit"));
      (st.nodes || []).forEach((id) => root.querySelector(`[data-node="${id}"]`).classList.add("bt-hl"));
      (st.now || []).forEach((id) => root.querySelector(`[data-node="${id}"]`).classList.add("bt-now"));
      (st.entries || []).forEach((e) => root.querySelector(`[data-ent="${e}"]`).classList.add("bt-hit"));
      (st.rows || []).forEach((r) => root.querySelector(`[data-row="${r}"]`).classList.add("bt-hit"));
      (st.pages || []).forEach((p) => root.querySelector(`[data-page="${p}"]`).classList.add("bt-hl"));
      if (st.nowPage !== undefined) root.querySelector(`[data-page="${st.nowPage}"]`).classList.add("bt-now");
      root.querySelector(".bt-note").innerHTML = step === 0
        ? "Press <b>Step</b> (or the → key) to follow the database one page read at a time."
        : st.note;
      root.querySelector(".bt-count").textContent = `step ${step}/${steps.length} · page reads: ${st.reads || 0}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length;
      root.querySelector('[data-act="all"]').disabled = step === steps.length;
      root.querySelectorAll(".bt-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = spec.modes[mode].steps.length;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = n;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.modes[mode].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
