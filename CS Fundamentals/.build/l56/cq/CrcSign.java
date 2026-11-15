import java.util.zip.CRC32;

public class CrcSign {
    public static void main(String[] args) {
        CRC32 crc = new CRC32();
        crc.update("emma".getBytes());
        int stored = (int) crc.getValue();      // the record header has 4 bytes for it: an int
        boolean asLong = crc.getValue() == stored;
        boolean asInt = (int) crc.getValue() == stored;
        System.out.println(asLong + " " + asInt);
    }
}
