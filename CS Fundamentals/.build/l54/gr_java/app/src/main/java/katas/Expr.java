package katas;

// Calc's syntax tree: a sealed interface whose permitted cases are records.
public sealed interface Expr {
    record Num(int value) implements Expr {}
    record Var(String name, Token at) implements Expr {}
    record Neg(Expr operand, Token at) implements Expr {}
    record Bin(T op, Expr left, Expr right, Token at) implements Expr {}
}
