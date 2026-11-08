package katas;

public record Token(T type, String text, int line, int col) {
    public String at() { return line + ":" + col; }

    @Override public String toString() { return type + " '" + text + "' " + at(); }
}
