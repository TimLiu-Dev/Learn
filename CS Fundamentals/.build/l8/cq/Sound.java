public class Sound {
    static class Animal {
        String sound() { return "..."; }
    }

    static class Dog extends Animal {
        String Sound() { return "Woof"; }
    }

    public static void main(String[] args) {
        Animal a = new Dog();
        System.out.println(a.sound());
    }
}
