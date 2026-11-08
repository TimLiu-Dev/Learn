import java.util.Properties

fun main() {
    val props = Properties()          // a Java class: getProperty has no nullability annotation
    val log = StringBuilder()
    try {
        val host: String = props.getProperty("host")
        log.append("stored ")
        log.append(host.length)
    } catch (e: NullPointerException) {
        log.append("NPE")
    }
    println(log)
}
