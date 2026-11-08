fun ticketPrice(age: Int): Int = when (age) {
    in 0..2 -> 0
    in 3..12 -> 5
    in 13..64 -> 12
    else -> if (age < 0) -1 else 6        // what's left: negative ages and 65+
}

fun fizzBuzz(i: Int): String = when {
    i % 15 == 0 -> "FizzBuzz"             // first: when runs the first branch that matches
    i % 3 == 0 -> "Fizz"
    i % 5 == 0 -> "Buzz"
    else -> i.toString()
}

fun fizzBuzzLine(n: Int): String {
    var line = ""
    for (i in 1..n) {                     // 1..0 is empty, so n = 0 gives ""
        if (i > 1) line += " "
        line += fizzBuzz(i)
    }
    return line
}
