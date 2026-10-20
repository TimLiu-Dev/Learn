class ThisDemo {
    val name = "outer ThisDemo"

    fun run() {
        val lambda = { println("lambda this: " + this.name) }
        val anon = object : Runnable {
            val name = "object expression"
            override fun run() { println("anon this:   " + this.name) }
        }
        lambda()
        anon.run()
    }
}

fun main() = ThisDemo().run()
