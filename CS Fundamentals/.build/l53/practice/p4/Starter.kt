// The arguments a program receives after bash expands `line`.
// vars are the shell's variables; args are the script's arguments ($1, $2, ...).
fun expand(line: String, vars: Map<String, String>, args: List<String>): List<String> =
    TODO("split the line on spaces, then expand each token by its form")
