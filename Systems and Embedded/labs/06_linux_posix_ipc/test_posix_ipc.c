#define _POSIX_C_SOURCE 200809L
#include <assert.h>
#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/socket.h>
#include <unistd.h>

typedef struct {
    uint32_t seq;
    int32_t speed_kmh_x100;
} ShmTelemetrySlot;

static volatile sig_atomic_t g_shutdown_requested = 0;

static void handle_sigterm(int sig) {
    (void)sig;
    g_shutdown_requested = 1;
}

static int set_nonblocking(int fd) {
    int flags = fcntl(fd, F_GETFL, 0);
    if (flags < 0) return -1;
    return fcntl(fd, F_SETFL, flags | O_NONBLOCK);
}

int main(void) {
    /* 1. Install safe async-signal flag handler */
    struct sigaction sa = {0};
    sa.sa_handler = handle_sigterm;
    sigemptyset(&sa.sa_mask);
    assert(sigaction(SIGTERM, &sa, NULL) == 0);

    /* 2. POSIX Shared Memory (shm_open + ftruncate + mmap MAP_SHARED) */
    const char *shm_name = "/cockpit_lab06_shm";
    shm_unlink(shm_name);
    int shm_fd = shm_open(shm_name, O_CREAT | O_EXCL | O_RDWR, 0600);
    assert(shm_fd >= 0);
    assert(ftruncate(shm_fd, (off_t)sizeof(ShmTelemetrySlot)) == 0);

    ShmTelemetrySlot *slot = mmap(NULL, sizeof(ShmTelemetrySlot),
                                  PROT_READ | PROT_WRITE, MAP_SHARED, shm_fd, 0);
    assert(slot != MAP_FAILED);

    slot->seq = 42;
    slot->speed_kmh_x100 = 8850; /* 88.50 km/h */

    /* 3. Non-blocking AF_UNIX socketpair + poll() readiness loop */
    int sv[2] = {-1, -1};
    assert(socketpair(AF_UNIX, SOCK_STREAM, 0, sv) == 0);
    assert(set_nonblocking(sv[0]) == 0);
    assert(set_nonblocking(sv[1]) == 0);

    /* Verify non-blocking EAGAIN when nothing is ready */
    uint8_t byte = 0;
    ssize_t empty_r = recv(sv[0], &byte, 1, 0);
    assert(empty_r < 0 && (errno == EAGAIN || errno == EWOULDBLOCK));

    /* Send notification byte across AF_UNIX and wake poll() */
    uint8_t ping = 0xA5;
    assert(send(sv[1], &ping, 1, 0) == 1);

    struct pollfd pfd = { .fd = sv[0], .events = POLLIN, .revents = 0 };
    assert(poll(&pfd, 1, 100) == 1);
    assert((pfd.revents & POLLIN) != 0);
    assert(recv(sv[0], &byte, 1, 0) == 1);
    assert(byte == 0xA5);

    /* Verify zero-copy shared memory telemetry matches */
    assert(slot->seq == 42 && slot->speed_kmh_x100 == 8850);

    /* Trigger graceful signal flag */
    raise(SIGTERM);
    assert(g_shutdown_requested == 1);

    close(sv[0]);
    close(sv[1]);
    munmap(slot, sizeof(ShmTelemetrySlot));
    close(shm_fd);
    shm_unlink(shm_name);

    printf("POSIX Linux Lab PASS: shm_open/mmap, non-blocking AF_UNIX EAGAIN/poll & sigaction verified.\n");
    return 0;
}
