// Hint ladder: reveals one hint at a time, never a full solution.
// Markup: <div class="hints"><ol><li>nudge</li><li>direction</li><li>key insight</li></ol></div>
document.querySelectorAll(".hints").forEach((box) => {
  const items = [...box.querySelectorAll("li")];
  const button = document.createElement("button");
  button.type = "button";
  let shown = 0;
  const label = () => {
    button.textContent = shown < items.length
      ? `Show hint ${shown + 1} of ${items.length}`
      : "No more hints — ask your teacher";
    button.disabled = shown >= items.length;
  };
  button.addEventListener("click", () => {
    items[shown++].classList.add("shown");
    label();
  });
  label();
  box.prepend(button);
});
