// Tiny runner used only to verify the demo: calls every @Test method, skipping @Disabled ones.
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

fun main() {
    val cls = fees.LateFeeTest::class.java
    for (m in cls.declaredMethods.sortedBy { it.name }) {
        if (!m.isAnnotationPresent(Test::class.java)) continue
        if (m.isAnnotationPresent(Disabled::class.java)) { println("SKIPPED ${m.name}"); continue }
        val result = runCatching { m.invoke(cls.getDeclaredConstructor().newInstance()) }
        println((if (result.isSuccess) "PASSED " else "FAILED ") + m.name)
    }
}
