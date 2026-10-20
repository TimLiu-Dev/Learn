package demo;

public class App {
    public static void main(String[] args) {
        Counter c = new UnsafeCounter();
        for (int i = 0; i < 3; i++) c.increment();
        System.out.println("one thread, three increments: " + c.value());
    }
}
