/* CS Fundamentals: resilience patterns, stepped through from real runs (L43).
 *
 *   <div class="resilviz" data-src="resil-1"></div>
 *   <script type="application/json" id="resil-1">{ ...spec... }</script>
 *   <script src="../assets/resilviz.js"></script>
 *
 * Spec: { "tabs": [ tab, ... ] }, one of four tab types:
 *   { "type": "breaker", "name": "…", "steps": [ { "t": 0, "outcome": "ok|error|fast", "text": "…",
 *         "state": "CLOSED|OPEN|HALF_OPEN", "note": "HTML ok" } ] }
 *   { "type": "retries", "name": "…", "bucketMs": 200,
 *     "series": [ { "label": "…", "counts": [300, 100, …], "summary": "…" } ], "steps": [ { "upTo": 3, "note": "…" } ] }
 *   { "type": "bucket", "name": "…", "capacity": 5,
 *     "steps": [ { "t": 0, "asked": 7, "ok": 5, "before": 5.0, "after": 0.0, "note": "…" } ] }
 *   { "type": "trace", "name": "…", "total": 171,
 *     "spans": [ { "label": "gateway", "depth": 0, "start": 0, "dur": 171, "self": 3 } ], "steps": [ { "focus": 0, "note": "…" } ] }
 * Every step is a full snapshot, so Back simply redraws the previous one.
 */
