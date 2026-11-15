import java.util.ArrayList;
import java.util.List;

public class Frames {
    static void c() {
        List<String> names = new ArrayList<>();
        for (StackTraceElement frame : new Throwable().getStackTrace())
            if (frame.getClassName().equals("Frames"))   // only this program's own frames
                names.add(frame.getMethodName());
        System.out.println(String.join(" ", names));
    }

    static void b() { c(); }
    static void a() { b(); }

    public static void main(String[] args) {
        a();
    }
}
