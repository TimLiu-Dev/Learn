public class Rounding {
    // Before: a member pays 90%.
    static int memberTotalOld(int cents) { return cents * 90 / 100; }

    // After an "obviously equivalent" cleanup: take 10% off.
    static int memberTotalNew(int cents) { return cents - cents * 10 / 100; }

    public static void main(String[] args) {
        System.out.println(memberTotalOld(1999) + " " + memberTotalNew(1999));
    }
}
