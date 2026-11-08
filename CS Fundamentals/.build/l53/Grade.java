import java.io.*;

// A well-behaved Unix tool: data in on stdin, results on stdout, problems on stderr,
// and an exit code that says how it went (0 ok, 1 some bad lines, 2 no input at all).
public class Grade {
    static String grade(int score) {
        return score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : "F";
    }

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int lines = 0, bad = 0;
        for (String line; (line = in.readLine()) != null; ) {
            lines++;
            Integer score = null;
            try { score = Integer.parseInt(line.trim()); } catch (NumberFormatException e) { }
            if (score == null || score < 0 || score > 100) {
                System.err.println("line " + lines + ": not a score: '" + line + "'");
                bad++;
            } else {
                System.out.println(score + " " + grade(score));
            }
        }
        if (lines == 0) {
            System.err.println("no input");
            System.exit(2);
        }
        System.exit(bad > 0 ? 1 : 0);
    }
}
