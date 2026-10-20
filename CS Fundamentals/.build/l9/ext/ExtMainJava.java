public class ExtMainJava {
    public static void main(String[] args) {
        System.out.println(StringUtils.initials("ada lovelace"));
        System.out.println(StringUtils.initials("grace brewster hopper"));
        System.out.println(StringUtils.lastChar("Kotlin"));

        String missing = null;
        System.out.println(StringUtils.orDash(missing));
        System.out.println(StringUtils.orDash("   "));
        System.out.println(StringUtils.orDash("Linus"));
    }
}
