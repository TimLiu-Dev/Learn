import java.io.IOException;

public class JavaFromJava {
    public static void main(String[] args) {
        Person p = new Person("Ada");
        System.out.println(p.getName());
        p.setName("Ada Lovelace");
        p.setActive(true);
        System.out.println(p.getName() + " active=" + p.isActive());
        System.out.println(p.is("Ada Lovelace"));
        System.out.println(Person.anonymous().getName());

        String nick = p.getNickname();
        System.out.println(nick != null ? nick : "(no nickname)");

        Tasks.runTwice(() -> System.out.println("tick"));
        System.out.println(Tasks.apply("kotlin", s -> s.toUpperCase()));

        try {
            Tasks.readConfig("app.conf");   // Java REQUIRES this try/catch (or throws)
        } catch (IOException e) {
            System.out.println("caught: " + e.getMessage());
        }

        String email = Directory.findEmail("grace");
        System.out.println(email != null ? email : "(no email)");

        String nickname = p.getNickname();  // no complaint here...
        System.out.println(nickname.length());   // ...the crash comes later, at the use
    }
}
