public class ArrayStore {
    public static void main(String[] args) {
        String[] names = {"Ada", "Linus"};
        Object[] things = names;              // allowed: Java arrays are covariant
        System.out.println(things[0]);        // reading is fine
        things[1] = 42;                       // compiles... but the array is really a String[]
        String n = names[1];                  // never reached: the JVM checks every array store
        System.out.println(n.length());
    }
}
