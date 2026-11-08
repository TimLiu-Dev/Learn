/* CS Fundamentals: step-through two pointers / sliding windows, backtracking board, and trie.
 *
 *   <div class="windowviz" data-src="wv-1"></div>
 *   <script type="application/json" id="wv-1">{ ...spec... }</script>
 *   <script src="../assets/windowviz.js"></script>
 *
 * Array spec (one tab per mode):
 *   { "kind": "array", "modes": [ { "name": "...", "cells": ["1", "3", ...],
 *       "steps": [ { "ptr": { "lo": 0, "hi": 6 }, "range": [l, r], "mark": [i, ...],
 *                    "rows": [ ["SUM", ["16"]], ... ], "note": "HTML ok" } ] } ] }
 *   range = the shaded window; mark = highlighted indexes; rows = extra labelled boxes.
 *
 * Board spec (N-queens backtracking):
 *   { "kind": "board", "n": 4, "steps": [ { "q": [col or -1 per row], "try": [r, c] | null,
 *       "bad": true|false, "sols": 0, "note": "..." } ] }
 *
 * Trie spec (layout is computed from the words):
 *   { "kind": "trie", "steps": [ { "words": [...], "path": "sh", "fresh": ["s", "sh"],
 *       "found": ["she", ...], "note": "..." } ] }
 *
 * All specs are generated from real runs (see .build/l50/viz/WindowTrace.java).
 */
