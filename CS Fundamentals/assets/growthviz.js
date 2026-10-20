/* CS Fundamentals: dynamic-array growth stepper (amortised cost made visible).
 *
 *   <div class="growthviz"></div>
 *   <script src="../assets/growthviz.js"></script>
 *
 * Optional attributes: data-max="64" (adds allowed), data-start="2" (initial capacity),
 * data-step="2" (slots added by the constant-growth policy).
 * Each add costs 1 write, plus `size` copies when the array is full and must be replaced.
 * Two policies: double the capacity, or add a constant number of slots.
 */
(function () {
  const css = `
  .growthviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .gv-tabs { display: flex; gap: .4rem; flex-wrap: wrap; margin-bottom: .6rem; }
  .gv-tabs button, .gv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .gv-tabs button[aria-selected="true"] { border-color: var(--kotlin); color: var(--kotlin); font-weight: 600; }
  .gv-ctl button:disabled { opacity: .4; cursor: default; }
  .gv-label { font-family: var(--mono); font-size: .68rem; color: var(--ink-soft); margin: .5rem 0 .25rem; text-transform: uppercase; letter-spacing: .06em; }
  .gv-cells { display: flex; flex-wrap: wrap; gap: 3px; min-height: 1.4rem; }
  .gv-cell { width: 1.15rem; height: 1.15rem; border: 1px solid var(--rule); border-radius: 3px; background: var(--bg); }
  .gv-cell.used { background: color-mix(in srgb, var(--kotlin) 35%, var(--bg)); border-color: var(--kotlin); }
  .gv-cell.copied { background: color-mix(in srgb, var(--java) 45%, var(--bg)); border-color: var(--java); }
  .gv-cell.fresh { background: var(--kotlin); border-color: var(--kotlin); }
  .gv-bars { display: flex; align-items: flex-end; gap: 2px; height: 5.5rem; border-bottom: 1px solid var(--rule); overflow: hidden; }
  .gv-bar { flex: 1 1 0; min-width: 2px; max-width: 12px; background: var(--kotlin); border-radius: 2px 2px 0 0; }
  .gv-bar.resize { background: var(--java); }
  .gv-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(7.5rem, 1fr)); gap: .4rem; margin: .7rem 0 .3rem; }
  .gv-stats div { background: var(--surface); border-radius: 5px; padding: .3rem .5rem; font-size: .78rem; color: var(--ink-soft); }
  .gv-stats b { display: block; font-family: var(--mono); font-size: 1rem; color: var(--ink); }
  .gv-note { font-size: .9rem; min-height: 2.4rem; margin: .5rem 0; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .gv-ctl { display: flex; gap: .5rem; flex-wrap: wrap; }
  .gv-key { font-size: .75rem; color: var(--ink-soft); margin-top: .3rem; }
  .gv-key i { display: inline-block; width: .7rem; height: .7rem; border-radius: 2px; vertical-align: -1px; margin: 0 .25rem 0 .6rem; }
  @media print { .gv-ctl, .gv-tabs { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  document.querySelectorAll(".growthviz").forEach((root) => {
    const max = +(root.dataset.max || 64);
    const start = +(root.dataset.start || 2);
    const plus = +(root.dataset.step || 2);
    const policies = [
      { name: "Double: ×2", grow: (c) => c * 2 },
      { name: `Constant: +${plus}`, grow: (c) => c + plus },
    ];
    let policy = 0, cap, size, copies, costs, lastCopied;

    root.innerHTML = `
      <div class="gv-tabs" role="tablist">${policies.map((p, k) =>
        `<button role="tab" data-p="${k}">${p.name}</button>`).join("")}</div>
      <div class="gv-label">backing array (capacity = boxes)</div>
      <div class="gv-cells"></div>
      <div class="gv-label">cost of each add (writes + copies)</div>
      <div class="gv-bars"></div>
      <div class="gv-key"><i style="background:var(--kotlin)"></i>cheap add: 1 write<i style="background:var(--java)"></i>add that resized: 1 write + size copies</div>
      <div class="gv-stats"></div>
      <div class="gv-note" aria-live="polite"></div>
      <div class="gv-ctl"><button data-a="1">Add 1</button><button data-a="8">Add 8</button><button data-a="all">Add to ${max}</button><button data-a="reset">Reset</button></div>`;
    const $ = (s) => root.querySelector(s);

    function reset() { cap = start; size = 0; copies = 0; costs = []; lastCopied = 0; render("Empty list, capacity " + start + ". Press <b>Add</b>."); }

    function add() {
      let cost = 1;
      lastCopied = 0;
      if (size === cap) {
        const old = cap;
        cap = policies[policy].grow(cap);
        cost += size;
        copies += size;
        lastCopied = size;
        add.msg = `Full at ${old}: new array of ${cap}, <b>${size} copies</b>, then 1 write.`;
      } else {
        add.msg = `Room left (${size}/${cap}): 1 write, no copies.`;
      }
      size++;
      costs.push(cost);
    }

    function render(msg) {
      $(".gv-cells").innerHTML = Array.from({ length: cap }, (_, i) =>
        `<span class="gv-cell ${i === size - 1 && size > 0 ? "fresh" : i < lastCopied ? "copied" : i < size ? "used" : ""}"></span>`).join("");
      const top = Math.max(1, ...costs);
      $(".gv-bars").innerHTML = costs.map((c) =>
        `<span class="gv-bar ${c > 1 ? "resize" : ""}" style="height:${Math.max(4, (c / top) * 100)}%" title="${c}"></span>`).join("");
      const work = size + copies;
      $(".gv-stats").innerHTML =
        `<div>adds<b>${size}</b></div><div>capacity<b>${cap}</b></div><div>copies so far<b>${copies}</b></div>` +
        `<div>work per add<b>${size ? (work / size).toFixed(2) : "–"}</b></div>`;
      $(".gv-note").innerHTML = msg;
      root.querySelectorAll(".gv-tabs button").forEach((b) => b.setAttribute("aria-selected", +b.dataset.p === policy));
      root.querySelectorAll(".gv-ctl button[data-a]").forEach((b) => { if (b.dataset.a !== "reset") b.disabled = size >= max; });
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      if (b.dataset.p) { policy = +b.dataset.p; reset(); return; }
      const a = b.dataset.a;
      if (a === "reset") { reset(); return; }
      const n = a === "all" ? max - size : +a;
      let resized = 0;
      for (let i = 0; i < n && size < max; i++) { add(); if (lastCopied) resized++; }
      let msg = n === 1 ? add.msg : `${n} adds, ${resized} of them resized. ` + add.msg;
      if (size >= max) msg += policy === 0
        ? ` Work per add stays under 3: <b>amortised O(1)</b>.`
        : ` Work per add keeps climbing as n grows: <b>O(n) per add, O(n²) overall</b>.`;
      render(msg);
    });

    reset();
  });
})();
