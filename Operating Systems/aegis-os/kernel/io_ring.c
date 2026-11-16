#include "aegis/kernel.h"

void io_ring_init(struct io_ring *r) {
  r->sq_head = 0;
  r->sq_tail = 0;
  r->cq_head = 0;
  r->cq_tail = 0;
}

int io_ring_submit_sqe(struct io_ring *r, const struct io_sqe *sqe) {
  if (!r || !sqe) return -EINVAL;
  if ((uint32_t)(r->sq_tail - r->sq_head) >= IORING_SIZE) return -ENOSPC;
  uint32_t slot = r->sq_tail & (IORING_SIZE - 1U);
  r->sq[slot] = *sqe;
  __atomic_store_n(&r->sq_tail, r->sq_tail + 1U, __ATOMIC_RELEASE);
  return 0;
}

int io_ring_process(struct io_ring *r, struct vfs_fs *fs, uint32_t max_items) {
  if (!r || !fs) return -EINVAL;
  uint32_t done = 0;
  while (done < max_items) {
    uint32_t sq_tail = __atomic_load_n(&r->sq_tail, __ATOMIC_ACQUIRE);
    if (r->sq_head == sq_tail) break;
    if ((uint32_t)(r->cq_tail - r->cq_head) >= IORING_SIZE) break;

    struct io_sqe sqe = r->sq[r->sq_head & (IORING_SIZE - 1U)];
    __atomic_store_n(&r->sq_head, r->sq_head + 1U, __ATOMIC_RELEASE);

    struct io_cqe cqe;
    cqe.user_data = sqe.user_data;
    if (sqe.opcode == IORING_OP_NOP) {
      cqe.res = 0;
    } else if (sqe.opcode == IORING_OP_WRITE) {
      size_t n = sqe.len < sizeof(sqe.buf) ? sqe.len : sizeof(sqe.buf);
      cqe.res = (int32_t)vfs_write(fs, sqe.ino, sqe.offset, sqe.buf, n);
    } else if (sqe.opcode == IORING_OP_READ) {
      size_t n = sqe.len < sizeof(cqe.buf) ? sqe.len : sizeof(cqe.buf);
      cqe.res = (int32_t)vfs_read(fs, sqe.ino, sqe.offset, cqe.buf, n);
    } else {
      cqe.res = -EINVAL;
    }

    r->cq[r->cq_tail & (IORING_SIZE - 1U)] = cqe;
    __atomic_store_n(&r->cq_tail, r->cq_tail + 1U, __ATOMIC_RELEASE);
    done++;
  }
  return (int)done;
}

int io_ring_pop_cqe(struct io_ring *r, struct io_cqe *out) {
  if (!r || !out) return -EINVAL;
  uint32_t cq_tail = __atomic_load_n(&r->cq_tail, __ATOMIC_ACQUIRE);
  if (r->cq_head == cq_tail) return -ENOENT;
  *out = r->cq[r->cq_head & (IORING_SIZE - 1U)];
  __atomic_store_n(&r->cq_head, r->cq_head + 1U, __ATOMIC_RELEASE);
  return 0;
}
