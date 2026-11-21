#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"
RUST_BIN="${DIR}/rust_validator"
RUST_LIB="${DIR}/librust_gateway.rlib"
rm -f "${RUST_BIN}" "${RUST_LIB}"

rustc --edition 2021 --crate-type rlib --crate-name rust_gateway -D warnings "${DIR}/../03_rust_gateway/src/lib.rs" -o "${RUST_LIB}"

cat > "${DIR}/rust_validator.rs" << 'EOF'
use std::io::{self, Read};

fn main() {
    let mut buf = Vec::new();
    io::stdin().read_to_end(&mut buf).expect("stdin");
    for chunk in buf.chunks_exact(16) {
        if let Some(speed) = rust_gateway::parse_vhal_speed_packet(chunk) {
            eprintln!("[Rust HAL Validator] CRC-16 OK -> speed={:.2} km/h", speed);
            for b in chunk {
                print!("{:02x}", b);
            }
            println!();
        }
    }
}
EOF

rustc --edition 2021 -D warnings --extern "rust_gateway=${RUST_LIB}" "${DIR}/rust_validator.rs" -o "${RUST_BIN}"

"${DIR}/mcu_sim" | "${RUST_BIN}" | (cd "${DIR}/../04_go_cloud_ingestor" && go run ../05_capstone_e2e/cloud_cli.go)
rm -f "${RUST_BIN}" "${RUST_LIB}" "${DIR}/rust_validator.rs"
