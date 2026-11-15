// cut -d"$delim" -f"$field"
fun cut(lines: List<String>, delim: Char, field: Int): List<String> =
    TODO("split each line on delim; a line without delim passes through whole")

// uniq -c: only ADJACENT equal lines are merged
fun uniqC(lines: List<String>): List<Pair<Int, String>> =
    TODO("extend the current run, or start a new one")

// sort | uniq -c | sort -rn | head -n $n   (equal counts: alphabetical)
fun topN(lines: List<String>, n: Int): List<Pair<Int, String>> =
    TODO("compose sorted(), uniqC, count descending and take")
