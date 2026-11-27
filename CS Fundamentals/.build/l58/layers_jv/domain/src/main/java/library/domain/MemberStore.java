package library.domain;

public interface MemberStore {
    Member find(long memberId);              // null if missing
}
