package katas;

// What running a program produced. Both back ends turn a CalcError into a value, so tests can compare them.
public sealed interface Outcome {
    record Value(int n) implements Outcome {}
    record Error(String message) implements Outcome {}
}
