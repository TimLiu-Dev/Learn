public class Fold {
    static final String JA = "ja";

    public static void main(String[] args) {
        String ja = "ja";
        boolean constant = (JA + "va") == "java";
        boolean runtime = (ja + "va") == "java";
        System.out.println(constant + " " + runtime);
    }
}
