#include "aegis/kernel.h"

void futex_init(struct futex_table *ft) {
  for (int i = 0; i < AEGIS_MAX_WAITERS; i++) {
    ft->waiters[i].active = false;
    ft->waiters[i].uaddr = 0;
    ft->waiters[i].pid = 0;
  }
}

int futex_wait(struct futex_table *ft, volatile uint32_t *word, uint32_t expected, uint32_t pid) {
  if (!ft || !word) return -EINVAL;
  /* Re-check word atomically before sleeping to prevent lost wakeups */
  uint32_t cur = __atomic_load_n(word, __ATOMIC_SEQ_CST);
  if (cur != expected) {
    return -11; /* -EAGAIN: value changed before sleep */
  }
  for (int i = 0; i < AEGIS_MAX_WAITERS; i++) {
    if (!ft->waiters[i].active) {
      ft->waiters[i].active = true;
      ft->waiters[i].uaddr = (uintptr_t)word;
      ft->waiters[i].pid = pid;
      return 0;
    }
  }
  return -ENOMEM;
}

int futex_wake(struct futex_table *ft, uintptr_t word_addr, int max_wake, uint32_t *woken_pids) {
  if (!ft || max_wake <= 0) return 0;
  int woken = 0;
  for (int i = 0; i < AEGIS_MAX_WAITERS && woken < max_wake; i++) {
    if (ft->waiters[i].active && ft->waiters[i].uaddr == word_addr) {
      if (woken_pids) woken_pids[woken] = ft->waiters[i].pid;
      ft->waiters[i].active = false;
      woken++;
    }
  }
  return woken;
}
