import org.junit.Assert.*
import org.junit.Test

interface Repo
class MemoryRepo : Repo
class Service(val repo: Repo)
class Handler(val service: Service)
class A(val b: B)
class B(val a: A)
class Lonely(val friend: Friend)
class Friend

class ContainerTest {
    private fun shelf() = Container().apply {
        register(MemoryRepo::class.java)
        register(Service::class.java)
        register(Handler::class.java, Scope.PROTOTYPE)
    }

    @Test fun `a singleton is built once`() {
        val c = shelf()
        assertSame("get(Repo) twice should return the same object", c.get(Repo::class.java), c.get(Repo::class.java))
    }

    @Test fun `a prototype is built on every get`() {
        val c = shelf()
        assertNotSame("two Handlers should be different objects", c.get(Handler::class.java), c.get(Handler::class.java))
    }

    @Test fun `constructor parameters are injected, and prototypes share singletons`() {
        val c = shelf()
        val h1 = c.get(Handler::class.java)
        val h2 = c.get(Handler::class.java)
        assertSame("both Handlers should get the one Service", h1.service, h2.service)
        assertTrue("Service.repo should be the MemoryRepo", h1.service.repo is MemoryRepo)
    }

    @Test fun `a cycle fails with its path`() {
        val c = Container().apply { register(A::class.java); register(B::class.java) }
        val e = assertThrows(IllegalStateException::class.java) { c.get(A::class.java) }
        assertEquals("Dependency cycle: A -> B -> A", e.message)
    }

    @Test fun `a missing bean names the type`() {
        val c = Container().apply { register(Lonely::class.java) }
        val e = assertThrows(IllegalStateException::class.java) { c.get(Lonely::class.java) }
        assertEquals("No bean of type Friend", e.message)
    }

    @Test fun `a failed get leaves the container usable`() {
        val c = Container().apply { register(Lonely::class.java) }
        assertThrows(IllegalStateException::class.java) { c.get(Lonely::class.java) }
        c.register(Friend::class.java)
        assertTrue(c.get(Lonely::class.java).friend is Friend)
    }
}
