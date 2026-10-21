import java.io.IOException;

public class Cmd {
    static String run(String... cmd) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes()).trim();
        p.waitFor();
        return out;
    }

    public static void main(String[] args) throws Exception {
        String name = "Ada; echo INJECTED";  // pretend this came from a web form

        // VULNERABLE: a shell parses the whole string, so ';' starts a second command
        System.out.println("shell:   " + run("sh", "-c", "echo Hello, " + name).replace("\n", " | "));

        // FIXED: no shell; the input is one argument, whatever it contains
        System.out.println("argList: " + run("echo", "Hello,", name));
    }
}
