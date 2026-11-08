import java.util.List;

public record Chunk(int[] code, String[] where, int maxStack, List<String> locals) {}