(function () {
  const css = `
  .resilviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; outline: none; }
  .resilviz:focus-visible { border-color: var(--accent); }
  .rsv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .7rem; }
  .rsv-tabs button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .rsv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .rsv-states { display: flex; gap: .5rem; flex-wrap: wrap; align-items: center; margin-bottom: .6rem; }
  .rsv-state { font-family: var(--mono); font-size: .74rem; padding: .3rem .7rem; border-radius: 999px; border: 1px dashed var(--rule); color: var(--ink-soft); }
  .rsv-state.on.CLOSED { border: 1px solid var(--ok); color: var(--ok); background: color-mix(in srgb, var(--ok) 12%, var(--bg)); font-weight: 600; }
  .rsv-state.on.OPEN { border: 1px solid var(--accent); color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--bg)); font-weight: 600; }
  .rsv-state.on.HALF_OPEN { border: 1px solid var(--java); color: var(--java); background: color-mix(in srgb, var(--java) 12%, var(--bg)); font-weight: 600; }
  .rsv-arrow { color: var(--ink-soft); font-size: .8rem; }
  .rsv-strip { display: flex; flex-wrap: wrap; gap: 3px; margin: .4rem 0; }
  .rsv-cell { width: 1.35rem; height: 1.35rem; border-radius: 3px; font-family: var(--mono); font-size: .6rem; display: flex; align-items: center; justify-content: center; color: var(--bg); }
  .rsv-cell.ok { background: var(--ok); } .rsv-cell.error { background: var(--accent); }
  .rsv-cell.fast { background: var(--surface); color: var(--ink-soft); border: 1px solid var(--rule); }
  .rsv-cell.cur { outline: 2px solid var(--ink); outline-offset: 1px; }
  .rsv-legend { font-family: var(--mono); font-size: .68rem; color: var(--ink-soft); }
  .rsv-row { display: flex; align-items: flex-end; gap: 2px; height: 5.5rem; border-bottom: 1px solid var(--rule); margin: .2rem 0 .1rem; }
  .rsv-bar { flex: 1; min-width: 0; background: var(--kotlin); border-radius: 2px 2px 0 0; }
  .rsv-bar.java { background: var(--java); }
  .rsv-bar.hidden { background: transparent; }
  .rsv-axis { display: flex; justify-content: space-between; font-family: var(--mono); font-size: .62rem; color: var(--ink-soft); }
  .rsv-series { font-size: .85rem; margin-top: .6rem; }
  .rsv-series b { font-family: var(--mono); font-size: .75rem; }
  .rsv-tokens { display: flex; gap: .35rem; flex-wrap: wrap; margin: .5rem 0; }
  .rsv-token { width: 2rem; height: 2rem; border-radius: 50%; border: 1px solid var(--rule); background: linear-gradient(to top, var(--ok) var(--fill), transparent var(--fill)); }
  .rsv-reqs { display: flex; gap: .25rem; flex-wrap: wrap; margin: .4rem 0; }
  .rsv-req { font-family: var(--mono); font-size: .7rem; padding: .1rem .4rem; border-radius: 4px; }
  .rsv-req.ok { color: var(--ok); background: color-mix(in srgb, var(--ok) 12%, var(--bg)); }
  .rsv-req.no { color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--bg)); text-decoration: line-through; }
  .rsv-wf { margin: .4rem 0; }
  .rsv-span { display: grid; grid-template-columns: 7.5rem 1fr 4.2rem; gap: .4rem; align-items: center; margin: .25rem 0; font-family: var(--mono); font-size: .72rem; }
  .rsv-span .lab { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
  .rsv-track { position: relative; height: 1.1rem; background: var(--surface); border-radius: 3px; }
  .rsv-fill { position: absolute; top: 0; bottom: 0; background: color-mix(in srgb, var(--kotlin) 35%, var(--bg)); border-radius: 3px; }
  .rsv-self { position: absolute; top: 0; bottom: 0; right: 0; background: var(--kotlin); border-radius: 0 3px 3px 0; }
  .rsv-span.focus .rsv-fill { background: color-mix(in srgb, var(--accent) 35%, var(--bg)); }
  .rsv-span.focus .rsv-self { background: var(--accent); }
  .rsv-span.dim { opacity: .45; }
  .rsv-note { font-size: .9rem; min-height: 2.6rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .rsv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .rsv-ctl button { font: inherit; font-size: .85rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .25rem .9rem; cursor: pointer; }
  .rsv-ctl button:disabled { opacity: .4; cursor: default; }
  .rsv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media (max-width: 30rem) { .rsv-span { grid-template-columns: 5.6rem 1fr 3.6rem; } .rsv-cell { width: 1.1rem; height: 1.1rem; } }
  @media print { .rsv-ctl, .rsv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));

  document.querySelectorAll(".resilviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    let tab = 0, step = 0;
    root.tabIndex = 0;
    root.innerHTML = `
      <div class="rsv-tabs" role="tablist">${spec.tabs.map((t, k) => `<button role="tab" data-tab="${k}">${esc(t.name)}</button>`).join("")}</div>
      <div class="rsv-body"></div>
      <div class="rsv-note" aria-live="polite"></div>
      <div class="rsv-ctl">
        <button data-act="prev">◀ Back</button><button data-act="next">Step ▶</button><button data-act="end">Jump to end</button>
        <span class="rsv-count"></span>
      </div>`;
    const body = root.querySelector(".rsv-body"), note = root.querySelector(".rsv-note");

    function breaker(t, k) {
      const cur = t.steps[k];
      const st = (s) => `<span class="rsv-state ${s}${cur.state === s ? " on" : ""}">${s}</span>`;
      const cells = t.steps.slice(0, k + 1).map((s, i) =>
        `<span class="rsv-cell ${s.outcome}${i === k ? " cur" : ""}" title="t=${s.t} ms: ${esc(s.text)}">${s.outcome === "fast" ? "–" : s.outcome === "ok" ? "✓" : "✗"}</span>`).join("");
      body.innerHTML = `<div class="rsv-states">${st("CLOSED")}<span class="rsv-arrow">→</span>${st("OPEN")}<span class="rsv-arrow">→</span>${st("HALF_OPEN")}</div>
        <div class="rsv-legend">t=${cur.t} ms · ${esc(cur.text)}</div>
        <div class="rsv-strip">${cells}</div>
        <div class="rsv-legend">✓ call succeeded · ✗ call failed · – rejected without calling (one cell every 100 ms)</div>`;
      return cur.note;
    }

    function retries(t, k) {
      const upTo = t.steps[k].upTo;
      const max = Math.max(...t.series.flatMap((s) => s.counts));
      const rows = t.series.map((s, i) => `<div class="rsv-series"><b>${esc(s.label)}</b></div>
        <div class="rsv-row">${s.counts.map((c, j) => `<div class="rsv-bar${i ? " java" : ""}${j > upTo ? " hidden" : ""}" style="height:${(100 * c / max).toFixed(1)}%" title="${j * t.bucketMs} ms: ${c} attempts"></div>`).join("")}</div>
        <div class="rsv-axis"><span>0 ms</span><span>${(s.counts.length * t.bucketMs) / 1000} s</span></div>
        ${k === t.steps.length - 1 ? `<div class="rsv-legend">${esc(s.summary)}</div>` : ""}`).join("");
      body.innerHTML = rows;
      return t.steps[k].note;
    }

    function bucket(t, k) {
      const s = t.steps[k];
      const tokens = (v) => Array.from({ length: t.capacity }, (_, i) =>
        `<span class="rsv-token" style="--fill:${(Math.max(0, Math.min(1, v - i)) * 100).toFixed(0)}%"></span>`).join("");
      const reqs = Array.from({ length: s.asked }, (_, i) => `<span class="rsv-req ${i < s.ok ? "ok" : "no"}">${i < s.ok ? "200" : "429"}</span>`).join("");
      body.innerHTML = `<div class="rsv-legend">t=${s.t} ms · before: ${s.before.toFixed(1)} tokens</div><div class="rsv-tokens">${tokens(s.before)}</div>
        <div class="rsv-legend">${s.asked} requests arrive:</div><div class="rsv-reqs">${reqs}</div>
        <div class="rsv-legend">after: ${s.after.toFixed(1)} tokens</div><div class="rsv-tokens">${tokens(s.after)}</div>`;
      return s.note;
    }

    function trace(t, k) {
      const focus = t.steps[k].focus;
      body.innerHTML = `<div class="rsv-wf">${t.spans.map((s, i) => {
        const left = (100 * s.start / t.total).toFixed(1), width = Math.max(0.8, 100 * s.dur / t.total).toFixed(1);
        const self = Math.max(4, 100 * s.self / Math.max(1, s.dur)).toFixed(1);
        return `<div class="rsv-span${i === focus ? " focus" : focus >= 0 ? " dim" : ""}">
          <span class="lab">${"&nbsp;&nbsp;".repeat(s.depth)}${esc(s.label)}</span>
          <span class="rsv-track"><span class="rsv-fill" style="left:${left}%;width:${width}%"><span class="rsv-self" style="width:${self}%"></span></span></span>
          <span>${s.dur} ms</span></div>`; }).join("")}</div>
        <div class="rsv-legend">dark part of each bar = self time (time not spent waiting for a child span)</div>`;
      return t.steps[k].note;
    }

    function draw() {
      const t = spec.tabs[tab];
      root.querySelectorAll(".rsv-tabs button").forEach((b, k) => b.setAttribute("aria-selected", k === tab));
      const n = t.steps.length;
      note.innerHTML = ({ breaker, retries, bucket, trace })[t.type](t, step) || "";
      root.querySelector(".rsv-count").textContent = `step ${step + 1} / ${n}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === n - 1;
      root.querySelector('[data-act="end"]').disabled = step === n - 1;
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button"); if (!b) return;
      const n = spec.tabs[tab].steps.length;
      if (b.dataset.tab) { tab = +b.dataset.tab; step = 0; }
      else if (b.dataset.act === "prev") step = Math.max(0, step - 1);
      else if (b.dataset.act === "next") step = Math.min(n - 1, step + 1);
      else if (b.dataset.act === "end") step = n - 1;
      draw();
    });
    root.addEventListener("keydown", (e) => {
      const n = spec.tabs[tab].steps.length;
      if (e.key === "ArrowRight") { step = Math.min(n - 1, step + 1); draw(); e.preventDefault(); }
      if (e.key === "ArrowLeft") { step = Math.max(0, step - 1); draw(); e.preventDefault(); }
    });
    draw();
  });
})();
