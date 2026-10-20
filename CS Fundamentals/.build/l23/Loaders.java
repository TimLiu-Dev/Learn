public class Loaders {
    static class Mine {}

    public static void main(String[] args) {
        System.out.println("String     -> " + String.class.getClassLoader());
        System.out.println("java.sql   -> " + java.sql.Connection.class.getClassLoader());
        System.out.println("Mine       -> " + Mine.class.getClassLoader());
        System.out.println("Mine's parent  -> " + Mine.class.getClassLoader().getParent());
    }
}
