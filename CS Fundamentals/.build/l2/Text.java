public class Text {
    public static void main(String[] args) {
        String name = "Ada";
        int year = 1815;
        System.out.println(name + " was born in " + year + ".");
        System.out.println("Ten years later: " + (year + 10));
        System.out.println(name.length() + " letters, upper: " + name.toUpperCase());
        System.out.println(String.format("%.2f", 3.14159));
        System.out.println("%.2f".formatted(3.14159));

        String poem = """
            Roses are red,
              violets are blue.""";
        System.out.println(poem);

        System.out.println('a' + 1);          // char + int -> int!
        System.out.println((char) ('a' + 1)); // cast back to get a char
        String smile = "😀";
        System.out.println(smile.length());
        System.out.println(smile.codePointCount(0, smile.length()));

        System.out.println("  hi  ".trim());
        System.out.println(java.util.Arrays.toString("a,b,,c".split(",")));
        System.out.println("ha".repeat(3));
        System.out.println("java".replace("j", "J"));
        System.out.println("Java".startsWith("Ja"));
        System.out.println("Java".substring(1, 3));

        String a = "hi";
        String b = new StringBuilder("h").append("i").toString();
        System.out.println(a == b);           // compares REFERENCES
        System.out.println(a.equals(b));      // compares contents
    }
}
