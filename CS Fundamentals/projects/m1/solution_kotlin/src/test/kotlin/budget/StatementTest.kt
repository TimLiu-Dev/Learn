package budget

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class StatementTest {
    private fun problemsIn(vararg lines: String) = parseStatement(listOf(HEADER) + lines).problems.map { it.reason }

    @Test
    fun `parses a good line with its line number`() {
        val statement = parseStatement(listOf(HEADER, "", "2026-02-28,\"Café Nero, York\",-3.10"))
        assertEquals(
            listOf(Transaction(3, LocalDate.of(2026, 2, 28), "Café Nero, York", Money(-310))),
            statement.transactions,
        )
        assertEquals(emptyList(), statement.problems)
    }

    @Test
    fun `reports one reason per bad line`() {
        assertEquals(
            listOf(
                "expected 3 fields, found 4",
                "invalid date '2026-02-30'",
                "invalid date '03/01/2026'",
                "empty description",
                "invalid amount '12.345'",
                "zero amount",
            ),
            problemsIn(
                "2026-01-03,Tesco,Leeds,-1.00",
                "2026-02-30,Tesco,-1.00",
                "03/01/2026,Tesco,-1.00",
                "2026-01-03,  ,-1.00",
                "2026-01-03,Tesco,12.345",
                "2026-01-03,Tesco,0.00",
            ),
        )
    }

    @Test
    fun `accepts a BOM and any case in the header`() {
        assertEquals(emptyList(), parseStatement(listOf("﻿Date,Description,Amount")).problems)
    }

    @Test
    fun `a missing header is a problem on line 1`() {
        val statement = parseStatement(listOf("2026-01-03,Tesco,-1.00"))
        assertEquals(listOf(LineProblem(1, "expected header 'date,description,amount'")), statement.problems)
        assertEquals(emptyList(), statement.transactions)
    }
}
