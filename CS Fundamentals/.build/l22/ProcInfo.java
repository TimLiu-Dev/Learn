import java.io.*;
import java.util.*;

public class ProcInfo {
    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessHandle me = ProcessHandle.current();
        System.out.println("my pid:     " + me.pid());
        System.out.println("parent pid: " + me.parent().map(ProcessHandle::pid).orElse(-1L));
        System.out.println("command:    " + me.info().command().orElse("?"));

        // One process, many threads: the JVM starts its own helpers.
        List<String> names = new ArrayList<>();
        for (Thread t : Thread.getAllStackTraces().keySet()) names.add(t.getName());
        Collections.sort(names);
        System.out.println(names.size() + " threads: " + names);

        // Start a child process and read what it prints.
        Process child = new ProcessBuilder("echo", "hello from a child").start();
        String text;
        try (BufferedReader r = child.inputReader()) {
            text = r.readLine();
        }
        System.out.println("child " + child.pid() + " said '" + text + "', exit " + child.waitFor());

        Process failing = new ProcessBuilder("sh", "-c", "exit 3").start();
        System.out.println("sh -c 'exit 3' gave exit " + failing.waitFor());
    }
}
