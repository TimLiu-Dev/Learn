package demo

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/** A tiny dependency-injection container: register instances, construct by type. */
class Container {
    private val instances = mutableMapOf<KClass<*>, Any>()

    fun <T : Any> register(type: KClass<T>, instance: T) {
        instances[type] = instance
    }

    fun <T : Any> create(type: KClass<T>): T {
        val ctor = requireNotNull(type.primaryConstructor) { "${type.simpleName} has no primary constructor" }
        val args = ctor.parameters.map { p ->
            instances[p.type.classifier] ?: error("No instance registered for ${p.type} (needed by ${type.simpleName})")
        }
        return ctor.call(*args.toTypedArray())
    }
}
