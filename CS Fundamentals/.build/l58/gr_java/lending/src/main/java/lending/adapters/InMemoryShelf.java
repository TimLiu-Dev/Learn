package lending.adapters;

import java.util.HashMap;
import java.util.Map;
import lending.domain.CopyId;
import lending.domain.Shelf;
import lending.domain.ShelfCopy;

public final class InMemoryShelf implements Shelf {
    private final Map<CopyId, ShelfCopy> copies = new HashMap<>();

    @Override public ShelfCopy find(CopyId id) { return copies.get(id); }
    @Override public void save(ShelfCopy copy) { copies.put(copy.id(), copy); }
}
