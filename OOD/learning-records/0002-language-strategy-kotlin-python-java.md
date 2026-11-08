# 0002: Language Strategy for 2027 OOD Interviews: Kotlin, Python, and Java

The user established a clear language priority hierarchy for 2027 Senior/Staff OOD & LLD interviews: **Kotlin (Primary)**, **Python (Secondary)**, and **Java (Tertiary)**.

In OOD interviews, language choice directly impacts how cleanly a candidate demonstrates encapsulation, immutability, thread safety, and state machine transitions within a 45-minute window. Kotlin is optimal because its `data class`, `sealed interface`, null-safety, and concise JVM concurrency allow senior design with minimal typing boilerplate. Python serves as a fast-typing scripting alternative when strict typing patterns are adhered to (`@dataclass(frozen=True)`). Java 17+ `record` serves as the universal enterprise fallback. All key architectural archetypes in reference materials will feature first-class Kotlin, Python, and Java implementations.
