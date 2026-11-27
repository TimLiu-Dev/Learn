package library.app

import java.io.DataInputStream
import java.io.InputStream

// Every class a compiled class mentions is written in its constant pool (JVMS §4.4).
// Imports are not: they vanish at compile time, so this sees what the code really USES.
object ClassRefs {
    private val DESCRIPTOR = Regex("""L([\w/$]+);""")   // e.g. (Ljava/sql/Connection;)V

    fun of(classFile: InputStream): Set<String> = DataInputStream(classFile.buffered()).use { input ->
        require(input.readInt() == 0xCAFEBABE.toInt()) { "not a class file" }
        input.skipBytes(4)                                // minor and major version
        val count = input.readUnsignedShort()
        val texts = HashMap<Int, String>()                // CONSTANT_Utf8 entries, by index
        val classNameIndexes = mutableListOf<Int>()       // CONSTANT_Class -> index of its name
        var i = 1                                         // the pool is numbered from 1
        while (i < count) {
            when (val tag = input.readUnsignedByte()) {
                1 -> texts[i] = input.readUTF()           // u2 length + modified UTF-8, as readUTF expects
                7 -> classNameIndexes += input.readUnsignedShort()
                8, 16, 19, 20 -> input.skipBytes(2)
                15 -> input.skipBytes(3)
                3, 4, 9, 10, 11, 12, 17, 18 -> input.skipBytes(4)
                5, 6 -> { input.skipBytes(8); i++ }       // Long and Double take two entries
                else -> error("unknown constant pool tag $tag")
            }
            i++
        }
        val names = classNameIndexes.map { texts.getValue(it).trimStart('[') }.toMutableSet()
        texts.values.forEach { text -> DESCRIPTOR.findAll(text).forEach { names += it.groupValues[1] } }
        names.map { it.removePrefix("L").removeSuffix(";").replace('/', '.') }.toSet()
    }
}
