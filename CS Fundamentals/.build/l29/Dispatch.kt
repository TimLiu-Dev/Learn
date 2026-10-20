import kotlinx.coroutines.*

fun where(label: String) =
    println("%-26s %s".format(label, Thread.currentThread().name))

fun main() = runBlocking {
    println("cores: ${Runtime.getRuntime().availableProcessors()}")
    where("runBlocking:")
    launch(Dispatchers.Default) { where("Default:") }.join()
    launch(Dispatchers.IO) { where("IO:") }.join()
    launch(Dispatchers.Unconfined) {
        where("Unconfined, before delay:")
        delay(10)
        where("Unconfined, after delay:")
    }.join()

    withContext(Dispatchers.Default) {
        where("Default, before delay:")
        delay(10)
        where("Default, after delay:")
    }
    where("back in runBlocking:")
}
