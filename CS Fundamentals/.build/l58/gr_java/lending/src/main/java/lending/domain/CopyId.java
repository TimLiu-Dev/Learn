package lending.domain;

// Lending's own words. A "copy" here is a physical item on the shelf; lending never needs
// an author or an ISBN, so it doesn't have them. Value objects: compared by value, valid on creation.
public record CopyId(String barcode) {
    public CopyId {
        if (barcode == null || barcode.isBlank()) throw new IllegalArgumentException("a barcode can't be blank");
    }
}
