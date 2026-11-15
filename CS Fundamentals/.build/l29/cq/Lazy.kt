import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() {
    val log = mutableListOf<String>()
    val numbers = flow {
        for (i in 1..5) {
            log += "e$i"
            emit(i)
        }
    }
    runBlocking {
        numbers
            .filter { it % 2 == 1 }
            .map { it * 10 }
            .take(2)
            .collect { log += "c$it" }
    }
    println(log.joinToString(" "))
}
