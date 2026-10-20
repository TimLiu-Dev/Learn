public class ThermostatDemo {
    public static void main(String[] args) {
        Thermostat t = new Thermostat(21);
        System.out.println("target=" + t.getTarget() + " current=" + t.getCurrent() + " heating=" + t.isHeating());
        for (int i = 1; i <= 3; i++) t.tick();
        System.out.println("target=" + t.getTarget() + " current=" + t.getCurrent() + " heating=" + t.isHeating());
        t.setTarget(19);
        System.out.println("target=" + t.getTarget() + " current=" + t.getCurrent() + " heating=" + t.isHeating());
        try {
            t.setTarget(40);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
        System.out.println("target is still " + t.getTarget());
    }
}

final class Thermostat {
    private int target;              // private: only Thermostat's own code can touch these
    private int current = 18;

    Thermostat(int target) {
        setTarget(target);           // reuse the validation
    }

    int getTarget() { return target; }

    void setTarget(int value) {
        if (value < 5 || value > 30) {
            throw new IllegalArgumentException("target must be 5..30, was " + value);
        }
        target = value;
    }

    int getCurrent() { return current; }      // getter only: no setCurrent

    boolean isHeating() { return current < target; }   // computed, no field

    void tick() {
        if (current < target) current++;
        else if (current > target) current--;
    }
}
