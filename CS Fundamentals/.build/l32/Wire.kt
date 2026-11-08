import java.time.*
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

// One small serializer per java.time type. Use the ISO formatters, not toString():
// toString() drops ":00" seconds, and RFC 3339 requires them
object LocalDateTimeIso : KSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDateTime) = encoder.encodeString(ISO_LOCAL_DATE_TIME.format(value))
    override fun deserialize(decoder: Decoder): LocalDateTime = LocalDateTime.parse(decoder.decodeString())
}
object ZoneIdString : KSerializer<ZoneId> {
    override val descriptor = PrimitiveSerialDescriptor("ZoneId", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ZoneId) = encoder.encodeString(value.id)
    override fun deserialize(decoder: Decoder): ZoneId = ZoneId.of(decoder.decodeString())
}
object ZonedIso : KSerializer<ZonedDateTime> {           // keeps the zone id: ...-04:00[America/New_York]
    override val descriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ZonedDateTime) = encoder.encodeString(ISO_ZONED_DATE_TIME.format(value))
    override fun deserialize(decoder: Decoder): ZonedDateTime = ZonedDateTime.parse(decoder.decodeString())
}

// A future event: store what the humans agreed on (local time + zone), not an instant
@Serializable
data class Meeting(
    val title: String,
    @Serializable(with = LocalDateTimeIso::class) val start: LocalDateTime,
    @Serializable(with = ZoneIdString::class) val zone: ZoneId,
) {
    fun startInstant(): Instant = start.atZone(zone).toInstant()   // computed with today's rules
}

@Serializable
data class Stamp(@Serializable(with = ZonedIso::class) val at: ZonedDateTime)

fun main() {
    val m = Meeting("Standup", LocalDateTime.of(2026, 11, 2, 9, 0), ZoneId.of("America/New_York"))
    val json = Json.encodeToString(m)
    println(json)
    println("starts at ${Json.decodeFromString<Meeting>(json).startInstant()}")

    // RFC 3339 strings carry an offset; parse them as OffsetDateTime or Instant
    val wire = "2026-11-01T01:30:00-05:00"
    println("offset  ${OffsetDateTime.parse(wire)}")
    println("instant ${kotlin.time.Instant.parse(wire)}")      // Kotlin's own Instant parses offsets too

    val stamp = Stamp(ZonedDateTime.of(2026, 11, 1, 1, 30, 0, 0, ZoneId.of("America/New_York")))
    println("toString ${stamp.at}")                      // no seconds: fine for logs, not for the wire
    val s = Json.encodeToString(stamp)
    println(s)
    val back = Json.decodeFromString<Stamp>(s)
    println("back    ${back.at}   equal? ${back == stamp}")
}
