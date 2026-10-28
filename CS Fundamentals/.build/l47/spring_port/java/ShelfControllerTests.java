package dev.shelf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

// NOT RUN HERE. The same idea as Shelf's ShelfApiTest: the whole app on a random port, real HTTP.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "shelf.db=build/shelf-test.db")
@AutoConfigureRestTestClient
class ShelfControllerTests {

    @Test
    void unknownBookIs404(@Autowired RestTestClient client) {
        client.get().uri("/books/999999").exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void lendingAnUnknownBookIs404(@Autowired RestTestClient client) {
        client.post().uri("/loans").contentType(MediaType.APPLICATION_JSON)
            .body("{\"bookId\":999999,\"memberId\":1}")
            .exchange()
            .expectStatus().isNotFound();
    }
}
