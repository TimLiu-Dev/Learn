import java.util.List;

public class TypeCheck {
    static class Animal {
        String sound() { return "..."; }
        @Override public String toString() { return "Animal"; }
    }

    static class Dog extends Animal {
        @Override String sound() { return "Woof"; }
        String fetch() { return "fetches the ball"; }
        @Override public String toString() { return "Dog"; }
    }

    static class Cat extends Animal {
        @Override String sound() { return "Meow"; }
        @Override public String toString() { return "Cat"; }
    }

    static String describe(Animal a) {
        if (a instanceof Dog d) return d + ": " + d.sound() + ", " + d.fetch();   // pattern binds d
        return a + ": " + a.sound();
    }

    static String soundByChecks(Animal a) {          // a smell: re-doing dispatch by hand
        if (a instanceof Dog) return "Woof";
        else if (a instanceof Cat) return "Meow";
        else return "...";
    }

    public static void main(String[] args) {
        List<Animal> zoo = List.of(new Dog(), new Cat(), new Animal());
        for (Animal a : zoo) System.out.println(describe(a));
        System.out.println(zoo);                     // toString() is dispatched too
        System.out.println(soundByChecks(new Cat()).equals(new Cat().sound()));
    }
}
