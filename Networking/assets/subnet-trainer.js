// Subnet trainer: endless random IPv4 subnetting problems, checked instantly.
//
// Markup:
//   <div class="widget subnet-trainer"
//        data-modes="network,broadcast,range,hosts,same,mask"   <!-- optional; default: all six -->
//        data-min-prefix="8" data-max-prefix="30"></div>        <!-- optional; defaults 8 and 30 -->
//   <script src="../assets/subnet-trainer.js"></script>
//
// Modes (each problem picks one at random from data-modes; the learner can also
// pick a single mode from the drop-down):
//   network    a.b.c.d/n → network address
//   broadcast  a.b.c.d/n → broadcast address (never asked for /31 or /32)
//   range      a.b.c.d/n → first and last usable host
//   hosts      a.b.c.d/n → number of usable host addresses
//   same       two addresses on /n → same subnet or not?
//   mask       /n → dotted netmask, or hex netmask (as macOS ifconfig prints it) → /n
//
// Prefixes are drawn from [data-min-prefix, data-max-prefix] (allowed 0–32),
// biased towards ones that don't fall on an octet boundary. Conventions:
// usable hosts = 2^(32−n) − 2 for n ≤ 30; a /31 has 2 usable addresses and no
// broadcast (RFC 3021); a /32 is a single host.
// A wrong answer shows a hint about the method and offers "Show answer";
// after each problem the binary working is shown. The score counts first tries.
// In Node, the pure functions are exported (module.exports) for testing.
(function () {
  // ---------- Pure IPv4 helpers (unsigned 32-bit numbers) ----------
  function maskOf(n) { return n === 0 ? 0 : (0xffffffff << (32 - n)) >>> 0; }
  function networkOf(ip, n) { return (ip & maskOf(n)) >>> 0; }
  function broadcastOf(ip, n) { return (networkOf(ip, n) | (~maskOf(n) >>> 0)) >>> 0; }
  function usableHosts(n) { return n === 32 ? 1 : n === 31 ? 2 : Math.pow(2, 32 - n) - 2; }
  function firstUsable(ip, n) { return n >= 31 ? networkOf(ip, n) : networkOf(ip, n) + 1; }
  function lastUsable(ip, n) { return n >= 31 ? broadcastOf(ip, n) : broadcastOf(ip, n) - 1; }
  function sameSubnet(a, b, n) { return networkOf(a, n) === networkOf(b, n); }
  function toDotted(x) { return [x >>> 24, (x >>> 16) & 255, (x >>> 8) & 255, x & 255].join("."); }
  function toHex(x) { return "0x" + ("00000000" + x.toString(16)).slice(-8); }
  function parseDotted(s) {
    var m = String(s).trim().match(/^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/);
    if (!m) return null;
    var o = m.slice(1).map(Number);
    if (o.some(function (v) { return v > 255; })) return null;
    return ((o[0] << 24) | (o[1] << 16) | (o[2] << 8) | o[3]) >>> 0;
  }
  function parseCount(s) {
    var t = String(s).trim().replace(/^\//, "").replace(/[\s,_']/g, "");
    return /^\d+$/.test(t) ? Number(t) : null;
  }
  function toBinary(x, n) {
    // "10101100.00010100.01001|101.00001001" with "|" after the first n bits
    var bits = ("0".repeat(32) + x.toString(2)).slice(-32), out = "";
    for (var i = 0; i < 32; i++) {
      if (i === n && n % 8 !== 0) out += "|";
      if (i > 0 && i % 8 === 0) out += (i === n ? "|" : ".");
      out += bits[i];
    }
    return out;
  }

  // ---------- Problem generation ----------
  var ALL = ["network", "broadcast", "range", "hosts", "same", "mask"];
  var LABELS = { network: "Network address", broadcast: "Broadcast address", range: "Usable host range",
    hosts: "Number of usable hosts", same: "Same subnet?", mask: "Netmasks" };

  function rint(lo, hi) { return lo + Math.floor(Math.random() * (hi - lo + 1)); }
  function rand32() { return Math.floor(Math.random() * 4294967296) >>> 0; }
  function randomAddress() {
    var r = Math.random();
    if (r < 0.2) return parseDotted("10." + rint(0, 255) + "." + rint(0, 255) + "." + rint(0, 255));
    if (r < 0.35) return parseDotted("172." + rint(16, 31) + "." + rint(0, 255) + "." + rint(0, 255));
    if (r < 0.5) return parseDotted("192.168." + rint(0, 255) + "." + rint(0, 255));
    return parseDotted(rint(1, 223) + "." + rint(0, 255) + "." + rint(0, 255) + "." + rint(0, 255));
  }
  function randomPrefix(lo, hi) {
    // Three times in four, prefer a prefix that splits an octet (the interesting case).
    var split = [];
    for (var n = lo; n <= hi; n++) if (n % 8 !== 0) split.push(n);
    if (split.length && Math.random() < 0.75) return split[rint(0, split.length - 1)];
    return rint(lo, hi);
  }

  // Hint describing the method for this particular prefix (never the answer itself).
  function octetHint(n) {
    if (n === 0) return "A /0 has no network bits at all.";
    if (n === 32) return "A /32 has no host bits: every bit belongs to the network.";
    if (n % 8 === 0) return "/" + n + " ends exactly on an octet boundary: the first " + n / 8 +
      " octet(s) are network, the rest are host.";
    var k = Math.floor(n / 8), b = n % 8, block = Math.pow(2, 8 - b);
    return "/" + n + " ends inside octet " + (k + 1) + " (" + b + " of its 8 bits are network bits), so in that octet " +
      "subnets come in blocks of 2^" + (8 - b) + " = " + block + ".";
  }

  function makeProblem(mode, lo, hi) {
    // A /31 or /32 has no broadcast address, so never ask for one (callers ensure lo <= 30).
    var n = randomPrefix(lo, mode === "broadcast" ? Math.min(hi, 30) : hi), ip = randomAddress();
    var p = { mode: mode, n: n, ip: ip };
    var cidr = "<code>" + toDotted(ip) + "/" + n + "</code>";
    if (mode === "network") {
      p.html = "What is the <strong>network address</strong> of " + cidr + "?";
      p.fields = ["network address"]; p.answer = [toDotted(networkOf(ip, n))];
      p.hint = "Network = address AND mask: keep the first " + n + " bits, set the rest to 0. " + octetHint(n) +
        (n % 8 ? " Round that octet down to a multiple of " + Math.pow(2, 8 - n % 8) + "; later octets become 0." : "");
    } else if (mode === "broadcast") {
      p.html = "What is the <strong>broadcast address</strong> of " + cidr + "?";
      p.fields = ["broadcast address"]; p.answer = [toDotted(broadcastOf(ip, n))];
      p.hint = "Broadcast = the network address with every host bit set to 1. " + octetHint(n) +
        (n % 8 ? " In that octet: network value + " + Math.pow(2, 8 - n % 8) + " − 1; later octets become 255." : "");
    } else if (mode === "range") {
      p.html = "What are the <strong>first and last usable host</strong> addresses in " + cidr + "?";
      p.fields = ["first usable", "last usable"];
      p.answer = [toDotted(firstUsable(ip, n)), toDotted(lastUsable(ip, n))];
      p.hint = n >= 31 ? "Special case: a /31 (RFC 3021) uses both of its addresses; a /32 is one host." :
        "First usable = network address + 1. Last usable = broadcast address − 1. " + octetHint(n);
    } else if (mode === "hosts") {
      p.html = "How many <strong>usable host addresses</strong> does " + cidr + " have?";
      p.fields = ["usable hosts"]; p.answer = [String(usableHosts(n))];
      p.hint = n >= 31 ? "Special case: a /31 has 2 usable addresses (RFC 3021); a /32 has 1." :
        "There are 32 − " + n + " = " + (32 - n) + " host bits, so 2^" + (32 - n) +
        " addresses. Subtract 2: the network and broadcast addresses can't be given to hosts.";
    } else if (mode === "same") {
      var b = ip;
      if (n > 0 && Math.random() < 0.5) {
        // A near miss: flip one of the last three network bits.
        b = (b ^ (1 << (32 - n + rint(0, Math.min(2, n - 1))))) >>> 0;
      }
      // Keep b's network part, randomise its host bits.
      b = (networkOf(b, n) | (rand32() & ~maskOf(n))) >>> 0;
      p.b = b;
      p.html = "Are <code>" + toDotted(ip) + "/" + n + "</code> and <code>" + toDotted(b) + "/" + n +
        "</code> in the <strong>same subnet</strong>?";
      p.choice = ["Same subnet", "Different subnets"];
      p.answer = [sameSubnet(ip, b, n) ? "Same subnet" : "Different subnets"];
      p.hint = "AND each address with the /" + n + " mask and compare the two network addresses. " + octetHint(n);
    } else if (mode === "mask") {
      if (Math.random() < 0.5) {
        p.html = "Write the prefix <code>/" + n + "</code> as a <strong>dotted-decimal netmask</strong>.";
        p.fields = ["netmask"]; p.answer = [toDotted(maskOf(n))];
        p.hint = "/" + n + " is " + n + " one-bits then " + (32 - n) + " zero-bits. A full octet of ones is 255; " +
          "an octet with b leading ones is 256 − 2^(8−b) (128, 192, 224, 240, 248, 252, 254).";
      } else {
        p.html = "<code>ifconfig</code> says <code>netmask " + toHex(maskOf(n)) + "</code>. What is the <strong>prefix length</strong>?";
        p.fields = ["prefix length (/n)"]; p.answer = [String(n)]; p.hexToPrefix = true;
        p.hint = "Each hex digit is 4 bits: f = 1111 (4 ones), e = 1110 (3), c = 1100 (2), 8 = 1000 (1), 0 = 0. Add up the ones.";
      }
    }
    return p;
  }

  function isCorrect(p, i, value) {
    if (p.choice) return value === p.answer[0];
    if (p.mode === "hosts" || p.hexToPrefix) return parseCount(value) === Number(p.answer[i]);
    var v = parseDotted(value);
    return v !== null && v === parseDotted(p.answer[i]);
  }

  if (typeof module !== "undefined" && module.exports) {
    module.exports = { maskOf: maskOf, networkOf: networkOf, broadcastOf: broadcastOf, usableHosts: usableHosts,
      firstUsable: firstUsable, lastUsable: lastUsable, sameSubnet: sameSubnet, toDotted: toDotted, toHex: toHex,
      parseDotted: parseDotted, parseCount: parseCount, toBinary: toBinary, makeProblem: makeProblem, isCorrect: isCorrect };
  }
  if (typeof document === "undefined") return;

  // ---------- Rendering ----------
  document.querySelectorAll(".subnet-trainer").forEach(function (el) {
    var modes = (el.dataset.modes || ALL.join(",")).split(",").map(function (s) { return s.trim(); })
      .filter(function (m) { return ALL.indexOf(m) !== -1; });
    if (!modes.length) modes = ALL.slice();
    var lo = Math.max(0, Math.min(32, el.dataset.minPrefix !== undefined ? +el.dataset.minPrefix : 8));
    var hi = Math.max(lo, Math.min(32, el.dataset.maxPrefix !== undefined ? +el.dataset.maxPrefix : 30));
    var score = { first: 0, total: 0 }, p, attempts, done;

    var picker = modes.length > 1 ? '<label style="display:flex;grid-template-columns:none;gap:.5rem;margin:0 0 .8rem">Practise ' +
      '<select data-r="mode"><option value="">Mixed</option>' + modes.map(function (m) {
        return '<option value="' + m + '">' + LABELS[m] + "</option>"; }).join("") + "</select></label>" : "";
    el.innerHTML = picker +
      '<p data-r="q" style="margin:0 0 .6rem;font-family:var(--serif);font-size:1rem"></p>' +
      '<form data-r="form" style="display:flex;flex-wrap:wrap;gap:.5rem;align-items:center" autocomplete="off"></form>' +
      '<p class="feedback" data-r="fb" aria-live="polite" style="min-height:1.3em;margin:.6rem 0 .3rem"></p>' +
      '<pre data-r="work" style="display:none;font-size:.78rem;margin:.4rem 0;background:var(--bg)"></pre>' +
      '<div style="display:flex;flex-wrap:wrap;gap:.5rem;align-items:center">' +
      '<button type="button" class="btn" data-r="show" style="display:none">Show answer</button>' +
      '<button type="button" class="btn" data-r="next">Next problem</button>' +
      '<span data-r="score" style="color:var(--muted);font-size:.8rem;margin-left:auto"></span></div>';
    function r(name) { return el.querySelector('[data-r="' + name + '"]'); }

    function renderScore() {
      r("score").textContent = score.total ? "First try: " + score.first + " / " + score.total : "";
    }
    function work() {
      if (p.mode === "mask") {
        return "/" + p.n + "  = " + toBinary(maskOf(p.n), p.n) + "\n       = " + toDotted(maskOf(p.n)) + " = " + toHex(maskOf(p.n));
      }
      var lines = ["address   " + toBinary(p.ip, p.n) + "  " + toDotted(p.ip),
        "mask /" + (p.n < 10 ? p.n + " " : p.n) + "  " + toBinary(maskOf(p.n), p.n) + "  " + toDotted(maskOf(p.n)),
        "network   " + toBinary(networkOf(p.ip, p.n), p.n) + "  " + toDotted(networkOf(p.ip, p.n))];
      if (p.mode === "same") {
        lines.push("", "address   " + toBinary(p.b, p.n) + "  " + toDotted(p.b),
          "network   " + toBinary(networkOf(p.b, p.n), p.n) + "  " + toDotted(networkOf(p.b, p.n)));
      } else if (p.n <= 30) {
        lines.push("broadcast " + toBinary(broadcastOf(p.ip, p.n), p.n) + "  " + toDotted(broadcastOf(p.ip, p.n)));
      } else {
        lines.push("(a /" + p.n + " has no broadcast address)");
      }
      return lines.join("\n");
    }
    function finish(correct, revealed) {
      done = true;
      if (attempts === 1 && correct) score.first++;
      score.total++;
      renderScore();
      r("show").style.display = "none";
      r("work").textContent = work();
      r("work").style.display = "block";
      el.querySelectorAll("form input, form button").forEach(function (x) { x.disabled = true; });
      var fb = r("fb");
      if (revealed) { fb.className = "feedback bad"; fb.textContent = "Answer: " + p.answer.join(" – ") + ". Study the working below, then try another."; }
      else { fb.className = "feedback ok"; fb.textContent = "✓ Correct" + (attempts === 1 ? ", first try." : "."); }
      r("next").focus();
    }
    function check(values) {
      if (done) return;
      attempts++;
      var wrong = values.map(function (v, i) { return !isCorrect(p, i, v); });
      if (wrong.indexOf(true) === -1) return finish(true, false);
      var fb = r("fb");
      fb.className = "feedback bad";
      var which = p.fields && p.fields.length > 1 ? " (" + p.fields.filter(function (f, i) { return wrong[i]; }).join(" and ") + ")" : "";
      fb.textContent = "✗ Not quite" + which + ". Hint: " + p.hint;
      r("show").style.display = "";
    }
    function next() {
      var sel = r("mode"), mode = sel && sel.value ? sel.value : modes[rint(0, modes.length - 1)];
      if (mode === "broadcast" && lo > 30) mode = "network";
      p = makeProblem(mode, lo, hi); attempts = 0; done = false;
      r("q").innerHTML = p.html;
      var form = r("form"), html = "";
      if (p.choice) {
        html = p.choice.map(function (c) { return '<button type="button" class="btn" style="margin:0" data-choice="' + c + '">' + c + "</button>"; }).join("");
      } else {
        html = p.fields.map(function (f) {
          return '<input type="text" spellcheck="false" inputmode="' + (p.mode === "hosts" || p.hexToPrefix ? "numeric" : "decimal") +
            '" placeholder="' + f + '" aria-label="' + f + '" style="font-family:var(--mono);width:11rem">';
        }).join("") + '<button type="submit" class="btn" style="margin:0">Check</button>';
      }
      form.innerHTML = html;
      form.querySelectorAll("[data-choice]").forEach(function (b) {
        b.addEventListener("click", function () { b.disabled = true; check([b.dataset.choice]); });
      });
      r("fb").textContent = ""; r("fb").className = "feedback";
      r("work").style.display = "none";
      r("show").style.display = "none";
      var first = form.querySelector("input");
      if (first && el.dataset.started) first.focus();
      el.dataset.started = "1";
    }
    r("form").addEventListener("submit", function (e) {
      e.preventDefault();
      check(Array.prototype.map.call(r("form").querySelectorAll("input"), function (i) { return i.value; }));
    });
    r("show").addEventListener("click", function () { finish(false, true); });
    r("next").addEventListener("click", next);
    if (r("mode")) r("mode").addEventListener("change", next);
    next();
  });
})();
