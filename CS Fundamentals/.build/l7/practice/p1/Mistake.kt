class Email(val address: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Email) return false
        return address.equals(other.address, ignoreCase = true)
    }

    override fun hashCode(): Int = address.hashCode()

    override fun toString(): String = "Email($address)"
}
