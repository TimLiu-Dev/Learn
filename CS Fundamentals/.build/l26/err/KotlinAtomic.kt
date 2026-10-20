import kotlin.concurrent.atomics.AtomicInt

val hits = AtomicInt(0)          // Kotlin's own atomics, without opting in
