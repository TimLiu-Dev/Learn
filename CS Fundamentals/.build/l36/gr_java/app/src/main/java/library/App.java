package library;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class App {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("library", ".db");
        try {
            LibraryRepository repo = new LibraryRepository(file.toString());
            repo.createSchema();
            long ada = repo.addMember("Ada Lovelace", "ada@example.com");
            long alan = repo.addMember("Alan Turing", "alan@example.com");
            long dune = repo.addBook("Dune", 1965);
            long mort = repo.addBook("Mort", 1987);

            LocalDate day = LocalDate.parse("2026-10-01");
            System.out.println("Ada borrows Dune:  " + repo.lend(dune, ada, day));
            System.out.println("Alan borrows Dune: " + repo.lend(dune, alan, day));
            System.out.println("Alan borrows Mort: " + repo.lend(mort, alan, day.plusDays(10)));
            System.out.println("Ada borrows #99:   " + repo.lend(99, ada, day));
            System.out.println("overdue on 10-21:  " + repo.overdueLoans(LocalDate.parse("2026-10-21")));
            System.out.println("Dune returned:     " + repo.returnBook(dune, LocalDate.parse("2026-10-21")));
            System.out.println("Alan borrows Dune: " + repo.lend(dune, alan, LocalDate.parse("2026-10-21")));
            System.out.println("top borrowers:     " + repo.topBorrowers(5));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
