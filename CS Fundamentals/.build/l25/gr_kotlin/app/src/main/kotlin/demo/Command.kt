package demo

/** Marks a method as a command the router can call. Read at runtime, so RUNTIME. */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val name: String, val help: String = "")
