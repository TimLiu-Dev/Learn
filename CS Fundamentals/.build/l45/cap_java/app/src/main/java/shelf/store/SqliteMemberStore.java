package shelf.store;

import static shelf.store.Db.insert;
import static shelf.store.Db.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import shelf.domain.Member;

public final class SqliteMemberStore implements MemberStore {
    private final Db db;

    public SqliteMemberStore(Db db) {
        this.db = db;
    }

    private static Member member(ResultSet rs) throws SQLException {
        return new Member(rs.getLong(1), rs.getString(2), rs.getString(3));
    }

    @Override
    public Member add(String name, String email) {
        return db.transaction(c ->
            new Member(insert(c, "INSERT INTO members (name, email) VALUES (?, ?)", name, email), name, email));
    }

    @Override
    public Optional<Member> find(long id) {
        return db.read(c ->
            query(c, "SELECT id, name, email FROM members WHERE id = ?", SqliteMemberStore::member, id).stream().findFirst());
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return db.read(c ->
            query(c, "SELECT id, name, email FROM members WHERE email = ?", SqliteMemberStore::member, email).stream().findFirst());
    }
}
