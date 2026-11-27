public class ChargeFromJava {
    public static void main(String[] args) {
        IdsKt.chargeUser(42L, 2000L);   // Java sees two plain longs...
        IdsKt.chargeUser(2000L, 42L);   // ...so swapping them compiles and runs
    }
}
