// Delay calculator: transmission delay (L/R) vs propagation delay (d/s) for one link.
//
// Markup:
//   <div class="widget delay-calc" data-size="1500" data-rate="100" data-distance="1000"></div>
//   <script src="../assets/delay-calc.js"></script>
//
// Propagation speed defaults to 2×10^8 m/s (optical fibre, Peterson & Davie §1.5).
(function () {
  var SPEED_KM_PER_MS = 200; // 2e8 m/s

  function fmt(ms) {
    if (ms >= 1000) return (ms / 1000).toFixed(2) + " s";
    if (ms >= 1) return ms.toFixed(2) + " ms";
    return (ms * 1000).toFixed(1) + " µs";
  }
  // Log-scale sliders so one control spans dial-up to datacentre links.
  function logSlider(min, max, value) {
    return { min: Math.log10(min), max: Math.log10(max), value: Math.log10(value) };
  }

  document.querySelectorAll(".delay-calc").forEach(function (el) {
    var size = +el.dataset.size || 1500, rate = +el.dataset.rate || 100, dist = +el.dataset.distance || 1000;
    var sizeS = logSlider(64, 1e7, size), rateS = logSlider(0.056, 100000, rate), distS = logSlider(0.01, 20000, dist);
    el.innerHTML =
      '<label>Packet size <input type="range" step="0.01" data-k="size" min="' + sizeS.min + '" max="' + sizeS.max + '" value="' + sizeS.value + '"><span class="val" data-v="size"></span></label>' +
      '<label>Link bandwidth <input type="range" step="0.01" data-k="rate" min="' + rateS.min + '" max="' + rateS.max + '" value="' + rateS.value + '"><span class="val" data-v="rate"></span></label>' +
      '<label>Distance <input type="range" step="0.01" data-k="dist" min="' + distS.min + '" max="' + distS.max + '" value="' + distS.value + '"><span class="val" data-v="dist"></span></label>' +
      '<div class="bar" aria-hidden="true"><span data-b="tx" style="background:var(--layer-link)"></span><span data-b="prop" style="background:var(--layer-network)"></span></div>' +
      '<div class="legend"><span><i class="swatch" style="background:var(--layer-link)"></i>Transmission <b data-o="tx"></b></span>' +
      '<span><i class="swatch" style="background:var(--layer-network)"></i>Propagation <b data-o="prop"></b></span>' +
      '<span>Total one-way <b data-o="total"></b></span></div>';

    function q(sel) { return el.querySelector(sel); }
    function update() {
      var bytes = Math.round(Math.pow(10, +q('[data-k=size]').value));
      var mbps = Math.pow(10, +q('[data-k=rate]').value);
      var km = Math.pow(10, +q('[data-k=dist]').value);
      var txMs = (bytes * 8) / (mbps * 1e6) * 1000;
      var propMs = km / SPEED_KM_PER_MS;
      var total = txMs + propMs;
      q('[data-v=size]').textContent = bytes >= 1e6 ? (bytes / 1e6).toFixed(1) + " MB" : bytes >= 1e3 ? (bytes / 1e3).toFixed(1) + " KB" : bytes + " B";
      q('[data-v=rate]').textContent = mbps >= 1000 ? (mbps / 1000).toFixed(1) + " Gbps" : mbps >= 1 ? mbps.toFixed(1) + " Mbps" : (mbps * 1000).toFixed(0) + " kbps";
      q('[data-v=dist]').textContent = km >= 1 ? km.toFixed(km < 10 ? 1 : 0) + " km" : (km * 1000).toFixed(0) + " m";
      q('[data-b=tx]').style.width = (txMs / total * 100) + "%";
      q('[data-b=prop]').style.width = (propMs / total * 100) + "%";
      q('[data-o=tx]').textContent = fmt(txMs);
      q('[data-o=prop]').textContent = fmt(propMs);
      q('[data-o=total]').textContent = fmt(total);
    }
    el.querySelectorAll("input").forEach(function (i) { i.addEventListener("input", update); });
    update();
  });
})();
