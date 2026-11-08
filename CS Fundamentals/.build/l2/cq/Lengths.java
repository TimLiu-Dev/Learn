public class Lengths {
    public static void main(String[] args) {
        String s = "é😀";   // é (e + combining accent), then 😀
        System.out.println(s.length() + " " + s.codePointCount(0, s.length()));
    }
}