(function () {
  const css = `
  .windowviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; outline: none; }
  .windowviz:focus-visible { border-color: var(--accent); }
  .wv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .7rem; }
  .wv-tabs button, .wv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .wv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .wv-ctl button:disabled { opacity: .4; cursor: default; }
  .wv-cells { display: flex; gap: 3px; flex-wrap: nowrap; overflow-x: auto; padding: .2rem 0 .1rem; }
  .wv-cell { flex: 0 0 auto; width: 2.5rem; text-align: center; font-family: var(--mono); }
  .wv-idx { font-size: .62rem; color: var(--ink-soft); height: 1rem; }
  .wv-box { height: 2.3rem; line-height: 2.3rem; border: 1.5px solid var(--rule); border-radius: 4px; font-size: .95rem; font-weight: 600; color: var(--ink); background: var(--bg); }
  .wv-cell.wv-in .wv-box { background: color-mix(in srgb, var(--kotlin) 16%, var(--bg)); border-color: var(--kotlin); }
  .wv-cell.wv-mark .wv-box { border-color: var(--ok); border-width: 3px; line-height: calc(2.3rem - 3px); }
  .wv-ptr { font-size: .66rem; font-weight: 600; color: var(--accent); min-height: 1.9rem; line-height: 1.05; padding-top: .15rem; }
  .wv-row { display: flex; align-items: center; gap: .35rem; flex-wrap: wrap; font-family: var(--mono); font-size: .78rem; margin: .3rem 0; min-height: 1.7rem; }
  .wv-row b { font-size: .66rem; letter-spacing: .08em; color: var(--ink-soft); min-width: 5.4rem; }
  .wv-row span { display: inline-block; padding: .1rem .4rem; border: 1px solid var(--java); border-radius: 4px; color: var(--java); }
  .wv-row em { color: var(--ink-soft); font-style: normal; font-size: .72rem; }
  .wv-board { display: grid; gap: 0; margin: .2rem auto .4rem; border: 1.5px solid var(--ink-soft); width: max-content; }
  .wv-sq { width: 2.9rem; height: 2.9rem; display: flex; align-items: center; justify-content: center; font-size: 1.6rem; color: var(--ink); position: relative; }
  .wv-sq.wv-light { background: var(--bg); }
  .wv-sq.wv-dark { background: var(--surface); }
  .wv-sq.wv-hit::after { content: ""; position: absolute; inset: 0; background: repeating-linear-gradient(45deg, transparent, transparent 5px, color-mix(in srgb, var(--accent) 22%, transparent) 5px, color-mix(in srgb, var(--accent) 22%, transparent) 7px); }
  .wv-sq.wv-try { outline: 3px solid var(--ok); outline-offset: -3px; }
  .wv-sq.wv-bad { outline: 3px solid var(--accent); outline-offset: -3px; color: var(--accent); }
  .wv-sols { font-family: var(--mono); font-size: .75rem; color: var(--ink-soft); text-align: center; }
  .wv-svg { width: 100%; height: auto; display: block; }
  .wv-svg line { stroke: var(--rule); stroke-width: 2; }
  .wv-svg line.wv-on { stroke: var(--accent); stroke-width: 3.5; }
  .wv-svg circle { fill: var(--bg); stroke: var(--ink-soft); stroke-width: 2; }
  .wv-svg circle.wv-end2 { fill: none; stroke: var(--ink-soft); stroke-width: 1.5; }
  .wv-svg g.wv-fresh circle:first-child { fill: color-mix(in srgb, var(--kotlin) 22%, var(--bg)); stroke: var(--kotlin); }
  .wv-svg g.wv-on circle:first-child { fill: color-mix(in srgb, var(--accent) 22%, var(--bg)); stroke: var(--accent); }
  .wv-svg g.wv-found circle { stroke: var(--ok); }
  .wv-svg g.wv-found circle:first-child { fill: color-mix(in srgb, var(--ok) 25%, var(--bg)); }
  .wv-svg text { font-family: var(--mono); font-size: 14px; font-weight: 600; fill: var(--ink); text-anchor: middle; dominant-baseline: central; }
  .wv-note { font-size: .9rem; min-height: 2.4rem; margin: .6rem 0 .4rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .wv-ctl { display: flex; align-items: center; gap: .5rem; flex-wrap: wrap; }
  .wv-count { font-family: var(--mono); font-size: .72rem; color: var(--ink-soft); }
  @media print { .wv-ctl, .wv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;" }[c]));
  const NS = "http://www.w3.org/2000/svg";

  function arrayView(root) {
    return {
      body: `<div class="wv-cells"></div><div class="wv-rows"></div>`,
      draw(mode, st) {
        const ptrAt = {};
        if (st) for (const [name, i] of Object.entries(st.ptr)) (ptrAt[i] = ptrAt[i] || []).push(name);
        const mark = new Set(st ? st.mark : []);
        root.querySelector(".wv-cells").innerHTML = mode.cells.map((v, i) => {
          const inR = st && i >= st.range[0] && i <= st.range[1];
          return `<div class="wv-cell${inR ? " wv-in" : ""}${mark.has(i) ? " wv-mark" : ""}"><div class="wv-idx">${i}</div>` +
            `<div class="wv-box">${esc(v)}</div><div class="wv-ptr">${(ptrAt[i] || []).map((p) => "▲ " + esc(p)).join("<br>")}</div></div>`;
        }).join("");
        root.querySelector(".wv-rows").innerHTML = (st ? st.rows : []).map(([label, items]) =>
          `<div class="wv-row"><b>${esc(label)}</b>${items.length ? items.map((x) => `<span>${esc(x)}</span>`).join("") : "<em>empty</em>"}</div>`).join("");
      },
    };
  }

  function boardView(root, spec) {
    const n = spec.n;
    let cells = "";
    for (let r = 0; r < n; r++) for (let c = 0; c < n; c++)
      cells += `<div class="wv-sq ${(r + c) % 2 ? "wv-dark" : "wv-light"}" data-r="${r}" data-c="${c}"></div>`;
    return {
      body: `<div class="wv-board" style="grid-template-columns: repeat(${n}, 2.9rem)">${cells}</div><div class="wv-sols"></div>`,
      draw(mode, st) {
        const q = st ? st.q : Array(n).fill(-1);
        root.querySelectorAll(".wv-sq").forEach((sq) => {
          const r = +sq.dataset.r, c = +sq.dataset.c;
          const hit = q.some((qc, qr) => qc >= 0 && qr !== r && (qc === c || Math.abs(qc - c) === Math.abs(qr - r)));
          const isTry = st && st.try && st.try[0] === r && st.try[1] === c;
          sq.className = `wv-sq ${(r + c) % 2 ? "wv-dark" : "wv-light"}` + (hit && q[r] !== c ? " wv-hit" : "") +
            (isTry ? (st.bad ? " wv-bad" : " wv-try") : "");
          sq.textContent = q[r] === c ? "♛" : isTry && st.bad ? "✕" : "";
        });
        root.querySelector(".wv-sols").textContent = `solutions found: ${st ? st.sols : 0}`;
      },
    };
  }

  function trieView(root) {
    return {
      body: `<svg class="wv-svg" xmlns="${NS}" role="img" aria-label="trie"></svg>`,
      draw(mode, st) {
        const words = st ? st.words : [];
        const kids = { "": [] }, end = new Set(words);
        for (const w of words) for (let i = 1; i <= w.length; i++) {
          const p = w.slice(0, i);
          if (!kids[p]) { kids[p] = []; kids[w.slice(0, i - 1)].push(p); }
        }
        for (const k in kids) kids[k].sort();
        const pos = {}; let leaf = 0, depth = 0;
        (function place(p, d) {
          depth = Math.max(depth, d);
          if (!kids[p].length) { pos[p] = [leaf++, d]; return; }
          kids[p].forEach((c) => place(c, d + 1));
          pos[p] = [(pos[kids[p][0]][0] + pos[kids[p][kids[p].length - 1]][0]) / 2, d];
        })("", 0);
        const W = 480, gap = leaf > 1 ? Math.min(70, (W - 40) / (leaf - 1)) : 0;
        const x0 = (W - gap * Math.max(0, leaf - 1)) / 2, H = 40 + depth * 36;
        const xy = (p) => [x0 + pos[p][0] * gap, 20 + pos[p][1] * 36];
        const on = new Set(), fresh = new Set(st ? st.fresh : []), found = new Set(st ? st.found : []);
        if (st) for (let i = 0; i <= st.path.length; i++) if (kids[st.path.slice(0, i)]) on.add(st.path.slice(0, i));
        let lines = "", nodes = "";
        for (const p in kids) for (const c of kids[p]) {
          const [x1, y1] = xy(p), [x2, y2] = xy(c);
          lines += `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" class="${on.has(p) && on.has(c) ? "wv-on" : ""}"/>`;
        }
        for (const p in kids) {
          const [x, y] = xy(p);
          const cls = found.has(p) ? "wv-found" : on.has(p) ? "wv-on" : fresh.has(p) ? "wv-fresh" : "";
          nodes += `<g class="${cls}"><circle cx="${x}" cy="${y}" r="13"/>` +
            (end.has(p) ? `<circle class="wv-end2" cx="${x}" cy="${y}" r="17"/>` : "") +
            `<text x="${x}" y="${y}">${p === "" ? "·" : esc(p[p.length - 1])}</text></g>`;
        }
        const svg = root.querySelector(".wv-svg");
        svg.setAttribute("viewBox", `0 0 ${W} ${Math.max(H, 60)}`);
        svg.innerHTML = lines + nodes;
      },
    };
  }

  document.querySelectorAll(".windowviz").forEach((root) => {
    const spec = JSON.parse(document.getElementById(root.dataset.src).textContent);
    const modes = spec.kind === "array" ? spec.modes : [{ name: spec.kind, steps: spec.steps }];
    const view = spec.kind === "array" ? arrayView(root) : spec.kind === "board" ? boardView(root, spec) : trieView(root);
    let mode = 0, step = 0, timer = null;
    root.tabIndex = 0;
    root.innerHTML = (modes.length > 1 ? `<div class="wv-tabs" role="tablist">${modes.map((m, k) =>
      `<button role="tab" data-mode="${k}">${esc(m.name)}</button>`).join("")}</div>` : "") +
      view.body +
      `<div class="wv-note" aria-live="polite"></div>
       <div class="wv-ctl"><button data-act="prev">◀ Back</button><button data-act="next">Step ▶</button>
       <button data-act="play">Play</button><button data-act="all">Run all</button><span class="wv-count"></span></div>`;

    function stop() { if (timer) { clearInterval(timer); timer = null; } }
    function draw() {
      const m = modes[mode], n = m.steps.length;
      const st = step > 0 ? m.steps[step - 1] : null;
      view.draw(m, st);
      root.querySelector(".wv-note").innerHTML = st ? st.note : "Press <b>Step</b> (or the → key) to run one step at a time, or <b>Play</b>.";
      root.querySelector(".wv-count").textContent = `step ${step}/${n}`;
      root.querySelector('[data-act="prev"]').disabled = step === 0;
      root.querySelector('[data-act="next"]').disabled = step === n;
      root.querySelector('[data-act="all"]').disabled = step === n;
      root.querySelector('[data-act="play"]').textContent = timer ? "Pause" : "Play";
      root.querySelector('[data-act="play"]').disabled = step === n && !timer;
      root.querySelectorAll(".wv-tabs button").forEach((b) => b.setAttribute("aria-selected", String(Number(b.dataset.mode) === mode)));
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
        }, 700);
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
