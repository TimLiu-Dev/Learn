enum class Scope { SINGLETON, PROTOTYPE }

class Container {
    private val scopes = mutableMapOf<Class<*>, Scope>()
    private val singletons = mutableMapOf<Class<*>, Any>()
    private val creating = mutableListOf<Class<*>>()   // beans under construction, outermost first

    fun register(type: Class<*>, scope: Scope = Scope.SINGLETON) {
        scopes[type] = scope
    }

    /** Builds (or reuses) the bean for [type], injecting its constructor parameters by type. */
    fun <T> get(type: Class<T>): T {
        val impl = implementationOf(type)
        TODO("reuse a singleton, detect a cycle, then build $impl through its constructor")
    }

    private fun implementationOf(want: Class<*>): Class<*> =
        scopes.keys.singleOrNull { want.isAssignableFrom(it) }
            ?: throw IllegalStateException("No bean of type ${want.simpleName}")
}
