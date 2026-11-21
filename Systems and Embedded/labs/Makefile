CC := clang
CXX := clang++
CFLAGS := -std=c11 -Wall -Wextra -Werror -Wconversion -O1 -g -fsanitize=address,undefined
CXXFLAGS := -std=c++20 -fno-exceptions -Wall -Wextra -Werror -O1 -g -fsanitize=address,undefined

.PHONY: all test test-c test-cpp test-rust test-go test-e2e test-posix test-boot clean

all: test

test: test-c test-cpp test-rust test-go test-posix test-boot test-e2e
	@echo "\n✅ ALL LOCAL SYSTEMS LABS PASSED (C11, C++20, Rust, Go, POSIX, ARM Boot, E2E)!"

test-c:
	@echo "==> [Lab 01] Building & running C11 wire protocol + ASan/UBSan tests..."
	$(CC) $(CFLAGS) 01_c_bridge/packet.c 01_c_bridge/test_packet.c -o 01_c_bridge/test_packet
	./01_c_bridge/test_packet

test-cpp:
	@echo "==> [Lab 02] Building & running C++20 lock-free SPSC + register tests..."
	$(CXX) $(CXXFLAGS) 02_cpp20_telemetry/test_telemetry.cpp -o 02_cpp20_telemetry/test_telemetry
	./02_cpp20_telemetry/test_telemetry

test-rust:
	@echo "==> [Lab 03] Running Rust gateway unit tests..."
	cd 03_rust_gateway && cargo test

test-go:
	@echo "==> [Lab 04] Running Go cloud ingestor race-detector tests..."
	cd 04_go_cloud_ingestor && go test -race ./...

test-posix:
	@echo "==> [Lab 06] Building & running POSIX shm_open/mmap + non-blocking AF_UNIX + sigaction..."
	$(CC) $(CFLAGS) 06_linux_posix_ipc/test_posix_ipc.c -o 06_linux_posix_ipc/test_posix_ipc
	./06_linux_posix_ipc/test_posix_ipc

test-boot:
	@echo "==> [Lab 07] Building & running ARM Cortex-M Reset_Handler .data/.bss verification..."
	$(CC) $(CFLAGS) 07_arm_cortex_m_boot/test_boot_layout.c -o 07_arm_cortex_m_boot/test_boot_layout
	./07_arm_cortex_m_boot/test_boot_layout
	@if command -v arm-none-eabi-gcc >/dev/null 2>&1; then \
		echo "    arm-none-eabi-gcc detected: linking real Thumb-2 ELF with cortex_m4.ld..."; \
		arm-none-eabi-gcc -mcpu=cortex-m4 -mthumb -nostdlib -ffreestanding -T 07_arm_cortex_m_boot/cortex_m4.ld 07_arm_cortex_m_boot/startup.c -o 07_arm_cortex_m_boot/firmware.elf; \
		arm-none-eabi-size 07_arm_cortex_m_boot/firmware.elf; \
	else \
		echo "    (Optional) Install arm-none-eabi-gcc via Homebrew to also emit firmware.elf from cortex_m4.ld."; \
	fi

test-e2e:
	@echo "==> [Lab 05] Running Silicon-to-Cloud E2E simulation (C -> Rust -> Go)..."
	$(CC) $(CFLAGS) 01_c_bridge/packet.c 05_capstone_e2e/mcu_sim.c -o 05_capstone_e2e/mcu_sim
	./05_capstone_e2e/run_e2e.sh

clean:
	rm -f 01_c_bridge/test_packet 02_cpp20_telemetry/test_telemetry 05_capstone_e2e/mcu_sim 06_linux_posix_ipc/test_posix_ipc 07_arm_cortex_m_boot/test_boot_layout 07_arm_cortex_m_boot/firmware.elf
	cd 03_rust_gateway && cargo clean
