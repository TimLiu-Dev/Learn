fun main() {
    val t = Thread({ print(Thread.currentThread().name + " ") }, "w")
    t.run()
    print("${t.state} ")
    t.start()
    t.join()
    println(t.state)
}
