import java.time.LocalDate

data class Overdue(val title: String, val member: String, val loaned: LocalDate)

// ---------- Before: one fat interface for everything the library does ----------
interface LibraryService {
    fun addBook(title: String, year: Int): Long
    fun removeBook(id: Long)
    fun lend(bookId: Long, memberId: Long): Boolean
    fun returnBook(bookId: Long): Boolean
    fun overdueLoans(today: LocalDate): List<Overdue>
    fun topBorrowers(limit: Int): List<String>
    fun exportCsv(): String
}

class OverdueEmailerBefore(private val library: LibraryService) {   // uses ONE of the seven methods
    fun drafts(today: LocalDate) = library.overdueLoans(today).map { "To ${it.member}: please return ${it.title}" }
}

// To test it, a fake must still implement all seven.
class FatFake : LibraryService {
    override fun overdueLoans(today: LocalDate) = listOf(Overdue("Dune", "Ada", LocalDate.parse("2026-10-01")))
    override fun addBook(title: String, year: Int): Long = TODO("not needed")
    override fun removeBook(id: Long) = TODO("not needed")
    override fun lend(bookId: Long, memberId: Long): Boolean = TODO("not needed")
    override fun returnBook(bookId: Long): Boolean = TODO("not needed")
    override fun topBorrowers(limit: Int): List<String> = TODO("not needed")
    override fun exportCsv(): String = TODO("not needed")
}

// ---------- After: small role interfaces; clients depend on the role they use ----------
interface BookCatalog { fun addBook(title: String, year: Int): Long; fun removeBook(id: Long) }
interface Lending { fun lend(bookId: Long, memberId: Long): Boolean; fun returnBook(bookId: Long): Boolean }
fun interface OverdueReport { fun overdueLoans(today: LocalDate): List<Overdue> }

class OverdueEmailer(private val report: OverdueReport) {
    fun drafts(today: LocalDate) = report.overdueLoans(today).map { "To ${it.member}: please return ${it.title}" }
}

fun main() {
    val today = LocalDate.parse("2026-10-21")
    println("before: " + OverdueEmailerBefore(FatFake()).drafts(today))
    val fake = OverdueReport { listOf(Overdue("Dune", "Ada", LocalDate.parse("2026-10-01"))) }   // the whole fake
    println("after:  " + OverdueEmailer(fake).drafts(today))
}
