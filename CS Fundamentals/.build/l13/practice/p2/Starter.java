import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class Animal {
        final String name;
        Animal(String name) { this.name = name; }
    }
    static class Cat extends Animal {
        Cat(String name) { super(name); }
    }

    // Java has no "out"/"in" on a declaration, so these stay as they are.
    interface Kennel<T> { T release(); int size(); }
    interface Vet<T> { String treat(T animal); }

    static class CatKennel implements Kennel<Cat> {
        private final List<Cat> cats;
        CatKennel(Cat... cats) { this.cats = new ArrayList<>(List.of(cats)); }
        public Cat release() { return cats.remove(0); }
        public int size() { return cats.size(); }
    }

    static class AnyVet implements Vet<Animal> {
        public String treat(Animal animal) { return "treated " + animal.name; }
    }

    static String rollCall(Animal[] animals) {
        StringBuilder sb = new StringBuilder();
        for (Animal a : animals) { if (sb.length() > 0) sb.append(" "); sb.append(a.name); }
        return sb.toString();
    }

    // Fix these two signatures with wildcards, at the use site.
    static String releaseFirst(Kennel<Animal> kennel) { return kennel.release().name; }
    static String checkUp(Cat cat, Vet<Cat> vet) { return vet.treat(cat); }

    static String kennelDemo() {
        throw new UnsupportedOperationException("TODO: return releaseFirst(new CatKennel(new Cat(\"Mia\"), new Cat(\"Tom\")))");
    }
    static String vetDemo() {
        throw new UnsupportedOperationException("TODO: return checkUp(new Cat(\"Kit\"), new AnyVet())");
    }
    static String rollCallDemo() {
        Cat[] cats = { new Cat("Mia"), new Cat("Tom") };
        return rollCall(cats);              // compiles already: why?
    }

    // CHECKS
    public static void main(String[] args) {
        check("a cat kennel can be used as a kennel of animals", "Mia", () -> kennelDemo());
        check("a vet for any animal can treat a cat", "treated Kit", () -> vetDemo());
        check("an array of cats can be read as animals", "Mia Tom", () -> rollCallDemo());
        check("a kennel releases cats in arrival order", "Ada Bo 0", () -> {
            CatKennel k = new CatKennel(new Cat("Ada"), new Cat("Bo"));
            return k.release().name + " " + k.release().name + " " + k.size();
        });
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
