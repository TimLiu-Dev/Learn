import java.io.IOException;

public class UseFromJava {
    public static void main(String[] args) {
        System.out.println(Temperatures.celsiusToFahrenheit(100.0));   // @file:JvmName
        System.out.println(Temperatures.ABSOLUTE_ZERO_C);              // const val

        Thermometer t = Thermometer.indoor();                 // @JvmStatic: a real static
        Thermometer out = Thermometer.Companion.outdoor();    // no @JvmStatic: through Companion
        System.out.println(t.getLocation() + " online=" + t.isOnline());   // getters
        out.setOnline(false);                                 // isOnline -> setOnline
        System.out.println(out.getLocation() + " online=" + out.isOnline());

        System.out.println(t.record(21.46));                  // overloads from @JvmOverloads
        System.out.println(t.record(21.46, "after lunch"));
        System.out.println(t.record(21.46, "rounded", true));
        System.out.println("readings=" + t.getReadings() + " unit=" + t.unit
                + " max=" + Thermometer.MAX_READINGS);        // @JvmField, const val

        Registry.INSTANCE.register("indoor");                 // object -> INSTANCE
        System.out.println("registered=" + Registry.INSTANCE.getCount());

        try {
            t.calibrate("cal.txt");
        } catch (IOException e) {                             // allowed thanks to @Throws
            System.out.println("caught: " + e.getMessage());
        }
    }
}
