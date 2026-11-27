#include <assert.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>

typedef struct {
    uint32_t r0, r1, r2, r3, r12, lr, pc, xpsr;
} CortexMExcFrame;

typedef struct {
    bool div_by_zero;
    bool unaligned;
    bool undef_instr;
    bool bus_precise;
    bool bfar_valid;
    bool mmfar_valid;
} FaultFlags;

static FaultFlags decode_cfsr(uint32_t cfsr) {
    FaultFlags f = {
        .div_by_zero = (cfsr & (1U << 25)) != 0U,
        .unaligned   = (cfsr & (1U << 24)) != 0U,
        .undef_instr = (cfsr & (1U << 16)) != 0U,
        .bus_precise = (cfsr & (1U << 9))  != 0U,
        .bfar_valid  = (cfsr & (1U << 15)) != 0U,
        .mmfar_valid = (cfsr & (1U << 7))  != 0U,
    };
    return f;
}

static uint32_t extract_fault_pc(const uint32_t *stacked_sp) {
    /* Cortex-M hardware pushes: R0, R1, R2, R3, R12, LR, PC, xPSR */
    return stacked_sp[6];
}

int main(void) {
    uint32_t mock_stack[8] = {
        0x00000001U, 0x00000002U, 0x00000003U, 0x00000004U,
        0x12121212U, 0x08001235U, 0x08004A1CU, 0x01000000U
    };
    assert(extract_fault_pc(mock_stack) == 0x08004A1CU);

    FaultFlags usage = decode_cfsr((1U << 25) | (1U << 24));
    assert(usage.div_by_zero && usage.unaligned && !usage.bus_precise);

    FaultFlags bus = decode_cfsr((1U << 15) | (1U << 9));
    assert(bus.bus_precise && bus.bfar_valid && !bus.mmfar_valid);

    printf("  [Lab 08] HardFault frame & CFSR decoder passed (PC=0x%08X).\n",
           extract_fault_pc(mock_stack));
    return 0;
}
