package budget

/**
 * The result of parsing one piece of untrusted input: a value, or the reason
 * it was rejected. Bad input is expected here, so it's a value, not an
 * exception (L10).
 */
sealed interface Parsed<out T> {
    data class Ok<out T>(val value: T) : Parsed<T>
    data class Bad(val reason: String) : Parsed<Nothing>
}

/** Transforms the value of an [Parsed.Ok]; a [Parsed.Bad] passes through untouched. */
inline fun <T, R> Parsed<T>.map(transform: (T) -> R): Parsed<R> = when (this) {
    is Parsed.Ok -> Parsed.Ok(transform(value))
    is Parsed.Bad -> this
}
