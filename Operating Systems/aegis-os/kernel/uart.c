#include "aegis/kernel.h"

#ifdef AEGIS_HOST_TEST
#include <stdio.h>
void uart_init(void) {}
void uart_putc(char c) { putchar(c); }
void uart_puts(const char *s) {
  while (*s) uart_putc(*s++);
}
void uart_put_hex(uint64_t val) {
  printf("0x%llx", (unsigned long long)val);
}
#else
#define PL011_BASE 0x09000000ULL
#define UARTDR     (*(volatile uint32_t *)(PL011_BASE + 0x00))
#define UARTFR     (*(volatile uint32_t *)(PL011_BASE + 0x18))
#define UARTFR_TXFF (1U << 5)

void uart_init(void) {}

void uart_putc(char c) {
  if (c == '\n') {
    while (UARTFR & UARTFR_TXFF) {}
    UARTDR = '\r';
  }
  while (UARTFR & UARTFR_TXFF) {}
  UARTDR = (uint32_t)c;
}

void uart_puts(const char *s) {
  while (*s) uart_putc(*s++);
}

void uart_put_hex(uint64_t val) {
  static const char hex[] = "0123456789abcdef";
  uart_puts("0x");
  for (int shift = 60; shift >= 0; shift -= 4) {
    uart_putc(hex[(val >> shift) & 0xFULL]);
  }
}
#endif
