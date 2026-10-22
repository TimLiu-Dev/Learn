package library;

import java.nio.file.Files;
import java.nio.file.Path;
import library.SqliteStores.SqliteLoanStore;
import library.SqliteStores.SqliteMemberStore;

class SqliteLoanStoreTest extends LoanStoreContract {
    private Path file;

    @Override LoanStore newStore() throws Exception {
        file = Files.createTempFile("loans", ".db");
        String url = "jdbc:sqlite:" + file;
        SqliteStores.createSchema(url);
        SqliteMemberStore members = new SqliteMemberStore(url);   // foreign keys: members 1 and 2 must exist
        members.add("Ada");
        members.add("Alan");
        return new SqliteLoanStore(url);
    }

    @Override void cleanUp() throws Exception { Files.deleteIfExists(file); }
}
