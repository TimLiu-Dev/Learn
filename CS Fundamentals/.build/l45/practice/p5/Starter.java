import java.util.*;

public class Practice {
    // Any function shaped like BookStore.list: the items from offset on, at most limit of them.
    interface Pager { List<Integer> page(List<Integer> items, int limit, int offset); }

    static List<Integer> page(List<Integer> items, int limit, int offset) {
        return items.stream().skip(offset).limit(limit).toList();
    }

    // null: the property held for every case. Otherwise: what failed, and for which input.
    static String checkPaging(Pager pager, long seed, int runs) {
        throw new UnsupportedOperationException("TODO: generate runs cases from new Random(seed), read every page, compare");
    }

    // CHECKS
    static final Pager SKIPS_ONE = (items, limit, offset) -> page(items, limit, offset + 1);
    static final Pager LOSES_LAST_PAGE = (items, limit, offset) ->
        offset > 0 && offset + limit > items.size() ? List.of() : page(items, limit, offset);
    static final Pager CRASHES_PAST_THE_END = (items, limit, offset) -> items.subList(offset, Math.min(offset + limit, items.size()));
    static final Pager ONE_TOO_MANY = (items, limit, offset) -> page(items, limit + 1, offset);
    static final Pager WRONG_WHEN_EXACTLY_FULL = (items, limit, offset) ->
        items.size() == limit ? items.subList(0, limit - 1) : page(items, limit, offset);

    public static void main(String[] args) {
        check("the correct pager passes", null, () -> checkPaging(Practice::page, 1, 300));
        check("a pager that skips an item is rejected", true, () -> checkPaging(SKIPS_ONE, 3, 300) != null);
        check("a pager that loses the last, partial page is rejected", true, () -> checkPaging(LOSES_LAST_PAGE, 4, 300) != null);
        check("a pager that throws is rejected, not crashed into", true, () -> checkPaging(CRASHES_PAST_THE_END, 5, 300) != null);
        check("pages longer than the limit are rejected", true, () -> checkPaging(ONE_TOO_MANY, 6, 300) != null);
        check("a bug that needs exactly limit items is found", true, () -> checkPaging(WRONG_WHEN_EXACTLY_FULL, 7, 300) != null);
        check("the same seed gives the same verdict", true,
            () -> Objects.equals(checkPaging(LOSES_LAST_PAGE, 8, 300), checkPaging(LOSES_LAST_PAGE, 8, 300)));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
