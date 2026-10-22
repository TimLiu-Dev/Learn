package kv;

import java.util.List;
import java.util.TreeMap;

public final class MemoryKvStore implements KvStore {
    private final TreeMap<String, String> map = new TreeMap<>();
    public void put(String key, String value) { map.put(key, value); }
    public String get(String key) { return map.get(key); }
    public boolean delete(String key) { return map.remove(key) != null; }
    public List<String> keys() { return List.copyOf(map.keySet()); }
}
