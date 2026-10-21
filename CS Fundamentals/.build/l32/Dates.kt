import java.time.LocalDate
import kotlin.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

// java.time types need a serializer: write the date as an ISO-8601 string
object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

@Serializable
data class Loan(
    val book: String,
    val at: Instant,                                              // kotlin.time: built in
    @Serializable(with = LocalDateSerializer::class) val due: LocalDate,
)

fun main() {
    val loan = Loan("Dune", Instant.parse("2026-10-20T09:30:00Z"), LocalDate.of(2026, 11, 3))
    val text = Json.encodeToString(loan)
    println(text)
    println(Json.decodeFromString<Loan>(text))
}
