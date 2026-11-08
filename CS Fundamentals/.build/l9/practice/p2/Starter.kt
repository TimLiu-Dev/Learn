import java.io.File
import java.util.Properties

fun port(config: Properties): Int {
    val raw: String = config.getProperty("port")   // getProperty returns String! (a platform type)
    return raw.toInt()
}

fun folderOf(path: String): String = TODO("File(path)'s parent folder, or \".\" if it has none")

fun describe(path: String): String = TODO("\"NAME (absolute)\" or \"NAME (relative)\"")
