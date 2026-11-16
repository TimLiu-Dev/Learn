#include <assert.h>
#include <stdio.h>
#include <string.h>
#include "aegis/kernel.h"

static int64_t mock_sys_add(uint64_t a, uint64_t b, uint64_t c) {
  (void)c;
  return (int64_t)(a + b);
}

static void test_trap_seccomp_uaccess(void) {
  trap_init();
  assert(trap_register_syscall(SYS_WRITE, mock_sys_add) == 0);

  struct aegis_task_seccomp sc = { .enforce = true, .allow_mask = {0} };
  seccomp_allow(&sc, SYS_WRITE);

  assert(dispatch_syscall(&sc, SYS_WRITE, 10, 32, 0) == 42);
  assert(dispatch_syscall(&sc, SYS_GETPID, 0, 0, 0) == -EPERM);
  assert(dispatch_syscall(NULL, 999, 0, 0, 0) == -ENOSYS);

  /* Overflow-safe uaccess checks */
  uint8_t user_buf[16] = "AegisOS-User";
  uint8_t kbuf[16] = {0};
  struct aegis_uaccess_region regions[1] = {
    { .base = (uintptr_t)user_buf, .len = sizeof(user_buf), .writable = true }
  };

  assert(copy_from_user_checked(kbuf, (uintptr_t)user_buf, 12, regions, 1) == 0);
  assert(memcmp(kbuf, "AegisOS-User", 12) == 0);

  /* Kernel-space pointer & integer wraparound CVE tests */
  assert(copy_from_user_checked(kbuf, AEGIS_USER_LIMIT, 4, regions, 1) == -EFAULT);
  assert(copy_from_user_checked(kbuf, AEGIS_USER_LIMIT - 8ULL, (size_t)-1ULL, regions, 1) == -EFAULT);
  assert(copy_from_user_checked(kbuf, (uintptr_t)user_buf + 8, 16, regions, 1) == -EFAULT);

  printf("[PASS] trap + seccomp + overflow-safe uaccess\n");
}

static void test_eevdf_scheduler(void) {
  struct eevdf_rq rq;
  sched_init(&rq);

  int s1 = sched_add_task(&rq, 101, 1024, 4000000ULL);
  int s2 = sched_add_task(&rq, 102, 2048, 2000000ULL); /* higher weight, shorter slice -> earlier deadline */
  assert(s1 >= 0 && s2 >= 0);

  int first = sched_pick_next(&rq);
  assert(first == s2);

  /* Run s2 until its vruntime exceeds s1, then verify s1 gets scheduled */
  for (int i = 0; i < 6; i++) {
    sched_tick(&rq, 1000000ULL);
  }
  int next = sched_pick_next(&rq);
  assert(next == s1);

  printf("[PASS] EEVDF virtual deadline scheduler\n");
}

static void test_futex_wait_wake(void) {
  struct futex_table ft;
  futex_init(&ft);

  volatile uint32_t lock_word = 2; /* contended */
  /* Lost wakeup guard: expected != current -> -11 (-EAGAIN) */
  assert(futex_wait(&ft, &lock_word, 1, 10) == -11);
  assert(futex_wait(&ft, &lock_word, 2, 10) == 0);
  assert(futex_wait(&ft, &lock_word, 2, 11) == 0);

  uint32_t woken[2] = {0};
  assert(futex_wake(&ft, (uintptr_t)&lock_word, 1, woken) == 1);
  assert(woken[0] == 10);
  assert(futex_wake(&ft, (uintptr_t)&lock_word, 4, woken) == 1);
  assert(woken[0] == 11);

  printf("[PASS] futex wait/wake lost-wakeup protection\n");
}

