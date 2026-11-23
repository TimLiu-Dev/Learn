package budget

import java.time.LocalDate
import java.time.format.DateTimeParseException

const val HEADER = "date,description,amount"

data class Transaction(val line: Int, val date: LocalDate, val description: String, val amount: Money)

data class LineProblem(val line: Int, val reason: String)

data class Statement(val transactions: List<Transaction>, val problems: List<LineProblem>)

private val DATE_SHAPE = Regex("""\d{4}-\d{2}-\d{2}""")

/** Parses a whole statement. Bad lines become [LineProblem]s; they never stop the parse. */
fun parseStatement(lines: List<String>): Statement {
    val transactions = mutableListOf<Transaction>()
    val problems = mutableListOf<LineProblem>()
    val header = lines.firstOrNull()?.removePrefix("﻿")?.trim()
    if (header == null || !header.equals(HEADER, ignoreCase = true)) {
        problems += LineProblem(1, "expected header '$HEADER'")
    }
    lines.withIndex().drop(1).filter { it.value.isNotBlank() }.forEach { (index, text) ->
        when (val parsed = parseTransaction(index + 1, text)) {
            is Parsed.Ok -> transactions += parsed.value
            is Parsed.Bad -> problems += LineProblem(index + 1, parsed.reason)
        }
    }
    return Statement(transactions, problems)
}

fun parseTransaction(lineNumber: Int, text: String): Parsed<Transaction> {
    val fields = when (val split = splitCsvLine(text)) {
        is Parsed.Ok -> split.value.map { it.trim() }
        is Parsed.Bad -> return split
    }
    if (fields.size != 3) return Parsed.Bad("expected 3 fields, found ${fields.size}")
    val (dateText, description, amountText) = fields
    val date = parseDate(dateText) ?: return Parsed.Bad("invalid date '$dateText'")
    if (description.isEmpty()) return Parsed.Bad("empty description")
    val amount = Money.parse(amountText) ?: return Parsed.Bad("invalid amount '$amountText'")
    if (amount == Money.ZERO) return Parsed.Bad("zero amount")
    return Parsed.Ok(Transaction(lineNumber, date, description, amount))
}

private fun parseDate(text: String): LocalDate? =
    if (!DATE_SHAPE.matches(text)) null
    else try {
        LocalDate.parse(text) // ISO_LOCAL_DATE is strict: 2026-02-30 is rejected
    } catch (e: DateTimeParseException) {
        null
    }
