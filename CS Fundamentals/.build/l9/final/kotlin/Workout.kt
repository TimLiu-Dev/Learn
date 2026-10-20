enum class ExerciseType(val label: String) {
    SQUAT("Squat"), BENCH("Bench press"), ROW("Row")
}

data class WorkoutSet(val exercise: ExerciseType, val reps: Int, val weightKg: Double)

sealed interface Entry {
    val day: String
}
data class Strength(override val day: String, val sets: List<WorkoutSet>) : Entry
data class Cardio(override val day: String, val minutes: Int, val km: Double) : Entry
data class Rest(override val day: String) : Entry

interface Summary {
    fun summary(): String
}

// Extensions: helpers that read like members
fun List<WorkoutSet>.volume(): Double {
    var total = 0.0
    for (s in this) total += s.reps * s.weightKg
    return total
}

fun Double.kgToLb(): Double = this * 2.20462

class WorkoutLog private constructor(val owner: String) : Summary {
    private val entries = mutableListOf<Entry>()

    val allEntries: List<Entry>
        get() = entries.toList()                       // a copy: callers can't edit the log

    val totalVolume: Double
        get() {
            var total = 0.0
            for (e in entries) if (e is Strength) total += e.sets.volume()
            return total
        }

    fun addStrength(day: String, sets: List<WorkoutSet>) {
        entries.add(Strength(day, sets.toList()))      // defensive copy on the way in
    }

    @JvmOverloads
    fun addCardio(day: String, minutes: Int, km: Double = 0.0) {
        entries.add(Cardio(day, minutes, km))
    }

    fun addRest(day: String) {
        entries.add(Rest(day))
    }

    override fun summary(): String {
        val sb = StringBuilder("Week for $owner\n")
        for (e in entries) {
            val line = when (e) {                      // exhaustive: no else needed
                is Strength -> "${e.day}: strength, ${e.sets.size} set(s), ${"%.0f".format(e.sets.volume())} kg"
                is Cardio -> if (e.km > 0) "${e.day}: cardio, ${e.minutes} min, ${e.km} km"
                             else "${e.day}: cardio, ${e.minutes} min"
                is Rest -> "${e.day}: rest"
            }
            sb.append(line).append('\n')
        }
        sb.append("Total volume: ${"%.0f".format(totalVolume)} kg (${"%.0f".format(totalVolume.kgToLb())} lb)")
        return sb.toString()
    }

    companion object {
        @JvmStatic
        fun create(owner: String): WorkoutLog = WorkoutLog(owner)
    }
}
