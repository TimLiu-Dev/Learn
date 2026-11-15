public class Triangulate {
    // Step 3's code: green for 1, 2, 3, 4 and 5
    static String toRoman(int number) {
        if (number == 4) return "IV";
        if (number == 5) return "V";
        return "I".repeat(number);
    }

    public static void main(String[] args) {
        int[] numbers = {1, 4, 5, 6, 9};
        String[] wanted = {"I", "IV", "V", "VI", "IX"};
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < numbers.length; i++) {
            dots.append(toRoman(numbers[i]).equals(wanted[i]) ? "." : "F");
        }
        System.out.println(dots);
    }
}
