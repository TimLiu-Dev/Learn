import java.time.Instant;
import java.time.LocalDate;
import tools.jackson.databind.json.JsonMapper;

public class Dates {
    public record Loan(String book, Instant at, LocalDate due) {}

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();     // Jackson 3: java.time built in, ISO strings
        var loan = new Loan("Dune", Instant.parse("2026-10-20T09:30:00Z"), LocalDate.of(2026, 11, 3));
        String text = mapper.writeValueAsString(loan);
        System.out.println(text);
        System.out.println(mapper.readValue(text, Loan.class));
    }
}
