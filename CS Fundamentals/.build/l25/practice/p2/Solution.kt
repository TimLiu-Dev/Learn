import java.lang.reflect.InvocationTargetException

annotation class Check   // Kotlin's default retention: visible at runtime

/** Runs every @Check method of [type] on a fresh instance, in name order. */
fun runChecks(type: Class<*>): List<String> =
    type.declaredMethods
        .filter { it.isAnnotationPresent(Check::class.java) }
        .sortedBy { it.name }                          // declaredMethods has no defined order
        .map { method ->
            val instance = type.getDeclaredConstructor().newInstance()   // fresh per check, like JUnit
            try {
                method.invoke(instance)
                "PASS ${method.name}"
            } catch (e: InvocationTargetException) {
                val real = e.cause!!                   // what the method itself threw
                "FAIL ${method.name}: ${real.javaClass.simpleName}: ${real.message}"
            }
        }
