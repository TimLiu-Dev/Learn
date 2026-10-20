fun main() {
    val jdk = HashMap<String?, Int?>()
    jdk[null] = 0                         // HashMap allows one null key...
    jdk["ghost"] = null                   // ...and null values
    println("${jdk[null]} ${jdk["ghost"]} ${jdk["missing"]}")
    println("containsKey: ${jdk.containsKey("ghost")} ${jdk.containsKey("missing")}")

    val ages: Map<String, Int> = mapOf("ada" to 36)   // V is non-null Int...
    val age: Int? = ages["bob"]                        // ...so null can only mean "absent"
    println("bob: $age, or default ${ages.getOrDefault("bob", -1)}")
    println(ages.getValue("bob"))                      // throws if absent
}
