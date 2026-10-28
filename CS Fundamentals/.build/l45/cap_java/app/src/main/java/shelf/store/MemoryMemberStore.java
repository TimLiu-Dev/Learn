package shelf.store;

import java.util.Optional;
import java.util.TreeMap;
import shelf.domain.Member;

public final class MemoryMemberStore implements MemberStore {
    private final TreeMap<Long, Member> members = new TreeMap<>();
    private long nextId = 1;

    @Override
    public synchronized Member add(String name, String email) {
        Member member = new Member(nextId++, name, email);
        members.put(member.id(), member);
        return member;
    }

    @Override
    public synchronized Optional<Member> find(long id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public synchronized Optional<Member> findByEmail(String email) {
        return members.values().stream().filter(m -> m.email().equals(email)).findFirst();
    }
}
