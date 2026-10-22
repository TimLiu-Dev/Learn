package library

import java.nio.file.Files
import java.time.Clock

// The composition root: the only code that names concrete classes and wires them together.
fun main() {
    val file = Files.createTempFile("library", ".db")
    try {
        val url = "jdbc:sqlite:$file"
        Schema.create(url)
        val members = SqliteMemberStore(url)
        val service = LendingService(SqliteLoanStore(url), members, Clock.systemUTC())

        val ada = members.add("Ada").id
        val alan = members.add("Alan").id
        println("Ada borrows book 1:  " + service.lend(1, ada))
        println("Ada borrows book 2:  " + service.lend(2, ada))
        println("Ada borrows book 3:  " + service.lend(3, ada))
        println("Ada borrows book 4:  " + service.lend(4, ada))
        println("Alan borrows book 1: " + service.lend(1, alan))
        println("member 99 borrows:   " + service.lend(5, 99))
        println("Ada returns loan 1:  " + service.returnBook(1))
        println("Ada returns loan 1:  " + service.returnBook(1))
        println("Alan borrows book 1: " + service.lend(1, alan))
    } finally {
        Files.deleteIfExists(file)
    }
}
