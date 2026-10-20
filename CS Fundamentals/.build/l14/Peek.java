import java.util.function.IntUnaryOperator;

public class Peek {
    public static void main(String[] args) {
        IntUnaryOperator addOne = x -> x + 1;
        System.out.println(addOne.applyAsInt(41));
    }
}
