public class Puzzle {
    static class Animal {
        String sound() { return "..."; }
    }

    static class Dog extends Animal {
        @Override
        String sound() { return "Woof"; }
    }

    static String describe(Animal a) { return "some animal"; }
    static String describe(Dog d) { return "a dog"; }

    public static void main(String[] args) {
        Animal a = new Dog();     // declared type Animal, runtime type Dog
        Dog d = new Dog();        // declared type Dog
        System.out.println(a.sound());
        System.out.println(describe(a));
        System.out.println(describe(d));
    }
}
