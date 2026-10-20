class Account(private var balance: Long) {
    fun deposit(cents: Int) { balance += cents }
    private fun audit(): String = "balance=$balance"
}

fun main() {
    val k = Account::class.java                   // plain Java reflection
    println("fields:  " + k.declaredFields.map { it.name }.sorted())
    println("methods: " + k.declaredMethods.map { it.name }.sorted())

    val acct = Account(1_000)
    // Look a method up by name + parameter types, then call it.
    val deposit = k.getMethod("deposit", Int::class.javaPrimitiveType)
    deposit.invoke(acct, 250)

    // Private members: allowed only after switching off access checks.
    val audit = k.getDeclaredMethod("audit").apply { isAccessible = true }
    println(audit.invoke(acct))

    val field = k.getDeclaredField("balance").apply { isAccessible = true }
    field.setLong(acct, 5)                        // bypasses every rule you wrote
    println(audit.invoke(acct))
}
