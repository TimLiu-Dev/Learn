package lending.adapters;

import catalogue.api.CatalogueEvent;
import lending.domain.CopyId;
import lending.domain.Shelf;
import lending.domain.ShelfCopy;
import platform.EventBus;

// The anti-corruption layer: the only lending class that knows the catalogue exists.
// It translates the catalogue's published events into lending's own model.
public final class CatalogueListener {
    private final Shelf shelf;

    public CatalogueListener(Shelf shelf) { this.shelf = shelf; }

    public void subscribeTo(EventBus bus) {
        bus.subscribe(CatalogueEvent.CopyAdded.class,
                e -> shelf.save(ShelfCopy.onShelf(new CopyId(e.barcode()), e.title())));
        bus.subscribe(CatalogueEvent.CopyWithdrawn.class, e -> {
            ShelfCopy copy = shelf.find(new CopyId(e.barcode()));
            if (copy != null) shelf.save(copy.withdrawnFromStock());
        });
    }
}
