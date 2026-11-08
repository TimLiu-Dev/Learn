// Mobile System Design 2027 - Course Navigation & Progress Tracker
document.addEventListener('DOMContentLoaded', () => {
  // Highlight active page in navigation
  const currentPath = window.location.pathname.split('/').pop() || 'index.html';
  const navLinks = document.querySelectorAll('.nav-link');

  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    if (href && href.includes(currentPath)) {
      link.classList.add('active');
    }
  });

  // Track lesson completion in localStorage
  const markCompleteBtn = document.getElementById('btn-mark-lesson-complete');
  if (markCompleteBtn) {
    const lessonKey = `lesson_completed_${currentPath}`;
    if (localStorage.getItem(lessonKey) === 'true') {
      markCompleteBtn.textContent = '✓ Completed';
      markCompleteBtn.classList.replace('btn-primary', 'btn-outline');
    }

    markCompleteBtn.addEventListener('click', () => {
      const isDone = localStorage.getItem(lessonKey) === 'true';
      if (!isDone) {
        localStorage.setItem(lessonKey, 'true');
        markCompleteBtn.textContent = '✓ Completed';
        markCompleteBtn.classList.replace('btn-primary', 'btn-outline');
      } else {
        localStorage.removeItem(lessonKey);
        markCompleteBtn.textContent = 'Mark Lesson Complete';
        markCompleteBtn.classList.replace('btn-outline', 'btn-primary');
      }
    });
  }
});
