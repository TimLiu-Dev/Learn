#include <assert.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>

static uint16_t modbus_crc16(const uint8_t *buf, size_t len) {
    uint16_t crc = 0xFFFFU;
    for (size_t i = 0; i < len; ++i) {
        crc ^= buf[i];
        for (int b = 0; b < 8; ++b) {
            if (crc & 1U) {
                crc = (uint16_t)((crc >> 1) ^ 0xA001U);
            } else {
                crc >>= 1;
            }
        }
    }
    return crc;
}

static bool modbus_verify_frame(const uint8_t *frame, size_t len) {
    if (frame == NULL || len < 4U) return false;
    uint16_t calc = modbus_crc16(frame, len - 2U);
    /* Modbus RTU transmits CRC little-endian: low byte first, high byte second */
    uint16_t wire = (uint16_t)frame[len - 2U] | (uint16_t)((uint16_t)frame[len - 1U] << 8);
    return calc == wire;
}

int main(void) {
    /* Standard Modbus RTU Read Holding Registers: Addr=0x01, Fn=0x03, Start=0x0000, Qty=0x0002 */
    uint8_t req[8] = { 0x01U, 0x03U, 0x00U, 0x00U, 0x00U, 0x02U, 0x00U, 0x00U };
    uint16_t crc = modbus_crc16(req, 6U);
    req[6] = (uint8_t)(crc & 0xFFU);
    req[7] = (uint8_t)((crc >> 8) & 0xFFU);

    assert(crc == 0x0BC4U); /* Known good vector: 0xC4, 0x0B on wire */
    assert(req[6] == 0xC4U && req[7] == 0x0BU);
    assert(modbus_verify_frame(req, sizeof(req)));

    req[4] ^= 0x01U; /* Corrupt single bit */
    assert(!modbus_verify_frame(req, sizeof(req)));
    assert(!modbus_verify_frame(req, 3U));

    printf("  [Lab 09] Modbus RTU CRC-16 (0x%04X) & frame check passed.\n", crc);
    return 0;
}
