import java.time.Instant

/** What's wrong with a security.txt (RFC 9116) at [now]; empty means it's usable. */
fun problems(text: String, now: Instant): List<String> =
    TODO("parse the fields, then check Contact and Expires")
