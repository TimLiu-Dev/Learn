import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Practice {
    static class Animal {
        String sound() { return "..."; }
    }

    static class Dog extends Animal { }
    static class Cat extends Animal { }
    static class Cow extends Animal { }

    // A chain of type checks: every new animal means editing this method.
    static String soundOf(Animal a) {
        if (a instanceof Dog) return "Woof";
        if (a instanceof Cat) return "Meow";
        return "...";
    }

    static String chorus(List<Animal> animals) {
        return animals.stream().map(Practice::soundOf).collect(Collectors.joining(" "));
    }

    // CHECKS
    static class Duck extends Animal {
        @Override String sound() { return "Quack"; }
    }

    public static void main(String[] args) {
        check("a dog knows its own sound", "Woof", () -> new Dog().sound());
        check("the cow is not forgotten", "Moo", () -> new Cow().sound());
        check("chorus uses each animal's sound", "Woof Meow Moo", () -> chorus(List.of(new Dog(), new Cat(), new Cow())));
        check("a new subclass works without editing chorus", "Quack Woof", () -> chorus(List.of(new Duck(), new Dog())));
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
