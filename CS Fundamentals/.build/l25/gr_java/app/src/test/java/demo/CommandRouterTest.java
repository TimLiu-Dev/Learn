package demo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandRouterTest {
    private final CommandRouter router = new CommandRouter(new Greetings(new Config("!")));

    @Test void routesACommandAndPassesTheRemainingArgs() {
        assertEquals("Hello, Ada!", router.dispatch(List.of("greet", "Ada")));
    }

    @Test void returnValuesAreTurnedIntoText() {
        assertEquals("6", router.dispatch(List.of("add", "1", "2", "3")));
    }

    @Test void unknownCommandsGetAHelpfulMessage() {
        assertEquals("Unknown command 'fly'. Try 'help'.", router.dispatch(List.of("fly")));
    }

    @Test void unannotatedMethodsAreNotCommands() {
        assertEquals("Unknown command 'internalHelper'. Try 'help'.", router.dispatch(List.of("internalHelper")));
    }

    @Test void helpIsGeneratedFromTheAnnotationsSorted() {
        String expected = String.join(System.lineSeparator(),
            "Commands:",
            "  add    add N...     sum whole numbers",
            "  greet  greet NAME   say hello",
            "  shout  shout WORDS  uppercase them");
        assertEquals(expected, router.dispatch(List.of("help")));
        assertEquals(expected, router.dispatch(List.of()));
    }

    @Test void anExceptionInsideAHandlerIsReportedNotThrown() {
        String out = router.dispatch(List.of("add", "1", "x"));
        assertTrue(out.startsWith("error in 'add': java.lang.NumberFormatException"), out);
    }

    static class Clash {
        @Command(name = "go") public String one(List<String> args) { return "1"; }
        @Command(name = "go") public String two(List<String> args) { return "2"; }
    }

    @Test void duplicateCommandNamesAreRejectedAtStartup() {
        var e = assertThrows(IllegalArgumentException.class, () -> new CommandRouter(new Clash()));
        assertEquals("Duplicate command 'go' in Clash", e.getMessage());
    }

    static class BadSignature {
        @Command(name = "bad") public String bad(String a, String b) { return a + b; }
    }

    @Test void commandMethodsMustTakeOneList() {
        var e = assertThrows(IllegalArgumentException.class, () -> new CommandRouter(new BadSignature()));
        assertEquals("@Command bad must take exactly one List<String>", e.getMessage());
    }
}
