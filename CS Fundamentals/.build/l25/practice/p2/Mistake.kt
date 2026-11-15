import java.lang.reflect.InvocationTargetException

annotation class Check

fun runChecks(type: Class<*>): List<String> =
    type.declaredMethods
        .filter { it.isAnnotationPresent(Check::class.java) }
        .sortedBy { it.name }
        .map { method ->
            val instance = type.getDeclaredConstructor().newInstance()
            try {
                method.invoke(instance)
                "PASS ${method.name}"
            } catch (e: Exception) {
                // Mistake: reports the reflection wrapper, not the cause.
                "FAIL ${method.name}: ${e.javaClass.simpleName}: ${e.message}"
            }
        }
