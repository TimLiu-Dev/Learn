// Calling the Kotlin extensions from Java: they're just static methods on StringUtilsKt
public class CallExtFromJava {
    public static void main(String[] args) {
        System.out.println(StringUtilsKt.initials("ada lovelace"));   // receiver becomes argument #1
        System.out.println(StringUtilsKt.getLastChar("Kotlin"));      // extension property -> getter
        System.out.println(StringUtilsKt.orDash(null));
    }
}
