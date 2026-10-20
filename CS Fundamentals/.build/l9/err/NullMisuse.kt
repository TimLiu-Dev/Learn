fun main() {
    val email: String = Directory.findEmail("ada")   // @Nullable into non-null
    Directory.findEmail(null)                        // null into @NonNull
    val phone: Int = Directory.findPhone("ada")      // shows the platform type's name
}
