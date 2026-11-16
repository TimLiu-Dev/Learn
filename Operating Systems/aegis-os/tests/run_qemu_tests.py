#!/usr/bin/env python3
"""Headless QEMU smoke test for AegisOS-64 ELF binary."""
import subprocess
import sys

def main() -> int:
    elf = sys.argv[1] if len(sys.argv) > 1 else "build/aegis.elf"
    cmd = [
        "qemu-system-aarch64",
        "-M", "virt",
        "-cpu", "cortex-a72",
        "-nographic",
        "-m", "256M",
        "-kernel", elf,
    ]
    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    output_lines = []
    try:
        for line in proc.stdout:
            output_lines.append(line)
            if "AEGIS_BOOT_SELF_CHECK_PASS" in line:
                proc.terminate()
                print("QEMU boot self-check PASSED")
                return 0
    finally:
        proc.kill()
    print("QEMU boot self-check FAILED:\n" + "".join(output_lines))
    return 1

if __name__ == "__main__":
    sys.exit(main())
