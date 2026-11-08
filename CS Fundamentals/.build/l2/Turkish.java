import java.util.Locale;

public class Turkish {
    public static void main(String[] args) {
        Locale turkish = Locale.forLanguageTag("tr-TR");
        System.out.println("title".toUpperCase(turkish));    // dotted capital İ
        System.out.println("TITLE".toLowerCase(turkish));    // dotless small ı

        Locale.setDefault(turkish);                          // as on a laptop set up in Istanbul
        String command = "quit";
        System.out.println(command.toUpperCase().equals("QUIT"));             // uses the DEFAULT locale
        System.out.println(command.toUpperCase(Locale.ROOT).equals("QUIT"));  // the same everywhere
    }
}
