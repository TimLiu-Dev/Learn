/* CS Fundamentals: step-through cache-pattern diagram (L41).
 * Four lanes, arrows that accumulate like a sequence diagram, plus the cache contents,
 * the database contents and counters after each step.
 *
 *   <div class="cacheflow" data-src="cf-1"></div>
 *   <script type="application/json" id="cf-1">{ ...spec... }</script>
 *   <script src="../assets/cacheflow.js"></script>
 *
 * Spec:
 *   { "lanes": ["Client", "App", "Cache", "Database"],      // exactly four columns
 *     "tabs": [
 *       { "name": "cache-aside",
 *         "steps": [
 *           { "from": 1, "to": 2, "text": "get 1", "kind": "miss",
 *             "cache": { "1": "Dune" },                       // optional: full cache state after this step
 *             "db": { "1": "Dune" },                          // optional: full database state after this step
 *             "counts": { "db reads": 1, "hits": 0 },         // optional: counters after this step
 *             "note": "HTML explanation" } ] } ] }
 * kind: req | res | hit | miss | write | async (dashed) | lost (dashed red) | burst (thick).
 * A step without cache / db / counts keeps the previous value.
 */
(function () {
  const css = `
  .cacheflow { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .cacheflow:focus-visible { border-color: var(--accent); }
  .cf-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .cf-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .cf-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .cf-lanes, .cf-row { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); }
  .cf-lanes div { text-align: center; font-family: var(--mono); font-size: .68rem; font-weight: 600; letter-spacing: .04em; text-transform: uppercase; color: var(--ink-soft); padding-bottom: .3rem; border-bottom: 1px solid var(--rule); overflow-wrap: anywhere; }
  .cf-seq { position: relative; min-height: 3rem; padding: .3rem 0 .5rem;
    background:
      linear-gradient(to right, transparent calc(12.5% - 1px), var(--rule) calc(12.5% - 1px), var(--rule) calc(12.5% + 1px), transparent calc(12.5% + 1px)),
      linear-gradient(to right, transparent calc(37.5% - 1px), var(--rule) calc(37.5% - 1px), var(--rule) calc(37.5% + 1px), transparent calc(37.5% + 1px)),
      linear-gradient(to right, transparent calc(62.5% - 1px), var(--rule) calc(62.5% - 1px), var(--rule) calc(62.5% + 1px), transparent calc(62.5% + 1px)),
      linear-gradient(to right, transparent calc(87.5% - 1px), var(--rule) calc(87.5% - 1px), var(--rule) calc(87.5% + 1px), transparent calc(87.5% + 1px)); }
  .cf-row { margin: .15rem 0; }
  .cf-cell { position: relative; padding: 0 0 .55rem; min-width: 0; }
  .cf-lbl { font-family: var(--mono); font-size: .66rem; line-height: 1.25; text-align: center; padding: 0 .15rem .15rem; overflow-wrap: anywhere; position: relative; z-index: 1; }
  .cf-lbl span { background: var(--bg); padding: 0 .2rem; border-radius: 3px; }
  .cf-line { position: absolute; bottom: .3rem; height: 0; border-top: 2px solid currentColor; }
  .cf-line::after { content: ""; position: absolute; top: -6px; border: 5px solid transparent; }
  .cf-r .cf-line::after { right: -2px; border-left: 7px solid currentColor; border-right: 0; }
  .cf-l .cf-line::after { left: -2px; border-right: 7px solid currentColor; border-left: 0; }
  .cf-k-req   { color: var(--ink); }
  .cf-k-res   { color: var(--ok); }
  .cf-k-hit   { color: var(--java); }
  .cf-k-miss  { color: var(--accent); }
  .cf-k-write { color: var(--kotlin); }
  .cf-k-async { color: var(--kotlin); }
  .cf-k-async .cf-line { border-top-style: dashed; }
  .cf-k-lost  { color: var(--accent); }
  .cf-k-lost .cf-line { border-top-style: dashed; }
  .cf-k-burst { color: var(--accent); }
  .cf-k-burst .cf-line { border-top-width: 5px; }
  .cf-row.cf-old { opacity: .45; }
  .cf-row.cf-now .cf-lbl span { outline: 2px solid currentColor; outline-offset: 1px; }
  .cf-state { display: grid; grid-template-columns: repeat(auto-fit, minmax(9rem, 1fr)); gap: .5rem; margin-top: .5rem; }
  .cf-box { border: 1px dashed var(--rule); border-radius: 6px; padding: .35rem .55rem; font-family: var(--mono); font-size: .72rem; min-width: 0; }
  .cf-box b { font-family: var(--serif); font-size: .8rem; display: block; margin-bottom: .1rem; }
  .cf-box div { overflow-wrap: anywhere; }
  .cf-box .cf-empty { color: var(--ink-soft); font-style: italic; }
  .cf-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .cf-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .cf-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .cf-ctl button:disabled { opacity: .4; cursor: default; }
  .cf-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .cf-ctl, .cf-tabs { display: none; } .cf-row.cf-old { opacity: 1; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".cacheflow").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let tab = 0;
    let step = 0;   // number of steps shown
    root.tabIndex = 0;

    root.innerHTML = `
      <div class="cf-tabs" role="tablist">${spec.tabs.map((t, k) =>
        `<button role="tab" data-tab="${k}">${esc(t.name)}</button>`).join("")}</div>
      <div class="cf-lanes">${spec.lanes.map((l) => `<div>${esc(l)}</div>`).join("")}</div>
      <div class="cf-seq"></div>
      <div class="cf-state"></div>
      <div class="cf-note" aria-live="polite"></div>
      <div class="cf-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Show all</button>
        <span class="cf-count"></span>
      </div>`;
    const seq = root.querySelector(".cf-seq");

    function row(st, cls) {
      const a = Math.min(st.from, st.to), b = Math.max(st.from, st.to);
      const span = b - a + 1;
      const inset = 50 / span;   // lane centres sit half a column in from each edge of the cell
      const dir = st.to > st.from ? "cf-r" : "cf-l";
      return `<div class="cf-row ${cls} cf-k-${st.kind || "req"} ${dir}">
        <div class="cf-cell" style="grid-column: ${a + 1} / ${b + 2}">
          <div class="cf-lbl"><span>${esc(st.text)}</span></div>
          <div class="cf-line" style="left: ${inset}%; right: ${inset}%"></div>
        </div></div>`;
    }

    function latest(steps, n, field) {
      let state = null;
      for (let k = 0; k < n; k++) if (steps[k][field]) state = steps[k][field];
      return state;
    }

    function box(title, obj, sep) {
      const keys = obj ? Object.keys(obj) : [];
      return `<div class="cf-box"><b>${esc(title)}</b>` + (keys.length
        ? keys.map((k) => `<div>${esc(k)}${sep === ":" ? ":" : " " + sep} ${esc(obj[k])}</div>`).join("")
        : `<div class="cf-empty">empty</div>`) + `</div>`;
    }

    function draw() {
      const steps = spec.tabs[tab].steps;
      seq.innerHTML = steps.slice(0, step).map((st, k) => row(st, k === step - 1 ? "cf-now" : "cf-old")).join("");
      const first = spec.tabs[tab].initial || {};
      root.querySelector(".cf-state").innerHTML =
        box("Cache", latest(steps, step, "cache") || first.cache, "→") +
        box("Database", latest(steps, step, "db") || first.db, "→") +
        box("Counters", latest(steps, step, "counts") || first.counts, ":");
      const cur = steps[step - 1];
      root.querySelector(".cf-note").innerHTML = cur ? cur.note
        : (spec.tabs[tab].intro || "Press <b>Step</b> (or the → key) to begin.");
      root.querySelector(".cf-count").textContent = `step ${step} / ${steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length;
      root.querySelector('[data-act="all"]').disabled = step === steps.length;
      root.querySelectorAll(".cf-tabs button").forEach((b, k) => b.setAttribute("aria-selected", k === tab));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.tab !== undefined) { tab = +b.dataset.tab; step = 0; }
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "next") step = Math.min(spec.tabs[tab].steps.length, step + 1);
      else if (b.dataset.act === "all") step = spec.tabs[tab].steps.length;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight") { step = Math.min(spec.tabs[tab].steps.length, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
