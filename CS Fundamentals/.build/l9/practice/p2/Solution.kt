import java.io.File
import java.util.Properties

fun port(config: Properties): Int {
    val raw: String? = config.getProperty("port")   // choose String?: null safety is back on
    return raw?.toIntOrNull() ?: 8080
}

fun folderOf(path: String): String = File(path).parent ?: "."   // getParent() returns null for "notes.txt"

fun describe(path: String): String {
    val file = File(path)
    val kind = if (file.isAbsolute) "absolute" else "relative"
    return "${file.name} ($kind)"
}
