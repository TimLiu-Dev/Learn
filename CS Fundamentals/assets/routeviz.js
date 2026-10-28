/* CS Fundamentals: trace one HTTP request through Shelf's layers (L45).
 *
 *   <div class="routeviz" data-src="rv-1"></div>
 *   <script type="application/json" id="rv-1">{ ...spec... }</script>
 *   <script src="../assets/routeviz.js"></script>
 *
 * Spec:
 *   { "layers": ["HttpServer", "Router", ...],          // top to bottom
 *     "scenarios": [
 *       { "name": "GET hit",                              // tab label
 *         "request": "GET /books/1",                      // shown above the stack
 *         "steps": [ { "layer": 0, "note": "HTML ok", "code": "optional code line" }, ... ],
 *         "response": "HTTP/1.1 200 OK\n...\n{...}" } ] } // shown after the last step
 * Each step lights one layer and explains what happens there. Layers a request
 * never reaches stay dim, which is the point: a 404 never touches the database.
 */
(function () {
  const css = `
  .routeviz, .routeviz * { font-variant-ligatures: none; }
  .routeviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .routeviz:focus-visible { border-color: var(--accent); }
  .rv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .rv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .rv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .rv-req { font-family: var(--mono); font-size: .8rem; font-weight: 600; margin: 0 0 .5rem; padding: .3rem .6rem; border-left: 3px solid var(--java); background: var(--surface); overflow-wrap: anywhere; }
  .rv-stack { display: grid; gap: .3rem; }
  .rv-layer { display: grid; grid-template-columns: 1.6rem minmax(0, 1fr); align-items: center; gap: .5rem; padding: .35rem .6rem; border: 1px solid var(--rule); border-radius: 6px; font-family: var(--mono); font-size: .78rem; color: var(--ink-soft); background: var(--bg); transition: background .15s, border-color .15s; }
  .rv-layer .rv-n { text-align: center; font-size: .7rem; color: var(--ink-soft); }
  .rv-layer.rv-done { color: var(--ink); border-color: color-mix(in srgb, var(--kotlin) 55%, var(--rule)); box-shadow: inset 4px 0 0 color-mix(in srgb, var(--kotlin) 55%, var(--rule)); }
  .rv-layer.rv-now { color: var(--ink); border-color: var(--accent); box-shadow: inset 4px 0 0 var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--bg)); font-weight: 600; }
  .rv-layer.rv-skip { opacity: .45; border-style: dashed; }
  .rv-code { display: block; margin-top: .15rem; font-weight: 400; font-size: .72rem; color: var(--kotlin); overflow-wrap: anywhere; }
  .rv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .rv-resp { font-family: var(--mono); font-size: .74rem; white-space: pre-wrap; overflow-wrap: anywhere; margin: .4rem 0; padding: .5rem .7rem; border-radius: 5px; border-left: 3px solid var(--ok); background: color-mix(in srgb, var(--ok) 7%, var(--bg)); }
  .rv-resp[hidden] { display: none; }
  .rv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .rv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .rv-ctl button:disabled { opacity: .4; cursor: default; }
  .rv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .rv-ctl, .rv-tabs { display: none; } .rv-resp[hidden] { display: block; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".routeviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let sc = 0;
    let step = 0;   // number of steps shown
    root.tabIndex = 0;

    root.innerHTML = `
      <div class="rv-tabs" role="tablist">${spec.scenarios.map((s, k) =>
        `<button role="tab" data-sc="${k}">${esc(s.name)}</button>`).join("")}</div>
      <div class="rv-req"></div>
      <div class="rv-stack">${spec.layers.map((l, i) =>
        `<div class="rv-layer" data-layer="${i}"><span class="rv-n">${i + 1}</span><span class="rv-body">${esc(l)}</span></div>`).join("")}</div>
      <div class="rv-note" aria-live="polite"></div>
      <div class="rv-resp" hidden></div>
      <div class="rv-ctl">
        <button data-act="prev">◀ Back</button>
        <button data-act="next">Step ▶</button>
        <button data-act="all">Jump to end</button>
        <span class="rv-count"></span>
      </div>`;

    function draw() {
      const s = spec.scenarios[sc];
      const steps = s.steps;
      root.querySelector(".rv-req").textContent = s.request;
      const reached = new Set(steps.map((st) => st.layer));
      root.querySelectorAll(".rv-layer").forEach((el) => {
        const i = Number(el.dataset.layer);
        el.className = "rv-layer" + (reached.has(i) ? "" : " rv-skip");
        el.querySelector(".rv-body").innerHTML = esc(spec.layers[i]);
      });
      for (let k = 0; k < step; k++) {
        const st = steps[k];
        const el = root.querySelector(`.rv-layer[data-layer="${st.layer}"]`);
        el.classList.add(k === step - 1 ? "rv-now" : "rv-done");
        if (st.code) el.querySelector(".rv-body").innerHTML = esc(spec.layers[st.layer]) + `<span class="rv-code">${esc(st.code)}</span>`;
      }
      const note = step === 0 ? "Press Step (or →) to send the request." : steps[step - 1].note;
      root.querySelector(".rv-note").innerHTML = note;
      const resp = root.querySelector(".rv-resp");
      resp.hidden = step < steps.length;
      resp.textContent = s.response;
      root.querySelector(".rv-count").textContent = `step ${step} / ${steps.length}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === steps.length;
      root.querySelector('[data-act="all"]').disabled = step === steps.length;
      root.querySelectorAll(".rv-tabs button").forEach((b) =>
        b.setAttribute("aria-selected", String(Number(b.dataset.sc) === sc)));
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.sc !== undefined) { sc = Number(b.dataset.sc); step = 0; }
      const n = spec.scenarios[sc].steps.length;
      if (b.dataset.act === "next") step = Math.min(n, step + 1);
      if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      if (b.dataset.act === "all") step = n;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.scenarios[sc].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
