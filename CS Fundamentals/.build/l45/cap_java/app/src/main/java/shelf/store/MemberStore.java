package shelf.store;

import java.util.Optional;
import shelf.domain.Member;

public interface MemberStore {
    Member add(String name, String email);
    Optional<Member> find(long id);
    Optional<Member> findByEmail(String email);
}
