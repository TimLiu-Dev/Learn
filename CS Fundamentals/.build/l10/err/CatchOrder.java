public class CatchOrder {
    public static void main(String[] args) {
        try {
            Integer.parseInt("80x");
        } catch (Exception e) {                     // general first...
            System.out.println("something failed");
        } catch (NumberFormatException e) {         // ...so this can never run
            System.out.println("not a number");
        }
    }
}
