import java.lang.reflect.InvocationTargetException

annotation class Check   // Kotlin's default retention: visible at runtime

/** Runs every @Check method of [type] on a fresh instance, in name order. */
fun runChecks(type: Class<*>): List<String> =
    TODO("find the @Check methods, sort them by name, and run each on a new instance")
