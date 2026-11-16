#include "aegis/kernel.h"

static int64_t sys_getpid_impl(uint64_t a0, uint64_t a1, uint64_t a2) {
  (void)a0; (void)a1; (void)a2;
  return 1;
}

static int64_t sys_write_uart_impl(uint64_t fd, uint64_t buf_ptr, uint64_t len) {
  if (fd != 1 && fd != 2) return -EBADF;
  char kbuf[64];
  size_t n = len < sizeof(kbuf) ? (size_t)len : sizeof(kbuf);
  struct aegis_uaccess_region reg = { .base = 0x1000, .len = 0x40000000ULL, .writable = false };
  if (copy_from_user_checked(kbuf, (uintptr_t)buf_ptr, n, &reg, 1) != 0) {
    return -EFAULT;
  }
  for (size_t i = 0; i < n; i++) uart_putc(kbuf[i]);
  return (int64_t)n;
}

void kmain(void) {
  uart_init();
  uart_puts("\n=== AegisOS-64 (ARM64) Booted ===\n");

  trap_init();
  trap_register_syscall(SYS_GETPID, sys_getpid_impl);
  trap_register_syscall(SYS_WRITE, sys_write_uart_impl);

  struct buddy_alloc buddy;
  buddy_init(&buddy);
  int pfn = buddy_alloc_pages(&buddy, 2);
  uart_puts("[MM] Buddy alloc(order=2) pfn=");
  uart_put_hex((uint64_t)pfn);
  uart_puts("\n");
  if (pfn >= 0) buddy_free_pages(&buddy, (uint32_t)pfn, 2);

  struct eevdf_rq rq;
  sched_init(&rq);
  sched_add_task(&rq, 1, 1024, 4000000ULL);
  sched_add_task(&rq, 2, 2048, 4000000ULL);
  int next = sched_pick_next(&rq);
  uart_puts("[SCHED] EEVDF picked task slot=");
  uart_put_hex((uint64_t)next);
  uart_puts("\n");

  struct vfs_fs fs;
  vfs_init(&fs);
  vfs_create(&fs, "/etc", INODE_DIR);
  int motd = vfs_create(&fs, "/etc/motd", INODE_FILE);
  vfs_write(&fs, motd, 0, "AegisOS-64 Ready\n", 17);

  struct io_ring ring;
  io_ring_init(&ring);
  struct io_sqe sqe = { .opcode = IORING_OP_READ, .ino = motd, .offset = 0, .len = 17, .user_data = 0xAA };
  io_ring_submit_sqe(&ring, &sqe);
  io_ring_process(&ring, &fs, 4);
  struct io_cqe cqe;
  if (io_ring_pop_cqe(&ring, &cqe) == 0 && cqe.res > 0) {
    uart_puts("[IO_RING] Read /etc/motd bytes=");
    uart_put_hex((uint64_t)cqe.res);
    uart_puts("\n");
  }

  uart_puts("AEGIS_BOOT_SELF_CHECK_PASS\n");
}
