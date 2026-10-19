/* CS Fundamentals: interactive bit-flipper.
 *
 *   <div class="bits" data-width="8" data-start="5"></div>
 *   <script src="../assets/bits.js"></script>
 *
 * Click a bit to flip it. Shows the pattern, its unsigned value, and its signed
 * (two's-complement) value, where the top bit is worth -2^(width-1).
 * The +1 / -1 buttons wrap around exactly like fixed-width integers on the JVM.
 */
(function () {
  document.querySelectorAll(".bits").forEach((root) => {
    const width = Number(root.dataset.width || 8);
    const mod = 2 ** width;
    let value = ((Number(root.dataset.start || 0) % mod) + mod) % mod; // stored unsigned

    root.innerHTML = `
      <div class="bits-row" role="group" aria-label="${width} bits"></div>
      <div class="bits-out">
        <span>binary <b class="b-bin"></b></span>
        <span>unsigned <b class="b-uns"></b></span>
        <span>signed (two's complement) <b class="b-sig"></b></span>
      </div>
      <div class="bits-ctl">
        <button data-op="inc">+1</button><button data-op="dec">−1</button>
        <button data-op="max">signed max</button><button data-op="min">signed min</button>
        <button data-op="zero">0</button>
      </div>`;

    const row = root.querySelector(".bits-row");
    const cells = [];
    for (let i = width - 1; i >= 0; i--) {
      const place = i === width - 1 ? `−${2 ** i}` : `${2 ** i}`;
      const btn = document.createElement("button");
      btn.className = "bit";
      btn.innerHTML = `<span class="v"></span><small>${place}</small>`;
      btn.addEventListener("click", () => { value ^= 2 ** i; render(); });
      row.appendChild(btn);
      cells.push({ i, btn });
    }

    root.querySelector(".bits-ctl").addEventListener("click", (e) => {
      const op = e.target.dataset.op;
      if (!op) return;
      if (op === "inc") value = (value + 1) % mod;
      if (op === "dec") value = (value - 1 + mod) % mod;
      if (op === "max") value = mod / 2 - 1;
      if (op === "min") value = mod / 2;
      if (op === "zero") value = 0;
      render();
    });

    function render() {
      cells.forEach(({ i, btn }) => {
        const on = Math.floor(value / 2 ** i) % 2 === 1;
        btn.classList.toggle("on", on);
        btn.querySelector(".v").textContent = on ? "1" : "0";
      });
      const signed = value >= mod / 2 ? value - mod : value;
      root.querySelector(".b-bin").textContent = value.toString(2).padStart(width, "0").replace(/(.{4})(?=.)/g, "$1_");
      root.querySelector(".b-uns").textContent = value;
      root.querySelector(".b-sig").textContent = signed;
    }
    render();
  });
})();
