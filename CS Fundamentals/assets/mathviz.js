/* CS Fundamentals: maths explorers for L48.
 *
 *   <div class="mathviz" data-kind="birthday"></div>   collision chance vs n: exact curve + Monte Carlo
 *   <div class="mathviz" data-kind="masks"></div>      two clickable 8-bit values and their and/or/xor/shifts
 *   <script src="../assets/mathviz.js"></script>
 *
 * Birthday: P(some pair shares a value) = 1 - prod_{i<n} (1 - i/d), the same formula as
 * Birthday.kt / Birthday.java. "Simulate" uses Math.random, so each press gives a new estimate.
 * Masks: 8-bit unsigned, so shl drops the top bit, like (x shl 1) and 0xFF.
 */
(function () {
  const css = `
  .mathviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 40rem; }
  .mz-row { display: flex; flex-wrap: wrap; align-items: center; gap: .5rem .9rem; font-size: .88rem; margin: .3rem 0; }
  .mz-row label { display: flex; align-items: center; gap: .4rem; }
  .mz-row input[type=range] { width: 11rem; accent-color: var(--accent); }
  .mz-row select, .mathviz button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .2rem .8rem; cursor: pointer; }
  .mz-big { font-family: var(--mono); font-size: 1.35rem; font-weight: 600; color: var(--accent); }
  .mz-svg { width: 100%; height: auto; display: block; margin: .4rem 0; }
  .mz-svg .axis { stroke: var(--rule); stroke-width: 1; }
  .mz-svg .half { stroke: var(--ink-soft); stroke-width: 1; stroke-dasharray: 4 4; }
  .mz-svg .curve { fill: none; stroke: var(--kotlin); stroke-width: 2.5; }
  .mz-svg .mark { stroke: var(--accent); stroke-width: 1.5; }
  .mz-svg circle.dot { fill: var(--accent); }
  .mz-svg circle.sim { fill: var(--java); }
  .mz-svg text { font-family: var(--mono); font-size: 11px; fill: var(--ink-soft); }
  .mz-note { font-size: .86rem; color: var(--ink-soft); margin: .3rem 0 0; }
  .mz-bits { display: grid; grid-template-columns: minmax(0, 8rem) repeat(8, minmax(1.3rem, 2rem)) minmax(2rem, auto); gap: .25rem .2rem; align-items: center; font-family: var(--mono); font-size: .82rem; }
  @media (max-width: 30rem) { .mz-bits { font-size: .72rem; grid-template-columns: minmax(0, 6.2rem) repeat(8, 1.3rem) minmax(1.8rem, auto); gap: .2rem .12rem; } .mz-row input[type=range] { width: 8rem; } .mz-row select { max-width: 100%; } }
  .mz-bits .lbl { padding-right: .3rem; color: var(--ink-soft); overflow-wrap: anywhere; }
  .mz-bits .dec { padding-left: .35rem; white-space: nowrap; }
  .mz-bits .b { text-align: center; border: 1px solid var(--rule); border-radius: 4px; padding: .15rem 0; }
  .mz-bits button.b { padding: .15rem 0; border-radius: 4px; font-family: var(--mono); }
  .mz-bits .on { background: color-mix(in srgb, var(--kotlin) 22%, var(--bg)); border-color: var(--kotlin); font-weight: 700; }
  .mz-bits .sep { grid-column: 1 / -1; border-top: 1px solid var(--rule); margin: .2rem 0; }
  @media print { .mathviz button, .mathviz input, .mathviz select { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const pShared = (n, d) => { let none = 1; for (let i = 0; i < n; i++) none *= 1 - i / d; return 1 - none; };

  function birthday(root) {
    const spaces = [["365 birthdays", 365], ["1,000 buckets", 1000], ["46,656 (3 chars of a–z0–9)", 46656]];
    root.innerHTML = `
      <div class="mz-row">
        <label>space <select class="mz-d">${spaces.map(([t, v]) => `<option value="${v}">${t}</option>`).join("")}</select></label>
        <label>n = <input class="mz-n" type="range" min="1" max="100" value="23"> <b class="mz-nv"></b></label>
      </div>
      <div class="mz-row"><span>P(at least one shared value) =</span><span class="mz-big mz-p"></span>
        <button class="mz-sim" type="button">Simulate 2,000 rooms</button><span class="mz-simv"></span></div>
      <svg class="mz-svg" viewBox="0 0 480 200" role="img" aria-label="collision probability curve"></svg>
      <p class="mz-note mz-half"></p>`;
    const sel = root.querySelector(".mz-d"), rng = root.querySelector(".mz-n"), svg = root.querySelector(".mz-svg");
    let sim = null;
    const X0 = 36, X1 = 470, Y0 = 175, Y1 = 12;

    function render() {
      const d = Number(sel.value);
      const maxN = d === 365 ? 100 : d === 1000 ? 120 : 600;
      rng.max = maxN;
      if (Number(rng.value) > maxN) rng.value = maxN;
      const n = Number(rng.value);
      const sx = (k) => X0 + (X1 - X0) * k / maxN, sy = (p) => Y0 - (Y0 - Y1) * p;
      let path = "";
      for (let k = 0; k <= maxN; k++) path += (k ? "L" : "M") + sx(k).toFixed(1) + " " + sy(pShared(k, d)).toFixed(1);
      let half = 1; while (pShared(half, d) < 0.5) half++;
      const p = pShared(n, d);
      svg.innerHTML = `
        <line class="axis" x1="${X0}" y1="${Y0}" x2="${X1}" y2="${Y0}"/><line class="axis" x1="${X0}" y1="${Y0}" x2="${X0}" y2="${Y1}"/>
        <line class="half" x1="${X0}" y1="${sy(0.5)}" x2="${X1}" y2="${sy(0.5)}"/>
        <text x="4" y="${sy(1) + 4}">1.0</text><text x="4" y="${sy(0.5) + 4}">0.5</text><text x="12" y="${Y0 + 4}">0</text>
        <text x="${X1 - 30}" y="${Y0 + 16}">n=${maxN}</text>
        <path class="curve" d="${path}"/>
        <line class="mark" x1="${sx(n)}" y1="${Y0}" x2="${sx(n)}" y2="${sy(p)}"/>
        <circle class="dot" cx="${sx(n)}" cy="${sy(p)}" r="5"/>
        ${sim && sim.n === n && sim.d === d ? `<circle class="sim" cx="${sx(n) + 9}" cy="${sy(sim.p)}" r="4"/>` : ""}`;
      root.querySelector(".mz-nv").textContent = n;
      root.querySelector(".mz-p").textContent = p.toFixed(4);
      root.querySelector(".mz-simv").textContent = sim && sim.n === n && sim.d === d ? `simulated ${sim.p.toFixed(3)}` : "";
      root.querySelector(".mz-half").textContent =
        `With ${d.toLocaleString("en-US")} equally likely values, the chance of a shared value first reaches 50% at n = ${half}. ` +
        `That's on the scale of √d ≈ ${Math.round(Math.sqrt(d))}, nowhere near d / 2 = ${Math.round(d / 2).toLocaleString("en-US")}.`;
    }
    root.querySelector(".mz-sim").addEventListener("click", () => {
      const d = Number(sel.value), n = Number(rng.value), rooms = 2000;
      let hits = 0;
      for (let t = 0; t < rooms; t++) {
        const seen = new Set();
        for (let i = 0; i < n; i++) { const v = Math.floor(Math.random() * d); if (seen.has(v)) { hits++; break; } seen.add(v); }
      }
      sim = { n, d, p: hits / rooms };
      render();
    });
    sel.addEventListener("change", () => { sim = null; render(); });
    rng.addEventListener("input", render);
    render();
  }

  function masks(root) {
    let a = 0b11001010, b = 0b01011100;
    const ops = [
      ["a and b", "a &amp; b", () => a & b],
      ["a or b", "a | b", () => a | b],
      ["a xor b", "a ^ b", () => a ^ b],
      ["a and b.inv()", "a &amp; ~b", () => a & ~b & 0xff],
      ["a shl 1", "a &lt;&lt; 1", () => (a << 1) & 0xff],
      ["a ushr 1", "a &gt;&gt;&gt; 1", () => a >>> 1],
    ];
    root.innerHTML = `<div class="mz-bits"></div>
      <div class="mz-row"><button type="button" data-set="a0">a = 0</button><button type="button" data-set="b0">b = 0</button>
      <button type="button" data-set="bf">b = 0x0F mask</button><button type="button" data-set="b8">b = one bit</button></div>
      <p class="mz-note">Click any bit of <b>a</b> or <b>b</b>. Each row shows the Kotlin name, then Java's operator. Values are 8-bit here; on the JVM an <code>Int</code> has 32.</p>`;
    const grid = root.querySelector(".mz-bits");
    function cells(v, clickable, which) {
      let h = "";
      for (let i = 7; i >= 0; i--) {
        const on = (v >> i) & 1;
        h += clickable ? `<button type="button" class="b${on ? " on" : ""}" data-w="${which}" data-i="${i}" aria-label="${which} bit ${i}">${on}</button>`
          : `<span class="b${on ? " on" : ""}">${on}</span>`;
      }
      return h;
    }
    function render() {
      let h = `<span class="lbl">a</span>${cells(a, true, "a")}<span class="dec">${a}</span>`;
      h += `<span class="lbl">b</span>${cells(b, true, "b")}<span class="dec">${b}</span><span class="sep"></span>`;
      for (const [kt, jv, f] of ops) h += `<span class="lbl">${kt}<br><small>${jv}</small></span>${cells(f(), false)}<span class="dec">${f()}</span>`;
      grid.innerHTML = h;
    }
    grid.addEventListener("click", (e) => {
      const t = e.target.closest("button[data-w]"); if (!t) return;
      const bit = 1 << Number(t.dataset.i);
      if (t.dataset.w === "a") a ^= bit; else b ^= bit;
      render();
    });
    root.querySelector(".mz-row").addEventListener("click", (e) => {
      const s = e.target.dataset.set; if (!s) return;
      if (s === "a0") a = 0; if (s === "b0") b = 0; if (s === "bf") b = 0x0f; if (s === "b8") b = 0b00001000;
      render();
    });
    render();
  }

  document.querySelectorAll(".mathviz").forEach((root) => {
    if (root.dataset.kind === "birthday") birthday(root);
    if (root.dataset.kind === "masks") masks(root);
  });
})();
