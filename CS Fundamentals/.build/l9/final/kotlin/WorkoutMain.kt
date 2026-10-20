fun main() {
    val log = WorkoutLog.create("Ada")
    log.addStrength("Mon", listOf(
        WorkoutSet(ExerciseType.SQUAT, 5, 100.0),
        WorkoutSet(ExerciseType.SQUAT, 5, 100.0),
        WorkoutSet(ExerciseType.BENCH, 8, 60.0),
    ))
    log.addCardio("Tue", 30, 5.2)
    log.addRest("Wed")
    log.addStrength("Thu", listOf(WorkoutSet(ExerciseType.ROW, 10, 50.0)))
    log.addCardio("Fri", 20)
    println(log.summary())
    println("entries: ${log.allEntries.size}")
}
