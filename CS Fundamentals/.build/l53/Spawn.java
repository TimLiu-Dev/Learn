import java.io.File;
import java.io.IOException;

public class Spawn {
    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "echo \"child pid $$, parent $PPID, cwd $(pwd), GREETING=$GREETING\"");
        pb.directory(new File("/tmp"));                       // the child's working directory
        pb.environment().put("GREETING", "from the parent");  // the child's copy of the environment
        pb.inheritIO();                                       // the child's fds 0, 1 and 2 are ours
        System.out.println("parent pid " + ProcessHandle.current().pid());
        System.out.println("child exit " + pb.start().waitFor());
    }
}
