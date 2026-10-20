package katas;

import java.util.ArrayList;
import java.util.List;

public class Password {
    public static PasswordResult check(String password) {
        List<Rule> failures = new ArrayList<>();
        if (password.length() < 8) failures.add(Rule.TOO_SHORT);

        boolean hasDigit = false;
        boolean hasUpper = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isUpperCase(c)) hasUpper = true;
        }
        if (!hasDigit) failures.add(Rule.NO_DIGIT);
        if (!hasUpper) failures.add(Rule.NO_UPPERCASE);
        if (password.contains(" ")) failures.add(Rule.HAS_SPACE);

        return failures.isEmpty()
                ? new PasswordResult.Ok()
                : new PasswordResult.Rejected(List.copyOf(failures));
    }
}
