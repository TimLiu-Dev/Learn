public class Bisect {
    // Commits 0..15: commit 0 is known good, commit 15 known bad. Commit 9 broke the build.
    static boolean isBad(int commit) { return commit >= 9; }

    public static void main(String[] args) {
        int good = 0, bad = 15, runs = 0;
        while (bad - good > 1) {
            int mid = (good + bad) / 2;
            runs++;
            if (isBad(mid)) bad = mid; else good = mid;
        }
        System.out.println("first bad " + bad + " after " + runs + " runs");
    }
}
