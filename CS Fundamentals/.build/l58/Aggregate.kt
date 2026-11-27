import java.time.LocalDate

data class CopyId(val barcode: String)                       // a value object
data class Loan(val copy: CopyId, val loaned: LocalDate)     // another one

// The aggregate root. It carries the version it was loaded at, like L36's version column.
class Borrower(val memberId: Long, val version: Int, loans: List<Loan>) {
    private val loans = loans.toMutableList()
    val openLoans: List<Loan> get() = loans.toList()

    fun lend(copy: CopyId, today: LocalDate): String {
        if (loans.size >= 3) return "Blocked: already has 3 loans"
        loans += Loan(copy, today)
        return "Lent ${copy.barcode}"
    }
}

class ConcurrentUpdate(message: String) : Exception(message)

// Saves a whole aggregate at once, and only if nobody saved it since it was loaded.
class BorrowerRepository {
    private val rows = HashMap<Long, Pair<Int, List<Loan>>>()   // memberId -> (version, loans)

    fun load(id: Long): Borrower {
        val (version, loans) = rows[id] ?: (0 to emptyList())
        return Borrower(id, version, loans)
    }

    fun save(b: Borrower) {
        val current = rows[b.memberId]?.first ?: 0
        if (current != b.version) throw ConcurrentUpdate("member ${b.memberId} is at version $current; you loaded ${b.version}")
        rows[b.memberId] = (current + 1) to b.openLoans
    }
}

fun main() {
    println("same value, same object? ${CopyId("C-1") == CopyId("C-1")}, ${CopyId("C-1") === CopyId("C-1")}")
    val repo = BorrowerRepository()
    val today = LocalDate.of(2026, 11, 26)
    repo.load(1).also { it.lend(CopyId("C-1"), today); it.lend(CopyId("C-2"), today); repo.save(it) }

    // Two desks load Ada's account at the same moment. Each lends her a third book.
    val desk1 = repo.load(1)
    val desk2 = repo.load(1)
    println("desk 1: " + desk1.lend(CopyId("C-3"), today))
    repo.save(desk1)
    println("desk 2: " + desk2.lend(CopyId("C-4"), today))
    try {
        repo.save(desk2)
    } catch (e: ConcurrentUpdate) {
        println("desk 2 save refused: ${e.message}")
        println("desk 2 reloads and retries: " + repo.load(1).lend(CopyId("C-4"), today))
    }
    println("Ada's loans: " + repo.load(1).openLoans.map { it.copy.barcode })
}
