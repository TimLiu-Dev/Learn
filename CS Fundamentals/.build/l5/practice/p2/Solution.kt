// Which items changed between two snapshots? A missing key counts as null.
fun changedKeys(before: Map<String, Int?>, after: Map<String, Int?>): List<String> =
    (before.keys + after.keys)
        .filter { before[it] != after[it] }   // compares values, boxed or not, and null == null
        .sorted()
