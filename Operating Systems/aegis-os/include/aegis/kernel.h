#ifndef AEGIS_KERNEL_H
#define AEGIS_KERNEL_H

#include "aegis/types.h"

/* ---------------- UART ---------------- */
void uart_init(void);
void uart_putc(char c);
void uart_puts(const char *s);
void uart_put_hex(uint64_t val);

/* ---------------- Trap & Syscalls ---------------- */
#define SYS_WRITE       64
#define SYS_EXIT        93
#define SYS_FUTEX       98
#define SYS_GETPID      172
#define SYS_IPC_SEND    200
#define SYS_IPC_RECV    201
#define SYS_IO_RING_SUB 202
#define AEGIS_MAX_SYSCALLS 256

struct pt_regs {
  uint64_t regs[31];
  uint64_t elr;
  uint64_t spsr;
};

struct aegis_uaccess_region {
  uintptr_t base;
  size_t len;
  bool writable;
};

typedef int64_t (*syscall_fn_t)(uint64_t a0, uint64_t a1, uint64_t a2);

struct aegis_task_seccomp {
  bool enforce;
  uint64_t allow_mask[4]; /* 256-bit syscall allowlist */
};

void trap_init(void);
int trap_register_syscall(uint32_t nr, syscall_fn_t fn);
void seccomp_allow(struct aegis_task_seccomp *sc, uint32_t nr);
bool seccomp_is_allowed(const struct aegis_task_seccomp *sc, uint32_t nr);
int copy_from_user_checked(void *kdst, uintptr_t uaddr, size_t len,
                           const struct aegis_uaccess_region *regions, size_t nregions);
int copy_to_user_checked(uintptr_t uaddr, const void *ksrc, size_t len,
                         const struct aegis_uaccess_region *regions, size_t nregions);
int64_t dispatch_syscall(const struct aegis_task_seccomp *sc, uint64_t nr,
                         uint64_t a0, uint64_t a1, uint64_t a2);
void el0_sync_handler(struct pt_regs *regs);

/* ---------------- EEVDF-lite Scheduler ---------------- */
#define AEGIS_MAX_TASKS 16
#define TASK_READY      0
#define TASK_RUNNING    1
#define TASK_BLOCKED    2
#define TASK_ZOMBIE     3

struct eevdf_task {
  uint32_t pid;
  uint32_t weight;
  uint64_t vruntime;
  uint64_t vdeadline;
  uint64_t slice_ns;
  uint8_t  state;
  bool     used;
};

struct eevdf_rq {
  struct eevdf_task tasks[AEGIS_MAX_TASKS];
  uint64_t min_vruntime;
  int      current_idx;
};

void sched_init(struct eevdf_rq *rq);
int  sched_add_task(struct eevdf_rq *rq, uint32_t pid, uint32_t weight, uint64_t slice_ns);
int  sched_pick_next(struct eevdf_rq *rq);
void sched_tick(struct eevdf_rq *rq, uint64_t delta_ns);

/* ---------------- Futex ---------------- */
#define FUTEX_WAIT 0
#define FUTEX_WAKE 1
#define AEGIS_MAX_WAITERS 32

struct futex_waiter {
  uintptr_t uaddr;
  uint32_t  pid;
  bool      active;
};

struct futex_table {
  struct futex_waiter waiters[AEGIS_MAX_WAITERS];
};

void futex_init(struct futex_table *ft);
int  futex_wait(struct futex_table *ft, volatile uint32_t *word, uint32_t expected, uint32_t pid);
int  futex_wake(struct futex_table *ft, uintptr_t word_addr, int max_wake, uint32_t *woken_pids);

/* ---------------- Buddy, Slab & Page Tables (CoW) ---------------- */
#define BUDDY_MAX_ORDER 5 /* 1..32 pages */
#define BUDDY_PAGES     (1U << BUDDY_MAX_ORDER)

struct buddy_alloc {
  uint8_t free_map[BUDDY_PAGES]; /* 1 = head of free block of order_map[i] */
  int8_t  order_map[BUDDY_PAGES];
};

void buddy_init(struct buddy_alloc *b);
int  buddy_alloc_pages(struct buddy_alloc *b, uint32_t order);
void buddy_free_pages(struct buddy_alloc *b, uint32_t pfn, uint32_t order);

#define SLAB_MAX_OBJS 64
struct slab_cache {
  size_t   obj_size;
  uint32_t capacity;
  uint32_t free_count;
  uint32_t free_stack[SLAB_MAX_OBJS];
  uint8_t *backing;
};