static void test_mm_buddy_slab_cow(void) {
  /* Buddy split + XOR coalesce back to full order 5 */
  struct buddy_alloc b;
  buddy_init(&b);
  int p0 = buddy_alloc_pages(&b, 2); /* 4 pages at pfn 0 */
  int p1 = buddy_alloc_pages(&b, 2); /* 4 pages at pfn 4 */
  assert(p0 == 0 && p1 == 4);
  buddy_free_pages(&b, (uint32_t)p0, 2);
  buddy_free_pages(&b, (uint32_t)p1, 2);
  int full = buddy_alloc_pages(&b, BUDDY_MAX_ORDER);
  assert(full == 0);

  /* SLUB object cache */
  uint8_t backing[128 * 8];
  struct slab_cache sc;
  assert(slab_init(&sc, backing, sizeof(backing), 128) == 0);
  void *objs[8];
  for (int i = 0; i < 8; i++) {
    objs[i] = slab_alloc(&sc);
    assert(objs[i] != NULL);
  }
  assert(slab_alloc(&sc) == NULL);
  assert(slab_free(&sc, objs[3]) == 0);
  assert(slab_alloc(&sc) == objs[3]);

  /* Copy-on-Write fork & fault isolation */
  struct vm_space parent, child;
  vm_init(&parent);
  assert(vm_map_page(&parent, 0, 0, PTE_USER | PTE_WRITE) == 0);
  memcpy(parent.phys_mem[0], "SHARED_DATA", 11);

  assert(vm_fork_cow(&parent, &child) == 0);
  assert((parent.ptes[0] & PTE_COW) && !(parent.ptes[0] & PTE_WRITE));
  assert((child.ptes[0] & PTE_COW) && !(child.ptes[0] & PTE_WRITE));
  assert(child.phys_refcount[0] == 2);

  assert(vm_handle_write_fault(&child, 0) == 0);
  assert((child.ptes[0] & PTE_WRITE) && !(child.ptes[0] & PTE_COW));
  uint32_t child_pfn = (uint32_t)(child.ptes[0] >> 12);
  assert(child_pfn != 0);
  child.phys_mem[child_pfn][0] = 'X';
  assert(parent.phys_mem[0][0] == 'S'); /* parent isolated */

  printf("[PASS] buddy + slab + CoW page fault\n");
}

static void test_binder_ipc(void) {
  struct binder_proc client, server;
  binder_proc_init(&client, 100);
  binder_proc_init(&server, 200);

  /* Client exports a DMA-BUF object ID 77 at local fd 2 */
  client.fd_table[2] = 77;
  int export_fds[1] = { 2 };
  const char payload[] = "BINDER_RPC";

  assert(binder_send(&client, &server, 1, payload, sizeof(payload), false, export_fds, 1) == 0);
  assert(binder_send(&client, &server, 2, NULL, BINDER_POOL_BYTES + 1, false, NULL, 0) == -E2BIG);
  assert(binder_send(&client, &server, 3, NULL, BINDER_ASYNC_CAP + 1, true, NULL, 0) == -ENOSPC);

  struct binder_tx rx;
  assert(binder_recv(&server, &rx) == 0);
  assert(rx.from_pid == 100 && rx.fd_count == 1);
  assert(server.fd_table[rx.fds[0]] == 77);
  assert(memcmp(rx.preview, "BINDER_RPC", 10) == 0);
  binder_free_buf(&server, &rx);
  assert(server.used_bytes == 0);

  printf("[PASS] Binder 1-copy IPC + 50%% async cap + FD translation\n");
}

static void test_vfs_and_io_ring(void) {
  struct vfs_fs fs;
  vfs_init(&fs);
  assert(vfs_create(&fs, "/data", INODE_DIR) > 0);
  int ino = vfs_create(&fs, "/data/log.txt", INODE_FILE);
  assert(ino > 0);
  assert(vfs_lookup(&fs, "/data/log.txt") == ino);
  assert(vfs_lookup(&fs, "/data/missing") == -ENOENT);

  struct io_ring ring;
  io_ring_init(&ring);

  struct io_sqe wsqe = { .opcode = IORING_OP_WRITE, .ino = ino, .offset = 0, .len = 9, .user_data = 1 };
  memcpy(wsqe.buf, "RING_DATA", 9);
  assert(io_ring_submit_sqe(&ring, &wsqe) == 0);

  struct io_sqe rsqe = { .opcode = IORING_OP_READ, .ino = ino, .offset = 0, .len = 9, .user_data = 2 };
  assert(io_ring_submit_sqe(&ring, &rsqe) == 0);

  assert(io_ring_process(&ring, &fs, 4) == 2);
  struct io_cqe c1, c2;
  assert(io_ring_pop_cqe(&ring, &c1) == 0 && c1.user_data == 1 && c1.res == 9);
  assert(io_ring_pop_cqe(&ring, &c2) == 0 && c2.user_data == 2 && c2.res == 9);
  assert(memcmp(c2.buf, "RING_DATA", 9) == 0);

  printf("[PASS] VFS + io_uring SQ/CQ async ring\n");
}

int main(void) {
  test_trap_seccomp_uaccess();
  test_eevdf_scheduler();
  test_futex_wait_wake();
  test_mm_buddy_slab_cow();
  test_binder_ipc();
  test_vfs_and_io_ring();
  printf("ALL_AEGIS_HOST_TESTS_PASSED\n");
  return 0;
}
