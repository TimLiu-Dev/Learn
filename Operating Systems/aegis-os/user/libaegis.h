#ifndef AEGIS_LIBAEGIS_H
#define AEGIS_LIBAEGIS_H

#include "aegis/types.h"

static inline long aegis_syscall3(long nr, long a0, long a1, long a2) {
  register long x0 __asm__("x0") = a0;
  register long x1 __asm__("x1") = a1;
  register long x2 __asm__("x2") = a2;
  register long x8 __asm__("x8") = nr;
  __asm__ volatile("svc #0" : "+r"(x0) : "r"(x1), "r"(x2), "r"(x8) : "memory");
  return x0;
}

static inline long sys_write(int fd, const void *buf, size_t len) {
  return aegis_syscall3(64, fd, (long)buf, (long)len);
}

static inline long sys_getpid(void) {
  return aegis_syscall3(172, 0, 0, 0);
}

static inline void sys_exit(int code) {
  aegis_syscall3(93, code, 0, 0);
  for (;;) {}
}

#endif /* AEGIS_LIBAEGIS_H */
