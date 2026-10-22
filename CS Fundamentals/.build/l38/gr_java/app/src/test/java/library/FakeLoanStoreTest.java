package library;

class FakeLoanStoreTest extends LoanStoreContract {
    @Override LoanStore newStore() { return new FakeLoanStore(); }
}
