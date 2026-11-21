#include "../01_c_bridge/packet.h"
#include <stdio.h>

int main(void) {
    uint8_t wire[COCKPIT_PACKET_SIZE];
    CockpitTelemetry samples[3] = {
        { .seq = 1, .pkt_type = 1, .speed_scaled = 6500,  .rpm = 2200, .gear = 3, .battery_temp_c = 34 },
        { .seq = 2, .pkt_type = 1, .speed_scaled = 9500,  .rpm = 3000, .gear = 4, .battery_temp_c = 36 },
        { .seq = 3, .pkt_type = 1, .speed_scaled = 12000, .rpm = 3800, .gear = 4, .battery_temp_c = 37 },
    };

    for (size_t i = 0; i < 3; ++i) {
        if (!encode_cockpit_packet(wire, samples[i])) return 1;
        if (fwrite(wire, 1, COCKPIT_PACKET_SIZE, stdout) != COCKPIT_PACKET_SIZE) return 1;
    }
    return 0;
}
