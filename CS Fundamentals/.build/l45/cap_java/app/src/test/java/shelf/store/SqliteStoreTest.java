package shelf.store;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;

class SqliteStoreTest extends StoreContract {
    private Path file;

    @Override
    protected Stores newStores() {
        try {
            file = Files.createTempFile("shelf-test", ".db");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Db db = new Db(file);
        db.createSchema();
        return new Stores(new SqliteBookStore(db), new SqliteMemberStore(db), new SqliteLoanStore(db));
    }

    @AfterEach
    void deleteFile() throws IOException {
        Files.deleteIfExists(file);
    }
}
