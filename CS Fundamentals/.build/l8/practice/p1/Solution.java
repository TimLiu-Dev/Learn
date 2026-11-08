import java.util.Objects;

public class Practice {
    static class Vehicle {
        final int wheels;
        Vehicle(int wheels) { this.wheels = wheels; }
        String describe() { return "vehicle with " + wheels + " wheels"; }
        final String honk() { return "beep"; }   // final: subclasses can't override it
    }

    static class Bike extends Vehicle {
        Bike() { super(2); }
    }

    static class Truck extends Vehicle {
        final int load;
        Truck(int load) { super(6); this.load = load; }
        @Override
        String describe() { return super.describe() + ", carrying " + load + " kg"; }
    }

    // CHECKS
    public static void main(String[] args) throws Exception {
        check("a bike describes itself", "vehicle with 2 wheels", () -> new Bike().describe());
        check("a truck adds its load", "vehicle with 6 wheels, carrying 500 kg", () -> new Truck(500).describe());
        check("a truck seen as a Vehicle", "vehicle with 6 wheels, carrying 80 kg", () -> { Vehicle v = new Truck(80); return v.describe(); });
        check("honk stays final", true, () -> java.lang.reflect.Modifier.isFinal(Vehicle.class.getDeclaredMethod("honk").getModifiers()));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
