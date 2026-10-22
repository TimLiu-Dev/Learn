package kv;

import java.nio.file.Files;
import java.nio.file.Path;

class SqliteKvStoreTest extends KvStoreContract {
    private Path file;

    @Override KvStore newStore() throws Exception {
        file = Files.createTempFile("kv", ".db");
        return new SqliteKvStore("jdbc:sqlite:" + file);
    }

    @Override void cleanUp() throws Exception { Files.deleteIfExists(file); }
}
