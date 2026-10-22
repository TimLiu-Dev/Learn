package kv;

class MemoryKvStoreTest extends KvStoreContract {
    @Override KvStore newStore() { return new MemoryKvStore(); }
}
