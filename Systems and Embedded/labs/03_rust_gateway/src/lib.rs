#![cfg_attr(not(test), no_std)]

pub fn crc16_ccitt(data: &[u8]) -> u16 {
    let mut crc: u16 = 0xFFFF;
    for &b in data {
        crc ^= (b as u16) << 8;
        for _ in 0..8 {
            if (crc & 0x8000) != 0 {
                crc = (crc << 1) ^ 0x1021;
            } else {
                crc <<= 1;
            }
        }
    }
    crc
}

pub fn parse_vhal_speed_packet(packet: &[u8]) -> Option<f32> {
    if packet.len() != 16 || packet[0] != 0xAA || packet[1] != 0x55 {
        return None;
    }
    let expected = u16::from_be_bytes([packet[14], packet[15]]);
    if crc16_ccitt(&packet[..14]) != expected {
        return None;
    }
    let scaled = u16::from_be_bytes([packet[4], packet[5]]);
    Some((scaled as f32) / 100.0)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn validates_sync_and_crc() {
        let mut pkt = [0u8; 16];
        pkt[0] = 0xAA;
        pkt[1] = 0x55;
        pkt[4] = 0x25; // 9500 = 95.00 km/h
        pkt[5] = 0x1C;
        let crc = crc16_ccitt(&pkt[..14]).to_be_bytes();
        pkt[14] = crc[0];
        pkt[15] = crc[1];

        assert_eq!(parse_vhal_speed_packet(&pkt), Some(95.0));

        pkt[4] ^= 0x01;
        assert_eq!(parse_vhal_speed_packet(&pkt), None);
    }
}
