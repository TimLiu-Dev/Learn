import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

// Four annotations, read at runtime by reflection (L25). Kotlin's default retention is RUNTIME.
@Target(AnnotationTarget.FUNCTION) annotation class Get(val path: String)
@Target(AnnotationTarget.FUNCTION) annotation class Post(val path: String)
@Target(AnnotationTarget.VALUE_PARAMETER) annotation class PathVar(val name: String)
@Target(AnnotationTarget.FUNCTION) annotation class OnError(val type: kotlin.reflect.KClass<out Throwable>)

// What you write: a controller with annotated methods. No routing code at all.
class BookController {
    private val books = mapOf(7L to "Dune", 8L to "Emma")

    @Get("/books/{id}")
    fun book(@PathVar("id") id: Long): String =
        books[id]?.let { """{"id":$id,"title":"$it"}""" } ?: throw NoSuchElementException("No book $id")

    @Get("/boom")
    fun boom(): String = throw IllegalAccessError("simulated JVM error")      // an Error, as in L45

    @OnError(NoSuchElementException::class)
    fun notFound(e: NoSuchElementException) = 404 to """{"title":"Not Found","detail":"${e.message}"}"""

    @OnError(IllegalAccessError::class)
    fun jvmError(e: IllegalAccessError) = 500 to """{"title":"Internal Server Error"}"""
}

class HandlerDispatchException(cause: Throwable) : Exception("Handler dispatch failed: $cause", cause)

// What the framework writes: scan, match, convert, invoke, map errors.
class MiniDispatcher(private val controller: Any) {
    private class Route(val method: String, val template: List<String>, val target: Method)

    private val routes = controller.javaClass.declaredMethods.flatMap { m ->
        listOfNotNull(
            m.getAnnotation(Get::class.java)?.let { Route("GET", split(it.path), m) },
            m.getAnnotation(Post::class.java)?.let { Route("POST", split(it.path), m) },
        )
    }
    private val errorHandlers = controller.javaClass.declaredMethods
        .mapNotNull { m -> m.getAnnotation(OnError::class.java)?.let { it.type.java to m } }

    fun dispatch(method: String, path: String): String {
        val parts = split(path)
        val shapeFits = routes.filter { bind(it.template, parts) != null }
        if (shapeFits.isEmpty()) return "404 no handler"
        val route = shapeFits.firstOrNull { it.method == method }
            ?: return "405 allowed=" + shapeFits.map { it.method }.distinct()
        val vars = bind(route.template, parts)!!
        val args: List<Any> = try {
            route.target.parameters.map { p ->
                val raw = vars.getValue(p.getAnnotation(PathVar::class.java).name)
                if (p.type == Long::class.javaPrimitiveType) raw.toLong() else raw     // type conversion
            }
        } catch (e: NumberFormatException) {
            return "400 cannot convert path variable: ${e.message}"
        }
        val failure: Exception = try {
            return "200 " + route.target.invoke(controller, *args.toTypedArray())
        } catch (e: InvocationTargetException) {
            when (val cause = e.targetException) {
                is Exception -> cause
                else -> HandlerDispatchException(cause)       // like DispatcherServlet: wrap an Error
            }
        }
        // Walk the cause chain, as @ExceptionHandler matching does.
        generateSequence<Throwable>(failure) { it.cause }.forEach { t ->
            errorHandlers.firstOrNull { (type, _) -> type.isInstance(t) }?.let { (_, handler) ->
                val (status, body) = handler.invoke(controller, t) as Pair<*, *>
                return "$status $body   (handled ${t.javaClass.simpleName} via ${handler.name})"
            }
        }
        return "500 unhandled ${failure.javaClass.simpleName}"
    }

    private fun bind(template: List<String>, parts: List<String>): Map<String, String>? {
        if (template.size != parts.size) return null
        val vars = mutableMapOf<String, String>()
        for ((t, p) in template.zip(parts)) {
            if (t.startsWith("{")) vars[t.trim('{', '}')] = p else if (t != p) return null
        }
        return vars
    }

    private fun split(path: String) = path.split('/').filter { it.isNotEmpty() }
}

fun main() {
    val dispatcher = MiniDispatcher(BookController())
    for ((method, path) in listOf("GET" to "/books/7", "GET" to "/books/99", "GET" to "/books/abc",
                                  "POST" to "/books/7", "GET" to "/boom", "GET" to "/authors")) {
        println("$method $path -> ${dispatcher.dispatch(method, path)}")
    }
}
