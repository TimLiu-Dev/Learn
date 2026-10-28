package shelf.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RouterTest {
    private final Router.Handler ok = req -> new Response(200);
    private final Router router = new Router();

    RouterTest() {
        router.get("/books", ok);
        router.post("/books", ok);
        router.get("/books/{id}", ok);
        router.delete("/books/{id}", ok);
        router.post("/loans/{id}/return", ok);
    }

    @Test
    void aTemplateBindsItsPathParameters() {
        var m = assertInstanceOf(Router.Match.Found.class, router.match("POST", "/loans/42/return"));
        assertEquals(Map.of("id", "42"), m.params());
    }

    @Test
    void trailingSlashesDoNotMatter() {
        assertInstanceOf(Router.Match.Found.class, router.match("GET", "/books/"));
    }

    @Test
    void aKnownPathWithTheWrongMethodListsTheAllowedOnes() {
        var m = assertInstanceOf(Router.Match.WrongMethod.class, router.match("PATCH", "/books/7"));
        assertEquals(List.of("DELETE", "GET"), m.allowed());
    }

    @Test
    void anUnknownPathIsNoRouteAtAll() {
        assertInstanceOf(Router.Match.NoRoute.class, router.match("GET", "/books/7/pages"));
        assertInstanceOf(Router.Match.NoRoute.class, router.match("GET", "/authors"));
    }
}
