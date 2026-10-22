package library;

import java.util.HashMap;
import java.util.Map;

final class FakeMemberStore implements MemberStore {
    private final Map<Long, Member> byId = new HashMap<>();

    FakeMemberStore(Member... members) {
        for (Member m : members) byId.put(m.id(), m);
    }

    public Member find(long memberId) { return byId.get(memberId); }
}
