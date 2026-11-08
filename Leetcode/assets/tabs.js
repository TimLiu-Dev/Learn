// Synchronized trilingual tabs across the lesson
document.addEventListener("DOMContentLoaded", function () {
  function switchLang(lang) {
    document.querySelectorAll(".lang-tabs").forEach(function (group) {
      group.querySelectorAll(".tab-btn").forEach(function (btn) {
        btn.classList.toggle("active", btn.getAttribute("data-lang") === lang);
      });
      group.querySelectorAll(".tab-pane").forEach(function (pane) {
        pane.classList.toggle("active", pane.getAttribute("data-lang") === lang);
      });
    });
    try {
      localStorage.setItem("dsa_preferred_lang", lang);
    } catch (e) {}
  }

  var savedLang = "kotlin";
  try {
    savedLang = localStorage.getItem("dsa_preferred_lang") || "kotlin";
  } catch (e) {}

  document.querySelectorAll(".lang-tabs").forEach(function (group) {
    group.querySelectorAll(".tab-btn").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var lang = this.getAttribute("data-lang");
        switchLang(lang);
      });
    });
  });

  switchLang(savedLang);
});
