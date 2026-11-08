import java.util.List;

public record Program(List<Let> lets, Expr result) {
    public record Let(String name, Expr value) {}
}
