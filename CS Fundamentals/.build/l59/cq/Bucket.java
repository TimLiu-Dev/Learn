public class Bucket {
    public static void main(String[] args) {
        String user = "polygenelubricants";      // its hashCode() is Integer.MIN_VALUE
        int bucket = Math.abs(user.hashCode()) % 100;
        System.out.println(bucket < 10 ? "canary " + bucket : "stable " + bucket);
    }
}
