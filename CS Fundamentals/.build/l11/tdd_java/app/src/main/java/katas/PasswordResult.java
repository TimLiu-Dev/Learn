package katas;

import java.util.List;

public sealed interface PasswordResult permits PasswordResult.Ok, PasswordResult.Rejected {
    record Ok() implements PasswordResult {}

    record Rejected(List<Rule> failures) implements PasswordResult {}
}
