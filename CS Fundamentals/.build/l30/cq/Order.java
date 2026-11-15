import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Order {
    public static void main(String[] args) {
        byte[] prefix = {0, 0, 1, 0};               // 4 bytes read off the wire
        int big = ByteBuffer.wrap(prefix).getInt();
        int little = ByteBuffer.wrap(prefix).order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(big + " " + little);
    }
}
