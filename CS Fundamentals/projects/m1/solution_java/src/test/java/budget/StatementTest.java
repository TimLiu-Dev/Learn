package budget;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StatementTest {
    private static List<String> problemsIn(String... lines) {
        List<String> all = Stream.concat(Stream.of(Statement.HEADER), Stream.of(lines)).toList();
        return Statement.parse(all).problems().stream().map(LineProblem::reason).toList();
    }

    @Test
    void parsesAGoodLineWithItsLineNumber() {
        Statement s = Statement.parse(List.of(Statement.HEADER, "", "2026-02-28,\"Café Nero, York\",-3.10"));
        assertEquals(List.of(new Transaction(3, LocalDate.of(2026, 2, 28), "Café Nero, York", Money.parse("-3.10"))),
            s.transactions());
        assertEquals(List.of(), s.problems());
    }

    @Test
    void reportsOneReasonPerBadLine() {
        assertEquals(List.of(
                "expected 3 fields, found 4",
                "invalid date '2026-02-30'",
                "invalid date '03/01/2026'",
                "empty description",
                "invalid amount '12.345'",
                "zero amount"),
            problemsIn(
                "2026-01-03,Tesco,Leeds,-1.00",
                "2026-02-30,Tesco,-1.00",
                "03/01/2026,Tesco,-1.00",
                "2026-01-03,  ,-1.00",
                "2026-01-03,Tesco,12.345",
                "2026-01-03,Tesco,0.00"));
    }

    @Test
    void acceptsABomAndAnyCaseInTheHeader() {
        assertEquals(List.of(), Statement.parse(List.of("﻿Date,Description,Amount")).problems());
    }

    @Test
    void aMissingHeaderIsAProblemOnLine1() {
        Statement s = Statement.parse(List.of("2026-01-03,Tesco,-1.00"));
        assertEquals(List.of(new LineProblem(1, "expected header 'date,description,amount'")), s.problems());
        assertEquals(List.of(), s.transactions());
    }
}
