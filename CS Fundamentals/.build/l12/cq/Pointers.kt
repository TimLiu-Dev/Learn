fun main() {
    val branches = mutableMapOf("main" to "c1")
    branches["fix"] = branches.getValue("main")   // git branch fix
    branches["fix"] = "c2"                         // a commit on fix
    println(branches)
}
