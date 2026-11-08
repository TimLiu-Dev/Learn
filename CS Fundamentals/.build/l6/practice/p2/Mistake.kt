class Battery(val capacity: Int, initialLevel: Int = capacity) {
    var level: Int = initialLevel   // the setter below never sees this value
        set(value) {
            field = value.coerceIn(0, capacity)
        }

    val percent: Int
        get() = level * 100 / capacity

    var recharges: Int = 0
        private set

    fun use(amount: Int) {
        level -= amount
    }

    fun recharge() {
        level = capacity
        recharges++
    }
}
