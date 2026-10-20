// A CSV row looks like: name,age   e.g. "Ada,36"
fun readAge(fields: List<String>): Int =
    try {
        fields[2].trim().toInt()          // BUG: age is at index 1, not 2
    } catch (e: Exception) {              // meant for bad numbers... catches EVERYTHING
        -1
    }

fun readAgeNarrow(fields: List<String>): Int =
    try {
        fields[2].trim().toInt()          // same bug
    } catch (e: NumberFormatException) {  // only the failure we actually expect
        -1
    }

fun main() {
    val row = "Ada,36".split(",")
    println("broad catch says age = ${readAge(row)}")     // looks like 'invalid age'. It isn't.
    println("narrow catch says age = ${readAgeNarrow(row)}")
}
