.section .text
.global cpu_switch_to

/*
 * void cpu_switch_to(uint64_t *prev_sp_slot, uint64_t next_sp)
 * Saves AArch64 callee-saved registers x19-x30 onto prev kernel stack,
 * stores SP into *prev_sp_slot, loads next_sp, and restores next task's frame.
 */
cpu_switch_to:
  sub sp, sp, #96
  stp x19, x20, [sp, #16 * 0]
  stp x21, x22, [sp, #16 * 1]
  stp x23, x24, [sp, #16 * 2]
  stp x25, x26, [sp, #16 * 3]
  stp x27, x28, [sp, #16 * 4]
  stp x29, x30, [sp, #16 * 5]

  mov x9, sp
  str x9, [x0]
  mov sp, x1

  ldp x19, x20, [sp, #16 * 0]
  ldp x21, x22, [sp, #16 * 1]
  ldp x23, x24, [sp, #16 * 2]
  ldp x25, x26, [sp, #16 * 3]
  ldp x27, x28, [sp, #16 * 4]
  ldp x29, x30, [sp, #16 * 5]
  add sp, sp, #96
  ret
