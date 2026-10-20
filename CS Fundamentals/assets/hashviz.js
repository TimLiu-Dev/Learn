/* CS Fundamentals: interactive hash table (separate chaining), matching L19's MyHashMap.
 *
 *   <div class="hashviz" data-cap="8" data-keys="one two three four five six seven"></div>
 *   <script src="../assets/hashviz.js"></script>
 *
 * Keys are strings, hashed with Java's String.hashCode formula; index = floorMod(hash, capacity).
 * put prepends to the chain; when size > capacity * 0.75 the table doubles and every key is
 * re-indexed (head to tail, bucket 0 first, prepending), exactly like MyHashMap.resize().
 * data-keys feeds the "Next demo key" button. "Constant hashCode" makes every hash 42.
 */
(function () {
  const css = `
  .hashviz { border: 1px solid var(--rule); border-radius: 8px; padding: .8rem; margin: 0 0 1.4rem; max-width: 60rem; }
  .hv-ctl { display: flex; gap: .4rem; flex-wrap: wrap; align-items: center; margin-bottom: .6rem; }
  .hv-ctl input[type=text] { font: inherit; font-family: var(--mono); font-size: .82rem; color: var(--ink); background: var(--bg); border: 1px solid var(--rule); border-radius: 6px; padding: .25rem .5rem; width: 9rem; max-width: 100%; }
  .hv-ctl button { font: inherit; font-size: .82rem; color: var(--ink); background: var(--surface); border: 1px solid var(--rule); border-radius: 999px; padding: .22rem .8rem; cursor: pointer; }
  .hv-ctl button:disabled { opacity: .4; cursor: default; }
  .hv-ctl label { font-size: .82rem; color: var(--ink-soft); display: inline-flex; gap: .3rem; align-items: center; }
  .hv-stats { font-family: var(--mono); font-size: .74rem; color: var(--ink-soft); margin: .2rem 0 .5rem; }
  .hv-stats b { color: var(--ink); font-weight: 600; }
  .hv-stats .hv-warn { color: var(--accent); }
  .hv-note { font-size: .9rem; min-height: 2.4rem; margin: 0 0 .6rem; padding: .45rem .7rem; background: var(--surface); border-radius: 5px; overflow-wrap: anywhere; }
  .hv-note code { font-size: .8rem; }
  .hv-rows { font-family: var(--mono); font-size: .76rem; }
  .hv-row { display: flex; align-items: flex-start; gap: .4rem; padding: .18rem 0; border-bottom: 1px dashed var(--rule); }
  .hv-idx { flex: 0 0 2.4rem; text-align: right; color: var(--ink-soft); padding-top: .12rem; }
  .hv-chain { display: flex; flex-wrap: wrap; gap: .25rem; align-items: center; min-height: 1.3rem; min-width: 0; }
  .hv-chain .hv-null { color: var(--rule); }
  .hv-key { display: inline-block; padding: .05rem .45rem; border-radius: 4px; color: var(--kotlin); background: color-mix(in srgb, var(--kotlin) 10%, var(--bg)); border: 1px solid transparent; }
  .hv-key.hv-new { color: var(--java); background: color-mix(in srgb, var(--java) 14%, var(--bg)); border-color: var(--java); font-weight: 600; }
  .hv-key.hv-seen { color: var(--accent); background: color-mix(in srgb, var(--accent) 10%, var(--bg)); }
  .hv-key.hv-hit { color: var(--ok); background: color-mix(in srgb, var(--ok) 14%, var(--bg)); border-color: var(--ok); font-weight: 600; }
  .hv-arrow { color: var(--ink-soft); }
  .hv-row.hv-active .hv-idx { color: var(--accent); font-weight: 600; }
  .hv-hidden { font-size: .74rem; color: var(--ink-soft); margin-top: .3rem; }
  @media print { .hv-ctl { display: none; } }`;
  const style = document.createElement("style");
  style.textContent = css;
  document.head.appendChild(style);

  const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));
  const javaHash = (s) => { let h = 0; for (let i = 0; i < s.length; i++) h = (Math.imul(31, h) + s.charCodeAt(i)) | 0; return h; };
  const floorMod = (h, n) => ((h % n) + n) % n;

  document.querySelectorAll(".hashviz").forEach((root) => {
    const startCap = Number(root.dataset.cap || 8);
    const demo = (root.dataset.keys || "").split(/\s+/).filter(Boolean);
    let buckets, size, demoPos, constant = false;
    let mark = { row: -1, keys: {} };   // highlighting for the last action

    root.innerHTML = `
      <div class="hv-ctl">
        <input type="text" aria-label="key" placeholder="type a key" maxlength="24">
        <button data-act="put">put</button>
        <button data-act="get">get</button>
        <button data-act="demo">Next demo key</button>
        <button data-act="reset">Reset</button>
        <label><input type="checkbox" data-act="const"> constant hashCode (42)</label>
      </div>
      <div class="hv-stats" aria-live="polite"></div>
      <div class="hv-note" aria-live="polite"></div>
      <div class="hv-rows"></div>`;
    const input = root.querySelector("input[type=text]");
    const hashOf = (k) => (constant ? 42 : javaHash(k));

    function reset(note) {
      buckets = Array.from({ length: startCap }, () => []);
      size = 0; demoPos = 0; mark = { row: -1, keys: {} };
      draw(note || "Type a key and press <b>put</b>, or press <b>Next demo key</b>.");
    }

    function resize() {
      const old = buckets;
      buckets = Array.from({ length: old.length * 2 }, () => []);
      for (const chain of old) for (const k of chain) buckets[floorMod(hashOf(k), buckets.length)].unshift(k);
    }

    function put(k) {
      const h = hashOf(k), i = floorMod(h, buckets.length), chain = buckets[i];
      const head = `<code>"${esc(k)}".hashCode()</code> = ${h} → <code>floorMod(${h}, ${buckets.length})</code> = <b>${i}</b>. `;
      const pos = chain.indexOf(k);
      if (pos >= 0) {
        mark = { row: i, keys: { [k]: "hv-hit" } };
        for (let j = 0; j < pos; j++) mark.keys[chain[j]] = "hv-seen";
        return draw(head + `Found after ${pos + 1} <code>equals()</code> call(s): the value is replaced and size stays ${size}.`);
      }
      chain.unshift(k); size++;
      let note = head + (chain.length > 1
        ? `Collision: ${chain.length - 1} key(s) already here, so the new entry is prepended to the chain.`
        : "Empty bucket: the entry becomes the chain's head.");
      if (size > buckets.length * 0.75) {
        const before = buckets.length;
        resize();
        note += ` Now size ${size} &gt; ${before} × 0.75 = ${before * 0.75}, so the table <b>resizes to ${buckets.length}</b> and every key is re-indexed with the new capacity.`;
        mark = { row: floorMod(h, buckets.length), keys: { [k]: "hv-new" } };
      } else {
        mark = { row: i, keys: { [k]: "hv-new" } };
      }
      draw(note);
    }

    function get(k) {
      const h = hashOf(k), i = floorMod(h, buckets.length), chain = buckets[i];
      const pos = chain.indexOf(k);
      mark = { row: i, keys: {} };
      const upto = pos >= 0 ? pos : chain.length;
      for (let j = 0; j < upto; j++) mark.keys[chain[j]] = "hv-seen";
      if (pos >= 0) mark.keys[k] = "hv-hit";
      const head = `get: bucket <b>${i}</b>. `;
      draw(head + (pos >= 0
        ? `Compared with ${pos + 1} key(s) using <code>equals()</code>, found <code>"${esc(k)}"</code>.`
        : `Compared with all ${chain.length} key(s) in the chain: not found, so <code>get</code> returns <code>null</code>.`));
    }

    function draw(note) {
      const cap = buckets.length;
      const longest = Math.max(...buckets.map((c) => c.length));
      const lf = size / cap;
      root.querySelector(".hv-stats").innerHTML =
        `size <b>${size}</b> · capacity <b>${cap}</b> · load factor <b>${lf.toFixed(2)}</b> (resize when &gt; 0.75) · longest chain <b class="${longest > 3 ? "hv-warn" : ""}">${longest}</b>`;
      root.querySelector(".hv-note").innerHTML = note;
      const hideEmpty = cap > 16;
      let rows = "", hidden = 0;
      buckets.forEach((chain, i) => {
        if (hideEmpty && chain.length === 0) { hidden++; return; }
        const chips = chain.length
          ? chain.map((k) => `<span class="hv-key ${mark.keys[k] || ""}">${esc(k)}</span>`).join('<span class="hv-arrow">→</span>')
          : '<span class="hv-null">null</span>';
        rows += `<div class="hv-row${i === mark.row ? " hv-active" : ""}"><span class="hv-idx">[${i}]</span><span class="hv-chain">${chips}</span></div>`;
      });
      if (hidden) rows += `<div class="hv-hidden">${hidden} empty bucket(s) hidden</div>`;
      root.querySelector(".hv-rows").innerHTML = rows;
      root.querySelector('[data-act="demo"]').disabled = demoPos >= demo.length;
    }

    root.addEventListener("click", (e) => {
      const b = e.target.closest("button");
      if (!b) return;
      const k = input.value.trim();
      if (b.dataset.act === "put" && k) { put(k); input.value = ""; }
      else if (b.dataset.act === "get" && k) get(k);
      else if (b.dataset.act === "demo" && demoPos < demo.length) put(demo[demoPos++]);
      else if (b.dataset.act === "reset") reset();
      input.focus({ preventScroll: true });
    });
    input.addEventListener("keydown", (e) => {
      if (e.key === "Enter" && input.value.trim()) { put(input.value.trim()); input.value = ""; e.preventDefault(); }
    });
    root.querySelector('[data-act="const"]').addEventListener("change", (e) => {
      constant = e.target.checked;
      reset(constant
        ? "Every key now has hashCode 42, so every key lands in bucket <code>floorMod(42, capacity)</code>. Add a few keys and watch one chain grow. Then try <b>get</b>."
        : "Back to real <code>String.hashCode</code> values.");
    });
    reset();
  });
})();
