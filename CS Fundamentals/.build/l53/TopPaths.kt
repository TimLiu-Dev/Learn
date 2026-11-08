import java.io.File

// cut -d' ' -f3 requests.log | sort | uniq -c | sort -rn | head -5, as one program
fun main() {
    File("requests.log").useLines { lines ->
        lines.map { it.split(' ')[2] }               // cut -d' ' -f3
            .groupingBy { it }.eachCount()           // sort | uniq -c, with a hash map (L19)
            .entries.sortedByDescending { it.value } // sort -rn
            .take(5)                                 // head -5
            .forEach { (path, n) -> println("%4d %s".format(n, path)) }
    }
}
