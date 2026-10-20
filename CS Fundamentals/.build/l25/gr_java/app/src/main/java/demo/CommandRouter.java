package demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class CommandRouter {
    private record Entry(Command command, Method method) {}

    private final Object handler;
    private final Map<String, Entry> commands = new HashMap<>();   // name -> method (L19)

    public CommandRouter(Object handler) {
        this.handler = handler;
        // Scan once, at startup.
        for (Method m : handler.getClass().getDeclaredMethods()) {
            Command cmd = m.getAnnotation(Command.class);
            if (cmd == null) continue;
            if (commands.containsKey(cmd.name())) {
                throw new IllegalArgumentException("Duplicate command '" + cmd.name()
                    + "' in " + handler.getClass().getSimpleName());
            }
            if (m.getParameterCount() != 1 || m.getParameterTypes()[0] != List.class) {
                throw new IllegalArgumentException("@Command " + m.getName() + " must take exactly one List<String>");
            }
            m.setAccessible(true);   // so non-public handler classes work too
            commands.put(cmd.name(), new Entry(cmd, m));
        }
    }

    public String help() {
        StringBuilder sb = new StringBuilder("Commands:");
        for (String name : new TreeSet<>(commands.keySet())) {
            sb.append(String.format("%n  %-6s %s", name, commands.get(name).command().help()));
        }
        return sb.toString();
    }

    public String dispatch(List<String> args) {
        if (args.isEmpty() || args.get(0).equals("help")) return help();
        String name = args.get(0);
        Entry entry = commands.get(name);
        if (entry == null) return "Unknown command '" + name + "'. Try 'help'.";
        try {
            return String.valueOf(entry.method().invoke(handler, args.subList(1, args.size())));
        } catch (InvocationTargetException e) {          // the handler itself threw
            return "error in '" + name + "': " + e.getCause();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
