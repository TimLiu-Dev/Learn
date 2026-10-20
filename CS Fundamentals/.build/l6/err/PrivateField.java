public class PrivateField {
    public static void main(String[] args) {
        Thermostat t = new Thermostat();
        t.current = 50;
    }
}

final class Thermostat {
    private int current = 18;
}
