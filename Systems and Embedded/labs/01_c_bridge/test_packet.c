#include "packet.h"
#include <assert.h>
#include <stdio.h>

int main(void) {
    uint8_t wire[COCKPIT_PACKET_SIZE];
    CockpitTelemetry tx = {
        .seq = 7,
        .pkt_type = 1,
        .speed_scaled = 9525, /* 95.25 km/h */
        .rpm = 3100,
        .gear = 4,
        .battery_temp_c = 38,
    };

    assert(encode_cockpit_packet(wire, tx));

    CockpitTelemetry rx = {0};
    assert(decode_cockpit_packet(wire, &rx));
    assert(rx.seq == 7);
    assert(rx.speed_scaled == 9525);
    assert(rx.rpm == 3100);
    assert(rx.battery_temp_c == 38);

    /* Corrupt 1 bit and verify CRC rejects packet */
    wire[5] ^= 0x01U;
    assert(!decode_cockpit_packet(wire, &rx));

    printf("C11 Lab PASS: 16-byte wire round-trip & CRC-16 tamper check verified.\n");
    return 0;
}
