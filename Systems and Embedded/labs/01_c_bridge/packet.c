#include "packet.h"
#include <string.h>

uint16_t compute_crc16_ccitt(const uint8_t *data, size_t len) {
    uint16_t crc = 0xFFFFU;
    for (size_t i = 0; i < len; ++i) {
        crc ^= (uint16_t)((uint16_t)data[i] << 8U);
        for (int bit = 0; bit < 8; ++bit) {
            if ((crc & 0x8000U) != 0U) {
                crc = (uint16_t)(((unsigned int)crc << 1U) ^ 0x1021U);
            } else {
                crc = (uint16_t)((unsigned int)crc << 1U);
            }
        }
    }
    return crc;
}

bool encode_cockpit_packet(uint8_t out[COCKPIT_PACKET_SIZE], CockpitTelemetry t) {
    if (!out) return false;
    memset(out, 0, COCKPIT_PACKET_SIZE);
    out[0] = COCKPIT_SYNC_0;
    out[1] = COCKPIT_SYNC_1;
    out[2] = t.seq;
    out[3] = t.pkt_type;
    out[4] = (uint8_t)(t.speed_scaled >> 8U);
    out[5] = (uint8_t)(t.speed_scaled & 0xFFU);
    out[6] = (uint8_t)(t.rpm >> 8U);
    out[7] = (uint8_t)(t.rpm & 0xFFU);
    out[8] = t.gear;
    out[9] = (uint8_t)t.battery_temp_c;

    uint16_t crc = compute_crc16_ccitt(out, 14U);
    out[14] = (uint8_t)(crc >> 8U);
    out[15] = (uint8_t)(crc & 0xFFU);
    return true;
}

bool decode_cockpit_packet(const uint8_t in[COCKPIT_PACKET_SIZE], CockpitTelemetry *out) {
    if (!in || !out) return false;
    if (in[0] != COCKPIT_SYNC_0 || in[1] != COCKPIT_SYNC_1) return false;

    uint16_t expected_crc = (uint16_t)(((uint16_t)in[14] << 8U) | in[15]);
    if (compute_crc16_ccitt(in, 14U) != expected_crc) return false;

    out->seq = in[2];
    out->pkt_type = in[3];
    out->speed_scaled = (uint16_t)(((uint16_t)in[4] << 8U) | in[5]);
    out->rpm = (uint16_t)(((uint16_t)in[6] << 8U) | in[7]);
    out->gear = in[8];
    out->battery_temp_c = (int8_t)in[9];
    return true;
}
