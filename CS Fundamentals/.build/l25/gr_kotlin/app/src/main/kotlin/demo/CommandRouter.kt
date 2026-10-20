package demo

import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class CommandRouter(private val handler: Any) {
    private class Entry(val command: Command, val function: KFunction<*>)

    // Scan once, at startup: name -> method (a hash map, L19).
    private val commands: Map<String, Entry> = buildMap {
        for (fn in handler::class.declaredFunctions) {
            val cmd = fn.findAnnotation<Command>() ?: continue
            require(cmd.name !in this) { "Duplicate command '${cmd.name}' in ${handler::class.simpleName}" }
            require(fn.parameters.size == 2) { "@Command ${fn.name} must take exactly one List<String>" }
            put(cmd.name, Entry(cmd, fn))
        }
    }

    fun help(): String = buildString {
        append("Commands:")
        for (name in commands.keys.sorted()) {
            append("\n  %-6s %s".format(name, commands.getValue(name).command.help))
        }
    }

    fun dispatch(args: List<String>): String {
        val name = args.firstOrNull() ?: return help()
        if (name == "help") return help()
        val entry = commands[name] ?: return "Unknown command '$name'. Try 'help'."
        return try {
            entry.function.call(handler, args.drop(1)).toString()
        } catch (e: InvocationTargetException) {      // the handler itself threw
            "error in '$name': ${e.cause}"
        }
    }
}
