/* CS Fundamentals: a Raft cluster, stepped through from real simulator runs (L57).
 *
 *   <div class="raftviz" data-src="raft-1"></div>
 *   <script type="application/json" id="raft-1">{ ...spec... }</script>
 *   <script src="../assets/raftviz.js"></script>
 *
 * Spec: { "healthy": [frame, ...], "split": [frame, ...] }, two runs of the same seed,
 * without and with a network partition. One frame per simulator event:
 *   { "t": 431, "note": "n5 commits up to index 1 (x=1), stored on 3 of 5",
 *     "from": 2, "to": 4,                 (optional: the message behind this event)
 *     "side": [0, 4],                     (optional: one side of the current partition)
 *     "nodes": [ { "role": "LEADER|CANDIDATE|FOLLOWER", "term": 1, "up": true, "commit": 1,
 *                  "log": [[1, "x=1"], ...] }, ... ] }
 * Every frame is a full snapshot, so Back simply redraws the previous one.
 */
(function () {
  const css = `
  .raftviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .raftviz:focus-visible { border-color: var(--accent); }
  .rft-bar { display: flex; flex-wrap: wrap; gap: .4rem .6rem; align-items: center; margin-bottom: .6rem; font-size: .82rem; }
  .rft-bar button { font: inherit; font-size: .8rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .75rem; cursor: pointer; }
  .rft-bar button:disabled { opacity: .45; cursor: default; }
  .rft-bar label { display: inline-flex; gap: .35rem; align-items: center; cursor: pointer; }
  .rft-pos { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); margin-left: auto; }
  .rft-body { display: flex; flex-wrap: wrap; gap: .8rem; align-items: flex-start; }
  .rft-svg { flex: 0 1 15rem; min-width: 12rem; max-width: 100%; }
  .rft-svg svg { width: 100%; height: auto; display: block; }
  .rft-logs { flex: 1 1 16rem; min-width: 0; font-size: .78rem; }
  .rft-row { display: flex; align-items: center; gap: .35rem; padding: .18rem 0; border-bottom: 1px solid var(--rule); }
  .rft-row:last-child { border-bottom: 0; }
  .rft-who { font-family: var(--mono); font-size: .72rem; width: 6.2rem; flex: none; white-space: nowrap; color: var(--ink-soft); }
  .rft-who b { color: var(--ink); }
  .rft-row.down .rft-who { text-decoration: line-through; }
  .rft-cells { display: flex; flex-wrap: wrap; gap: 3px; }
  .rft-cell { font-family: var(--mono); font-size: .66rem; padding: .05rem .3rem; border-radius: 3px; border: 1.5px solid var(--c); color: var(--ink); background: var(--bg); }
  .rft-cell.done { background: color-mix(in srgb, var(--c) 22%, var(--bg)); }
  .rft-cell sup { font-size: .55rem; color: var(--ink-soft); margin-left: .1rem; }
  .rft-empty { font-family: var(--mono); font-size: .66rem; color: var(--ink-soft); }
  .rft-note { font-size: .9rem; min-height: 2.6rem; margin-top: .6rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .rft-note b { font-family: var(--mono); font-size: .75rem; color: var(--ink-soft); font-weight: 400; margin-right: .4rem; }
  .rft-legend { font-size: .7rem; color: var(--ink-soft); margin-top: .4rem; }
  `;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const NS = "http://www.w3.org/2000/svg";
  const TERM_COLOURS = ["var(--kotlin)", "var(--java)", "var(--accent)", "var(--ok)"];
  const termColour = t => TERM_COLOURS[(t - 1) % TERM_COLOURS.length];

  function el(tag, attrs, parent) {
    const e = document.createElementNS(NS, tag);
    for (const k in attrs) e.setAttribute(k, attrs[k]);
    if (parent) parent.appendChild(e);
    return e;
  }

  function esc(s) { return String(s).replace(/[&<>]/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c])); }

  function init(box) {
    const spec = JSON.parse(document.getElementById(box.dataset.src).textContent);
    const n = spec.healthy[0].nodes.length;
    const pos = [...Array(n)].map((_, i) => {
      const a = -Math.PI / 2 + (2 * Math.PI * i) / n;
      return [120 + 82 * Math.cos(a), 104 + 82 * Math.sin(a)];
    });
    let run = "healthy", i = 0, timer = null;

    box.tabIndex = 0;
    box.innerHTML = `
      <div class="rft-bar">
        <button data-act="back">◀ Back</button><button data-act="next">Next ▶</button><button data-act="play">Play</button>
        <label><input type="checkbox" data-act="split"> partition at 700 ms</label>
        <span class="rft-pos"></span>
      </div>
      <div class="rft-body"><div class="rft-svg"></div><div class="rft-logs"></div></div>
      <div class="rft-note" aria-live="polite"></div>
      <div class="rft-legend">Filled cells are committed. A cell's colour is the term it was written in. Red dashed links are cut.</div>`;
    const svg = el("svg", { viewBox: "0 0 240 210", role: "img", "aria-label": "Raft cluster" }, box.querySelector(".rft-svg"));
    const logs = box.querySelector(".rft-logs"), note = box.querySelector(".rft-note"), posLabel = box.querySelector(".rft-pos");
    const back = box.querySelector('[data-act="back"]'), next = box.querySelector('[data-act="next"]');
    const play = box.querySelector('[data-act="play"]'), split = box.querySelector('[data-act="split"]');
    el("defs", {}, svg).innerHTML =
      '<marker id="rft-arrow-' + box.dataset.src + '" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="var(--kotlin)"/></marker>';

    function draw() {
      const frames = spec[run], f = frames[i];
      for (const old of svg.querySelectorAll(".rft-g")) old.remove();
      const g = el("g", { class: "rft-g" }, svg);
      const side = f.side ? new Set(f.side) : null;
      for (let a = 0; a < n; a++) for (let b = a + 1; b < n; b++) {
        const isCut = side && side.has(a) !== side.has(b);
        el("line", { x1: pos[a][0], y1: pos[a][1], x2: pos[b][0], y2: pos[b][1],
          stroke: isCut ? "var(--accent)" : "var(--rule)", "stroke-width": isCut ? 1.2 : 1,
          "stroke-dasharray": isCut ? "4 3" : "none", opacity: isCut ? .8 : 1 }, g);
      }
      if (f.from != null && f.to != null) {
        const [x1, y1] = pos[f.from], [x2, y2] = pos[f.to];
        const d = Math.hypot(x2 - x1, y2 - y1), ux = (x2 - x1) / d, uy = (y2 - y1) / d;
        el("line", { x1: x1 + ux * 21, y1: y1 + uy * 21, x2: x2 - ux * 22, y2: y2 - uy * 22, stroke: "var(--kotlin)",
          "stroke-width": 2.2, "marker-end": "url(#rft-arrow-" + box.dataset.src + ")" }, g);
      }
      f.nodes.forEach((nd, k) => {
        const [x, y] = pos[k];
        const fill = !nd.up ? "var(--bg)" : nd.role === "LEADER" ? "var(--ok)" : nd.role === "CANDIDATE" ? "var(--java)" : "var(--surface)";
        const ink = nd.up && nd.role !== "FOLLOWER" ? "var(--bg)" : "var(--ink)";
        el("circle", { cx: x, cy: y, r: 19, fill, stroke: nd.up ? "var(--ink-soft)" : "var(--rule)", "stroke-width": 1.3,
          "stroke-dasharray": nd.up ? "none" : "3 3" }, g);
        const t1 = el("text", { x, y: y - 2, "text-anchor": "middle", "font-size": 11, "font-weight": 600, fill: ink, "font-family": "var(--mono)" }, g);
        t1.textContent = "n" + (k + 1);
        const t2 = el("text", { x, y: y + 10, "text-anchor": "middle", "font-size": 8, fill: ink, "font-family": "var(--mono)" }, g);
        t2.textContent = nd.up ? "term " + nd.term : "down";
      });
      logs.innerHTML = f.nodes.map((nd, k) => {
        const cells = nd.log.length
          ? nd.log.map(([t, cmd], j) => `<span class="rft-cell${j < nd.commit ? " done" : ""}" style="--c:${termColour(t)}">${esc(cmd)}<sup>${t}</sup></span>`).join("")
          : '<span class="rft-empty">empty log</span>';
        const role = nd.up ? nd.role.toLowerCase() : "down";
        return `<div class="rft-row${nd.up ? "" : " down"}"><span class="rft-who"><b>n${k + 1}</b> ${role}</span><span class="rft-cells">${cells}</span></div>`;
      }).join("");
      note.innerHTML = `<b>${f.t} ms</b>${esc(f.note)}`;
      posLabel.textContent = `${i + 1} / ${frames.length}`;
      back.disabled = i === 0;
      next.disabled = i === frames.length - 1;
    }

    function stop() { if (timer) { clearInterval(timer); timer = null; play.textContent = "Play"; } }
    function step(d) { const len = spec[run].length; i = Math.max(0, Math.min(len - 1, i + d)); draw(); }
    back.onclick = () => { stop(); step(-1); };
    next.onclick = () => { stop(); step(1); };
    play.onclick = () => {
      if (timer) return stop();
      if (i === spec[run].length - 1) i = 0;
      play.textContent = "Pause";
      timer = setInterval(() => { if (i >= spec[run].length - 1) stop(); else step(1); }, 1100);
    };
    split.onchange = () => {                    // keep the same moment in time when switching runs
      const t = spec[run][i].t;
      run = split.checked ? "split" : "healthy";
      const frames = spec[run];
      let j = 0;
      while (j + 1 < frames.length && frames[j + 1].t <= t) j++;
      i = j;
      draw();
    };
    box.addEventListener("keydown", e => {
      if (e.key === "ArrowRight") { stop(); step(1); e.preventDefault(); }
      if (e.key === "ArrowLeft") { stop(); step(-1); e.preventDefault(); }
    });
    draw();
  }

  function boot() { document.querySelectorAll(".raftviz").forEach(init); }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", boot); else boot();
})();
