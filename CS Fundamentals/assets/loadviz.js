/* CS Fundamentals: before/after load-test dashboard (L46).
 *
 *   <div class="loadviz" data-src="lv-1"></div>
 *   <script type="application/json" id="lv-1">{ ...spec... }</script>
 *   <script src="../assets/loadviz.js"></script>
 *
 * Spec (built from a real load-test capture):
 *   { "summary": [ { "label": "p50 (ms)", "before": 3.5, "after": 0.6, "better": "lower" }, ... ],
 *     "runs": [ { "name": "before", "series": [ { "t": 0.0, "n": 140, "p95": 6.1, "r429": 0, "err": 0 }, ... ] },
 *               { "name": "after",  "series": [ ... ] } ] }
 *     "notes": { "n": "…", "p95": "…", "r429": "…" }      // optional: what this run's chart shows
 * Top: one pair of bars per summary metric. Bottom: one metric over time (0.5 s buckets),
 * both runs on the same axes; tabs pick the metric. Plain SVG and theme tokens, light and dark.
 */
(function () {
  const css = `
  .loadviz, .loadviz * { font-variant-ligatures: none; }
  .loadviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .lv-sum { display: grid; gap: .55rem; margin-bottom: 1rem; }
  .lv-metric { display: grid; grid-template-columns: minmax(5.5rem, 8rem) minmax(0, 1fr); gap: .5rem; align-items: center; }
  .lv-label { font-family: var(--mono); font-size: .74rem; color: var(--ink-soft); }
  .lv-bars { display: grid; gap: .18rem; }
  .lv-bar { display: grid; grid-template-columns: 3.4rem minmax(0, 1fr) 3.6rem; gap: .35rem; align-items: center; font-family: var(--mono); font-size: .7rem; }
  .lv-track { height: .7rem; background: var(--surface); border-radius: 3px; overflow: hidden; }
  .lv-fill { height: 100%; border-radius: 3px; }
  .lv-before .lv-fill { background: var(--java); }
  .lv-after .lv-fill { background: var(--kotlin); }
  .lv-val { text-align: right; color: var(--ink); }
  .lv-who { color: var(--ink-soft); }
  .lv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin: .2rem 0 .5rem; }
  .lv-tabs button { font: inherit; font-size: .8rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .lv-tabs button[aria-selected="true"] { border-color: var(--accent); color: var(--accent); font-weight: 600; }
  .lv-chart svg { width: 100%; height: auto; display: block; }
  .lv-chart text { font-family: var(--mono); font-size: 11px; fill: var(--ink-soft); }
  .lv-chart .lv-axis { stroke: var(--rule); stroke-width: 1; }
  .lv-chart .lv-line-before { fill: none; stroke: var(--java); stroke-width: 2; }
  .lv-chart .lv-line-after { fill: none; stroke: var(--kotlin); stroke-width: 2; }
  .lv-legend { display: flex; gap: 1rem; flex-wrap: wrap; font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); margin-top: .3rem; }
  .lv-key { display: inline-block; width: .9rem; height: .25rem; vertical-align: middle; margin-right: .3rem; border-radius: 2px; }
  .lv-note { font-size: .85rem; color: var(--ink-soft); margin: .4rem 0 0; }
  @media print { .lv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const metrics = [
    { key: "n", label: "requests per 0.5 s", note: "Traffic: requests started in each half-second." },
    { key: "p95", label: "p95 latency (ms)", note: "The slow tail of the paced clients in each half-second." },
    { key: "r429", label: "429s per 0.5 s", note: "Requests refused by the rate limiter in each half-second." },
  ];

  document.querySelectorAll(".loadviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const runs = Object.fromEntries(spec.runs.map((r) => [r.name, r.series]));

    const sum = spec.summary.map((m) => {
      const max = Math.max(m.before, m.after) || 1;
      const bar = (who, v) => `<div class="lv-bar lv-${who}"><span class="lv-who">${who}</span>
          <span class="lv-track"><span class="lv-fill" style="display:block;width:${(100 * v / max).toFixed(1)}%"></span></span>
          <span class="lv-val">${esc(v)}</span></div>`;
      return `<div class="lv-metric"><span class="lv-label">${esc(m.label)}</span>
        <div class="lv-bars">${bar("before", m.before)}${bar("after", m.after)}</div></div>`;
    }).join("");

    root.innerHTML = `<div class="lv-sum">${sum}</div>
      <div class="lv-tabs" role="tablist">${metrics.map((m, i) => `<button role="tab" data-i="${i}">${esc(m.label)}</button>`).join("")}</div>
      <div class="lv-chart"></div>
      <div class="lv-legend"><span><span class="lv-key" style="background:var(--java)"></span>before: no pool, no cache, no limit</span>
        <span><span class="lv-key" style="background:var(--kotlin)"></span>after: pool, cache, rate limit</span></div>
      <p class="lv-note" aria-live="polite"></p>`;

    const chart = root.querySelector(".lv-chart");
    const note = root.querySelector(".lv-note");
    const W = 600, H = 170, L = 44, R = 10, T = 10, B = 26;

    function draw(i) {
      const m = metrics[i];
      root.querySelectorAll(".lv-tabs button").forEach((b, k) => b.setAttribute("aria-selected", String(k === i)));
      const all = [...runs.before, ...runs.after];
      const tMax = Math.max(...all.map((p) => p.t)) || 1;
      const yMax = Math.max(1, ...all.map((p) => p[m.key]));
      const x = (t) => L + (W - L - R) * t / tMax;
      const y = (v) => T + (H - T - B) * (1 - v / yMax);
      const line = (series, cls) => `<polyline class="${cls}" points="${series.map((p) => `${x(p.t).toFixed(1)},${y(p[m.key]).toFixed(1)}`).join(" ")}"/>`;
      const ticks = [0, yMax / 2, yMax].map((v) => `<text x="${L - 6}" y="${y(v) + 4}" text-anchor="end">${Math.round(v * 10) / 10}</text>
          <line class="lv-axis" x1="${L}" x2="${W - R}" y1="${y(v)}" y2="${y(v)}" stroke-dasharray="2 4"/>`).join("");
      const xt = [0, tMax / 2, tMax].map((t) => `<text x="${x(t)}" y="${H - 8}" text-anchor="middle">${t.toFixed(1)} s</text>`).join("");
      chart.innerHTML = `<svg viewBox="0 0 ${W} ${H}" role="img" aria-label="${esc(m.label)} over time, before and after">
          ${ticks}${xt}<line class="lv-axis" x1="${L}" x2="${L}" y1="${T}" y2="${H - B}"/>
          ${line(runs.before, "lv-line-before")}${line(runs.after, "lv-line-after")}</svg>`;
      note.textContent = (spec.notes && spec.notes[m.key]) || m.note;
    }

    root.querySelectorAll(".lv-tabs button").forEach((b) => b.addEventListener("click", () => draw(Number(b.dataset.i))));
    draw(0);
  });
})();
