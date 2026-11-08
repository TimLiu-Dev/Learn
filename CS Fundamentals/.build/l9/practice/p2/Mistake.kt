import java.io.File
import java.util.Properties

fun port(config: Properties): Int {
    val raw: String = config.getProperty("port")
    if (raw == null) return 8080                    // too late: the assignment above already threw
    return raw.toIntOrNull() ?: 8080
}

fun folderOf(path: String): String = File(path).parent ?: "."

fun describe(path: String): String {
    val file = File(path)
    val kind = if (file.isAbsolute) "absolute" else "relative"
    return "${file.name} ($kind)"
}
