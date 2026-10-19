fun main() {
    val names = mutableListOf("Ada")
    names.add("Linus")              // fine: val freezes the reference, not the list
    // names = mutableListOf()      // this is what val forbids
    println(names)

    val readOnly: List<String> = names   // a read-only VIEW of the same list
    names.add("Grace")
    println(readOnly)                    // sees the change

    val snapshot = names.toList()        // a COPY
    names.add("Alan")
    println(snapshot)
    println(names)
}
