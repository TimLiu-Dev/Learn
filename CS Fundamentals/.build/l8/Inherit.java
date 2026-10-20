import java.util.List;

public class Inherit {
    static class Employee {
        private final String name;

        Employee(String name) { this.name = name; }

        String getName() { return name; }

        String describe() { return "Employee " + name; }

        final String badge() { return "[" + name.toUpperCase() + "]"; }   // final: can't be overridden
    }

    static class Manager extends Employee {
        private final int reports;

        Manager(String name, int reports) {
            super(name);                       // run Employee's constructor
            this.reports = reports;
        }

        @Override
        String describe() { return super.describe() + ", manages " + reports; }
    }

    static class Intern extends Employee {
        Intern(String name) { super(name); }

        @Override
        final String describe() { return "Intern " + getName(); }   // overridden, and closed again
    }

    public static void main(String[] args) {
        List<Employee> people = List.of(new Employee("Ada"), new Manager("Grace", 3), new Intern("Linus"));
        for (Employee p : people) System.out.println(p.badge() + " " + p.describe());
    }
}
