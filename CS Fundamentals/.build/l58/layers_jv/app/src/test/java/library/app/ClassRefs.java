package library.app;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Every class a compiled class mentions is written in its constant pool (JVMS §4.4).
// Imports are not: they vanish at compile time, so this sees what the code really USES.
final class ClassRefs {
    private static final Pattern DESCRIPTOR = Pattern.compile("L([\\w/$]+);");   // e.g. (Ljava/sql/Connection;)V

    private ClassRefs() {}

    static Set<String> of(InputStream classFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(classFile))) {
            if (in.readInt() != 0xCAFEBABE) throw new IllegalArgumentException("not a class file");
            in.skipBytes(4);                                  // minor and major version
            int count = in.readUnsignedShort();
            Map<Integer, String> texts = new HashMap<>();     // CONSTANT_Utf8 entries, by index
            List<Integer> classNameIndexes = new ArrayList<>();
            for (int i = 1; i < count; i++) {                 // the pool is numbered from 1
                int tag = in.readUnsignedByte();
                switch (tag) {
                    case 1 -> texts.put(i, in.readUTF());     // u2 length + modified UTF-8, as readUTF expects
                    case 7 -> classNameIndexes.add(in.readUnsignedShort());
                    case 8, 16, 19, 20 -> in.skipBytes(2);
                    case 15 -> in.skipBytes(3);
                    case 3, 4, 9, 10, 11, 12, 17, 18 -> in.skipBytes(4);
                    case 5, 6 -> { in.skipBytes(8); i++; }    // Long and Double take two entries
                    default -> throw new IllegalArgumentException("unknown constant pool tag " + tag);
                }
            }
            Set<String> names = new HashSet<>();
            for (int index : classNameIndexes) names.add(texts.get(index).replaceFirst("^\\[+", ""));
            for (String text : texts.values()) {
                Matcher m = DESCRIPTOR.matcher(text);
                while (m.find()) names.add(m.group(1));
            }
            Set<String> result = new HashSet<>();
            for (String n : names) result.add(n.replaceFirst("^L", "").replaceFirst(";$", "").replace('/', '.'));
            return result;
        }
    }
}
