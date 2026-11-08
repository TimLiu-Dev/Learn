import kotlin.reflect.KClass

class Favorites {
    // @PublishedApi lets the public inline functions below use this internal map.
    // (Inline functions are pasted into the caller, so everything they touch must be visible there. L14 covers inline.)
    @PublishedApi internal val slots = mutableMapOf<KClass<*>, Any>()

    inline fun <reified T : Any> put(value: T) {
        slots[T::class] = value                 // the key is the type argument, known at the call site
    }

    // as? T is a real, checked cast here, because T is reified
    inline fun <reified T : Any> get(): T? = slots[T::class] as? T
}
