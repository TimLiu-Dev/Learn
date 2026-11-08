fun ticketPrice(age: Int): Int = when (age) {
    in 0..2 -> 0
    in 3..12 -> 5
    in 13..64 -> 12
    else -> if (age < 0) -1 else 6
}

fun fizzBuzz(i: Int): String = when {
    i % 3 == 0 -> "Fizz"
    i % 5 == 0 -> "Buzz"
    i % 15 == 0 -> "FizzBuzz"
    else -> i.toString()
}

fun fizzBuzzLine(n: Int): String {
    var line = ""
    for (i in 1..n) {
        if (i > 1) line += " "
        line += fizzBuzz(i)
    }
    return line
}
