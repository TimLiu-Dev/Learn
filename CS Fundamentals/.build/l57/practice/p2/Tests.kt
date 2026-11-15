import org.junit.Assert.assertEquals
import org.junit.Test

class LogicalClockTest {
    @Test fun `local events and sends count up on each process`() {
        assertEquals(listOf(1L, 2L, 1L), lamportStamps(listOf(Local(0), Send(0, "m"), Local(1))))
    }

    @Test fun `a receive jumps past the message's stamp`() {
        val script = listOf(Local(0), Local(0), Send(0, "m"), Recv(1, "m"))
        assertEquals(listOf(1L, 2L, 3L, 4L), lamportStamps(script))
    }

    @Test fun `a receive never moves a clock backwards`() {
        val script = listOf(Send(0, "m"), Local(1), Local(1), Local(1), Local(1), Recv(1, "m"))
        assertEquals("P2 was already at 4 when m (stamp 1) arrived",
            listOf(1L, 1L, 2L, 3L, 4L, 5L), lamportStamps(script))
    }

    @Test fun `the lesson's script gives the lesson's stamps`() {
        val script = listOf(Local(0), Send(0, "m1"), Local(1), Recv(1, "m1"), Send(1, "m2"),
                            Local(2), Recv(2, "m2"), Local(0))
        assertEquals(listOf(1L, 2L, 1L, 3L, 4L, 1L, 5L, 3L), lamportStamps(script))
    }

    @Test fun `compare spots before, after and equal`() {
        assertEquals(Order.BEFORE, compare(listOf(1, 0, 0), listOf(2, 1, 0)))
        assertEquals(Order.AFTER, compare(listOf(2, 3, 2), listOf(2, 3, 0)))
        assertEquals(Order.EQUAL, compare(listOf(2, 2, 0), listOf(2, 2, 0)))
    }

    @Test fun `compare spots concurrent events`() {
        assertEquals("[2,1,0] vs [1,1,3]", Order.CONCURRENT, compare(listOf(2, 1, 0), listOf(1, 1, 3)))
        assertEquals("[0,1,0] vs [3,0,0]", Order.CONCURRENT, compare(listOf(0, 1, 0), listOf(3, 0, 0)))
    }
}
