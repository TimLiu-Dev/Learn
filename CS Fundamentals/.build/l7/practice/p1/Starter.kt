class Email(val address: String) {
    override fun equals(other: Any?): Boolean = TODO("same address, ignoring letter case")

    override fun hashCode(): Int = TODO("must agree with equals")

    override fun toString(): String = TODO("Email(ADDRESS), as typed")
}
