import java.util.function.Function;
import kotlin.jvm.functions.Function1;

public class JavaTools {
    static int applyTwiceJ(Function<Integer, Integer> f, int x) {
        return f.apply(f.apply(x));
    }

    public static void main(String[] args) {
        // Java calling Kotlin: (Int) -> Int is the interface Function1<Integer, Integer>
        System.out.println(FnsKt.applyTwice(x -> x + 10, 1));
        Function1<Integer, Integer> triple = x -> x * 3;   // a Kotlin function type
        System.out.println(triple.invoke(7));              // its method is invoke
    }
}
