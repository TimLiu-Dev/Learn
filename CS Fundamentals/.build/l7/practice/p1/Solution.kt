class Email(val address: String) {
    // The three steps: same object? right type? then compare what defines the value.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Email) return false
        return address.equals(other.address, ignoreCase = true)
    }

    // Hash exactly what equals compares: the address with case ignored.
    override fun hashCode(): Int = address.lowercase().hashCode()

    override fun toString(): String = "Email($address)"
}
