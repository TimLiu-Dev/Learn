#include "aegis/kernel.h"

static syscall_fn_t g_syscall_table[AEGIS_MAX_SYSCALLS];

void trap_init(void) {
  for (uint32_t i = 0; i < AEGIS_MAX_SYSCALLS; i++) {
    g_syscall_table[i] = NULL;
  }
}

int trap_register_syscall(uint32_t nr, syscall_fn_t fn) {
  if (nr >= AEGIS_MAX_SYSCALLS) return -EINVAL;
  g_syscall_table[nr] = fn;
  return 0;
}

void seccomp_allow(struct aegis_task_seccomp *sc, uint32_t nr) {
  if (!sc || nr >= AEGIS_MAX_SYSCALLS) return;
  sc->allow_mask[nr >> 6] |= (1ULL << (nr & 63U));
}

bool seccomp_is_allowed(const struct aegis_task_seccomp *sc, uint32_t nr) {
  if (!sc || !sc->enforce) return true;
  if (nr >= AEGIS_MAX_SYSCALLS) return false;
  return (sc->allow_mask[nr >> 6] & (1ULL << (nr & 63U))) != 0ULL;
}

static bool uaccess_range_valid(uintptr_t uaddr, size_t len, bool write_needed,
                                const struct aegis_uaccess_region *regions, size_t nregions) {
  if (uaddr >= AEGIS_USER_LIMIT) return false;
  if (len > (size_t)(AEGIS_USER_LIMIT - uaddr)) return false; /* overflow-safe */
  if (len == 0) return true;
  if (!regions || nregions == 0) return false;

  uintptr_t end = uaddr + len;
  for (size_t i = 0; i < nregions; i++) {
    uintptr_t rbase = regions[i].base;
    uintptr_t rend = rbase + regions[i].len;
    if (uaddr >= rbase && end <= rend) {
      if (write_needed && !regions[i].writable) return false;
      return true;
    }
  }
  return false;
}

int copy_from_user_checked(void *kdst, uintptr_t uaddr, size_t len,
                           const struct aegis_uaccess_region *regions, size_t nregions) {
  if (!kdst && len > 0) return -EINVAL;
  if (!uaccess_range_valid(uaddr, len, false, regions, nregions)) return -EFAULT;

  uint8_t *dst = (uint8_t *)kdst;
  const volatile uint8_t *src = (const volatile uint8_t *)uaddr;
  for (size_t i = 0; i < len; i++) {
    dst[i] = src[i];
  }
  return 0;
}

int copy_to_user_checked(uintptr_t uaddr, const void *ksrc, size_t len,
                         const struct aegis_uaccess_region *regions, size_t nregions) {
  if (!ksrc && len > 0) return -EINVAL;
  if (!uaccess_range_valid(uaddr, len, true, regions, nregions)) return -EFAULT;

  volatile uint8_t *dst = (volatile uint8_t *)uaddr;
  const uint8_t *src = (const uint8_t *)ksrc;
  for (size_t i = 0; i < len; i++) {
    dst[i] = src[i];
  }
  return 0;
}

int64_t dispatch_syscall(const struct aegis_task_seccomp *sc, uint64_t nr,
                         uint64_t a0, uint64_t a1, uint64_t a2) {
  if (nr >= AEGIS_MAX_SYSCALLS) return -ENOSYS;
  uint32_t snr = (uint32_t)nr;
  if (!seccomp_is_allowed(sc, snr)) return -EPERM;
  if (!g_syscall_table[snr]) return -ENOSYS;
  return g_syscall_table[snr](a0, a1, a2);
}

void el0_sync_handler(struct pt_regs *regs) {
#ifdef AEGIS_HOST_TEST
  if (!regs) return;
  regs->regs[0] = (uint64_t)dispatch_syscall(NULL, regs->regs[8],
                                             regs->regs[0], regs->regs[1], regs->regs[2]);
#else
  uint64_t esr = 0;
  __asm__ volatile("mrs %0, esr_el1" : "=r"(esr));
  uint32_t ec = (uint32_t)((esr >> 26) & 0x3FULL);
  if (ec == 0x15U) { /* SVC from AArch64 EL0 */
    regs->regs[0] = (uint64_t)dispatch_syscall(NULL, regs->regs[8],
                                               regs->regs[0], regs->regs[1], regs->regs[2]);
  } else {
    uart_puts("[TRAP] Unhandled sync exception ec=");
    uart_put_hex(ec);
    uart_puts("\n");
    regs->regs[0] = (uint64_t)(-EFAULT);
  }
#endif
}
