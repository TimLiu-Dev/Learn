import org.junit.Assert.assertEquals
import org.junit.Test

val mapping = """
com.example.shelf.LoanDesk -> a.b.a:
    java.util.Map loans -> a
    10:14:void lend(java.lang.String,int) -> a
    15:22:boolean giveBack(int) -> a
    23:30:int count() -> b
com.example.shelf.MainActivity -> a.b.b:
    void onCreate(android.os.Bundle) -> a
""".trimIndent()

class RetraceTest {
    @Test fun `class and method names come back`() {
        assertEquals("at com.example.shelf.LoanDesk.count(SourceFile:25)", retrace(mapping, "at a.b.a.b(SourceFile:25)"))
    }

    @Test fun `the line range picks between methods that share a short name`() {
        assertEquals("at com.example.shelf.LoanDesk.lend(SourceFile:12)", retrace(mapping, "at a.b.a.a(SourceFile:12)"))
        assertEquals("at com.example.shelf.LoanDesk.giveBack(SourceFile:17)", retrace(mapping, "at a.b.a.a(SourceFile:17)"))
    }

    @Test fun `a field named a is not a method named a`() {
        assertEquals("at com.example.shelf.MainActivity.onCreate(SourceFile:40)", retrace(mapping, "at a.b.b.a(SourceFile:40)"))
    }

    @Test fun `classes R8 never renamed stay as they are`() {
        val frame = "at java.util.HashMap.get(HashMap.java:556)"
        assertEquals(frame, retrace(mapping, frame))
    }
}
