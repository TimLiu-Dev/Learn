/* CS Fundamentals: live SHA-256 avalanche demo (L33).
 *
 *   <div class="hashviz2" data-a="hello" data-b="hellp"></div>
 *   <script src="../assets/hashviz2.js"></script>
 *
 * Two text boxes, each hashed with the browser's Web Crypto SHA-256 as you type.
 * Hex digits that differ between the two hashes are highlighted, and the number of
 * differing bits (out of 256) is shown. Also shows Java's String.hashCode for both
 * inputs, to contrast a fast table hash (L19) with a cryptographic one.
 * Works from file:// (Chrome treats file: as a secure context). If crypto.subtle is
 * missing, the widget says so instead of failing.
 */
(function () {
  const css = `
  .hashviz2 { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .hv2-row { margin-bottom: .7rem; }
  .hv2-row label { display: block; font-size: .8rem; color: var(--ink-soft); margin-bottom: .2rem; }
  .hv2-row input { font: inherit; font-family: var(--mono); font-size: .9rem; width: 100%; box-sizing: border-box;
    color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 5px; padding: .3rem .5rem; }
  .hv2-hex { font-family: var(--mono); font-size: .74rem; line-height: 1.6; word-break: break-all; margin-top: .3rem; }
  .hv2-hex .d { color: var(--accent); background: color-mix(in srgb, var(--accent) 12%, var(--bg)); border-radius: 2px; }
  .hv2-stats { font-size: .9rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; }
  .hv2-stats b { font-family: var(--mono); }
  .hv2-bar { height: .5rem; background: var(--rule); border-radius: 999px; overflow: hidden; margin-top: .35rem; }
  .hv2-bar span { display: block; height: 100%; background: var(--kotlin); }
  .hv2-java { font-family: var(--mono); font-size: .76rem; color: var(--ink-soft); margin-top: .4rem; }
  @media print { .hv2-row input { border: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const enc = new TextEncoder();

  async function sha256(s) {
    const buf = await crypto.subtle.digest("SHA-256", enc.encode(s));
    return new Uint8Array(buf);
  }

  // Java's String.hashCode: s[0]*31^(n-1) + ... + s[n-1], in 32-bit int arithmetic, over UTF-16 units
  function javaHash(s) {
    let h = 0;
    for (let i = 0; i < s.length; i++) h = (Math.imul(31, h) + s.charCodeAt(i)) | 0;
    return h;
  }

  const hex = (bytes) => Array.from(bytes, (b) => b.toString(16).padStart(2, "0")).join("");
  const popcount = (x) => { let n = 0; while (x) { n += x & 1; x >>>= 1; } return n; };

  document.querySelectorAll(".hashviz2").forEach((root) => {
    root.innerHTML = `
      <div class="hv2-row"><label>Input A</label><input data-k="a" spellcheck="false" autocomplete="off">
        <div class="hv2-hex" data-out="a"></div></div>
      <div class="hv2-row"><label>Input B</label><input data-k="b" spellcheck="false" autocomplete="off">
        <div class="hv2-hex" data-out="b"></div></div>
      <div class="hv2-stats" aria-live="polite"></div>`;
    const ia = root.querySelector('[data-k="a"]');
    const ib = root.querySelector('[data-k="b"]');
    ia.value = root.dataset.a || "hello";
    ib.value = root.dataset.b || "hellp";
    const outA = root.querySelector('[data-out="a"]');
    const outB = root.querySelector('[data-out="b"]');
    const stats = root.querySelector(".hv2-stats");

    if (!(window.crypto && crypto.subtle)) {
      stats.textContent = "This browser doesn't expose Web Crypto here, so the live hash is unavailable. Run Hashes.kt instead.";
      return;
    }

    async function update() {
      const [a, b] = await Promise.all([sha256(ia.value), sha256(ib.value)]);
      const ha = hex(a), hb = hex(b);
      const mark = (h, other) => Array.from(h, (c, i) => c === other[i] ? c : `<span class="d">${c}</span>`).join("");
      outA.innerHTML = mark(ha, hb);
      outB.innerHTML = mark(hb, ha);
      let bits = 0;
      for (let i = 0; i < a.length; i++) bits += popcount(a[i] ^ b[i]);
      const ja = javaHash(ia.value), jb = javaHash(ib.value);
      stats.innerHTML = `SHA-256 bits that differ: <b>${bits}</b> of 256 (${Math.round(bits / 2.56)}%)
        <div class="hv2-bar"><span style="width:${bits / 2.56}%"></span></div>
        <div class="hv2-java">String.hashCode: A = ${ja}, B = ${jb}${ja === jb && ia.value !== ib.value ? "  ← collision!" : ""}</div>`;
    }
    ia.addEventListener("input", update);
    ib.addEventListener("input", update);
    update();
  });
})();
