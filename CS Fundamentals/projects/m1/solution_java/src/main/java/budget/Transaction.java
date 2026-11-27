package budget;

import java.time.LocalDate;

public record Transaction(int line, LocalDate date, String description, Money amount) {}
