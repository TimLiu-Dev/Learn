package katas;

// The syntax tree of a regular expression (sealed types, L7)
public sealed interface Node {
    record Empty() implements Node {}                       // matches "": an empty pattern or branch, as in "a|"
    record Lit(char c) implements Node {}                   // one exact character
    record AnyChar() implements Node {}                     // .
    record Cat(Node left, Node right) implements Node {}    // left, then right
    record Alt(Node left, Node right) implements Node {}    // left | right
    record Star(Node body) implements Node {}               // body*
    record Plus(Node body) implements Node {}               // body+
    record Opt(Node body) implements Node {}                // body?
}
