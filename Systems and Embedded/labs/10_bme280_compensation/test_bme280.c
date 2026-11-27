#include <assert.h>
#include <stdint.h>
#include <stdio.h>

typedef struct {
    uint16_t dig_T1;
    int16_t  dig_T2;
    int16_t  dig_T3;
} Bme280TempCalib;

/* Bosch BME280 datasheet 32-bit integer temperature compensation (returns 0.01 degC) */
static int32_t bme280_compensate_T_int32(int32_t adc_T, const Bme280TempCalib *cal, int32_t *t_fine_out) {
    int32_t var1 = ((((adc_T >> 3) - ((int32_t)cal->dig_T1 << 1))) * ((int32_t)cal->dig_T2)) >> 11;
    int32_t tmp  = ((adc_T >> 4) - ((int32_t)cal->dig_T1));
    int32_t var2 = (((tmp * tmp) >> 12) * ((int32_t)cal->dig_T3)) >> 14;
    int32_t t_fine = var1 + var2;
    if (t_fine_out) *t_fine_out = t_fine;
    return (t_fine * 5 + 128) >> 8;
}

int main(void) {
    /* Datasheet reference calibration vector (BME280 Section 4.2.3) */
    const Bme280TempCalib cal = { .dig_T1 = 27504U, .dig_T2 = 26435, .dig_T3 = -1000 };
    int32_t t_fine = 0;
    int32_t temp_centi = bme280_compensate_T_int32(519888, &cal, &t_fine);

    /* Expected: ~25.08 degC -> 2508 centi-degrees */
    assert(temp_centi == 2508);
    assert(t_fine > 128000);

    printf("  [Lab 10] BME280 fixed-point compensation passed (T=%.2f C).\n",
           (double)temp_centi / 100.0);
    return 0;
}
