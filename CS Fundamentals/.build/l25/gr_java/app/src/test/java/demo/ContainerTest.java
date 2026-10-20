package demo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {
    @Test void constructorDependenciesAreInjectedByType() {
        Container c = new Container();
        c.register(Config.class, new Config("?"));
        assertEquals("Hello, Bo?", c.create(Greetings.class).greet(List.of("Bo")));
    }

    @Test void aMissingDependencyFailsWithAClearMessage() {
        var e = assertThrows(IllegalStateException.class, () -> new Container().create(Greetings.class));
        assertEquals("No instance registered for demo.Config (needed by Greetings)", e.getMessage());
    }
}
