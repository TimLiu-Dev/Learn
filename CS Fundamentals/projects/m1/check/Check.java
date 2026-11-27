import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Black-box checker for Mini project M1 · Budget CLI.
 *
 *   java check/Check.java <path-to-launcher> [--stage N]
 *
 * The launcher is your build/install/budget/bin/budget (from ./gradlew installDist).
 * Every check writes small input files into a fresh temp folder, runs your program
 * there, and compares exit code and stdout with the spec. Runs of spaces count as
 * one space, and spaces at the ends of lines are ignored, so alignment is up to you.
 * --stage N runs stages 1..N. JDK only, no libraries.
 */
public class Check {
    static final int TIMEOUT_SECONDS = 60;
    static final String[] STAGES = {
        "", "CLI skeleton and validate", "CSV parsing and categorise", "Monthly report",
        "Top expenses and budgets", "Edge cases"};

    static Path launcher;
    static Path work;
    static int checkNumber = 0;
    static final Map<Integer, int[]> tally = new TreeMap<>(); // stage -> {passed, failed}
    static String firstFail;

    record Result(int exit, String out, String err, boolean timedOut) {}

    public static void main(String[] args) throws Exception {
        int maxStage = 5;
        String launcherArg = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--stage") && i + 1 < args.length) {
                try {
                    maxStage = Integer.parseInt(args[++i]);
                } catch (NumberFormatException e) {
                    maxStage = -1;
                }
            } else if (launcherArg == null) {
                launcherArg = args[i];
            }
        }
        if (launcherArg == null || maxStage < 1 || maxStage > 5) {
            System.out.println("Usage: java check/Check.java <path-to-launcher> [--stage N]   (N = 1..5)");
            System.out.println("Example: java ../m1/check/Check.java build/install/budget/bin/budget --stage 1");
            System.exit(2);
        }
        launcher = Path.of(launcherArg).toAbsolutePath();
        if (!Files.isRegularFile(launcher)) {
            System.out.println("No launcher at " + launcher);
            System.out.println("Run ./gradlew installDist in your project first; it creates build/install/budget/bin/budget.");
            System.exit(2);
        }
        if (!Files.isExecutable(launcher)) {
            System.out.println(launcher + " is not executable. Pass the start script in bin/, not a jar.");
            System.exit(2);
        }

        work = Files.createTempDirectory("budget-check");
        System.out.println("Checking " + launcher + " (stages 1.." + maxStage + ")");
        try {
            if (maxStage >= 1) stage1();
            if (maxStage >= 2) stage2();
            if (maxStage >= 3) stage3();
            if (maxStage >= 4) stage4();
            if (maxStage >= 5) stage5();
        } finally {
            deleteTree(work);
        }

        System.out.println();
        int passed = 0, failed = 0;
        for (var e : tally.entrySet()) {
            int p = e.getValue()[0], f = e.getValue()[1];
            passed += p;
            failed += f;
            System.out.printf("stage %d · %-28s %2d/%d passed%s%n", e.getKey(), STAGES[e.getKey()], p, p + f, f == 0 ? "" : "  <-");
        }
        if (failed == 0) {
            System.out.println("ALL " + passed + " CHECKS PASS" + (maxStage < 5 ? " for stages 1.." + maxStage + ". On to the next milestone." : ". Now do the senior review."));
        } else {
            System.out.println(failed + " of " + (passed + failed) + " checks FAIL. Fix the first one first: " + firstFail);
        }
        System.exit(failed == 0 ? 0 : 1);
    }

    // ───────────────────────────── stage 1 ─────────────────────────────

    static final String CLEAN = """
        date,description,amount
        2026-03-01,Moorside Lettings,-950.00
        2026-03-02,Tesco Stores,-23.40
        2026-03-25,ACME LTD SALARY,2480.00
        """;

    static void stage1() {
        header(1);
        Result help = run(Map.of(), "--help");
        List<String> missing = Stream.of("Usage: budget", "validate", "categorise", "report", "top", "budget")
            .filter(w -> !help.out().contains(w)).toList();
        record(1, "--help prints usage to stdout and exits 0",
            help.exit() == 0 && missing.isEmpty() && !help.timedOut(),
            "budget --help", Map.of(), "exit 0, stdout starting with \"Usage: budget\" and naming all five commands",
            help, missing.isEmpty() ? null : "stdout doesn't mention: " + missing,
            "Check for --help (or -h) before anything else and print the usage text with println, not System.err.");

        expectError(1, "no arguments is a usage error", Map.of(), "", List.of(),
            "With no command, print \"error: missing command ...\" to stderr and exit 1. Nothing on stdout.");
        expectError(1, "an unknown command is a usage error", Map.of(), "frobnicate statement.csv", List.of("frobnicate"),
            "Name the bad command: error: unknown command 'frobnicate'. Exit 1.");
        expectError(1, "validate without a file is a usage error", Map.of(), "validate", List.of(),
            "validate needs <statement.csv>: error: missing statement file. Exit 1.");
        expectError(1, "a statement that doesn't exist is an error naming the file", Map.of(), "validate nope.csv", List.of("nope.csv"),
            "Catch the IOException where you read the file and turn it into: error: cannot read 'nope.csv'. Exit 1, no stack trace.");

        expectOutput(1, "validate a clean statement", files("statement.csv", CLEAN), "validate statement.csv", 0,
            "3 valid, 0 malformed", null,
            "Line 1 is the header; every other line is a transaction. With no problems, print only the summary and exit 0.");

        expectOutput(1, "validate counts fields and keeps physical line numbers", files("statement.csv", """
            date,description,amount
            2026-03-01,Moorside Lettings,-950.00
            2026-03-02,Tesco Stores

            2026-03-03,Tesco,Leeds,-1.00
            2026-03-04,Tesco Stores,-12.00,
            2026-03-05,Boots,-2.00
            """), "validate statement.csv", 2, """
            line 3: expected 3 fields, found 2
            line 5: expected 3 fields, found 4
            line 6: expected 3 fields, found 4
            2 valid, 3 malformed
            """, null,
            "Line numbers count every physical line, the header and blank lines included (blank lines are skipped, not errors). "
                + "A trailing comma adds an empty 4th field. Exit 2 when anything is malformed.");

        expectOutput(1, "validate rejects impossible and badly shaped dates", files("statement.csv", """
            date,description,amount
            2026-02-30,Tesco Stores,-12.00
            03/04/2026,Tesco Stores,-12.00
            2026-4-01,Tesco Stores,-12.00
            2026-02-28,Tesco Stores,-12.00
            """), "validate statement.csv", 2, """
            line 2: invalid date '2026-02-30'
            line 3: invalid date '03/04/2026'
            line 4: invalid date '2026-4-01'
            1 valid, 3 malformed
            """, null,
            "Dates are YYYY-MM-DD and must exist: LocalDate.parse rejects 2026-02-30; check the shape with a regex first.");

        expectOutput(1, "validate checks description and amount", files("statement.csv", """
            date,description,amount
            2026-03-04,   ,-3.00
            2026-03-05,Pret A Manger,£4.50
            2026-03-06,Pret A Manger,4.505
            2026-03-07,Refund,0.00
            2026-03-08,Boots,+2.00
            2026-03-09,Boots,-2
            """), "validate statement.csv", 2, """
            line 2: empty description
            line 3: invalid amount '£4.50'
            line 4: invalid amount '4.505'
            line 5: zero amount
            line 6: invalid amount '+2.00'
            1 valid, 5 malformed
            """, null,
            "Trim each field. An amount is -?digits with an optional . and 1-2 digits; nothing else (no £, no +, no 3rd decimal). "
                + "0.00 parses but is rejected as 'zero amount'. Report only the first problem on each line: date, then description, then amount.");

        expectOutput(1, "a missing header is reported on line 1", files("statement.csv", """
            2026-03-01,Moorside Lettings,-950.00
            2026-03-02,Tesco Stores,-23.40
            """), "validate statement.csv", 2, """
            line 1: expected header 'date,description,amount'
            1 valid, 1 malformed
            """, null,
            "Line 1 must be the header (any case). If it isn't, it's reported and NOT read as a transaction.");
    }

    // ───────────────────────────── stage 2 ─────────────────────────────

    static final String QUOTED = """
        date,description,amount
        2026-03-02,"Sainsbury's, Headingley",-41.15
        2026-03-03,"Boots ""The Chemist\"\"\",-4.50
        "2026-03-04","Tesco Stores","-1.20"
        2026-03-05,"Lidl GB, Leeds, LS6",-7.00
        """;

    static final String CAT = """
        date,description,amount
        2026-03-01,Moorside Lettings,-950.00
        2026-03-02,TESCO STORES 2841,-23.40
        2026-03-02,"Sainsbury's, Headingley",-41.15
        2026-03-03,Boots,-4.50
        2026-03-25,Acme Ltd Salary,2480.00
        """;

    static final String CAT_RULES = """
        # comment line

        salary -> Pay
        moorside -> Rent
        tesco -> Groceries
        sainsbury -> Groceries
        """;

    static void stage2() {
        header(2);
        expectOutput(2, "validate accepts commas and doubled quotes inside quoted fields", files("statement.csv", QUOTED),
            "validate statement.csv", 0, "4 valid, 0 malformed", null,
            "line.split(\",\") cuts \"Sainsbury's, Headingley\" in two. Walk the line character by character: inside quotes a comma is "
                + "text and \"\" is one quote character.");

        expectOutput(2, "validate reports broken quoting", files("statement.csv", """
            date,description,amount
            2026-03-02,"Uber, London,-8.20
            2026-03-03,"Boots" Chemist,-4.50
            2026-03-04,Tesco,-1.20
            """), "validate statement.csv", 2, """
            line 2: unterminated quoted field
            line 3: text after closing quote
            1 valid, 2 malformed
            """, null,
            "A quoted field must close before the end of the line, and its closing quote must be followed by a comma or the end of the line.");

        expectOutput(2, "categorise labels every transaction in file order", files("statement.csv", CAT, "rules.txt", CAT_RULES),
            "categorise statement.csv --rules rules.txt", 0, """
            2026-03-01 | -950.00 | Rent | Moorside Lettings
            2026-03-02 | -23.40 | Groceries | TESCO STORES 2841
            2026-03-02 | -41.15 | Groceries | Sainsbury's, Headingley
            2026-03-03 | -4.50 | Uncategorised | Boots
            2026-03-25 | 2480.00 | Pay | Acme Ltd Salary
            5 transactions, 1 uncategorised
            """, null,
            "Substring rules ignore case (\"tesco\" matches \"TESCO STORES\"). Skip blank lines and # comments in the rules file. "
                + "No match means Uncategorised. Print amounts with exactly two decimals.");

        expectOutput(2, "the first matching rule wins", files("statement.csv", """
            date,description,amount
            2026-03-02,Tesco Express 113,-3.20
            2026-03-03,Tesco Superstore,-54.10
            2026-03-04,Netflix.com,-10.99
            2026-03-05,TfL Travel Charge,-2.80
            """, "rules.txt", """
            tesco express -> Snacks
            tesco -> Groceries
            /^tfl\\b/ -> Transport
            netflix -> Subscriptions
            flix -> Films
            """), "categorise statement.csv --rules rules.txt", 0, """
            2026-03-02 | -3.20 | Snacks | Tesco Express 113
            2026-03-03 | -54.10 | Groceries | Tesco Superstore
            2026-03-04 | -10.99 | Subscriptions | Netflix.com
            2026-03-05 | -2.80 | Transport | TfL Travel Charge
            4 transactions, 0 uncategorised
            """, null,
            "Stop at the FIRST rule that matches (firstOrNull / a loop that returns). A loop that keeps overwriting the category gives "
                + "the LAST match: Tesco Express becomes Groceries and Netflix becomes Films.");

        expectOutput(2, "a /regex/ rule matches anywhere in the description", files("statement.csv", """
            date,description,amount
            2026-03-02,TESCO STORES 2841,-23.40
            2026-03-03,Card 12,-1.00
            """, "rules.txt", """
            /\\d{4}$/ -> Card purchase
            """), "categorise statement.csv --rules rules.txt", 0, """
            2026-03-02 | -23.40 | Card purchase | TESCO STORES 2841
            2026-03-03 | -1.00 | Uncategorised | Card 12
            2 transactions, 1 uncategorised
            """, null,
            "A pattern between slashes is a regex, matched with find() (Kotlin: containsMatchIn), not matches(); it ignores case. "
                + "Category names may contain spaces.");

        expectOutput(2, "categorise prints quoted descriptions unquoted", files("statement.csv", QUOTED, "rules.txt", """
            boots -> Health
            /lidl|sainsbury/ -> Groceries
            """), "categorise statement.csv --rules rules.txt", 0, """
            2026-03-02 | -41.15 | Groceries | Sainsbury's, Headingley
            2026-03-03 | -4.50 | Health | Boots "The Chemist"
            2026-03-04 | -1.20 | Uncategorised | Tesco Stores
            2026-03-05 | -7.00 | Groceries | Lidl GB, Leeds, LS6
            4 transactions, 1 uncategorised
            """, null,
            "The surrounding quotes aren't part of the value, and \"\" inside quotes is one \". Even the date and amount may be quoted.");

        expectOutput(2, "categorise skips malformed lines and warns on stderr", files("statement.csv", """
            date,description,amount
            2026-03-02,Tesco Stores,-23.40
            2026-03-03,Tesco Stores,oops
            2026-02-30,Tesco Stores,-1.00
            2026-03-04,Boots,-4.50
            """, "rules.txt", CAT_RULES), "categorise statement.csv --rules rules.txt", 0, """
            2026-03-02 | -23.40 | Groceries | Tesco Stores
            2026-03-04 | -4.50 | Uncategorised | Boots
            2 transactions, 1 uncategorised
            """, "skipped 2",
            "Only validate fails on bad lines. Every other command uses the good ones, exits 0, and prints "
                + "\"warning: skipped 2 malformed line(s); run 'budget validate' for details\" to stderr.");

        expectError(2, "a missing rules file is an error naming the file", files("statement.csv", CAT),
            "categorise statement.csv --rules nope.txt", List.of("nope.txt"),
            "error: cannot read 'nope.txt', exit 1.");
        expectError(2, "categorise without --rules is a usage error", files("statement.csv", CAT),
            "categorise statement.csv", List.of("--rules"),
            "--rules is required for categorise, report, top and budget: error: missing --rules. Exit 1.");
    }

    // ───────────────────────────── stage 3 ─────────────────────────────

    static final String REPORT = """
        date,description,amount
        2026-02-28,Tesco Stores,-11.11
        2026-03-01,Moorside Lettings,-950.00
        2026-03-02,Tesco Stores,-4.35
        2026-03-03,Tesco Stores,-0.29
        2026-03-04,Café Nero,-1.15
        2026-03-05,Pret A Manger,-2.10
        2026-03-06,Amazon Marketplace,-24.99
        2026-03-09,Amazon Marketplace,24.99
        2026-03-12,Boots,-15.00
        2026-03-14,Spotify AB,-15.00
        2026-03-15,Waterstones,-15.00
        2026-03-25,ACME LTD SALARY,2480.00
        2026-03-31,Tesco Stores,-18.20
        2026-04-01,Tesco Stores,-99.00
        """;

    static final String REPORT_RULES = """
        salary -> Pay
        moorside -> Rent
        tesco -> Groceries
        café -> Eating Out
        pret -> Eating Out
        amazon -> Shopping
        spotify -> Subscriptions
        waterstones -> Books
        """;

    static void stage3() {
        header(3);
        Map<String, String> f = files("statement.csv", REPORT, "rules.txt", REPORT_RULES);
        expectOutput(3, "money is exact: 4.35 + 0.29 + 1.15 is not 5.76", files("statement.csv", """
            date,description,amount
            2026-05-01,Tesco,-4.35
            2026-05-02,Tesco,-0.29
            2026-05-03,Tesco,-1.15
            2026-05-04,Tesco,0.10
            2026-05-05,Tesco,0.20
            """, "rules.txt", "tesco -> Groceries\n"),
            "report statement.csv --rules rules.txt --month 2026-05", 0, """
            Report 2026-05
            Income 0.30
            Spending -5.79
            Net -5.49
            Groceries -5.49 (5)
            """, null,
            "Never use Double for money. The double nearest 4.35 is 4.3499999999999996447..., so (4.35 * 100).toLong() is 434. "
                + "Parse the text straight into whole pence (Long) or BigDecimal.");

        expectOutput(3, "a month includes its last day", f, "report statement.csv --rules rules.txt --month 2026-02", 0, """
            Report 2026-02
            Income 0.00
            Spending -11.11
            Net -11.11
            Groceries -11.11 (1)
            """, null,
            "2026-02-28 is in February. A range test like date.isBefore(month.atEndOfMonth()) drops the last day; "
                + "compare YearMonth.from(date) == month instead, and there's no boundary to get wrong.");

        expectOutput(3, "a month includes its first day", f, "report statement.csv --rules rules.txt --month 2026-04", 0, """
            Report 2026-04
            Income 0.00
            Spending -99.00
            Net -99.00
            Groceries -99.00 (1)
            """, null,
            "2026-04-01 is in April. Check how you build the start of the month.");

        expectOutput(3, "report totals a month and sorts its categories", f,
            "report statement.csv --rules rules.txt --month 2026-03", 0, """
            Report 2026-03
            Income 2504.99
            Spending -1046.08
            Net 1458.91
            Pay 2480.00 (1)
            Rent -950.00 (1)
            Groceries -22.84 (3)
            Books -15.00 (1)
            Subscriptions -15.00 (1)
            Eating Out -3.25 (2)
            Shopping 0.00 (2)
            Uncategorised -15.00 (1)
            """, null,
            "Income = sum of positive amounts, Spending = sum of negative ones, Net = Income + Spending. A category's total is the "
                + "sum of its amounts (a refund nets off). Sort by the size of the total, largest first; equal sizes by name; "
                + "Uncategorised always last. Lines are 'Category total (count)'.");

        expectError(3, "report rejects an impossible month", f, "report statement.csv --rules rules.txt --month 2026-13",
            List.of("2026-13"), "--month must be YYYY-MM and a real month: error: invalid --month '2026-13' (expected YYYY-MM). Exit 1.");
        expectError(3, "report rejects a month that isn't YYYY-MM", f, "report statement.csv --rules rules.txt --month 2026-3",
            List.of("2026-3"), "Two digits for the month: 2026-03. Check the shape before YearMonth.parse.");
        expectError(3, "report without --month is a usage error", f, "report statement.csv --rules rules.txt",
            List.of("--month"), "--month is required for report, top and budget: error: missing --month. Exit 1.");
    }

    // ───────────────────────────── stage 4 ─────────────────────────────

    static final String TOP = """
        date,description,amount
        2026-06-20,Tesco,-45.00
        2026-06-03,Boots,-45.00
        2026-06-25,Salary,2480.00
        2026-06-01,Rent,-950.00
        2026-06-03,Pret,-45.00
        2026-06-10,Zara,-120.00
        2026-05-31,Tesco,-500.00
        2026-06-12,Refund,45.00
        2026-06-30,Tesco,-7.50
        """;

    static final String TOP_RULES = """
        tesco -> Groceries
        boots -> Health
        rent -> Rent
        salary -> Pay
        """;

    static final String BUDGET = """
        date,description,amount
        2026-06-01,Tesco,-249.00
        2026-06-02,Pret A Manger,-60.00
        2026-06-03,TfL,-10.00
        2026-06-05,Netflix,-10.99
        2026-06-06,Amazon,-30.00
        2026-06-07,Amazon,30.00
        """;

    static final String BUDGET_RULES = """
        tesco -> Groceries
        pret -> Eating Out
        netflix -> Subscriptions
        tfl -> Transport
        amazon -> Shopping
        """;

    static final String BUDGETS = """
        # monthly budgets
        Groceries = 400.00
        Eating Out = 50
        Transport = 10.00
        Gym = 20.00
        Shopping = 25.00
        """;

    static void stage4() {
        header(4);
        Map<String, String> t = files("statement.csv", TOP, "rules.txt", TOP_RULES);
        expectOutput(4, "top lists the biggest expenses with a deterministic tie-break", t,
            "top statement.csv --rules rules.txt --month 2026-06 --n 4", 0, """
            Top 4 expenses 2026-06
            1 | 2026-06-01 | -950.00 | Rent | Rent
            2 | 2026-06-10 | -120.00 | Uncategorised | Zara
            3 | 2026-06-03 | -45.00 | Health | Boots
            4 | 2026-06-03 | -45.00 | Uncategorised | Pret
            """, null,
            "Expenses only (amount < 0), most negative first. Equal amounts: earlier date first, then earlier line in the file. "
                + "Note the file isn't in date order, so a stable sort by amount alone puts Tesco (06-20) first.");

        expectOutput(4, "top defaults to 5", t, "top statement.csv --rules rules.txt --month 2026-06", 0, """
            Top 5 expenses 2026-06
            1 | 2026-06-01 | -950.00 | Rent | Rent
            2 | 2026-06-10 | -120.00 | Uncategorised | Zara
            3 | 2026-06-03 | -45.00 | Health | Boots
            4 | 2026-06-03 | -45.00 | Uncategorised | Pret
            5 | 2026-06-20 | -45.00 | Groceries | Tesco
            """, null, "Without --n, show 5.");

        expectOutput(4, "top shows them all when there are fewer than N", t,
            "top statement.csv --rules rules.txt --month 2026-06 --n 10", 0, """
            Top 10 expenses 2026-06
            1 | 2026-06-01 | -950.00 | Rent | Rent
            2 | 2026-06-10 | -120.00 | Uncategorised | Zara
            3 | 2026-06-03 | -45.00 | Health | Boots
            4 | 2026-06-03 | -45.00 | Uncategorised | Pret
            5 | 2026-06-20 | -45.00 | Groceries | Tesco
            6 | 2026-06-30 | -7.50 | Groceries | Tesco
            """, null,
            "The header keeps the N you were asked for. Income and refunds are never expenses; 2026-05-31 is another month; "
                + "2026-06-30 is the last day of June.");

        expectError(4, "top rejects --n 0", t, "top statement.csv --rules rules.txt --month 2026-06 --n 0", List.of("--n"),
            "--n must be a positive whole number: error: invalid --n '0' (expected a positive integer). Exit 1.");
        expectError(4, "top rejects --n abc", t, "top statement.csv --rules rules.txt --month 2026-06 --n abc", List.of("abc"),
            "toIntOrNull() / a NumberFormatException turns into a usage error, not a crash.");

        Map<String, String> b = files("statement.csv", BUDGET, "rules.txt", BUDGET_RULES, "budgets.txt", BUDGETS);
        expectOutput(4, "budget compares spending with each budget", b,
            "budget statement.csv --rules rules.txt --budgets budgets.txt --month 2026-06", 0, """
            Budget 2026-06
            Groceries | 400.00 | 249.00 | 151.00 under | 62.2%
            Eating Out | 50.00 | 60.00 | 10.00 over | 120.0%
            Transport | 10.00 | 10.00 | on budget | 100.0%
            Gym | 20.00 | 0.00 | 20.00 under | 0.0%
            Shopping | 25.00 | 0.00 | 25.00 under | 0.0%
            Total | 505.00 | 319.00 | 186.00 under | 63.2%
            """, null,
            "Rows in budgets-file order. spent = minus the category's net total (refunds count). percent = spent * 100 / budget to "
                + "1 dp, rounded HALF_EVEN: 249/400 is exactly 62.25%, which rounds to 62.2 (HALF_UP would give 62.3). "
                + "Unbudgeted categories are left out; Total sums the rows above it.");

        expectError(4, "a bad budget line is an error naming its line", files("statement.csv", BUDGET, "rules.txt", BUDGET_RULES,
                "budgets.txt", "# budgets\nGroceries = lots\n"),
            "budget statement.csv --rules rules.txt --budgets budgets.txt --month 2026-06", List.of("line 2"),
            "Budgets are all-or-nothing, like rules: error: budgets.txt line 2: invalid amount 'lots'. Exit 1.");
        expectError(4, "a category budgeted twice is an error", files("statement.csv", BUDGET, "rules.txt", BUDGET_RULES,
                "budgets.txt", "Groceries = 400\nTransport = 10\nGroceries = 300\n"),
            "budget statement.csv --rules rules.txt --budgets budgets.txt --month 2026-06", List.of("line 3"),
            "error: budgets.txt line 3: duplicate category 'Groceries'. Exit 1.");
        expectError(4, "budget without --budgets is a usage error", files("statement.csv", BUDGET, "rules.txt", BUDGET_RULES),
            "budget statement.csv --rules rules.txt --month 2026-06", List.of("--budgets"),
            "error: missing --budgets. Exit 1.");
    }

    // ───────────────────────────── stage 5 ─────────────────────────────

    static void stage5() {
        header(5);
        Map<String, String> edge = files("statement.csv", """
            date,description,amount
            2025-12-31,Tesco,-10.00
            2026-01-01,Tesco,-20.00
            2026-02-29,Tesco,-40.00
            2026-12-15,Tesco,-5.00
            2028-02-29,Tesco,-30.00
            """, "rules.txt", "tesco -> Groceries\n");
        expectOutput(5, "December is a month of one year only", edge, "report statement.csv --rules rules.txt --month 2025-12", 0, """
            Report 2025-12
            Income 0.00
            Spending -10.00
            Net -10.00
            Groceries -10.00 (1)
            """, "skipped 1",
            "Compare year AND month (YearMonth does both). 2026-12-15 is not in 2025-12, 2025-12-31 is, and 2026-02-29 doesn't exist "
                + "(2026 isn't a leap year), so it's skipped with a warning.");
        expectOutput(5, "a leap day belongs to its February", edge, "report statement.csv --rules rules.txt --month 2028-02", 0, """
            Report 2028-02
            Income 0.00
            Spending -30.00
            Net -30.00
            Groceries -30.00 (1)
            """, null, "2028 is a leap year, so 2028-02-29 is real and is the last day of 2028-02.");

        Map<String, String> b = files("statement.csv", BUDGET, "rules.txt", BUDGET_RULES, "budgets.txt", BUDGETS);
        expectOutput(5, "report on an empty month", b, "report statement.csv --rules rules.txt --month 2026-07", 0, """
            Report 2026-07
            Income 0.00
            Spending 0.00
            Net 0.00
            No transactions
            """, null, "Zero totals print as 0.00 (never -0.00), followed by the line 'No transactions'.");
        expectOutput(5, "top on an empty month", b, "top statement.csv --rules rules.txt --month 2026-07 --n 3", 0, """
            Top 3 expenses 2026-07
            No expenses
            """, null, "No expenses in the month: print the header, then 'No expenses'.");
        expectOutput(5, "budget on an empty month", b,
            "budget statement.csv --rules rules.txt --budgets budgets.txt --month 2026-07", 0, """
            Budget 2026-07
            Groceries | 400.00 | 0.00 | 400.00 under | 0.0%
            Eating Out | 50.00 | 0.00 | 50.00 under | 0.0%
            Transport | 10.00 | 0.00 | 10.00 under | 0.0%
            Gym | 20.00 | 0.00 | 20.00 under | 0.0%
            Shopping | 25.00 | 0.00 | 25.00 under | 0.0%
            Total | 505.00 | 0.00 | 505.00 under | 0.0%
            """, null, "Every budget still gets a row, with 0.00 spent.");

        expectOutput(5, "percentages round HALF_EVEN, and refunds can go negative", files("statement.csv", """
            date,description,amount
            2026-08-01,Coffee,-0.02
            2026-08-02,Books,-12.35
            2026-08-03,Bus,-7.25
            2026-08-04,Gift refund,5.00
            """, "rules.txt", """
            coffee -> Coffee
            books -> Books
            bus -> Bus
            gift -> Gifts
            """, "budgets.txt", """
            Coffee = 40.00
            Books = 100.00
            Bus = 20.00
            Gifts = 40.00
            """), "budget statement.csv --rules rules.txt --budgets budgets.txt --month 2026-08", 0, """
            Budget 2026-08
            Coffee | 40.00 | 0.02 | 39.98 under | 0.0%
            Books | 100.00 | 12.35 | 87.65 under | 12.4%
            Bus | 20.00 | 7.25 | 12.75 under | 36.2%
            Gifts | 40.00 | -5.00 | 45.00 under | -12.5%
            Total | 200.00 | 14.62 | 185.38 under | 7.3%
            """, null,
            "Exact percentages: 0.05 -> 0.0, 12.35 -> 12.4, 36.25 -> 36.2 (HALF_EVEN rounds a tie to the even digit). "
                + "Use BigDecimal: (spent * 100).divide(budget, 1, RoundingMode.HALF_EVEN). A refund-only category has negative spending.");

        String bomCrlf = "﻿date,description,amount\r\n2026-09-01,CAFÉ NERO,-3.10\r\n2026-09-02,ZOË'S BAKERY,-4.20\r\n"
            + "2026-09-03,東京ラーメン Leeds,-14.50\r\n2026-09-04,Łódź Pierogi Bar,-21.00\r\n";
        Map<String, String> uni = files("statement.csv", bomCrlf, "rules.txt", """
            café -> Coffee
            /^zoë/ -> Bakery
            ラーメン -> Ramen
            /łódź/ -> Pierogi
            """);
        expectOutput(5, "a UTF-8 BOM and Windows line endings are accepted", uni, "validate statement.csv", 0,
            "4 valid, 0 malformed", null,
            "Excel saves CSV with a byte-order mark (U+FEFF) before the header and \\r\\n line ends. Strip a leading \\uFEFF from "
                + "line 1 and make sure no \\r is left on the amount.");
        expectOutput(5, "case-insensitive matching works beyond ASCII", uni, "categorise statement.csv --rules rules.txt", 0, """
            2026-09-01 | -3.10 | Coffee | CAFÉ NERO
            2026-09-02 | -4.20 | Bakery | ZOË'S BAKERY
            2026-09-03 | -14.50 | Ramen | 東京ラーメン Leeds
            2026-09-04 | -21.00 | Pierogi | Łódź Pierogi Bar
            4 transactions, 0 uncategorised
            """, null,
            "Read and print UTF-8. Java's Pattern.CASE_INSENSITIVE alone only folds ASCII: add Pattern.UNICODE_CASE so /^zoë/ matches "
                + "ZOË (Kotlin's RegexOption.IGNORE_CASE adds it for you).");

        String stmt = "date,description,amount\n2026-03-02,Tesco,-1.00\n";
        expectError(5, "a rule without '->' rejects the rules file, naming the line",
            files("statement.csv", stmt, "rules.txt", "# rules\n\ntesco Groceries\n"),
            "categorise statement.csv --rules rules.txt", List.of("line 3"),
            "Rules are configuration: one bad line stops the run with error: rules.txt line 3: missing '->'. Exit 1, nothing on stdout.");
        expectError(5, "an invalid regex rejects the rules file, naming the line",
            files("statement.csv", stmt, "rules.txt", "tesco -> Groceries\n/[a-/ -> Broken\n"),
            "categorise statement.csv --rules rules.txt", List.of("line 2"),
            "Catch PatternSyntaxException when compiling: error: rules.txt line 2: invalid regex '/[a-/'. Exit 1.");
        expectError(5, "a rule with no category rejects the rules file",
            files("statement.csv", stmt, "rules.txt", "tesco ->\n"),
            "categorise statement.csv --rules rules.txt", List.of("line 1"),
            "error: rules.txt line 1: empty category. Exit 1.");

        Map<String, String> amounts = files("statement.csv", """
            date,description,amount
            2026-10-01,Tesco,-12
            2026-10-02,Tesco,-12.5
            2026-10-03,Tesco,-0.00
            2026-10-04,Tesco,"1,000.00"
            2026-10-05 , Tesco ,  -3.00
            """, "rules.txt", "tesco -> Groceries\n");
        expectOutput(5, "amounts may have 0-2 decimals; output always has 2", amounts, "categorise statement.csv --rules rules.txt", 0, """
            2026-10-01 | -12.00 | Groceries | Tesco
            2026-10-02 | -12.50 | Groceries | Tesco
            2026-10-05 | -3.00 | Groceries | Tesco
            3 transactions, 0 uncategorised
            """, "skipped 2",
            "-12 is -12.00 and -12.5 is -12.50; spaces around fields are trimmed. -0.00 is a zero amount and 1,000.00 is invalid "
                + "(no thousands separators), so both are skipped.");
        expectOutput(5, "a header-only statement is valid", files("statement.csv", "date,description,amount\n"),
            "validate statement.csv", 0, "0 valid, 0 malformed", null, "No transactions is fine: exit 0.");
        expectError(5, "an option another command owns is rejected", files("statement.csv", stmt, "rules.txt", "tesco -> G\n"),
            "report statement.csv --rules rules.txt --month 2026-03 --n 3", List.of("--n"),
            "Each command accepts only its own options: error: unknown option '--n' for report. Exit 1.");
    }

    // ───────────────────────────── machinery ─────────────────────────────

    static void header(int stage) {
        System.out.println();
        System.out.println("── stage " + stage + " · " + STAGES[stage] + " ──");
    }

    static Map<String, String> files(String... nameThenContent) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < nameThenContent.length; i += 2) m.put(nameThenContent[i], nameThenContent[i + 1]);
        return m;
    }

    /** A run that must succeed with this exit code and exactly this stdout (after normalising spaces). */
    static void expectOutput(int stage, String title, Map<String, String> files, String args, int exit, String stdout,
                             String stderrMustContain, String hint) {
        Result r = run(files, args);
        List<String> want = normalise(stdout), got = normalise(r.out());
        String problem = null;
        if (r.timedOut()) problem = "timed out after " + TIMEOUT_SECONDS + " s";
        else if (r.exit() != exit) problem = "exit code " + r.exit() + ", expected " + exit;
        else if (!want.equals(got)) problem = firstDifference(want, got);
        else if (stderrMustContain != null && !r.err().contains(stderrMustContain))
            problem = "stdout is right, but stderr should contain \"" + stderrMustContain + "\"";
        record(stage, title, problem == null, ("budget " + args).strip(), files,
            "exit " + exit + ", stdout:\n" + indent(String.join("\n", want)), r, problem, hint);
    }

    /** A run that must fail: exit 1, nothing on stdout, stderr starting "error:" and mentioning each word. */
    static void expectError(int stage, String title, Map<String, String> files, String args, List<String> mentions, String hint) {
        Result r = run(files, args);
        String err = r.err().strip();
        String problem = null;
        if (r.timedOut()) problem = "timed out after " + TIMEOUT_SECONDS + " s";
        else if (r.exit() != 1) problem = "exit code " + r.exit() + ", expected 1";
        else if (!r.out().isBlank()) problem = "stdout should be empty: errors go to stderr";
        else if (!err.startsWith("error:")) problem = "stderr should start with \"error:\"";
        else {
            for (String m : mentions) if (!err.contains(m)) problem = "the error message should mention \"" + m + "\"";
        }
        if (problem == null && err.lines().anyMatch(l -> l.startsWith("\tat ") || l.startsWith("Exception in thread")))
            problem = "the error message is right, but there's a stack trace too: catch the exception and exit cleanly";
        record(stage, title, problem == null, ("budget " + args).strip(), files,
            "exit 1, empty stdout, stderr: error: ..." + (mentions.isEmpty() ? "" : " (mentioning " + String.join(", ", mentions) + ")"),
            r, problem, hint);
    }

    static void record(int stage, String title, boolean ok, String ran, Map<String, String> files, String expected,
                       Result r, String problem, String hint) {
        int[] t = tally.computeIfAbsent(stage, k -> new int[2]);
        if (ok) {
            t[0]++;
            System.out.println("PASS stage " + stage + " · " + title);
            return;
        }
        t[1]++;
        if (firstFail == null) firstFail = "stage " + stage + " · " + title;
        StringBuilder sb = new StringBuilder();
        sb.append("FAIL stage ").append(stage).append(" · ").append(title).append('\n');
        sb.append("     ran: ").append(ran).append('\n');
        files.forEach((name, content) -> {
            sb.append("     ").append(name).append(':').append('\n');
            String[] lines = content.split("\n", -1);
            int n = content.endsWith("\n") ? lines.length - 1 : lines.length;
            for (int i = 0; i < n; i++) sb.append(String.format("       %2d | %s%n", i + 1, visible(lines[i])));
        });
        sb.append("     expected: ").append(expected).append('\n');
        sb.append("     got: exit ").append(r.timedOut() ? "(killed)" : r.exit()).append(", stdout:\n")
            .append(r.out().isBlank() ? "       (empty)" : indent(r.out().stripTrailing())).append('\n');
        if (!r.err().isBlank()) sb.append("     stderr:\n").append(indent(trimStackTrace(r.err()))).append('\n');
        sb.append("     problem: ").append(problem).append('\n');
        sb.append("     hint: ").append(crashHint(r.err()) != null ? crashHint(r.err()) : hint);
        System.out.println(sb);
    }

    /** When the program crashed, the crash is the first thing to fix. */
    static String crashHint(String err) {
        String crash = err.lines().filter(l -> l.startsWith("Exception in thread")).findFirst().orElse(null);
        if (crash == null) return null;
        if (crash.contains("NotImplementedError") || crash.contains("UnsupportedOperationException"))
            return "your program reached a TODO from the starter (" + crash.replaceFirst("^Exception in thread \"main\" ", "")
                + "). Implement that part first.";
        return "your program crashed with an uncaught exception. Run the same command yourself, read the stack trace from the top, "
            + "and turn expected failures into results or error messages (L10).";
    }

    static String trimStackTrace(String err) {
        List<String> lines = err.stripTrailing().lines().toList();
        List<String> kept = new ArrayList<>();
        int frames = 0;
        for (String l : lines) {
            if (l.startsWith("\tat ")) {
                if (++frames > 3) continue;
            }
            kept.add(l);
        }
        if (frames > 3) kept.add("\t... (" + (frames - 3) + " more stack frames)");
        return String.join("\n", kept);
    }

    static String visible(String line) {
        return line.replace("﻿", "<BOM>").replace("\r", "<CR>");
    }

    static String indent(String text) {
        return text.lines().map(l -> "       " + l).reduce((a, b) -> a + "\n" + b).orElse("       (empty)");
    }

    /** Runs of spaces/tabs count as one space; line-end spaces and trailing blank lines don't count. */
    static List<String> normalise(String text) {
        List<String> lines = new ArrayList<>(Arrays.asList(text.replace("\r\n", "\n").split("\n", -1)));
        lines.replaceAll(l -> l.replaceAll("[ \\t]+", " ").strip());
        while (!lines.isEmpty() && lines.getLast().isEmpty()) lines.removeLast();
        return lines;
    }

    static String firstDifference(List<String> want, List<String> got) {
        for (int i = 0; i < Math.max(want.size(), got.size()); i++) {
            String w = i < want.size() ? want.get(i) : null, g = i < got.size() ? got.get(i) : null;
            if (w == null) return "stdout has an extra line " + (i + 1) + ": \"" + g + "\"";
            if (g == null) return "stdout stops before line " + (i + 1) + ", expected \"" + w + "\"";
            if (!w.equals(g)) return "stdout line " + (i + 1) + " differs: expected \"" + w + "\" but got \"" + g + "\"";
        }
        return "stdout differs";
    }

    static Result run(Map<String, String> files, String args) {
        try {
            Path dir = Files.createDirectory(work.resolve(String.format("check%02d", ++checkNumber)));
            for (var f : files.entrySet()) Files.writeString(dir.resolve(f.getKey()), f.getValue(), StandardCharsets.UTF_8);
            List<String> command = new ArrayList<>();
            command.add(launcher.toString());
            if (!args.isBlank()) command.addAll(Arrays.asList(args.trim().split(" +")));
            Path out = dir.resolve(".stdout"), err = dir.resolve(".stderr");
            ProcessBuilder pb = new ProcessBuilder(command).directory(dir.toFile())
                .redirectOutput(out.toFile()).redirectError(err.toFile());
            // Ask a JVM program for UTF-8 output whatever the terminal's locale is.
            String opts = pb.environment().getOrDefault("JAVA_OPTS", "");
            pb.environment().put("JAVA_OPTS", (opts + " -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8").trim());
            Process p = pb.start();
            boolean finished = p.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                p.descendants().forEach(ProcessHandle::destroyForcibly);
                p.destroyForcibly().waitFor();
            }
            return new Result(finished ? p.exitValue() : -1, Files.readString(out, StandardCharsets.UTF_8),
                Files.readString(err, StandardCharsets.UTF_8), !finished);
        } catch (IOException e) {
            return new Result(-1, "", "checker could not run the launcher: " + e.getMessage(), false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result(-1, "", "interrupted", true);
        }
    }

    static void deleteTree(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(p);
        }
    }
}