int   slab_init(struct slab_cache *sc, void *backing_mem, size_t backing_bytes, size_t obj_size);
void *slab_alloc(struct slab_cache *sc);
int   slab_free(struct slab_cache *sc, void *ptr);

#define PTE_VALID (1ULL << 0)
#define PTE_USER  (1ULL << 1)
#define PTE_WRITE (1ULL << 2)
#define PTE_COW   (1ULL << 3)
#define PTE_EXEC  (1ULL << 4)

#define VM_MAX_VPAGES 64
struct vm_space {
  uint64_t ptes[VM_MAX_VPAGES];
  uint16_t phys_refcount[VM_MAX_VPAGES];
  uint8_t  phys_mem[VM_MAX_VPAGES][64]; /* compact 64B test frames in simulator/kernel */
  uint32_t next_free_frame;
};

void vm_init(struct vm_space *vm);
int  vm_map_page(struct vm_space *vm, uint32_t vpage, uint32_t pframe, uint64_t flags);
int  vm_fork_cow(struct vm_space *parent, struct vm_space *child);
int  vm_handle_write_fault(struct vm_space *vm, uint32_t vpage);

/* ---------------- Binder-Inspired Single-Copy IPC ---------------- */
#define BINDER_POOL_BYTES     (1024U * 1024U)
#define BINDER_ASYNC_CAP      (BINDER_POOL_BYTES / 2U)
#define BINDER_MAX_TX_QUEUE   16
#define BINDER_MAX_FDS        8

struct binder_tx {
  uint32_t from_pid;
  uint32_t to_pid;
  uint32_t code;
  uint32_t data_len;
  uint32_t offset;
  bool     oneway;
  int      fd_count;
  int      fds[BINDER_MAX_FDS];
  uint8_t  preview[64];
};

struct binder_proc {
  uint32_t pid;
  uint32_t used_bytes;
  uint32_t async_used_bytes;
  uint32_t q_head;
  uint32_t q_tail;
  uint32_t q_count;
  struct binder_tx queue[BINDER_MAX_TX_QUEUE];
  int      fd_table[BINDER_MAX_FDS]; /* global object ID or -1 */
};

void binder_proc_init(struct binder_proc *p, uint32_t pid);
int  binder_send(struct binder_proc *src, struct binder_proc *dst, uint32_t code,
                 const void *data, uint32_t len, bool oneway, const int *src_fds, int n_fds);
int  binder_recv(struct binder_proc *dst, struct binder_tx *out_tx);
void binder_free_buf(struct binder_proc *dst, const struct binder_tx *tx);

/* ---------------- VFS & RamFS ---------------- */
#define VFS_MAX_INODES 32
#define VFS_MAX_NAME   32
#define VFS_FILE_CAP   256

#define INODE_FREE 0
#define INODE_FILE 1
#define INODE_DIR  2

struct vfs_inode {
  uint8_t  type;
  char     name[VFS_MAX_NAME];
  int      parent_id;
  size_t   size;
  uint8_t  data[VFS_FILE_CAP];
};

struct vfs_fs {
  struct vfs_inode inodes[VFS_MAX_INODES];
};

void    vfs_init(struct vfs_fs *fs);
int     vfs_create(struct vfs_fs *fs, const char *path, uint8_t type);
int     vfs_lookup(const struct vfs_fs *fs, const char *path);
ssize_t vfs_write(struct vfs_fs *fs, int ino, size_t offset, const void *buf, size_t len);
ssize_t vfs_read(const struct vfs_fs *fs, int ino, size_t offset, void *buf, size_t len);

/* ---------------- Async io_uring-style Ring ---------------- */
#define IORING_SIZE 16 /* power of 2 */

#define IORING_OP_NOP   0
#define IORING_OP_READ  1
#define IORING_OP_WRITE 2

struct io_sqe {
  uint8_t  opcode;
  int      ino;
  uint32_t offset;
  uint32_t len;
  uint64_t user_data;
  uint8_t  buf[64];
};

struct io_cqe {
  uint64_t user_data;
  int32_t  res;
  uint8_t  buf[64];
};

struct io_ring {
  uint32_t sq_head;
  uint32_t sq_tail;
  uint32_t cq_head;
  uint32_t cq_tail;
  struct io_sqe sq[IORING_SIZE];
  struct io_cqe cq[IORING_SIZE];
};

void io_ring_init(struct io_ring *r);
int  io_ring_submit_sqe(struct io_ring *r, const struct io_sqe *sqe);
int  io_ring_process(struct io_ring *r, struct vfs_fs *fs, uint32_t max_items);
int  io_ring_pop_cqe(struct io_ring *r, struct io_cqe *out);

#endif /* AEGIS_KERNEL_H */
