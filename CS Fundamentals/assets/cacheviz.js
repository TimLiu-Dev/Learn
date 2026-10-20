/* CS Fundamentals: memory-hierarchy widgets.
 *
 * 1. Latency ladder (log-scale bars, real time vs "human" time):
 *   <div class="cacheviz" data-mode="ladder" data-src="cv-ladder"></div>
 *   <script type="application/json" id="cv-ladder">
 *     { "anchor": 0,                       // row index that becomes "1 second" in human mode
 *       "anchorText": "an L1 cache fetch",  // optional wording for the tab ("If … took 1 second")
 *       "rows": [ { "label": "L1 cache reference", "ns": 0.5, "kind": "book" },     // kind: book | mine
 *                 ... ] }
 *   </script>
 *
 * 2. Cache stepper (a toy cache: row-major vs column-major over a small int grid):
 *   <div class="cacheviz" data-mode="grid" data-rows="8" data-cols="8" data-line="4" data-lines="4"></div>
 *   data-line = ints per cache line, data-lines = how many lines the cache holds (LRU).
 *
 *   <script src="../assets/cacheviz.js"></script>
 */
(function () {
  const css = `
  .cacheviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .cacheviz:focus-visible { border-color: var(--accent); }
  .cv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .7rem; }
  .cv-tabs button, .cv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .cv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .cv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; margin-top: .6rem; }
  .cv-ctl button:disabled { opacity: .4; cursor: default; }
  .cv-count { font-family: var(--mono); font-size: .74rem; color: var(--ink-soft); }
  .cv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 0; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  /* ladder */
  .cv-row { display: grid; grid-template-columns: minmax(0, 11rem) minmax(0, 1fr) 6.2rem; gap: .5rem; align-items: center; padding: .22rem 0; border-bottom: 1px dashed var(--rule); font-size: .84rem; }
  .cv-row .cv-lbl { line-height: 1.2; }
  .cv-row .cv-val { font-family: var(--mono); font-size: .74rem; text-align: right; white-space: nowrap; }
  .cv-bar { height: .7rem; border-radius: 3px; background: color-mix(in srgb, var(--ink-soft) 45%, var(--bg)); min-width: 2px; }
  .cv-mine .cv-bar { background: var(--kotlin); }
  .cv-mine .cv-lbl::after { content: " · measured here"; color: var(--kotlin); font-size: .72rem; }
  .cv-legend { font-size: .74rem; color: var(--ink-soft); margin-top: .5rem; }
  @media (max-width: 34rem) { .cv-row { grid-template-columns: minmax(0, 1fr) 5.6rem; } .cv-row .cv-bar { grid-column: 1 / -1; grid-row: 2; } }
  /* grid */
  .cv-gridwrap { display: flex; gap: 1rem; flex-wrap: wrap; align-items: flex-start; }
  .cv-grid { border-collapse: separate; border-spacing: 2px; font-family: var(--mono); font-size: .68rem; }
  .cv-grid td { width: 1.7rem; height: 1.7rem; text-align: center; border-radius: 3px; background: var(--surface); color: var(--ink-soft); }
  .cv-grid td.cv-incache { box-shadow: inset 0 0 0 2px var(--kotlin); }
  .cv-grid td.cv-hit { background: color-mix(in srgb, var(--ok) 28%, var(--bg)); color: var(--ink); }
  .cv-grid td.cv-miss { background: color-mix(in srgb, var(--accent) 28%, var(--bg)); color: var(--ink); }
  .cv-grid td.cv-now { outline: 2px solid var(--ink); outline-offset: 1px; }
  .cv-cache { font-family: var(--mono); font-size: .74rem; min-width: 10rem; }
  .cv-cache div { padding: .15rem .4rem; border: 1px solid var(--rule); border-radius: 4px; margin-bottom: .25rem; }
  .cv-cache .cv-cap { font-family: var(--serif); font-size: .78rem; color: var(--ink-soft); border: 0; padding: 0; }
  @media print { .cv-ctl, .cv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  function fmtNs(ns) {
    if (ns < 1000) return (ns < 10 ? ns.toFixed(ns < 1 ? 2 : 1) : Math.round(ns)) + " ns";
    if (ns < 1e6) return +(ns / 1e3).toPrecision(3) + " µs";
    if (ns < 1e9) return +(ns / 1e6).toPrecision(3) + " ms";
    return +(ns / 1e9).toPrecision(3) + " s";
  }
  function fmtHuman(sec) {
    const units = [["years", 31557600], ["days", 86400], ["hours", 3600], ["min", 60]];
    for (const [u, s] of units) if (sec >= s) return +(sec / s).toPrecision(2) + " " + u;
    return +sec.toPrecision(2) + " s";
  }

  function ladder(root) {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const rows = spec.rows;
    const anchorNs = rows[spec.anchor || 0].ns;
    const lo = Math.log10(Math.min(...rows.map((r) => r.ns)));
    const hi = Math.log10(Math.max(...rows.map((r) => r.ns)));
    let human = false;
    root.innerHTML = `
      <div class="cv-tabs" role="tablist">
        <button role="tab" data-h="0">Real time</button>
        <button role="tab" data-h="1">If ${esc(spec.anchorText || rows[spec.anchor || 0].label)} took 1 second</button>
      </div>
      <div class="cv-rows">${rows.map((r) => `
        <div class="cv-row${r.kind === "mine" ? " cv-mine" : ""}">
          <span class="cv-lbl">${esc(r.label)}</span>
          <div class="cv-bar" style="width:${(4 + 96 * (Math.log10(r.ns) - lo) / (hi - lo)).toFixed(1)}%"></div>
          <span class="cv-val"></span>
        </div>`).join("")}</div>
      <div class="cv-legend">Bars use a log scale: each step to the right is about 10× slower.</div>`;
    function draw() {
      root.querySelectorAll(".cv-val").forEach((el, i) => {
        el.textContent = human ? fmtHuman(rows[i].ns / anchorNs) : fmtNs(rows[i].ns);
      });
      root.querySelectorAll(".cv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String((b.dataset.h === "1") === human)));
    }
    root.addEventListener("click", (e) => {
      const b = e.target.closest("button[data-h]");
      if (b) { human = b.dataset.h === "1"; draw(); }
    });
    draw();
  }

  function grid(root) {
    const R = +root.dataset.rows || 8, C = +root.dataset.cols || 8;
    const LINE = +root.dataset.line || 4, CAP = +root.dataset.lines || 4;
    const orders = [
      { name: "Row-major: for r { for c }", seq: [] },
      { name: "Column-major: for c { for r }", seq: [] },
    ];
    for (let r = 0; r < R; r++) for (let c = 0; c < C; c++) orders[0].seq.push([r, c]);
    for (let c = 0; c < C; c++) for (let r = 0; r < R; r++) orders[1].seq.push([r, c]);
    let mode = 0, step = 0;
    root.tabIndex = 0;
    const lineOf = (r, c) => `r${r}·[${Math.floor(c / LINE) * LINE}–${Math.floor(c / LINE) * LINE + LINE - 1}]`;

    root.innerHTML = `
      <div class="cv-tabs" role="tablist">${orders.map((o, k) =>
        `<button role="tab" data-mode="${k}">${esc(o.name)}</button>`).join("")}</div>
      <div class="cv-gridwrap">
        <table class="cv-grid">${Array.from({ length: R }, (_, r) =>
          `<tr>${Array.from({ length: C }, (_, c) => `<td data-r="${r}" data-c="${c}">${r},${c}</td>`).join("")}</tr>`).join("")}</table>
        <div class="cv-cache"></div>
      </div>
      <div class="cv-note" aria-live="polite"></div>
      <div class="cv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="row">Next row/column</button>
        <button data-act="all">Run all</button>
        <span class="cv-count"></span>
      </div>`;

    function simulate(n) {           // replay the first n accesses through an LRU cache
      const cache = [];              // most recently used last
      const result = [];
      for (let k = 0; k < n; k++) {
        const [r, c] = orders[mode].seq[k];
        const id = lineOf(r, c);
        const at = cache.indexOf(id);
        const hit = at >= 0;
        if (hit) cache.splice(at, 1);
        else if (cache.length === CAP) cache.shift();
        cache.push(id);
        result.push({ r, c, hit });
      }
      return { cache, result };
    }

    function draw() {
      const total = orders[mode].seq.length;
      const { cache, result } = simulate(step);
      root.querySelectorAll(".cv-grid td").forEach((td) => { td.className = ""; });
      let hits = 0, misses = 0;
      result.forEach((a, k) => {
        const td = root.querySelector(`td[data-r="${a.r}"][data-c="${a.c}"]`);
        td.classList.add(a.hit ? "cv-hit" : "cv-miss");
        if (k === result.length - 1) td.classList.add("cv-now");
        a.hit ? hits++ : misses++;
      });
      root.querySelectorAll(".cv-grid td").forEach((td) => {
        if (cache.includes(lineOf(+td.dataset.r, +td.dataset.c))) td.classList.add("cv-incache");
      });
      root.querySelector(".cv-cache").innerHTML =
        `<div class="cv-cap">Cache: ${CAP} lines × ${LINE} ints (LRU)</div>` +
        Array.from({ length: CAP }, (_, i) => `<div>${cache[i] ? esc(cache[i]) : "—"}</div>`).join("");
      let note;
      if (step === 0) note = "Press <b>Step</b>. Each access loads a whole line of " + LINE + " neighbouring ints into the cache.";
      else {
        const a = result[result.length - 1];
        note = a.hit
          ? `Read [${a.r}][${a.c}]: <b>hit</b>. Its line was already loaded by an earlier miss, so this read is nearly free.`
          : `Read [${a.r}][${a.c}]: <b>miss</b>. Load line ${esc(lineOf(a.r, a.c))} from memory` +
            (step > CAP ? ", evicting the least recently used line." : ".");
        if (step === total) note += ` Done: <b>${misses} misses</b> out of ${total} reads.`;
      }
      root.querySelector(".cv-note").innerHTML = note;
      root.querySelector(".cv-count").textContent = `read ${step}/${total} · hits ${hits} · misses ${misses}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      ["next", "row", "all"].forEach((a) => { root.querySelector(`[data-act="${a}"]`).disabled = step === total; });
      root.querySelectorAll(".cv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const total = orders[mode].seq.length;
      const inner = mode === 0 ? C : R;
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(total, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "row") step = Math.min(total, (Math.floor(step / inner) + 1) * inner);
      else if (b.dataset.act === "all") step = total;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const total = orders[mode].seq.length;
      if (e.key === "ArrowRight") { step = Math.min(total, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  }

  document.querySelectorAll(".cacheviz").forEach((root) => {
    if (root.dataset.mode === "ladder") ladder(root);
    else grid(root);
  });
})();
