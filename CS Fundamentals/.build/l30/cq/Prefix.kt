import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

fun main() {
    val buffer = ByteArrayOutputStream()
    DataOutputStream(buffer).writeInt(300)     // a length prefix for a 300-byte message
    println(buffer.toByteArray().toList())
}
