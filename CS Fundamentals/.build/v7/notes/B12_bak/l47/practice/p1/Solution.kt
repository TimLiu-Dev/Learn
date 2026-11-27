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
        singletons[impl]?.let { return type.cast(it) }
        if (impl in creating) {
            // The cycle is the part of the path from impl's first appearance, back to impl.
            val path = creating.dropWhile { it != impl } + impl
            throw IllegalStateException("Dependency cycle: " + path.joinToString(" -> ") { it.simpleName })
        }
        creating += impl
        try {
            val constructor = impl.declaredConstructors.single()
            val args = constructor.parameterTypes.map { get(it) }
            val bean = constructor.newInstance(*args.toTypedArray())
            if (scopes.getValue(impl) == Scope.SINGLETON) singletons[impl] = bean
            return type.cast(bean)
        } finally {
            creating.removeAt(creating.lastIndex)   // also on failure, or the next get sees a false cycle
        }
    }

    private fun implementationOf(want: Class<*>): Class<*> =
        scopes.keys.singleOrNull { want.isAssignableFrom(it) }
            ?: throw IllegalStateException("No bean of type ${want.simpleName}")
}
