package library;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import library.SqliteStores.SqliteLoanStore;
import library.SqliteStores.SqliteMemberStore;

// The composition root: the only code that names concrete classes and wires them together.
public class App {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("library", ".db");
        try {
            String url = "jdbc:sqlite:" + file;
            SqliteStores.createSchema(url);
            SqliteMemberStore members = new SqliteMemberStore(url);
            LendingService service = new LendingService(new SqliteLoanStore(url), members, Clock.systemUTC());

            long ada = members.add("Ada").id();
            long alan = members.add("Alan").id();
            System.out.println("Ada borrows book 1:  " + service.lend(1, ada));
            System.out.println("Ada borrows book 2:  " + service.lend(2, ada));
            System.out.println("Ada borrows book 3:  " + service.lend(3, ada));
            System.out.println("Ada borrows book 4:  " + service.lend(4, ada));
            System.out.println("Alan borrows book 1: " + service.lend(1, alan));
            System.out.println("member 99 borrows:   " + service.lend(5, 99));
            System.out.println("Ada returns loan 1:  " + service.returnBook(1));
            System.out.println("Ada returns loan 1:  " + service.returnBook(1));
            System.out.println("Alan borrows book 1: " + service.lend(1, alan));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
