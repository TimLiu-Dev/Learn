package shelf.store;

class MemoryStoreTest extends StoreContract {
    @Override
    protected Stores newStores() {
        return new Stores(new MemoryBookStore(), new MemoryMemberStore(), new MemoryLoanStore());
    }
}
