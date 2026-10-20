package demo;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/** A tiny dependency-injection container: register instances, construct by type. */
public class Container {
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public <T> void register(Class<T> type, T instance) {
        instances.put(type, instance);
    }

    public <T> T create(Class<T> type) {
        Constructor<?> ctor = type.getConstructors()[0];   // assume one public constructor
        Object[] args = new Object[ctor.getParameterCount()];
        Class<?>[] needed = ctor.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
            args[i] = instances.get(needed[i]);
            if (args[i] == null) {
                throw new IllegalStateException("No instance registered for " + needed[i].getName()
                    + " (needed by " + type.getSimpleName() + ")");
            }
        }
        try {
            return type.cast(ctor.newInstance(args));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create " + type.getSimpleName(), e);
        }
    }
}
