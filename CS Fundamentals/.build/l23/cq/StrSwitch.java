public class StrSwitch {
    static String kind(String s) {
        switch (s) {
            case "Aa": return "first";
            case "BB": return "second";
            default:   return "neither";
        }
    }

    public static void main(String[] args) {
        boolean sameHash = "Aa".hashCode() == "BB".hashCode();
        System.out.println(sameHash + " " + kind("BB"));
    }
}
