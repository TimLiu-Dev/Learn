enum class Scope { SINGLETON, PROTOTYPE }

class Container {
    private val scopes = mutableMapOf<Class<*>, Scope>()
    private val singletons = mutableMapOf<Class<*>, Any>()
    private val creating = mutableListOf<Class<*>>()

    fun register(type: Class<*>, scope: Scope = Scope.SINGLETON) {
        scopes[type] = scope
    }

    fun <T> get(type: Class<T>): T {
        val impl = implementationOf(type)
        singletons[impl]?.let { return type.cast(it) }
        if (impl in creating) {
            val path = creating.dropWhile { it != impl } + impl
            throw IllegalStateException("Dependency cycle: " + path.joinToString(" -> ") { it.simpleName })
        }
        creating += impl
        val constructor = impl.declaredConstructors.single()
        val args = constructor.parameterTypes.map { get(it) }
        val bean = constructor.newInstance(*args.toTypedArray())
        if (scopes.getValue(impl) == Scope.SINGLETON) singletons[impl] = bean
        creating.removeAt(creating.lastIndex)   // skipped when anything above throws
        return type.cast(bean)
    }

    private fun implementationOf(want: Class<*>): Class<*> =
        scopes.keys.singleOrNull { want.isAssignableFrom(it) }
            ?: throw IllegalStateException("No bean of type ${want.simpleName}")
}
