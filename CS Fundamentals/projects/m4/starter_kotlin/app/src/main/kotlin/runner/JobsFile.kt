package runner

/** Parses and validates a jobs file. Every problem is an [InputError] naming the line. */
object JobsFile {
    fun parse(text: String): List<Job> =
        TODO("Milestone 1: parse 'id=a duration=200 fail=2 deps=b,c' lines (see the spec on the project page)")
}
