package catalogue.api;

// The catalogue's published language: what other modules may know. Plain values only;
// no catalogue.internal type ever appears in these signatures.
public sealed interface CatalogueEvent {
    record CopyAdded(String barcode, String isbn, String title) implements CatalogueEvent {}
    record CopyWithdrawn(String barcode) implements CatalogueEvent {}
}
