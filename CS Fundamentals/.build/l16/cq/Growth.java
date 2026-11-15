public class Growth {
    public static void main(String[] args) {
        int capacity = 10, size = 0;                    // OpenJDK's first capacity
        StringBuilder log = new StringBuilder("" + capacity);
        for (int i = 0; i < 40; i++) {                  // 40 adds
            if (size == capacity) {
                capacity = capacity + (capacity >> 1);  // OpenJDK's "preferred growth"
                log.append(" ").append(capacity);
            }
            size++;
        }
        System.out.println(log);
    }
}
