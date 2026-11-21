#ifndef COCKPIT_PACKET_H
#define COCKPIT_PACKET_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#define COCKPIT_PACKET_SIZE 16U
#define COCKPIT_SYNC_0 0xAAU
#define COCKPIT_SYNC_1 0x55U

typedef struct {
    uint8_t seq;
    uint8_t pkt_type;
    uint16_t speed_scaled; /* km/h * 100 */
    uint16_t rpm;
    uint8_t gear;
    int8_t battery_temp_c;
} CockpitTelemetry;

uint16_t compute_crc16_ccitt(const uint8_t *data, size_t len);
bool encode_cockpit_packet(uint8_t out[COCKPIT_PACKET_SIZE], CockpitTelemetry t);
bool decode_cockpit_packet(const uint8_t in[COCKPIT_PACKET_SIZE], CockpitTelemetry *out);

#endif
