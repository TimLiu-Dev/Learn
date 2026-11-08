package katas

// The syntax tree of a regular expression (sealed types, L7)
sealed interface Node {
    data object Empty : Node                        // matches "": an empty pattern or branch, as in "a|"
    data class Lit(val c: Char) : Node              // one exact character
    data object AnyChar : Node                      // .
    data class Cat(val left: Node, val right: Node) : Node   // left, then right
    data class Alt(val left: Node, val right: Node) : Node   // left | right
    data class Star(val body: Node) : Node          // body*
    data class Plus(val body: Node) : Node          // body+
    data class Opt(val body: Node) : Node           // body?
}
