package budget;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public record Statement(List<Transaction> transactions, List<LineProblem> problems) {
    public static final String HEADER = "date,description,amount";
    private static final Pattern DATE_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public Statement {
        transactions = List.copyOf(transactions);
        problems = List.copyOf(problems);
    }

    /** Parses a whole statement. Bad lines become LineProblems; they never stop the parse. */
    public static Statement parse(List<String> lines) {
        List<Transaction> transactions = new ArrayList<>();
        List<LineProblem> problems = new ArrayList<>();
        String header = lines.isEmpty() ? null : lines.getFirst().replaceFirst("^﻿", "").strip();
        if (header == null || !header.equalsIgnoreCase(HEADER)) {
            problems.add(new LineProblem(1, "expected header '" + HEADER + "'"));
        }
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            switch (parseTransaction(i + 1, lines.get(i))) {
                case Parsed.Ok<Transaction>(Transaction tx) -> transactions.add(tx);
                case Parsed.Bad<Transaction>(String reason) -> problems.add(new LineProblem(i + 1, reason));
            }
        }
        return new Statement(transactions, problems);
    }

    static Parsed<Transaction> parseTransaction(int lineNumber, String text) {
        List<String> fields;
        switch (Csv.splitLine(text)) {
            case Parsed.Ok<List<String>>(List<String> values) -> fields = values.stream().map(String::strip).toList();
            case Parsed.Bad<List<String>>(String reason) -> {
                return Parsed.bad(reason);
            }
        }
        if (fields.size() != 3) return Parsed.bad("expected 3 fields, found " + fields.size());
        String dateText = fields.get(0), description = fields.get(1), amountText = fields.get(2);
        LocalDate date = parseDate(dateText);
        if (date == null) return Parsed.bad("invalid date '" + dateText + "'");
        if (description.isEmpty()) return Parsed.bad("empty description");
        Money amount = Money.parse(amountText);
        if (amount == null) return Parsed.bad("invalid amount '" + amountText + "'");
        if (amount.signum() == 0) return Parsed.bad("zero amount");
        return Parsed.ok(new Transaction(lineNumber, date, description, amount));
    }

    private static LocalDate parseDate(String text) {
        if (!DATE_SHAPE.matcher(text).matches()) return null;
        try {
            return LocalDate.parse(text); // ISO_LOCAL_DATE is strict: 2026-02-30 is rejected
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
