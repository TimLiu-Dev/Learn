/* CS Fundamentals: step-through HTTP request/response + cache diagram (a small sequence diagram).
 *
 *   <div class="httpviz" data-src="hv-1"></div>
 *   <script type="application/json" id="hv-1">{ ...spec... }</script>
 *   <script src="../assets/httpviz.js"></script>
 *
 * Spec:
 *   { "lanes": ["Client", "Cache", "Origin"],             // exactly three columns
 *     "tabs": [
 *       { "name": "/time · max-age=2",
 *         "steps": [
 *           { "from": 0, "to": 1, "text": "GET /time", "kind": "req",
 *             "t": "0.0 s",                                  // optional clock label
 *             "cache": { "/time": "21:34:57 · fresh until 2.0 s" },   // optional: full cache state after this step
 *             "note": "HTML explanation" } ] } ] }
 * kind is one of req | res | hit | cond | nm (304) | skip (not stored). Arrows accumulate as you step,
 * like a sequence diagram read top to bottom. A step without "cache" keeps the previous cache state.
 */
(function () {
  const css = `
  .httpviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .httpviz:focus-visible { border-color: var(--accent); }
  .hv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .hv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .hv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .hv-lanes, .hv-row { display: grid; grid-template-columns: 1fr 1fr 1fr; }
  .hv-lanes div { text-align: center; font-family: var(--mono); font-size: .72rem; font-weight: 600; letter-spacing: .04em; text-transform: uppercase; color: var(--ink-soft); padding-bottom: .3rem; border-bottom: 1px solid var(--rule); }
  .hv-seq { position: relative; min-height: 3rem; padding: .3rem 0 .5rem;
    background: linear-gradient(to right, transparent calc(16.666% - 1px), var(--rule) calc(16.666% - 1px), var(--rule) calc(16.666% + 1px), transparent calc(16.666% + 1px),
                                          transparent calc(50% - 1px), var(--rule) calc(50% - 1px), var(--rule) calc(50% + 1px), transparent calc(50% + 1px),
                                          transparent calc(83.333% - 1px), var(--rule) calc(83.333% - 1px), var(--rule) calc(83.333% + 1px), transparent calc(83.333% + 1px)); }
  .hv-row { margin: .15rem 0; }
  .hv-cell { position: relative; padding: 0 0 .55rem; min-width: 0; }
  .hv-lbl { font-family: var(--mono); font-size: .68rem; line-height: 1.25; text-align: center; padding: 0 .2rem .15rem; overflow-wrap: anywhere; position: relative; z-index: 1; }
  .hv-lbl span { background: var(--bg); padding: 0 .2rem; border-radius: 3px; }
  .hv-line { position: absolute; bottom: .3rem; height: 0; border-top: 2px solid currentColor; }
  .hv-line::after { content: ""; position: absolute; top: -6px; border: 5px solid transparent; }
  .hv-r .hv-line::after { right: -2px; border-left: 7px solid currentColor; border-right: 0; }
  .hv-l .hv-line::after { left: -2px; border-right: 7px solid currentColor; border-left: 0; }
  .hv-k-req  { color: var(--ink); }
  .hv-k-res  { color: var(--ok); }
  .hv-k-hit  { color: var(--java); }
  .hv-k-cond { color: var(--kotlin); }
  .hv-k-nm   { color: var(--kotlin); }
  .hv-k-skip { color: var(--accent); }
  .hv-k-skip .hv-line { border-top-style: dashed; }
  .hv-row.hv-old { opacity: .45; }
  .hv-row.hv-now .hv-lbl span { outline: 2px solid currentColor; outline-offset: 1px; }
  .hv-cache { margin-top: .5rem; border: 1px dashed var(--rule); border-radius: 6px; padding: .4rem .6rem; font-family: var(--mono); font-size: .74rem; }
  .hv-cache b { font-family: var(--serif); font-size: .82rem; }
  .hv-cache .hv-empty { color: var(--ink-soft); font-style: italic; }
  .hv-cache div { overflow-wrap: anywhere; }
  .hv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .hv-note .hv-t { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); margin-right: .4rem; }
  .hv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .hv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .hv-ctl button:disabled { opacity: .4; cursor: default; }
  .hv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .hv-ctl, .hv-tabs { display: none; } .hv-row.hv-old { opacity: 1; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".httpviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let tab = 0;
    let step = 0;   // number of steps shown
    root.tabIndex = 0;

    root.innerHTML = `
      <div class="hv-tabs" role="tablist">${spec.tabs.map((t, k) =>
        `<button role="tab" data-tab="${k}">${esc(t.name)}</button>`).join("")}</div>
      <div class="hv-lanes">${spec.lanes.map((l) => `<div>${esc(l)}</div>`).join("")}</div>
      <div class="hv-seq"></div>
      <div class="hv-cache"></div>
      <div class="hv-note" aria-live="polite"></div>
      <div class="hv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Show all</button>
        <span class="hv-count"></span>
      </div>`;
    const seq = root.querySelector(".hv-seq");

    function row(st, cls) {
      const a = Math.min(st.from, st.to), b = Math.max(st.from, st.to);
      const span = b - a + 1;
      const inset = 50 / span;   // lane centres sit half a column in from each edge of the cell
      const dir = st.to > st.from ? "hv-r" : "hv-l";
      return `<div class="hv-row ${cls} hv-k-${st.kind || "req"} ${dir}">
        <div class="hv-cell" style="grid-column: ${a + 1} / ${b + 2}">
          <div class="hv-lbl"><span>${esc(st.text)}</span></div>
          <div class="hv-line" style="left: ${inset}%; right: ${inset}%"></div>
        </div></div>`;
    }

    function cacheAt(steps, n) {
      let state = {};
      for (let k = 0; k < n; k++) if (steps[k].cache) state = steps[k].cache;
      return state;
    }

    function draw() {
      const steps = spec.tabs[tab].steps;
      seq.innerHTML = steps.slice(0, step).map((st, k) => row(st, k === step - 1 ? "hv-now" : "hv-old")).join("");
      const cache = cacheAt(steps, step);
      const keys = Object.keys(cache);
      root.querySelector(".hv-cache").innerHTML = `<b>${esc(spec.lanes[1])} contents</b>` + (keys.length
        ? keys.map((k) => `<div>${esc(k)} → ${esc(cache[k])}</div>`).join("")
        : `<div class="hv-empty">empty</div>`);
      const cur = steps[step - 1];
      root.querySelector(".hv-note").innerHTML = cur
        ? (cur.t ? `<span class="hv-t">t = ${esc(cur.t)}</span>` : "") + cur.note
        : "Press <b>Step</b> (or the → key) to send the first request.";
      root.querySelector(".hv-count").textContent = `step ${step} / ${steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length;
      root.querySelector('[data-act="all"]').disabled = step === steps.length;
      root.querySelectorAll(".hv-tabs button").forEach((b, k) => b.setAttribute("aria-selected", k === tab));
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
