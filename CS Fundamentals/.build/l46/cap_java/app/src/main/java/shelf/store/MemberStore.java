package shelf.store;

import java.util.Optional;
import shelf.domain.Member;

public interface MemberStore {
    Member add(String name, String email);
    Optional<Member> find(long id);
    Optional<Member> findByEmail(String email);
    /** Insert unless the email is taken, as ONE atomic step; empty if it was taken. */
    Optional<Member> tryAdd(String name, String email);
}
