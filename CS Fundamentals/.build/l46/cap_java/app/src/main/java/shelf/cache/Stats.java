package shelf.cache;

public record Stats(long hits, long misses, long evictions) {
    public double hitRate() { return hits + misses == 0 ? 0 : (double) hits / (hits + misses); }
}
