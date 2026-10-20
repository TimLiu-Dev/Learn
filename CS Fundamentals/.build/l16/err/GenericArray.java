public class GenericArray<T> {
    private T[] data = new T[4];                      // wants a real T at runtime

    public static void main(String[] args) {
        System.out.println(new GenericArray<String>());
    }
}
