package dev.shelf

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient

// NOT RUN HERE. The same idea as Shelf's ShelfApiTest: the whole app on a random port, real HTTP.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = ["shelf.db=build/shelf-test.db"])
@AutoConfigureRestTestClient
class ShelfControllerTests {

    @Test
    fun `an unknown book is a 404`(@Autowired client: RestTestClient) {
        client.get().uri("/books/999999").exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `lending an unknown book is a 404`(@Autowired client: RestTestClient) {
        client.post().uri("/loans").contentType(MediaType.APPLICATION_JSON)
            .body("""{"bookId":999999,"memberId":1}""")
            .exchange()
            .expectStatus().isNotFound
    }
}
