class Battery(val capacity: Int, initialLevel: Int = capacity) {
    var level: Int = initialLevel
        // TODO: a custom setter that clamps into 0..capacity (what about the starting value?)

    val percent: Int
        get() = TODO("level as a whole-number percentage of capacity")

    var recharges: Int = 0      // TODO: outsiders may read this, but not assign it

    fun use(amount: Int) {
        TODO("drain by amount, never below 0")
    }

    fun recharge() {
        level = capacity
        recharges++
    }
}
