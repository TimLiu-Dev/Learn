public class Holder {
    static class Config {
        Config() { System.out.println("Config built"); }
    }

    static class Lazy {
        static final Config INSTANCE = new Config();
    }

    public static void main(String[] args) {
        System.out.println("main starts");
        Config a = Lazy.INSTANCE;
        Config b = Lazy.INSTANCE;
        System.out.println(a == b);
    }
}
