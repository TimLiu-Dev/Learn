import kotlin.reflect.KClass

class Favorites {
    // @PublishedApi lets the public inline functions below use this internal map.
    // (Inline functions are pasted into the caller, so everything they touch must be visible there. L14 covers inline.)
    @PublishedApi internal val slots = mutableMapOf<KClass<*>, Any>()

    inline fun <reified T : Any> put(value: T) {
        TODO("store value under T's slot")
    }

    inline fun <reified T : Any> get(): T? = TODO("read T's slot, checked against T")
}
