package lending.domain;

public interface Borrowers {
    Borrower find(MemberId id);                // a member with no history yet gets a fresh Borrower
    void save(Borrower borrower);
}
