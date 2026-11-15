fun interface Mailer {
    fun send(to: String)
}

class RecordingMailer : Mailer {
    val sent = mutableListOf<String>()
    override fun send(to: String) { sent += to }
}

class ReminderJob(private val mailer: Mailer) {
    fun run(members: List<String>) = members.forEach { mailer.send(it) }
}

fun main() {
    val spy = RecordingMailer()
    val before = spy.sent
    ReminderJob(spy).run(listOf("ada", "alan"))
    println("${before.size} ${before === spy.sent}")
}
