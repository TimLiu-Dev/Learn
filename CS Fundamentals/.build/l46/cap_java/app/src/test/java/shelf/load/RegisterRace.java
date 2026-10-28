package shelf.load;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import shelf.Shelf;

// Exercise 1: 10 sign-ups with the SAME email, all at once.
public final class RegisterRace {
    public static void main(String[] args) throws Exception {
        Path dbFile = Files.createTempFile("shelf-register", ".db");
        try (Shelf shelf = new Shelf(dbFile, 0, Clock.systemUTC()).start()) {
            Api api = new Api(shelf.port());
            for (int round = 1; round <= 3; round++) {
                String email = "ada" + round + "@example.com";
                var codes = Api.burst(10, i -> api.post("/members", "{\"name\":\"Ada\",\"email\":\"" + email + "\"}"));
                System.out.println("round " + round + ": " + Api.tally(codes));
            }
        }
        Files.deleteIfExists(dbFile);
    }
}
