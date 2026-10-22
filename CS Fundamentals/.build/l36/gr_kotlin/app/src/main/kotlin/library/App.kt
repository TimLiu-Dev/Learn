package library

import java.nio.file.Files
import java.time.LocalDate

fun main() {
    val file = Files.createTempFile("library", ".db")
    try {
        val repo = LibraryRepository(file.toString()).apply { createSchema() }
        val ada = repo.addMember("Ada Lovelace", "ada@example.com")
        val alan = repo.addMember("Alan Turing", "alan@example.com")
        val dune = repo.addBook("Dune", 1965)
        val mort = repo.addBook("Mort", 1987)

        val day = LocalDate.parse("2026-10-01")
        println("Ada borrows Dune:  " + repo.lend(dune, ada, day))
        println("Alan borrows Dune: " + repo.lend(dune, alan, day))
        println("Alan borrows Mort: " + repo.lend(mort, alan, day.plusDays(10)))
        println("Ada borrows #99:   " + repo.lend(99, ada, day))
        println("overdue on 10-21:  " + repo.overdueLoans(LocalDate.parse("2026-10-21")))
        println("Dune returned:     " + repo.returnBook(dune, LocalDate.parse("2026-10-21")))
        println("Alan borrows Dune: " + repo.lend(dune, alan, LocalDate.parse("2026-10-21")))
        println("top borrowers:     " + repo.topBorrowers(5))
    } finally {
        Files.deleteIfExists(file)
    }
}
