#include "aegis/kernel.h"

/* ---------------- Physical Buddy Allocator ---------------- */
void buddy_init(struct buddy_alloc *b) {
  for (uint32_t i = 0; i < BUDDY_PAGES; i++) {
    b->free_map[i] = 0;
    b->order_map[i] = -1;
  }
  b->free_map[0] = 1;
  b->order_map[0] = BUDDY_MAX_ORDER;
}

int buddy_alloc_pages(struct buddy_alloc *b, uint32_t order) {
  if (!b || order > BUDDY_MAX_ORDER) return -EINVAL;
  for (uint32_t cur_order = order; cur_order <= BUDDY_MAX_ORDER; cur_order++) {
    uint32_t step = 1U << cur_order;
    for (uint32_t pfn = 0; pfn < BUDDY_PAGES; pfn += step) {
      if (b->free_map[pfn] && b->order_map[pfn] == (int8_t)cur_order) {
        b->free_map[pfn] = 0;
        b->order_map[pfn] = -1;
        while (cur_order > order) {
          cur_order--;
          uint32_t buddy = pfn + (1U << cur_order);
          b->free_map[buddy] = 1;
          b->order_map[buddy] = (int8_t)cur_order;
        }
        return (int)pfn;
      }
    }
  }
  return -ENOMEM;
}

void buddy_free_pages(struct buddy_alloc *b, uint32_t pfn, uint32_t order) {
  if (!b || order > BUDDY_MAX_ORDER || pfn >= BUDDY_PAGES) return;
  while (order < BUDDY_MAX_ORDER) {
    uint32_t buddy = pfn ^ (1U << order);
    if (buddy >= BUDDY_PAGES || !b->free_map[buddy] || b->order_map[buddy] != (int8_t)order) {
      break;
    }
    b->free_map[buddy] = 0;
    b->order_map[buddy] = -1;
    if (buddy < pfn) pfn = buddy;
    order++;
  }
  b->free_map[pfn] = 1;
  b->order_map[pfn] = (int8_t)order;
}

/* ---------------- SLUB-Style Fixed Object Cache ---------------- */
int slab_init(struct slab_cache *sc, void *backing_mem, size_t backing_bytes, size_t obj_size) {
  if (!sc || !backing_mem || obj_size == 0) return -EINVAL;
  uint32_t cap = (uint32_t)(backing_bytes / obj_size);
  if (cap == 0) return -EINVAL;
  if (cap > SLAB_MAX_OBJS) cap = SLAB_MAX_OBJS;

  sc->obj_size = obj_size;
  sc->capacity = cap;
  sc->free_count = cap;
  sc->backing = (uint8_t *)backing_mem;
  for (uint32_t i = 0; i < cap; i++) {
    sc->free_stack[i] = cap - 1U - i;
  }
  return 0;
}

void *slab_alloc(struct slab_cache *sc) {
  if (!sc || sc->free_count == 0) return NULL;
  uint32_t idx = sc->free_stack[--sc->free_count];
  return sc->backing + ((size_t)idx * sc->obj_size);
}

int slab_free(struct slab_cache *sc, void *ptr) {
  if (!sc || !ptr || sc->free_count >= sc->capacity) return -EINVAL;
  uintptr_t base = (uintptr_t)sc->backing;
  uintptr_t p = (uintptr_t)ptr;
  if (p < base) return -EINVAL;
  size_t off = (size_t)(p - base);
  if (off % sc->obj_size != 0) return -EINVAL;
  uint32_t idx = (uint32_t)(off / sc->obj_size);
  if (idx >= sc->capacity) return -EINVAL;
  sc->free_stack[sc->free_count++] = idx;
  return 0;
}

/* ---------------- Virtual Memory & Copy-on-Write ---------------- */
void vm_init(struct vm_space *vm) {
  vm->next_free_frame = 0;
  for (uint32_t i = 0; i < VM_MAX_VPAGES; i++) {
    vm->ptes[i] = 0;
    vm->phys_refcount[i] = 0;
    for (int j = 0; j < 64; j++) vm->phys_mem[i][j] = 0;
  }
}

int vm_map_page(struct vm_space *vm, uint32_t vpage, uint32_t pframe, uint64_t flags) {
  if (!vm || vpage >= VM_MAX_VPAGES || pframe >= VM_MAX_VPAGES) return -EINVAL;
  vm->ptes[vpage] = ((uint64_t)pframe << 12) | (flags & 0xFFFULL) | PTE_VALID;
  vm->phys_refcount[pframe]++;
  if (pframe >= vm->next_free_frame) vm->next_free_frame = pframe + 1U;
  return 0;
}

int vm_fork_cow(struct vm_space *parent, struct vm_space *child) {
  if (!parent || !child) return -EINVAL;
  vm_init(child);
  child->next_free_frame = parent->next_free_frame;
  for (uint32_t i = 0; i < VM_MAX_VPAGES; i++) {
    child->phys_refcount[i] = parent->phys_refcount[i];
    for (int j = 0; j < 64; j++) child->phys_mem[i][j] = parent->phys_mem[i][j];

    if (parent->ptes[i] & PTE_VALID) {
      if (parent->ptes[i] & PTE_WRITE) {
        parent->ptes[i] = (parent->ptes[i] & ~PTE_WRITE) | PTE_COW;
      }
      child->ptes[i] = parent->ptes[i];
      uint32_t pfn = (uint32_t)(parent->ptes[i] >> 12);
      if (pfn < VM_MAX_VPAGES) {
        parent->phys_refcount[pfn]++;
        child->phys_refcount[pfn] = parent->phys_refcount[pfn];
      }
    }
  }
  return 0;
}

int vm_handle_write_fault(struct vm_space *vm, uint32_t vpage) {
  if (!vm || vpage >= VM_MAX_VPAGES) return -EFAULT;
  uint64_t pte = vm->ptes[vpage];
  if (!(pte & PTE_VALID) || !(pte & PTE_COW)) return -EFAULT;

  uint32_t old_pfn = (uint32_t)(pte >> 12);
  if (vm->phys_refcount[old_pfn] <= 1U) {
    /* Last owner: promote directly to writable without copying */
    vm->ptes[vpage] = (pte & ~PTE_COW) | PTE_WRITE;
    return 0;
  }
  if (vm->next_free_frame >= VM_MAX_VPAGES) return -ENOMEM;

  uint32_t new_pfn = vm->next_free_frame++;
  for (int j = 0; j < 64; j++) {
    vm->phys_mem[new_pfn][j] = vm->phys_mem[old_pfn][j];
  }
  vm->phys_refcount[old_pfn]--;
  vm->phys_refcount[new_pfn] = 1;
  vm->ptes[vpage] = ((uint64_t)new_pfn << 12) | ((pte & 0xFFFULL) & ~PTE_COW) | PTE_WRITE;
  return 0;
}
