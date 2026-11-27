package catalogue.internal

// A value object: equal when the digits are equal, and impossible to build invalid.
// ISBNs have 13 digits; the last is a check digit, "calculated using a Modulus 10 system
// with alternate weights of 1 and 3" (isbn-international.org).
@JvmInline
internal value class Isbn private constructor(val digits: String) {
    companion object {
        fun parse(text: String): Isbn {
            val digits = text.filter { it != '-' && it != ' ' }
            require(digits.length == 13 && digits.all(Char::isDigit)) { "an ISBN has 13 digits: '$text'" }
            val sum = digits.mapIndexed { i, c -> c.digitToInt() * if (i % 2 == 0) 1 else 3 }.sum()
            require(sum % 10 == 0) { "bad check digit: '$text'" }
            return Isbn(digits)
        }
    }
}

// The catalogue's Book: a title with bibliographic data and the copies it owns.
internal class Book(val isbn: Isbn, val title: String, val author: String) {
    val barcodes = mutableSetOf<String>()
}
