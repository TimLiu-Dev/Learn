#include <array>
#include <atomic>
#include <cassert>
#include <cstdint>
#include <cstdio>
#include <span>

template <typename T, std::size_t Capacity>
class SpscRingBuffer {
    static_assert((Capacity & (Capacity - 1)) == 0, "Capacity must be power of two");
public:
    bool push(const T& item) noexcept {
        const auto head = head_.load(std::memory_order_relaxed);
        const auto next = (head + 1) & (Capacity - 1);
        if (next == tail_.load(std::memory_order_acquire)) return false;
        buf_[head] = item;
        head_.store(next, std::memory_order_release);
        return true;
    }

    bool pop(T& out) noexcept {
        const auto tail = tail_.load(std::memory_order_relaxed);
        if (tail == head_.load(std::memory_order_acquire)) return false;
        out = buf_[tail];
        tail_.store((tail + 1) & (Capacity - 1), std::memory_order_release);
        return true;
    }

private:
    alignas(64) std::atomic<std::size_t> head_{0};
    alignas(64) std::atomic<std::size_t> tail_{0};
    std::array<T, Capacity> buf_{};
};

constexpr uint32_t make_mask(uint8_t offset, uint8_t width) {
    return ((1U << width) - 1U) << offset;
}

int main() {
    static_assert(make_mask(4, 3) == 0x70U);

    SpscRingBuffer<uint16_t, 8> queue;
    for (uint16_t i = 1; i <= 7; ++i) {
        assert(queue.push(i * 100U));
    }
    assert(!queue.push(999U)); // Full at Capacity - 1

    uint16_t val = 0;
    for (uint16_t i = 1; i <= 7; ++i) {
        assert(queue.pop(val));
        assert(val == i * 100U);
    }
    assert(!queue.pop(val));

    std::array<const uint8_t, 4> frame{0xAA, 0x55, 0x25, 0x1C};
    std::span<const uint8_t> view(frame);
    assert(view.size() == 4 && view[0] == 0xAA);

    std::printf("C++20 Lab PASS: constexpr masks, std::span & cache-aligned SPSC ring verified.\n");
    return 0;
}
