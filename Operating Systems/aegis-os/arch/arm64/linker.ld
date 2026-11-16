ENTRY(_start)

SECTIONS
{
  /* QEMU virt loads kernel image at 0x40080000 */
  . = 0x40080000;

  .text : ALIGN(4096) {
    *(.text.boot)
    *(.text .text.*)
  }

  .rodata : ALIGN(4096) {
    *(.rodata .rodata.*)
  }

  .data : ALIGN(4096) {
    *(.data .data.*)
  }

  .bss (NOLOAD) : ALIGN(16) {
    __bss_start = .;
    *(.bss .bss.*)
    *(COMMON)
    __bss_end = .;
  }

  . = ALIGN(4096);
  . += 0x10000; /* 64KB initial kernel stack */
  __stack_top = .;
}
