fun changedKeys(before: Map<String, Int?>, after: Map<String, Int?>): List<String> =
    (before.keys + after.keys)
        .filter { before[it]!! != after[it]!! }
        .sorted()
