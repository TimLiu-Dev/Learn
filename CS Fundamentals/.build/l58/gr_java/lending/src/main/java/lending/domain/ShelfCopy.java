package lending.domain;

// Lending's idea of a "book": one copy, whether it may go out, and who has it.
public record ShelfCopy(CopyId id, String title, boolean withdrawn, MemberId onLoanTo) {
    public static ShelfCopy onShelf(CopyId id, String title) { return new ShelfCopy(id, title, false, null); }
    public ShelfCopy withdrawnFromStock() { return new ShelfCopy(id, title, true, onLoanTo); }
    public ShelfCopy lentTo(MemberId member) { return new ShelfCopy(id, title, withdrawn, member); }
    public ShelfCopy backOnShelf() { return new ShelfCopy(id, title, withdrawn, null); }
}
