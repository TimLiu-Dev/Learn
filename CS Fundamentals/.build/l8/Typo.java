public class Typo {
    static class Animal {
        String sound() { return "..."; }
    }

    static class Dog extends Animal {
        String sond() { return "Woof"; }   // typo: this is a NEW method, not an override
    }

    public static void main(String[] args) {
        Animal a = new Dog();
        System.out.println(a.sound());
    }
}
