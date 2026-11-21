/**
 * Shared milestone checklist: persists checkbox state in localStorage and updates a progress label.
 * Usage: <input type="checkbox" class="ms-check" data-key="unique-key"> ... <span id="ms-progress"></span>
 *        <script src="../assets/checklist.js"></script>
 */
(function () {
  const boxes = Array.from(document.querySelectorAll('input.ms-check'));
  const label = document.getElementById('ms-progress');
  const prefix = 'checklist_' + window.location.pathname + '_';

  function update() {
    if (!label) return;
    const done = boxes.filter(b => b.checked).length;
    label.textContent = done + ' of ' + boxes.length + ' done';
  }

  boxes.forEach(b => {
    b.checked = localStorage.getItem(prefix + b.dataset.key) === '1';
    b.addEventListener('change', () => {
      localStorage.setItem(prefix + b.dataset.key, b.checked ? '1' : '0');
      update();
    });
  });
  update();
})();
