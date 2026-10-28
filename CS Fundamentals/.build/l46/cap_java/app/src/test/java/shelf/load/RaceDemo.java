package shelf.load;

import static shelf.load.Api.burst;
import static shelf.load.Api.tally;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import shelf.Shelf;

// Fire many requests at the same instant and count the status codes.
public final class RaceDemo {
    public static void main(String[] args) throws Exception {
        Path dbFile = Files.createTempFile("shelf-race", ".db");
        try (Shelf shelf = new Shelf(dbFile, 0, Clock.systemUTC()).start()) {
            Api api = new Api(shelf.port());
            for (int i = 0; i < 20; i++) api.post("/members", "{\"name\":\"M" + i + "\",\"email\":\"m" + i + "@example.com\"}");

            System.out.println("Race 1: 20 members borrow the SAME book at once (5 rounds)");
            for (int round = 1; round <= 5; round++) {
                long bookId = api.newBook("Dune " + round);
                List<Integer> codes = burst(20, i -> api.post("/loans", "{\"bookId\":" + bookId + ",\"memberId\":" + (i + 1) + "}"));
                System.out.println("  round " + round + ": " + tally(codes));
            }

            System.out.println("Race 2: ONE member borrows 10 DIFFERENT books at once (limit is 3)");
            long member = Api.id(api.post("/members", "{\"name\":\"Greedy\",\"email\":\"greedy@example.com\"}"));
            List<Long> books = new ArrayList<>();
            for (int i = 1; i <= 10; i++) books.add(api.newBook("Book " + i));
            List<Integer> codes = burst(10, i -> api.post("/loans", "{\"bookId\":" + books.get(i) + ",\"memberId\":" + member + "}"));
            String loans = api.get("/members/" + member + "/loans").body();
            long open = Pattern.compile("\\{[^}]*\\}").matcher(loans).results()
                .filter(m -> !m.group().contains("returned")).count();   // open loans have no "returned"
            System.out.println("  " + tally(codes) + " -> member " + member + " now has " + open + " open loans");
        }
        Files.deleteIfExists(dbFile);
    }
}
