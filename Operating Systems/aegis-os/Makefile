HOST_CC ?= clang
ARM_CC  ?= clang --target=aarch64-none-elf
ARM_LD  ?= ld.lld

HOST_CFLAGS = -std=c11 -Wall -Wextra -Werror -O2 -DAEGIS_HOST_TEST -Iinclude
ARM_CFLAGS  = -std=c11 -Wall -Wextra -Werror -ffreestanding -nostdlib -mgeneral-regs-only -O2 -Iinclude

KERNEL_SRCS = \
	kernel/uart.c \
	kernel/trap.c \
	kernel/sched.c \
	kernel/futex.c \
	kernel/mm.c \
	kernel/ipc_binder.c \
	kernel/vfs.c \
	kernel/io_ring.c \
	kernel/main.c

HOST_TEST_SRCS = \
	kernel/uart.c \
	kernel/trap.c \
	kernel/sched.c \
	kernel/futex.c \
	kernel/mm.c \
	kernel/ipc_binder.c \
	kernel/vfs.c \
	kernel/io_ring.c \
	tests/test_kernel_subsystems.c

ARM_ASM_SRCS = \
	arch/arm64/boot.S \
	arch/arm64/vectors.S \
	arch/arm64/switch.S

ARM_OBJS = \
	build/boot.o \
	build/vectors.o \
	build/switch.o \
	build/uart.o \
	build/trap.o \
	build/sched.o \
	build/futex.o \
	build/mm.o \
	build/ipc_binder.o \
	build/vfs.o \
	build/io_ring.o \
	build/main.o

.PHONY: all test-host check-arm64 build-arm64 run test-qemu clean

all: test-host check-arm64

build:
	mkdir -p build

# Run full C unit + security boundary tests natively on macOS/Linux
test-host: build
	$(HOST_CC) $(HOST_CFLAGS) $(HOST_TEST_SRCS) -o build/host_tests
	./build/host_tests

# Compile-check all bare-metal ARM64 assembly and C kernel objects (works with Apple Clang!)
check-arm64: build
	$(ARM_CC) -c arch/arm64/boot.S -o build/boot.o
	$(ARM_CC) -c arch/arm64/vectors.S -o build/vectors.o
	$(ARM_CC) -c arch/arm64/switch.S -o build/switch.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/uart.c -o build/uart.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/trap.c -o build/trap.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/sched.c -o build/sched.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/futex.c -o build/futex.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/mm.c -o build/mm.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/ipc_binder.c -o build/ipc_binder.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/vfs.c -o build/vfs.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/io_ring.c -o build/io_ring.o
	$(ARM_CC) $(ARM_CFLAGS) -c kernel/main.c -o build/main.o
	$(ARM_CC) $(ARM_CFLAGS) -c user/init.c -o build/user_init.o

# Link full QEMU ELF binary (requires lld / aarch64-elf-ld)
build-arm64: check-arm64
	$(ARM_LD) -T arch/arm64/linker.ld -o build/aegis.elf $(ARM_OBJS)

run: build-arm64
	qemu-system-aarch64 -M virt -cpu cortex-a72 -nographic -m 256M -kernel build/aegis.elf

test-qemu: build-arm64
	python3 tests/run_qemu_tests.py build/aegis.elf

clean:
	rm -rf build
