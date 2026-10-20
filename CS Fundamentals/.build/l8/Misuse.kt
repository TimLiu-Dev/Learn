open class EmailNotifier {                       // Kotlin made us type `open` to allow this
    fun send(to: String, message: String) = println("[email] to $to: ${format(message)}")
    protected open fun format(message: String) = message
}

open class SmsNotifier {
    fun send(to: String, message: String) = println("[sms] to $to: ${format(message)}")
    protected open fun format(message: String) = message
}

// "Urgent" bolted on by subclassing: one subclass per channel, same code twice
class UrgentEmailNotifier : EmailNotifier() {
    override fun format(message: String) = "URGENT: " + super.format(message)
}

class UrgentSmsNotifier : SmsNotifier() {
    override fun format(message: String) = "URGENT: " + super.format(message)
}

fun main() {
    UrgentEmailNotifier().send("ada", "Server down")
    UrgentSmsNotifier().send("ada", "Server down")
}
