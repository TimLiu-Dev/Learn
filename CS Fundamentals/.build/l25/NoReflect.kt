package demo

class Book(val title: String, val pages: Int)

fun main() {
    val prop = Book::title                    // a KProperty1: part of the stdlib
    println("name: ${prop.name}")             // works without kotlin-reflect
    println("get:  ${prop.get(Book("Dune", 412))}")
    println("type: ${prop.returnType}")       // needs kotlin-reflect at runtime
}
