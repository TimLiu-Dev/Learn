/* CS Fundamentals: step through a log-structured store (L56).
 *
 *   <div class="logviz" data-src="lz-1"></div>
 *   <script type="application/json" id="lz-1">{ "modes": [ ... ] }</script>
 *   <script src="../assets/logviz.js"></script>
 *
 * Log mode   { "name": "...", "kind": "log", "steps": [ { "log": [ {"k","v","off","size","dead"} ],
 *              "index": [ [key, offset] ], "hl": offset | null, "note": "HTML ok" } ] }
 *   v == null is a tombstone; dead records are garbage; hl highlights a record and its index row.
 * LSM mode   { "name": "...", "kind": "lsm", "steps": [ { "mem": [ [key, value] ],
 *              "tables": [ { "name", "mark": "new"|"hit"|"probe"|"skip"|null, "rows": [ [key, value] ] } ], "note" } ] }
 *   Tables are listed newest first; value null is a tombstone.
 *
 * The specs are generated from the lesson's real LogKV and SsTable code (.build/l56/viz/VizTrace.java).
 */
(function () {
  const css = `
  .logviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; outline: none; }
  .logviz:focus-visible { border-color: var(--accent); }
  .lz-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .7rem; }
  .lz-tabs button, .lz-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .lz-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .lz-ctl button:disabled { opacity: .4; cursor: default; }
  .lz-label { font-family: var(--mono); font-size: .64rem; letter-spacing: .08em; color: var(--ink-soft); margin: .5rem 0 .3rem; }
  .lz-strip { display: flex; flex-wrap: wrap; gap: 4px; min-height: 3.2rem; }
  .lz-rec { font-family: var(--mono); font-size: .74rem; border: 1.5px solid var(--java); border-radius: 4px; padding: .2rem .4rem; background: var(--bg); color: var(--ink); line-height: 1.3; }
  .lz-rec small { display: block; font-size: .62rem; color: var(--ink-soft); }
  .lz-rec.lz-dead { border-color: var(--rule); color: var(--ink-soft); background: var(--surface); }
  .lz-rec.lz-dead .lz-body { text-decoration: line-through; }
  .lz-rec.lz-hl { outline: 3px solid var(--accent); outline-offset: 1px; }
  .lz-index { display: grid; grid-template-columns: max-content max-content; gap: .15rem .9rem; font-family: var(--mono); font-size: .78rem; min-height: 1.4rem; }
  .lz-index span.lz-hl { color: var(--accent); font-weight: 700; }
  .lz-box { border: 1.5px dashed var(--kotlin); border-radius: 6px; padding: .35rem .45rem; min-height: 2.1rem; display: flex; flex-wrap: wrap; gap: 4px; align-items: center; }
  .lz-kv { font-family: var(--mono); font-size: .74rem; padding: .1rem .35rem; border: 1px solid var(--rule); border-radius: 3px; background: var(--bg); color: var(--ink); }
  .lz-kv.lz-tomb { color: var(--accent); border-color: var(--accent); }
  .lz-table { border: 1.5px solid var(--java); border-radius: 6px; padding: .35rem .45rem; margin-bottom: .4rem; }
  .lz-table header { display: flex; align-items: center; gap: .5rem; font-family: var(--mono); font-size: .7rem; color: var(--ink-soft); margin-bottom: .3rem; }
  .lz-table .lz-rows { display: flex; flex-wrap: wrap; gap: 4px; }
  .lz-badge { font-size: .62rem; font-weight: 700; letter-spacing: .05em; padding: 0 .35rem; border-radius: 3px; border: 1px solid currentColor; }
  .lz-table.lz-m-hit { border-color: var(--ok); } .lz-m-hit .lz-badge { color: var(--ok); }
  .lz-table.lz-m-skip { opacity: .55; border-style: dashed; } .lz-m-skip .lz-badge { color: var(--ink-soft); }
  .lz-table.lz-m-probe { border-color: var(--accent); } .lz-m-probe .lz-badge { color: var(--accent); }
  .lz-table.lz-m-new { border-color: var(--kotlin); } .lz-m-new .lz-badge { color: var(--kotlin); }
  .lz-empty { font-size: .78rem; color: var(--ink-soft); font-style: italic; }
  .lz-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .lz-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .lz-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .lz-ctl, .lz-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const kv = ([k, v]) => v === null
    ? `<span class="lz-kv lz-tomb">${esc(k)} ✝</span>`
    : `<span class="lz-kv">${esc(k)}=${esc(v)}</span>`;
  const BADGE = { new: "NEW", hit: "FOUND", probe: "READ, MISS", skip: "BLOOM: NO" };

  function drawLog(st) {
    if (!st) return `<div class="lz-label">LOG FILE (disk, append-only)</div><div class="lz-strip"><span class="lz-empty">empty file</span></div>
      <div class="lz-label">INDEX (memory)</div><div class="lz-index"><span class="lz-empty">empty</span></div>`;
    const hlKey = (st.index.find(([, off]) => off === st.hl) || [])[0];
    const recs = st.log.map((r) => {
      const body = r.v === null ? `${esc(r.k)} ✝` : `${esc(r.k)}=${esc(r.v)}`;
      return `<div class="lz-rec${r.dead ? " lz-dead" : ""}${r.off === st.hl ? " lz-hl" : ""}"><span class="lz-body">${body}</span><small>@${r.off} · ${r.size} B</small></div>`;
    }).join("");
    const idx = st.index.length ? st.index.map(([k, off]) =>
      `<span class="${k === hlKey ? "lz-hl" : ""}">${esc(k)}</span><span class="${k === hlKey ? "lz-hl" : ""}">→ @${off}</span>`).join("")
      : `<span class="lz-empty">empty</span>`;
    return `<div class="lz-label">LOG FILE (disk, append-only)</div><div class="lz-strip">${recs}</div>
      <div class="lz-label">INDEX (memory)</div><div class="lz-index">${idx}</div>`;
  }

  function drawLsm(st) {
    const mem = st && st.mem.length ? st.mem.map(kv).join("") : `<span class="lz-empty">empty</span>`;
    const tables = st && st.tables.length ? st.tables.map((t) =>
      `<div class="lz-table${t.mark ? " lz-m-" + t.mark : ""}"><header>${esc(t.name)}${t.mark ? `<span class="lz-badge">${BADGE[t.mark]}</span>` : ""}</header>
       <div class="lz-rows">${t.rows.map(kv).join("")}</div></div>`).join("")
      : `<span class="lz-empty">none yet</span>`;
    return `<div class="lz-label">MEMTABLE (memory, sorted)</div><div class="lz-box">${mem}</div>
      <div class="lz-label">SSTABLES (disk, newest first)</div>${tables}`;
  }

  document.querySelectorAll(".logviz").forEach((root) => {
    const modes = JSON.parse(document.getElementById(root.dataset.src).textContent).modes;
    let mode = 0, step = 0, timer = null;
    root.tabIndex = 0;
    root.innerHTML = (modes.length > 1 ? `<div class="lz-tabs" role="tablist">${modes.map((m, k) =>
      `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>` : "") +
      `<div class="lz-view"></div><div class="lz-note" aria-live="polite"></div>
       <div class="lz-ctl"><button data-act="prev">◀ Back</button><button data-act="next">Step ▶</button>
       <button data-act="play">Play</button><button data-act="all">Run all</button><span class="lz-count"></span></div>`;

    function stop() { if (timer) { clearInterval(timer); timer = null; } }
    function draw() {
      const m = modes[mode], n = m.steps.length, st = step > 0 ? m.steps[step - 1] : null;
      root.querySelector(".lz-view").innerHTML = m.kind === "log" ? drawLog(st) : drawLsm(st);
      root.querySelector(".lz-note").innerHTML = st ? st.note : "Press <b>Step</b> (or the → key) to run one operation at a time, or <b>Play</b>.";
      root.querySelector(".lz-count").textContent = `step ${step}/${n}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === n;
      root.querySelector('[data-act="all"]').disabled = step === n;
      root.querySelector('[data-act="play"]').textContent = timer ? "Pause" : "Play";
      root.querySelector('[data-act="play"]').disabled = step === n && !timer;
      root.querySelectorAll(".lz-tabs button").forEach((b) => b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
    }
    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const n = modes[mode].steps.length;
      if (b.dataset.act !== "play") stop();
      if (b.dataset.mode !== undefined) { mode = Number(b.dataset.mode); step = 0; }
      else if (b.dataset.act === "next") step = Math.min(n, step + 1);
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "all") step = n;
      else if (b.dataset.act === "play") {
        if (timer) stop();
        else timer = setInterval(() => {
          if (step >= modes[mode].steps.length) { stop(); draw(); return; }
          step++; draw();
        }, 1100);
      }
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = modes[mode].steps.length;
      if (e.key === "ArrowRight") { stop(); step = Math.min(n, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { stop(); step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
