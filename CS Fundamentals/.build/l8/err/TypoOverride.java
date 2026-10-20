public class TypoOverride {
    static class Animal {
        String sound() { return "..."; }
    }

    static class Dog extends Animal {
        @Override
        String sond() { return "Woof"; }
    }
}
