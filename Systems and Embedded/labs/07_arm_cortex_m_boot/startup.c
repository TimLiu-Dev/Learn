#include <stdint.h>

extern uint32_t _estack;
extern uint32_t _sidata;
extern uint32_t _sdata;
extern uint32_t _edata;
extern uint32_t _sbss;
extern uint32_t _ebss;

int main(void);

void Default_Handler(void) {
    while (1) { }
}

void Reset_Handler(void) {
    uint32_t *src = &_sidata;
    uint32_t *dst = &_sdata;
    while (dst < &_edata) {
        *dst++ = *src++;
    }

    dst = &_sbss;
    while (dst < &_ebss) {
        *dst++ = 0U;
    }

    (void)main();
    while (1) { }
}

__attribute__((section(".isr_vector")))
const uint32_t vector_table[2] = {
    (uint32_t)(uintptr_t)&_estack,
    (uint32_t)(uintptr_t)&Reset_Handler
};

static uint32_t initialized_magic = 0xCAFEBABEU;
static uint32_t zero_init_counter;

int main(void) {
    if (initialized_magic == 0xCAFEBABEU && zero_init_counter == 0U) {
        zero_init_counter = 1U;
    }
    return 0;
}
