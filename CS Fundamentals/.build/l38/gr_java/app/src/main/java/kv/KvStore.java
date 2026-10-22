package kv;

import java.util.List;

public interface KvStore {
    void put(String key, String value);
    String get(String key);              // null if missing
    boolean delete(String key);
    List<String> keys();                 // sorted
}
