import org.junit.Assert.assertEquals
import org.junit.Test

val realLog = mutableListOf<String>()
open class Base { companion object { init { realLog += "init Base" } } }
class Child : Base() { companion object { init { realLog += "init Child"; Helper.ping() } } }
object Helper { init { realLog += "init Helper" }; fun ping() {} }

class InitTest {
    @Test fun `a class initialises on first use, and only once`() {
        val jvm = MiniJvm(listOf(ClassDef("Registry"), ClassDef("Settings")))
        jvm.use("Registry"); jvm.use("Settings"); jvm.use("Registry")
        assertEquals(listOf("init Registry", "init Settings"), jvm.events)
    }

    @Test fun `nothing initialises until it is used`() {
        val jvm = MiniJvm(listOf(ClassDef("Registry"), ClassDef("Unused")))
        jvm.use("Registry")
        assertEquals(listOf("init Registry"), jvm.events)
    }

    @Test fun `the superclass initialises first, the whole chain`() {
        val jvm = MiniJvm(listOf(ClassDef("Object"), ClassDef("Animal", "Object"), ClassDef("Dog", "Animal")))
        jvm.use("Dog")
        assertEquals(listOf("init Object", "init Animal", "init Dog"), jvm.events)
    }

    @Test fun `an initialiser can trigger other classes`() {
        val jvm = MiniJvm(listOf(ClassDef("Registry", initUses = listOf("CollectionsKt")), ClassDef("CollectionsKt")))
        jvm.use("Registry")
        assertEquals(listOf("init Registry", "init CollectionsKt"), jvm.events)
    }

    @Test(timeout = 2000) fun `a cycle between two initialisers ends`() {
        val jvm = MiniJvm(listOf(ClassDef("A", initUses = listOf("B")), ClassDef("B", initUses = listOf("A"))))
        jvm.use("A")
        assertEquals(listOf("init A", "init B"), jvm.events)
    }

    @Test fun `the simulation matches the real JVM`() {
        Child(); Child()
        val jvm = MiniJvm(listOf(ClassDef("Base"), ClassDef("Child", "Base", listOf("Helper")), ClassDef("Helper")))
        jvm.use("Child"); jvm.use("Child")
        assertEquals("real log", listOf("init Base", "init Child", "init Helper"), realLog)
        assertEquals(realLog, jvm.events)
    }
}
