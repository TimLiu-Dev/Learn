package library;

public interface MemberStore {
    Member find(long memberId);              // null if missing
}
