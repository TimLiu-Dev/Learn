import java.util.*;

public class Practice {
    static class Repo {
        final Map<String, String> parent = new HashMap<>();          // commit id -> its parent (null for the first)
        final Map<String, String> branches = new TreeMap<>();        // branch name -> the commit it points at
        String head = "main";                                        // the branch you're on
        private int made = 1;

        Repo() { parent.put("c1", null); branches.put("main", "c1"); }

        // A new commit whose parent is the current commit. Returns its id: c2, c3, ...
        String commit() {
            String id = "c" + (++made);
            parent.put(id, branches.get(head));
            branches.put(head, id);          // only the current branch moves
            return id;
        }

        // Like `git branch name`: a new pointer at the current commit. HEAD doesn't move.
        void createBranch(String name) {
            if (branches.containsKey(name)) throw new IllegalArgumentException("a branch named '" + name + "' already exists");
            branches.put(name, branches.get(head));   // copies one id, not any files
        }

        // Like `git switch name`.
        void switchTo(String name) {
            if (!branches.containsKey(name)) throw new IllegalArgumentException("invalid reference: " + name);
            head = name;
        }

        // Like `git log --oneline`: newest first.
        List<String> log() {
            List<String> ids = new ArrayList<>();
            for (String id = branches.get(head); id != null; id = parent.get(id)) ids.add(id);
            return ids;
        }
    }

    // CHECKS
    static boolean throwsIAE(Runnable r) {
        try { r.run(); return false; } catch (IllegalArgumentException e) { return true; }
    }

    public static void main(String[] args) {
        check("a new branch points at the current commit and makes no commit", "c2 main 2", () -> {
            Repo r = new Repo(); r.commit(); r.createBranch("fix");
            return r.branches.get("fix") + " " + r.head + " " + r.parent.size();
        });
        check("committing moves only the branch HEAD points to", "{fix=c2, main=c1}", () -> {
            Repo r = new Repo(); r.createBranch("fix"); r.switchTo("fix"); r.commit();
            return r.branches.toString();
        });
        check("switching moves HEAD, not any branch", "main {fix=c2, main=c1}", () -> {
            Repo r = new Repo(); r.createBranch("fix"); r.switchTo("fix"); r.commit(); r.switchTo("main");
            return r.head + " " + r.branches;
        });
        check("log follows parents back from HEAD", "[c4, c2, c1] [c3, c2, c1]", () -> {
            Repo r = new Repo(); r.commit(); r.createBranch("fix"); r.switchTo("fix"); r.commit();
            r.switchTo("main"); r.commit();
            String mainLog = r.log().toString();
            r.switchTo("fix");
            return mainLog + " " + r.log();
        });
        check("bad branch names are refused", "true true {main=c1}", () -> {
            Repo r = new Repo();
            return throwsIAE(() -> r.createBranch("main")) + " " + throwsIAE(() -> r.switchTo("nope")) + " " + r.branches;
        });
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
