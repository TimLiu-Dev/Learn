/* CS Fundamentals: step-through sorting visualizer (bars).
 *
 *   <div class="sortviz" data-src="sv-1"></div>
 *   <script type="application/json" id="sv-1">{ ...spec... }</script>
 *   <script src="../assets/sortviz.js"></script>
 *
 * Spec (generate it from an instrumented run, e.g. .build/l18/trace/SortTrace.java):
 *   { "algos": [ { "name": "Insertion",
 *       "steps": [ { "a": [5,2,8], "m": {"1": "cmp"}, "r": [0, 2], "c": 3, "n": "note (HTML)" }, ... ] } ] }
 *   a = the array after this step; m = index -> mark (cmp | write | pivot | key);
 *   r = active range [lo, hi] (bars outside it are dimmed) or null; c = compares so far; n = note.
 */
(function () {
  const css = `
  .sortviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 46rem; outline: none; }
  .sortviz:focus-visible { border-color: var(--accent); }
  .sv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .7rem; }
  .sv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .sv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .sv-bars { display: flex; align-items: flex-end; gap: 4%; height: 9rem; padding: 0 .2rem; border-bottom: 1px solid var(--rule); }
  .sv-col { flex: 1 1 0; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; height: 100%; min-width: 0; transition: opacity .15s; }
  .sv-bar { width: 100%; border-radius: 4px 4px 0 0; background: color-mix(in srgb, var(--ink-soft) 30%, var(--bg)); border: 2px solid transparent; box-sizing: border-box; transition: height .15s, background .15s; }
  .sv-val { font-family: var(--mono); font-size: .78rem; margin-bottom: .15rem; }
  .sv-idx { display: flex; gap: 4%; padding: .15rem .2rem 0; }
  .sv-idx span { flex: 1 1 0; text-align: center; font-family: var(--mono); font-size: .62rem; color: var(--ink-soft); min-width: 0; }
  .sv-col.out { opacity: .3; }
  .sv-col.cmp .sv-bar { border-color: var(--accent); background: color-mix(in srgb, var(--accent) 18%, var(--bg)); }
  .sv-col.write .sv-bar { background: var(--kotlin); }
  .sv-col.pivot .sv-bar { background: var(--java); }
  .sv-col.key .sv-bar { border-color: var(--kotlin); border-style: dashed; background: color-mix(in srgb, var(--kotlin) 15%, var(--bg)); }
  .sv-legend { display: flex; gap: .9rem; flex-wrap: wrap; font-size: .72rem; color: var(--ink-soft); margin: .45rem 0 0; }
  .sv-legend i { display: inline-block; width: .7rem; height: .7rem; border-radius: 2px; margin-right: .3rem; vertical-align: -1px; border: 2px solid transparent; box-sizing: border-box; }
  .sv-legend .l-cmp { border-color: var(--accent); }
  .sv-legend .l-write { background: var(--kotlin); }
  .sv-legend .l-pivot { background: var(--java); }
  .sv-legend .l-key { border-color: var(--kotlin); border-style: dashed; }
  .sv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .sv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .sv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .sv-ctl button:disabled { opacity: .4; cursor: default; }
  .sv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .sv-ctl, .sv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  document.querySelectorAll(".sortviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let algo = 0;
    let step = 0;
    root.tabIndex = 0;
    const n = spec.algos[0].steps[0].a.length;
    const max = Math.max(...spec.algos.flatMap((al) => al.steps[0].a));

    root.innerHTML = `
      <div class="sv-tabs" role="tablist">${spec.algos.map((al, k) =>
        `<button role="tab" data-algo="${k}">${al.name}</button>`).join("")}</div>
      <div class="sv-bars">${Array.from({ length: n }, () =>
        `<div class="sv-col"><span class="sv-val"></span><div class="sv-bar"></div></div>`).join("")}</div>
      <div class="sv-idx">${Array.from({ length: n }, (_, i) => `<span>${i}</span>`).join("")}</div>
      <div class="sv-legend"><span><i class="l-cmp"></i>compare</span><span><i class="l-write"></i>write / swap</span><span><i class="l-pivot"></i>pivot</span><span><i class="l-key"></i>key</span><span>faded = outside the active range</span></div>
      <div class="sv-note" aria-live="polite"></div>
      <div class="sv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="end">Jump to end</button>
        <button data-act="reset">Reset</button>
        <span class="sv-count"></span>
      </div>`;

    const cols = root.querySelectorAll(".sv-col");

    function draw() {
      const steps = spec.algos[algo].steps;
      const s = steps[step];
      cols.forEach((col, i) => {
        const v = s.a[i];
        col.className = "sv-col" + (s.m[i] ? " " + s.m[i] : "") + (s.r && (i < s.r[0] || i > s.r[1]) ? " out" : "");
        col.querySelector(".sv-bar").style.height = (12 + 80 * v / max) + "%";
        col.querySelector(".sv-val").textContent = v;
      });
      root.querySelector(".sv-note").innerHTML = s.n;
      root.querySelector(".sv-count").textContent = `step ${step + 1} / ${steps.length} · compares: ${s.c}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length - 1;
      root.querySelectorAll(".sv-tabs button").forEach((b, k) => b.setAttribute("aria-selected", k === algo));
    }

    function go(d) {
      step = Math.max(0, Math.min(spec.algos[algo].steps.length - 1, step + d));
      draw();
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.algo !== undefined) { algo = +b.dataset.algo; step = 0; draw(); return; }
      const act = b.dataset.act;
      if (act === "prev") go(-1);
      if (act === "next") go(1);
      if (act === "end") { step = spec.algos[algo].steps.length - 1; draw(); }
      if (act === "reset") { step = 0; draw(); }
    });
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") { go(1); e.preventDefault(); }
      if (e.key === "ArrowLeft") { go(-1); e.preventDefault(); }
    });
    draw();
  });
})();
