import java.lang.reflect.Modifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleTest {
    @Test fun `a bike describes itself`() {
        assertEquals("vehicle with 2 wheels", Bike().describe())
    }

    @Test fun `a truck adds its load`() {
        assertEquals("vehicle with 6 wheels, carrying 500 kg", Truck(500).describe())
    }

    @Test fun `a truck seen as a Vehicle still runs the truck's describe`() {
        val v: Vehicle = Truck(80)
        assertEquals("vehicle with 6 wheels, carrying 80 kg", v.describe())
    }

    @Test fun `honk stays final`() {
        val honk = Vehicle::class.java.getMethod("honk")
        assertTrue("honk() must not be open", Modifier.isFinal(honk.modifiers))
        assertEquals("beep", Truck(1).honk())
    }
}
