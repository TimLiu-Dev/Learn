// Environment variables: a map of strings every process inherits from its parent
public class Env {
    public static void main(String[] args) throws Exception {
        String g = System.getenv("GREETING");
        System.out.println("GREETING in me:  " + (g != null ? g : "(not set)"));
        System.out.println("HOME in me:      " + System.getenv("HOME"));
        try {
            System.getenv().put("GREETING", "changed");
        } catch (UnsupportedOperationException e) {
            System.out.println("can't change my own environment: " + e.getClass().getSimpleName());
        }
        ProcessBuilder child = new ProcessBuilder("sh", "-c", "echo \"GREETING in child: $GREETING\"");
        child.environment().put("GREETING", "set by the parent");  // a copy, only for this child
        child.inheritIO().start().waitFor();
        g = System.getenv("GREETING");
        System.out.println("GREETING in me:  " + (g != null ? g : "(not set)"));
    }
}
