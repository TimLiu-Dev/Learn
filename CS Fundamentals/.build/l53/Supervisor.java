// A parent process that stops its child politely, then forcibly
public class Supervisor {
    public static void main(String[] args) throws Exception {
        for (boolean force : new boolean[] {false, true}) {
            Process child = new ProcessBuilder("java", "Hook.java", "30")
                .inheritIO()                  // the child writes straight to our terminal
                .start();
            Thread.sleep(1500);               // give it time to compile, start and register its hook
            if (force) child.destroyForcibly(); else child.destroy();
            System.out.println((force ? "destroyForcibly()" : "destroy()") + ": child exited with " + child.waitFor());
        }
    }
}
