class Battery(val capacity: Int, initialLevel: Int = capacity) {
    // The setter doesn't run for the initializer, so clamp the starting value too.
    var level: Int = initialLevel.coerceIn(0, capacity)
        set(value) {
            field = value.coerceIn(0, capacity)
        }

    val percent: Int
        get() = level * 100 / capacity

    var recharges: Int = 0
        private set

    fun use(amount: Int) {
        level -= amount             // goes through the setter, so it stops at 0
    }

    fun recharge() {
        level = capacity
        recharges++
    }
}
