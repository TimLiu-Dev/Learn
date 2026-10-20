open class Employee(val name: String) {
    open fun describe(): String = "Employee $name"
    fun badge(): String = "[${name.uppercase()}]"     // not open: subclasses can't change it
}

class Manager(name: String, val reports: Int) : Employee(name) {   // calls Employee's constructor
    override fun describe(): String = super.describe() + ", manages $reports"
}

open class Intern(name: String) : Employee(name) {
    final override fun describe(): String = "Intern $name"   // overridden, and closed again
}

fun main() {
    val people = listOf(Employee("Ada"), Manager("Grace", 3), Intern("Linus"))
    for (p in people) println("${p.badge()} ${p.describe()}")
}
