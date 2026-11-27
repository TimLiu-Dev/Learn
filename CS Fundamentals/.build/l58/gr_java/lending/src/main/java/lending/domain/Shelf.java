package lending.domain;

// Ports: what the lending rules need from the outside world. Adapters implement them.
public interface Shelf {
    ShelfCopy find(CopyId id);                 // null if lending has never heard of it
    void save(ShelfCopy copy);
}
