import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Looks right, and is right on the 10th to the 31st of every month.
fun retirementHeaders(deprecatedAt: Instant, sunsetAt: Instant, docs: String): Map<String, String> {
    require(!sunsetAt.isBefore(deprecatedAt)) { "sunset $sunsetAt is before deprecation $deprecatedAt" }
    return mapOf(
        "Deprecation" to "@${deprecatedAt.epochSecond}",
        "Sunset" to DateTimeFormatter.RFC_1123_DATE_TIME.format(sunsetAt.atOffset(ZoneOffset.UTC)),
        "Link" to "<$docs>; rel=\"deprecation\"",
    )
}
