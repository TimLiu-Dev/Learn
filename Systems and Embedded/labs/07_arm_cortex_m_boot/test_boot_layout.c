#include <assert.h>
#include <stdint.h>
#include <stdio.h>

static void crt0_init(const uint32_t *sidata, uint32_t *sdata, const uint32_t *edata,
                      uint32_t *sbss, const uint32_t *ebss) {
    const uint32_t *src = sidata;
    uint32_t *dst = sdata;
    while (dst < edata) {
        *dst++ = *src++;
    }
    dst = sbss;
    while (dst < ebss) {
        *dst++ = 0U;
    }
}

int main(void) {
    const uint32_t flash_sidata[3] = {0xCAFEBABEU, 0x12345678U, 0x0000BEEFU};
    uint32_t sram_data[3] = {0xAAAAAAAAU, 0xAAAAAAAAU, 0xAAAAAAAAU};
    uint32_t sram_bss[4]  = {0xDEADBEEFU, 0xDEADBEEFU, 0xDEADBEEFU, 0xDEADBEEFU};

    crt0_init(flash_sidata, sram_data, sram_data + 3, sram_bss, sram_bss + 4);

    for (int i = 0; i < 3; ++i) {
        assert(sram_data[i] == flash_sidata[i]);
    }
    for (int i = 0; i < 4; ++i) {
        assert(sram_bss[i] == 0U);
    }

    printf("ARM Boot Lab PASS: Flash-to-SRAM .data copy & .bss zero-initialization verified.\n");
    return 0;
}
