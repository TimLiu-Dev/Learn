#include "aegis/kernel.h"

void sched_init(struct eevdf_rq *rq) {
  rq->min_vruntime = 0;
  rq->current_idx = -1;
  for (int i = 0; i < AEGIS_MAX_TASKS; i++) {
    rq->tasks[i].used = false;
    rq->tasks[i].state = TASK_ZOMBIE;
  }
}

int sched_add_task(struct eevdf_rq *rq, uint32_t pid, uint32_t weight, uint64_t slice_ns) {
  if (!rq || weight == 0 || slice_ns == 0) return -EINVAL;
  for (int i = 0; i < AEGIS_MAX_TASKS; i++) {
    if (!rq->tasks[i].used) {
      rq->tasks[i].used = true;
      rq->tasks[i].pid = pid;
      rq->tasks[i].weight = weight;
      rq->tasks[i].vruntime = rq->min_vruntime;
      rq->tasks[i].slice_ns = slice_ns;
      rq->tasks[i].vdeadline = rq->tasks[i].vruntime + (slice_ns * 1024ULL) / weight;
      rq->tasks[i].state = TASK_READY;
      return i;
    }
  }
  return -ENOMEM;
}

static void update_min_vruntime(struct eevdf_rq *rq) {
  uint64_t sum_weighted_v = 0;
  uint64_t sum_w = 0;
  for (int i = 0; i < AEGIS_MAX_TASKS; i++) {
    if (rq->tasks[i].used && (rq->tasks[i].state == TASK_READY || rq->tasks[i].state == TASK_RUNNING)) {
      sum_weighted_v += rq->tasks[i].vruntime * rq->tasks[i].weight;
      sum_w += rq->tasks[i].weight;
    }
  }
  if (sum_w > 0) {
    uint64_t avg = sum_weighted_v / sum_w;
    if (avg > rq->min_vruntime) rq->min_vruntime = avg;
  }
}

int sched_pick_next(struct eevdf_rq *rq) {
  if (!rq) return -1;
  update_min_vruntime(rq);

  int best = -1;
  for (int i = 0; i < AEGIS_MAX_TASKS; i++) {
    struct eevdf_task *t = &rq->tasks[i];
    if (!t->used || (t->state != TASK_READY && t->state != TASK_RUNNING)) continue;
    /* EEVDF eligibility: non-negative lag (vruntime <= avg/min_vruntime) */
    if (t->vruntime <= rq->min_vruntime) {
      if (best == -1 || t->vdeadline < rq->tasks[best].vdeadline) {
        best = i;
      }
    }
  }

  /* Fallback to smallest vruntime if rounding leaves none <= min_vruntime */
  if (best == -1) {
    for (int i = 0; i < AEGIS_MAX_TASKS; i++) {
      struct eevdf_task *t = &rq->tasks[i];
      if (!t->used || (t->state != TASK_READY && t->state != TASK_RUNNING)) continue;
      if (best == -1 || t->vruntime < rq->tasks[best].vruntime) {
        best = i;
      }
    }
  }

  if (rq->current_idx >= 0 && rq->current_idx != best &&
      rq->tasks[rq->current_idx].state == TASK_RUNNING) {
    rq->tasks[rq->current_idx].state = TASK_READY;
  }
  if (best >= 0) {
    rq->tasks[best].state = TASK_RUNNING;
    rq->current_idx = best;
  }
  return best;
}

void sched_tick(struct eevdf_rq *rq, uint64_t delta_ns) {
  if (!rq || rq->current_idx < 0) return;
  struct eevdf_task *cur = &rq->tasks[rq->current_idx];
  if (!cur->used || cur->state != TASK_RUNNING) return;

  uint64_t dv = (delta_ns * 1024ULL) / cur->weight;
  if (dv == 0) dv = 1;
  cur->vruntime += dv;
  if (cur->vruntime >= cur->vdeadline) {
    cur->vdeadline = cur->vruntime + (cur->slice_ns * 1024ULL) / cur->weight;
  }
  update_min_vruntime(rq);
}
