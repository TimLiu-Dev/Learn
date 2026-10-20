import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun numbers(): Flow<Int> = flow {
    println("  flow block starts")
    for (i in 1..3) {
        delay(100)                 // may suspend: a Sequence can't
        println("  emit $i")
        emit(i)
    }
}

fun main() = runBlocking {
    val f = numbers()
    println("flow created, nothing ran yet")
    f.collect { println("collected $it") }
    println("collect again, through map:")
    println(f.map { it * 10 }.toList())
}
