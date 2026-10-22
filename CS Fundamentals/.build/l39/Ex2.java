import java.util.*;

public class Ex2 {
    static final class Email {
        private final String to, subject, body;
        private final List<String> cc;
        private final boolean html;

        private Email(Builder b) { to = b.to; subject = b.subject; body = b.body; cc = List.copyOf(b.cc); html = b.html; }

        static Builder to(String to) { return new Builder(to); }           // the required field starts the chain

        // A named static factory for the common case
        static Email reminder(String to, String title, int daysLate) {
            return to(to).subject("Overdue: " + title).body(title + " is " + daysLate + " days late.").build();
        }

        @Override public String toString() {
            return "Email(to=" + to + ", subject=" + subject + ", body=" + body + ", cc=" + cc + ", html=" + html + ")";
        }

        static final class Builder {
            private final String to;
            private String subject = "", body = "";
            private final List<String> cc = new ArrayList<>();
            private boolean html = false;
            private Builder(String to) { this.to = to; }
            Builder subject(String v) { subject = v; return this; }
            Builder body(String v) { body = v; return this; }
            Builder cc(String address) { cc.add(address); return this; }   // repeatable: builders handle lists well
            Builder html(boolean v) { html = v; return this; }
            Email build() {
                if (!to.contains("@")) throw new IllegalStateException("bad address: " + to);
                if (subject.isBlank()) throw new IllegalStateException("subject is required");
                return new Email(this);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(Email.to("ada@uni.test").subject("Hello").cc("desk@library.test").cc("boss@library.test").build());
        System.out.println(Email.reminder("ada@uni.test", "Dune", 3));
        try { Email.to("ada").subject("Hi").build(); } catch (IllegalStateException e) { System.out.println("rejected: " + e.getMessage()); }
    }
}
