import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

// IMF-fixdate: "Sun, 06 Nov 1994 08:49:37 GMT". English names whatever the JVM's locale, and "dd", not "d".
val IMF_FIXDATE: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH).withZone(ZoneOffset.UTC)

fun retirementHeaders(deprecatedAt: Instant, sunsetAt: Instant, docs: String): Map<String, String> {
    require(!sunsetAt.isBefore(deprecatedAt)) { "sunset $sunsetAt is before deprecation $deprecatedAt" }
    return mapOf(
        "Deprecation" to "@${deprecatedAt.epochSecond}",
        "Sunset" to IMF_FIXDATE.format(sunsetAt),
        "Link" to "<$docs>; rel=\"deprecation\"",
    )
}
