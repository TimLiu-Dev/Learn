public class Lcs {
    // dp[i][j] = LCS length of the first i chars of a and the first j chars of b.
    static String lcs(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];  // row 0 / column 0 = empty prefix = 0
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                dp[i][j] = a.charAt(i - 1) == b.charAt(j - 1)
                    ? dp[i - 1][j - 1] + 1
                    : Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        // Walk back from the bottom-right corner to recover the letters.
        StringBuilder sb = new StringBuilder();
        int i = a.length(), j = b.length();
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) { sb.append(a.charAt(i - 1)); i--; j--; }
            else if (dp[i - 1][j] >= dp[i][j - 1]) i--;
            else j--;
        }
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        String[][] pairs = {{"STACK", "TRACK"}, {"HASHMAP", "HEAP"}, {"AGGTAB", "GXTXAYB"}, {"ABC", "XYZ"}};
        for (String[] p : pairs) {
            String s = lcs(p[0], p[1]);
            System.out.println("lcs(" + p[0] + ", " + p[1] + ") = \"" + s + "\" (" + s.length() + ")");
        }
    }
}
