public class TempDemo {
    public static void main(String[] args) {
        Temperature t = new Temperature(100.0);
        System.out.println(t.getCelsius() + "C = " + t.getFahrenheit() + "F");
        t.setFahrenheit(32.0);
        System.out.println(t.getCelsius() + "C = " + t.getFahrenheit() + "F");
        try {
            t.setCelsius(-300.0);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}

final class Temperature {
    private double celsius;

    Temperature(double celsius) { setCelsius(celsius); }

    double getCelsius() { return celsius; }

    void setCelsius(double value) {
        if (value < -273.15) throw new IllegalArgumentException("below absolute zero: " + value);
        celsius = value;
    }

    double getFahrenheit() { return celsius * 9 / 5 + 32; }

    void setFahrenheit(double value) { setCelsius((value - 32) * 5 / 9); }
}
