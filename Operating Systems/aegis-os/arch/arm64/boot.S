.section .text.boot
.global _start
.extern __bss_start
.extern __bss_end
.extern __stack_top
.extern vectors
.extern kmain

_start:
  /* Check CurrentEL; if booted at EL2, drop cleanly to EL1 */
  mrs x0, CurrentEL
  and x0, x0, #0xC
  cmp x0, #0x8
  b.ne el1_entry

  /* Configure EL2 -> EL1 AArch64 execution */
  mov x0, #(1 << 31) /* HCR_EL2.RW = 1 (AArch64 EL1) */
  msr hcr_el2, x0
  mov x0, #0x3C5     /* EL1h, DAIF masked */
  msr spsr_el2, x0
  adr x0, el1_entry
  msr elr_el2, x0
  eret

el1_entry:
  /* Set initial EL1 kernel stack */
  ldr x0, =__stack_top
  mov sp, x0

  /* Zero BSS */
  ldr x1, =__bss_start
  ldr x2, =__bss_end
1:
  cmp x1, x2
  b.hs 2f
  str xzr, [x1], #8
  b 1b
2:
  /* Install 2KB-aligned exception vector table */
  ldr x0, =vectors
  msr vbar_el1, x0
  isb

  bl kmain

halt:
  wfe
  b halt
