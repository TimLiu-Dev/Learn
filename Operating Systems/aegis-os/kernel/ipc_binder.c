#include "aegis/kernel.h"

void binder_proc_init(struct binder_proc *p, uint32_t pid) {
  p->pid = pid;
  p->used_bytes = 0;
  p->async_used_bytes = 0;
  p->q_head = 0;
  p->q_tail = 0;
  p->q_count = 0;
  for (int i = 0; i < BINDER_MAX_FDS; i++) {
    p->fd_table[i] = -1;
  }
}

static int alloc_dst_fd(struct binder_proc *dst, int global_obj) {
  for (int i = 0; i < BINDER_MAX_FDS; i++) {
    if (dst->fd_table[i] == -1) {
      dst->fd_table[i] = global_obj;
      return i;
    }
  }
  return -ENOMEM;
}

int binder_send(struct binder_proc *src, struct binder_proc *dst, uint32_t code,
                const void *data, uint32_t len, bool oneway, const int *src_fds, int n_fds) {
  if (!src || !dst) return -EINVAL;
  if (len > BINDER_POOL_BYTES) return -E2BIG;
  if (dst->q_count >= BINDER_MAX_TX_QUEUE) return -ENOSPC;
  if (n_fds < 0 || n_fds > BINDER_MAX_FDS) return -EINVAL;
  if (dst->used_bytes + len > BINDER_POOL_BYTES) return -ENOSPC;
  if (oneway && (dst->async_used_bytes + len > BINDER_ASYNC_CAP)) return -ENOSPC;

  struct binder_tx *slot = &dst->queue[dst->q_tail];
  slot->from_pid = src->pid;
  slot->to_pid = dst->pid;
  slot->code = code;
  slot->data_len = len;
  slot->offset = dst->used_bytes;
  slot->oneway = oneway;
  slot->fd_count = 0;

  /* Translate exported FDs (e.g., memfd / DMA-BUF) into target process's FD table */
  for (int i = 0; i < n_fds; i++) {
    int sfd = src_fds[i];
    if (sfd < 0 || sfd >= BINDER_MAX_FDS || src->fd_table[sfd] == -1) {
      /* Roll back any FDs already installed for this failed tx */
      for (int r = 0; r < slot->fd_count; r++) {
        dst->fd_table[slot->fds[r]] = -1;
      }
      return -EBADF;
    }
    int dfd = alloc_dst_fd(dst, src->fd_table[sfd]);
    if (dfd < 0) {
      for (int r = 0; r < slot->fd_count; r++) {
        dst->fd_table[slot->fds[r]] = -1;
      }
      return -ENOMEM;
    }
    slot->fds[slot->fd_count++] = dfd;
  }

  /* Single copy: user payload -> target kernel mmap region */
  uint32_t copy_n = len < sizeof(slot->preview) ? len : (uint32_t)sizeof(slot->preview);
  const uint8_t *bytes = (const uint8_t *)data;
  for (uint32_t i = 0; i < copy_n; i++) {
    slot->preview[i] = bytes ? bytes[i] : 0;
  }

  dst->used_bytes += len;
  if (oneway) dst->async_used_bytes += len;
  dst->q_tail = (dst->q_tail + 1U) % BINDER_MAX_TX_QUEUE;
  dst->q_count++;
  return 0;
}

int binder_recv(struct binder_proc *dst, struct binder_tx *out_tx) {
  if (!dst || !out_tx) return -EINVAL;
  if (dst->q_count == 0) return -ENOENT;
  *out_tx = dst->queue[dst->q_head];
  dst->q_head = (dst->q_head + 1U) % BINDER_MAX_TX_QUEUE;
  dst->q_count--;
  return 0;
}

void binder_free_buf(struct binder_proc *dst, const struct binder_tx *tx) {
  if (!dst || !tx) return;
  if (dst->used_bytes >= tx->data_len) dst->used_bytes -= tx->data_len;
  else dst->used_bytes = 0;
  if (tx->oneway) {
    if (dst->async_used_bytes >= tx->data_len) dst->async_used_bytes -= tx->data_len;
    else dst->async_used_bytes = 0;
  }
}
