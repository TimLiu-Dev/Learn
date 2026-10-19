// Hands-on steps component: a numbered checklist that remembers progress.
//
// Markup:
//   <ol class="steps" data-key="0001-ping"> <li>Step…</li> … </ol>
//   <script src="../assets/steps.js"></script>
//
// data-key must be unique across the course; progress is kept in this
// browser only (localStorage), and the page works fine without it.
(function () {
  function load(key) {
    try { return JSON.parse(localStorage.getItem("steps:" + key)) || []; } catch (e) { return []; }
  }
  function save(key, done) {
    try { localStorage.setItem("steps:" + key, JSON.stringify(done)); } catch (e) {}
  }
  document.querySelectorAll("ol.steps").forEach(function (list) {
    var key = list.dataset.key || location.pathname;
    var done = load(key);
    list.querySelectorAll(":scope > li").forEach(function (li, i) {
      var btn = document.createElement("button");
      btn.type = "button";
      btn.className = "step-toggle";
      function render() {
        var on = done.indexOf(i) !== -1;
        li.classList.toggle("done", on);
        btn.textContent = on ? "undo" : "done";
        btn.setAttribute("aria-pressed", on);
      }
      btn.addEventListener("click", function () {
        var at = done.indexOf(i);
        if (at === -1) done.push(i); else done.splice(at, 1);
        save(key, done);
        render();
      });
      li.insertBefore(btn, li.firstChild);
      render();
    });
  });
})();
