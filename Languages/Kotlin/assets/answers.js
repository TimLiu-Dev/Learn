window.EXERCISE_ANSWERS = {
  "0000-ex1": {
    "title": "Diagnostic Task 1 · Profile & Heart-Rate Classifier (Write from blank)",
    "code": "fun main() {\n    val name = \"Alex\"\n    val bpm = 154\n    val maxHr = 190\n    val pct = (bpm * 100) / maxHr\n    val zone = when {\n        pct >= 90 -> \"Peak\"\n        pct >= 70 -> \"Cardio\"\n        else -> \"Base\"\n    }\n    println(\"$name: $bpm bpm ($zone, $pct%)\")\n}",
    "why": "All references are immutable `val`. `zone` is computed directly via a `when` expression. String templates eliminate clumsy `+` concatenation.",
    "expectedOutput": "Alex: 154 bpm (Cardio, 81%)"
  },
  "0000-ex2": {
    "title": "Diagnostic Task 2 · Audit & Clean Java-Flavoured Code (Refactor)",
    "code": "fun main() {\n    val rawInput: String? = \"45\"\n    val minutes = rawInput?.toIntOrNull() ?: 30\n    val title = if (minutes > 40) \"Long Run\" else \"Recovery\"\n    println(\"Session: $title ($minutes min)\")\n}",
    "why": "Eliminates all `var`s. Uses `?.toIntOrNull() ?: 30` to combine safe transformation with an Elvis default fallback. `title` uses a clean single `if` expression.",
    "expectedOutput": "Session: Long Run (45 min)"
  },
  "0001-ex1": {
    "title": "Exercise 1 · Interval session (Write from blank)",
    "code": "fun main() {\n    val athlete = \"Sam\"\n    val maxHeartRate = 185\n    val distanceKm = 4.8\n    var calories = 0\n    calories += 120\n    calories += 95\n    calories += 140\n    println(\"$athlete ran $distanceKm km, burned $calories kcal (max HR: $maxHeartRate)\")\n}",
    "why": "Immutable values (athlete, maxHeartRate, distanceKm) use `val`. `calories` is the only value that changes, so it is the single `var`. The compiler infers `Double` for 4.8 and `Int` for 0 without verbose type annotations."
  },
  "0001-ex2": {
    "title": "Exercise 2 · Clean up an AI-generated snippet (Refactor)",
    "code": "fun main() {\n    val userName = \"Sam\"\n    val maxHeartRate = 220 - 34\n    val zones = mutableListOf(\"Warm-up\", \"Fat burn\", \"Cardio\")\n    val label = if (maxHeartRate > 180) \"High\" else \"Normal\"\n    val totalMinutes = 10 + 25\n    println(\"$userName: $label, ${zones.size} zones, $totalMinutes min\")\n}",
    "why": "Zero `var`s remain. `zones` is a `val` pointing to a `MutableList` (the reference never reassigns). `label` is initialized directly from an `if` expression rather than declared empty and reassigned. Redundant type declarations (`: String`, `: Int`) are removed.",
    "expectedOutput": "Sam: High, 3 zones, 35 min"
  },
  "0002-ex1": {
    "title": "Exercise 1 · Post-workout summary (Write from blank)",
    "code": "fun main() {\n    val athlete = \"Priya\"\n    val minutes = 42\n    val distanceKm = 7.3\n    val avgHeartRate = 151\n\n    println(\"$athlete ran $distanceKm km in $minutes min (avg $avgHeartRate bpm)\")\n\n    val summary = \"\"\"\n        Workout summary\n        Athlete: $athlete\n        Distance: $distanceKm km\n        Calories: ${minutes * 10}\n    \"\"\".trimIndent()\n    println(summary)\n}",
    "why": "String templates ($variable) eliminate all `+` concatenation. Curly braces are used exclusively for expressions (`${minutes * 10}`). Raw strings with `trimIndent()` remove shared leading indentation while avoiding ugly `\\n` escape sequences."
  },
  "0002-ex2": {
    "title": "Exercise 2 · Clean up the string building (Refactor)",
    "code": "fun main() {\n    val userName = \"Sam\"\n    val label = \"High\"\n    val zones = listOf(\"Warm-up\", \"Fat burn\", \"Cardio\")\n    val totalMinutes = 35\n    val price = 4.99\n\n    println(\"$userName: $label, ${zones.size} zones, $totalMinutes min\")\n    println(\"First zone: ${zones[0]} (${zones[0].length} letters)\")\n    println(\"Upgrade for $$price/month\")\n\n    val report = \"\"\"\n        Weekly report\n        User: $userName\n        Minutes: $totalMinutes\n        Goal: 150\n    \"\"\".trimIndent()\n    println(report)\n}",
    "why": "A literal dollar sign is escaped as `$$price`. Collections access and property calls inside strings use `${...}`. `report` is converted to a clean raw multiline string with `.trimIndent()`, killing manual `\\\n` and `.toString()` calls.",
    "expectedOutput": "Sam: High, 3 zones, 35 min\nFirst zone: Warm-up (7 letters)\nUpgrade for $4.99/month\nWeekly report\nUser: Sam\nMinutes: 35\nGoal: 150"
  },
  "0003-ex1": {
    "title": "Exercise 1 · Session timer (Write from blank)",
    "code": "fun main() {\n    // 1. Reps countdown\n    for (rep in 10 downTo 1) {\n        print(\"$rep \")\n    }\n    println()\n\n    // 2. Minute markers (excluding 30)\n    for (m in 0..<30 step 5) {\n        print(\"$m \")\n    }\n    println()\n\n    // 3. Heart rate in range\n    val heartRate = 142\n    val zone = if (heartRate in 133..152) \"Cardio\" else \"Other\"\n    println(zone)\n\n    // 4. Incline bounded to 0..15\n    val requestedIncline = 18\n    val incline = requestedIncline.coerceIn(0, 15)\n    println(incline)\n}",
    "why": "Progressions use `downTo` for descending counts, `..<` for open-ended upper bounds, and `step` for custom strides. `in` checks range membership, and `coerceIn(min, max)` safely clamps numeric limits without awkward nested `if` comparisons."
  },
  "0003-ex2": {
    "title": "Exercise 2 · Off-by-one country (Refactor)",
    "code": "fun main() {\n    val zones = listOf(\"Warm-up\", \"Fat burn\", \"Cardio\", \"Peak\")\n    for (zone in zones) {\n        println(\"Zone: $zone\")\n    }\n\n    val heartRate = 128\n    if (heartRate in 114..133) {\n        println(\"Fat burn zone\")\n    }\n\n    val age = 34\n    if (age !in 18..100) {\n        println(\"Age out of range\")\n    } else {\n        println(\"Age OK\")\n    }\n\n    for (minute in 20 downTo 0 step 10) {\n        println(\"Cool-down check at $minute min\")\n    }\n\n    val incline = 18.coerceIn(0, 15)\n    println(\"Incline: $incline\")\n}",
    "why": "Direct collection iteration `for (zone in zones)` replaces index loops with `size - 1`. `in` and `!in` replace dual `<`, `>` checks. `for (m in 20 downTo 0 step 10)` replaces mutable while-loop counters. `coerceIn` replaces manual boundary ifs.",
    "expectedOutput": "Zone: Warm-up\nZone: Fat burn\nZone: Cardio\nZone: Peak\nFat burn zone\nAge OK\nCool-down check at 20 min\nCool-down check at 10 min\nCool-down check at 0 min\nIncline: 15"
  },
  "0004-ex1": {
    "title": "Exercise 1 · Two small functions (Write from blank)",
    "code": "fun paceLabel(minPerKm: Double): String =\n    if (minPerKm < 5.0) \"Fast\" else \"Steady\"\n\nfun parseReps(input: String): Int =\n    try {\n        input.toInt()\n    } catch (e: NumberFormatException) {\n        0\n    }\n\nfun main() {\n    println(paceLabel(4.6))\n    println(paceLabel(5.8))\n    println(parseReps(\"12\"))\n    println(parseReps(\"twelve\"))\n}",
    "why": "`if` and `try` are expressions that evaluate to a value. Both functions use single-expression bodies (`=`) directly returning the expression result without temporary variables or return statements in each branch."
  },
  "0004-ex2": {
    "title": "Exercise 2 · Assign-in-every-branch cleanup (Refactor)",
    "code": "fun statusText(isPaused: Boolean): String =\n    if (isPaused) \"Paused\" else \"Running\"\n\nfun main() {\n    val heartRate = 172\n    val zone = if (heartRate > 160) \"Peak\" else \"Cardio\"\n    val totalMinutes = 45\n    val goalMet = totalMinutes >= 30\n    println(\"$zone, $totalMinutes min, goal met: $goalMet, ${statusText(false)}\")\n}",
    "why": "Kotlin has no ternary operator (`? :`) because `if` itself is an expression. Assigning `val zone = if (...)` ensures the variable is guaranteed immutable and never uninitialized. Boolean expressions like `totalMinutes >= 30` evaluate directly to true/false.",
    "expectedOutput": "Peak, 45 min, goal met: true, Running"
  },
  "0005-ex1": {
    "title": "Exercise 1 · Heart-rate zones (Write from blank)",
    "code": "fun zoneFor(percent: Int): String = when (percent) {\n    in 0..49 -> \"Rest\"\n    in 50..59 -> \"Warm-up\"\n    in 60..69 -> \"Fat burn\"\n    in 70..79 -> \"Cardio\"\n    in 80..89 -> \"Hard\"\n    in 90..100 -> \"Peak\"\n    else -> \"Invalid\"\n}\n\nfun main() {\n    val bpm = 152\n    val maxHr = 190\n    val percent = (bpm * 100) / maxHr\n    println(\"$bpm bpm → ${zoneFor(percent)} ($percent%)\")\n}",
    "why": "`when` with a subject (`percent`) supports range checks using `in a..b`. Single-expression body (`= when`) cleanly returns the resulting branch value. The `else` branch guarantees exhaustiveness for integer inputs."
  },
  "0005-ex2": {
    "title": "Exercise 2 · Untangle the ladders (Refactor)",
    "code": "fun describeWorkout(type: String, minutes: Int): String {\n    val category = when (type) {\n        \"run\", \"jog\", \"row\" -> \"Cardio\"\n        \"squat\", \"deadlift\" -> \"Strength\"\n        else -> \"Other\"\n    }\n    val effort = when {\n        minutes < 20 -> \"short\"\n        minutes < 45 -> \"medium\"\n        else -> \"long\"\n    }\n    return \"$category ($effort)\"\n}\n\nfun main() {\n    println(describeWorkout(\"jog\", 30))\n    println(describeWorkout(\"deadlift\", 15))\n    println(describeWorkout(\"yoga\", 60))\n}",
    "why": "Combines matching branches with commas (`\"run\", \"jog\", \"row\"`). Replaces complex cascading `if-else` blocks with two clean `when` expressions, returning a templated string `$category ($effort)`.",
    "expectedOutput": "Cardio (medium)\nStrength (short)\nOther (long)"
  },
  "0006-ex1": {
    "title": "Exercise 1 · Pace, week and plan (Write from blank)",
    "code": "fun splitPace(totalSeconds: Int): Pair<Int, Int> =\n    (totalSeconds / 60) to (totalSeconds % 60)\n\nfun main() {\n    val weeklyMinutes = mapOf(\"Mon\" to 30, \"Wed\" to 45, \"Sat\" to 60)\n    val planned = listOf(\"Run\", \"Row\")\n    val completed = listOf(\"Run\", \"Row\")\n\n    for ((day, mins) in weeklyMinutes) {\n        println(\"$day: $mins min\")\n    }\n\n    val (mins, secs) = splitPace(335)\n    println(\"Pace: $mins:$secs /km\")\n\n    if (completed == planned) {\n        println(\"Plan complete\")\n    }\n}",
    "why": "`to` creates a `Pair` succinctly, destructuring unpacks map entries and Pairs without `.key`/`.first`, and `==` checks structural list contents."
  },
  "0006-ex2": {
    "title": "Exercise 2 · Fix the bug, then clean up (Refactor)",
    "code": "fun bestAndWorstDay(): Pair<String, String> = \"Sat\" to \"Mon\"\n\nfun main() {\n    val weeklyMinutes = mapOf(\"Mon\" to 20, \"Wed\" to 45, \"Sat\" to 70)\n    for ((day, minutes) in weeklyMinutes) {\n        println(\"$day: $minutes min\")\n    }\n\n    val (best, worst) = bestAndWorstDay()\n    println(\"Best: $best, worst: $worst\")\n\n    val todayType = \"Ru\" + \"n\"\n    println(if (todayType == \"Run\") \"Cardio day\" else \"Rest day\")\n}",
    "why": "Replaced `===` (referential identity on a freshly concatenated string) and `.equals` with structural `==`, built the Pair using `to`, destructured the map entry and Pair, and merged the two opposite `if`s into one `if` expression.",
    "expectedOutput": "Mon: 20 min\nWed: 45 min\nSat: 70 min\nBest: Sat, worst: Mon\nCardio day"
  },
  "0007-ex1": {
    "title": "Exercise 1 · Weekly report (Write from blank)",
    "code": "fun main() {\n    val week = mapOf(\n        \"Mon\" to 25, \"Tue\" to 0, \"Wed\" to 50, \"Thu\" to 35,\n        \"Fri\" to 0, \"Sat\" to 75, \"Sun\" to 40,\n    )\n\n    var total = 0\n    var activeDays = 0\n\n    for ((day, mins) in week) {\n        val label = when (mins) {\n            0 -> \"rest\"\n            in 1..29 -> \"short\"\n            in 30..59 -> \"medium\"\n            else -> \"long\"\n        }\n        if (mins > 0) activeDays++\n        total += mins\n        println(\"$day: $mins min ($label)\")\n    }\n\n    val goalMet = if (total >= 150) \"yes\" else \"no\"\n    println(\"\"\"\n        Total: $total min\n        Active days: $activeDays of ${week.size}\n        Goal met: $goalMet\n    \"\"\".trimIndent())\n}",
    "why": "Integrates map destructuring, `when` with range branches, single `if` expression for goal evaluation, and a raw multiline summary string using `trimIndent()`."
  },
  "0007-ex2": {
    "title": "Exercise 2 · The full AI-flavoured special (Refactor)",
    "code": "fun main() {\n    val goalSetting = \"45min\"\n    val goal = try {\n        goalSetting.toInt()\n    } catch (e: NumberFormatException) {\n        30\n    }\n\n    for (i in 1..3) {\n        println(\"Warm-up set $i of 3\")\n    }\n\n    val sessions = mapOf(\n        \"Run\" to 1800,\n        \"Row\" to 1260,\n        \"Bike\" to 3725,\n    )\n\n    for ((name, seconds) in sessions) {\n        val mins = seconds / 60\n        val intensity = when (mins) {\n            in 0..<25 -> \"easy\"\n            in 25..<60 -> \"steady\"\n            else -> \"endurance\"\n        }\n        val goalNote = if (mins >= goal) \"goal met\" else \"below goal\"\n        val fav = if (name == \"Run\") \" (favourite)\" else \"\"\n        println(\"$name$fav: ${mins}m ${seconds % 60}s, $intensity, $goalNote\")\n    }\n}",
    "why": "Replaces every `var` and statement ladder with `val`s initialized directly from `try`, `when`, and `if` expressions; uses a range `1..3`, read-only `mapOf(... to ...)`, destructuring `(name, seconds)`, structural `==`, and string templates while preserving exact output.",
    "expectedOutput": "Warm-up set 1 of 3\nWarm-up set 2 of 3\nWarm-up set 3 of 3\nRun (favourite): 30m 0s, steady, goal met\nRow: 21m 0s, easy, below goal\nBike: 62m 5s, endurance, goal met"
  },
  "0008-ex1": {
    "title": "Exercise 1 · Workout banner safely (Write from blank)",
    "code": "class Coach(val name: String)\nclass Workout(val title: String, val coach: Coach?)\nclass Profile(val nickname: String?, val currentWorkout: Workout?)\n\nfun coachName(profile: Profile?): String? =\n    profile?.currentWorkout?.coach?.name\n\nfun nicknameLength(profile: Profile?): Int? =\n    profile?.nickname?.length\n\nfun main() {\n    val full = Profile(\"Kai\", Workout(\"Intervals\", Coach(\"Maya\")))\n    val noWorkout = Profile(null, null)\n    println(coachName(full))\n    println(coachName(noWorkout))\n    println(coachName(null))\n    println(nicknameLength(full))\n    println(nicknameLength(noWorkout))\n}",
    "why": "Chains safe calls (`?.`) cleanly across nullable objects without any `if (x != null)` checks or unsafe `!!` assertions. Both functions return nullable types (`String?` / `Int?`)."
  },
  "0008-ex2": {
    "title": "Exercise 2 · Flatten the null-check pyramid (Refactor)",
    "code": "class Device(val name: String)\nclass Session(var notes: String?, val device: Device?)\nclass Athlete(val nickname: String?, val session: Session?)\n\nfun deviceLabel(athlete: Athlete?): String? =\n    athlete?.session?.device?.name?.uppercase()\n\nfun saveNotes(athlete: Athlete?, text: String) {\n    athlete?.session?.notes = text\n}\n\nfun main() {\n    val full = Athlete(\"Kai\", Session(null, Device(\"Treadmill T5\")))\n    val noSession = Athlete(null, null)\n    println(deviceLabel(full))\n    println(deviceLabel(noSession))\n    println(deviceLabel(null))\n    saveNotes(full, \"Felt strong\")\n    saveNotes(noSession, \"Ignored\")\n    println(full.session?.notes)\n}",
    "why": "Replaces nested 3-level `if (athlete != null) { if (session != null) ... }` pyramids with a single safe-call chain. Safe-call property assignment `athlete?.session?.notes = newNotes` executes only when both receiver references are non-null.",
    "expectedOutput": "TREADMILL T5\nnull\nnull\nFelt strong"
  },
  "0009-ex1": {
    "title": "Exercise 1 · Display name and workout banner (Write from blank)",
    "code": "class Coach(val name: String)\nclass Workout(val title: String, val coach: Coach?)\nclass Profile(val nickname: String?, val email: String?, val currentWorkout: Workout?)\n\nfun displayName(profile: Profile?): String =\n    profile?.nickname ?: profile?.email ?: \"Athlete\"\n\nfun describeWorkout(profile: Profile?): String {\n    val workout = profile?.currentWorkout ?: return \"Rest day\"\n    val coach = workout.coach ?: throw IllegalStateException(\"${workout.title} has no coach\")\n    return \"${workout.title} with ${coach.name}\"\n}\n\nfun main() {\n    println(displayName(Profile(\"Kai\", \"kai@example.com\", null)))\n    println(displayName(Profile(null, \"maya@example.com\", null)))\n    println(displayName(null))\n\n    val profiles = listOf(\n        null,\n        Profile(\"Kai\", null, Workout(\"Intervals\", Coach(\"Maya\"))),\n        Profile(\"Kai\", null, Workout(\"Tempo run\", null)),\n    )\n    for (p in profiles) {\n        val message = try { describeWorkout(p) } catch (e: IllegalStateException) { \"Error: ${e.message}\" }\n        println(message)\n    }\n}",
    "why": "Chains Elvis fallbacks (`nickname ?: email ?: \"Athlete\"`) in `displayName`, and uses `?: return` (normal missing workout) plus `?: throw` (data bug: missing coach) as flat guard clauses in `describeWorkout`."
  },
  "0009-ex2": {
    "title": "Exercise 2 · Flatten the defaults (Refactor)",
    "code": "class Workout(val title: String)\nclass Profile(val nickname: String?, val currentWorkout: Workout?)\n\nfun weeklyGoalMessage(settings: Map<String, String>, profile: Profile?): String {\n    val name = profile?.nickname ?: \"Athlete\"\n    val goal = settings[\"weeklyGoal\"]?.toIntOrNull() ?: 3\n    return \"$name, your goal is $goal workouts this week\"\n}\n\nfun workoutTitle(profile: Profile?): String {\n    val nonNullProfile = profile ?: throw IllegalArgumentException(\"No profile\")\n    val workout = nonNullProfile.currentWorkout ?: return \"No workout\"\n    return workout.title\n}\n\nfun main() {\n    println(weeklyGoalMessage(mapOf(\"weeklyGoal\" to \"4\"), Profile(\"Kai\", null)))\n    println(weeklyGoalMessage(mapOf(\"weeklyGoal\" to \"lots\"), null))\n    println(workoutTitle(Profile(null, Workout(\"Intervals\"))))\n    println(workoutTitle(Profile(null, null)))\n}",
    "why": "Replaces temporary `var` and `if (x == null)` ladders with Elvis default fallbacks. `?: throw` cleanly validates preconditions and eliminates the possibility of downstream null references.",
    "expectedOutput": "Kai, your goal is 4 workouts this week\nAthlete, your goal is 3 workouts this week\nIntervals\nNo workout"
  },
  "0010-ex1": {
    "title": "Exercise 1 · Describe a synced value (Write from blank)",
    "code": "fun describe(value: Any): String = when (value) {\n    is Int -> \"$value bpm\"\n    is String -> \"Note (${value.length} chars): $value\"\n    is Double -> \"$value km\"\n    else -> \"Unknown\"\n}\n\nfun main() {\n    val synced: List<Any> = listOf(150, \"Morning run\", 5.2, true)\n    for (value in synced) {\n        println(describe(value))\n    }\n}",
    "why": "In a `when` branch with `is String`, Kotlin automatically smart-casts `value` from `Any` to `String`, allowing direct access to `.length` without manual casting like `(value as String).length`."
  },
  "0010-ex2": {
    "title": "Exercise 2 · Remove the casts and assertions (Refactor)",
    "code": "class Workout(val title: String, val avgHeartRate: Int?)\n\nclass WorkoutScreen {\n    var current: Workout? = null\n\n    fun headline(): String {\n        val workout = current ?: return \"No workout\"\n        return if (workout.avgHeartRate != null) {\n            \"${workout.title} · ${workout.avgHeartRate} bpm\"\n        } else {\n            workout.title\n        }\n    }\n\n    fun minutesFrom(raw: Any): Int = raw as? Int ?: 0\n}\n\nfun main() {\n    val screen = WorkoutScreen()\n    println(screen.headline())\n    screen.current = Workout(\"Tempo run\", 158)\n    println(screen.headline())\n    screen.current = Workout(\"Yoga\", null)\n    println(screen.headline())\n    println(screen.minutesFrom(45))\n    println(screen.minutesFrom(\"45\"))\n}",
    "why": "Smart casts fail on `var` properties because another thread or getter could change the value between the check and usage. Copying `current` to a local `val workout = current ?: return` enables reliable smart casting. Safe cast `as?` returns null on type mismatch instead of throwing ClassCastException.",
    "expectedOutput": "No workout\nTempo run · 158 bpm\nYoga\n45\n0"
  },
  "0011-ex1": {
    "title": "Exercise 1 · Heart-rate monitor (Write from blank)",
    "code": "class HeartRateMonitor {\n    lateinit var deviceName: String\n\n    fun pair(name: String) {\n        deviceName = name\n    }\n\n    fun reading(raw: String?): String {\n        val safeRaw = requireNotNull(raw) { \"No reading from $deviceName\" }\n        val bpm = safeRaw.toIntOrNull() ?: error(\"Bad reading: $safeRaw\")\n        return \"$deviceName: $bpm bpm\"\n    }\n}\n\nfun main() {\n    val monitor = HeartRateMonitor()\n    monitor.pair(\"Chest strap\")\n    println(monitor.reading(\"142\"))\n    try { monitor.reading(null) } catch (e: IllegalArgumentException) { println(e.message) }\n    try { monitor.reading(\"abc\") } catch (e: IllegalStateException) { println(e.message) }\n}",
    "why": "`lateinit var` avoids making `deviceName` nullable, `requireNotNull` signals bad arguments (`IllegalArgumentException`), and `toIntOrNull() ?: error(...)` turns malformed input into `IllegalStateException` with a clear message."
  },
  "0011-ex2": {
    "title": "Exercise 2 · The session that crashes on launch (Refactor)",
    "code": "class WorkoutSession {\n    val deviceName: String? = System.getProperty(\"workout.device\")\n    lateinit var userName: String\n    var startedAt: Long? = null\n\n    fun start(name: String, time: Long) {\n        userName = name\n        startedAt = time\n    }\n\n    fun header(): String {\n        val device = deviceName ?: \"unknown device\"\n        return \"${userName.uppercase()} on ${device.lowercase()}\"\n    }\n\n    fun elapsedSeconds(now: Long): Long {\n        val start = checkNotNull(startedAt) { \"Session not started\" }\n        return (now - start) / 1000\n    }\n}\n\nfun label(raw: Any?): String =\n    (raw as? String)?.trim() ?: \"Untitled\"\n\nfun main() {\n    val session = WorkoutSession()\n    session.start(\"Sam\", 1_000)\n    println(session.header())\n    println(session.elapsedSeconds(61_000))\n    println(label(\"  Hill sprints \"))\n    println(label(null))\n}",
    "why": "`deviceName` explicitly declares `String?` to tame Java's `String!` platform type. `userName` uses `lateinit var` (reference type), `startedAt` stays `Long?` (primitive cannot be `lateinit`) guarded by `checkNotNull`, and `label` swaps unsafe `as String` + `!!` for `(raw as? String)?.trim() ?: \"Untitled\"`.",
    "expectedOutput": "SAM on unknown device\n60\nHill sprints\nUntitled"
  },
  "0012-ex1": {
    "title": "Exercise 1 · Coach message (Write from blank)",
    "code": "fun coachMessage(name: String?, bpm: Int?, maxHr: Int): String {\n    val athlete = name ?: \"Athlete\"\n    val rate = bpm ?: return \"$athlete: no heart-rate signal\"\n    val pct = rate * 100 / maxHr\n    val message = when (pct) {\n        in 90..Int.MAX_VALUE -> \"ease off\"\n        in 70..89 -> \"great pace\"\n        else -> \"pick it up\"\n    }\n    return \"$athlete, $pct% of max: $message\"\n}\n\nfun main() {\n    println(coachMessage(\"Ana\", 171, 190))\n    println(coachMessage(null, 140, 190))\n    println(coachMessage(\"Ben\", null, 185))\n    println(coachMessage(\"Cy\", 100, 200))\n}",
    "why": "Combines Elvis fallback for strings (`name ?: \"Athlete\"`), early exit guard for missing signal (`val rate = bpm ?: return`), and when expression for zone classification without mutable vars."
  },
  "0012-ex2": {
    "title": "Exercise 2 · Untangle the profile summary (Refactor)",
    "code": "class Profile(val name: String?, val maxHeartRate: Int?)\n\nclass Repository {\n    fun find(id: String): Any? = when (id) {\n        \"u1\" -> Profile(\"Ana\", 190)\n        \"u2\" -> Profile(null, null)\n        \"bad\" -> \"corrupted row\"\n        else -> null\n    }\n}\n\nfun summary(repo: Repository, id: String): String = when (val raw = repo.find(id)) {\n    null -> \"No profile\"\n    !is Profile -> \"Invalid data\"\n    else -> {\n        val displayName = raw.name ?: \"Unknown\"\n        val max = raw.maxHeartRate?.let { \"$it bpm\" } ?: \"not set\"\n        \"$displayName (max $max)\"\n    }\n}\n\nfun main() {\n    val repo = Repository()\n    for (id in listOf(\"u1\", \"u2\", \"bad\", \"zzz\")) {\n        println(summary(repo, id))\n    }\n}",
    "why": "Replaces unsafe casts and nested ifs with safe cast `as? Profile ?: return`. Uses `profile.name ?: \"Unknown\"` for null safety. Uses `.let { \"$it bpm\" } ?: \"no HR on file\"` to format nullable numbers cleanly.",
    "expectedOutput": "Ana (max 190 bpm)\nUnknown (max not set)\nInvalid data\nNo profile"
  },
  "0012-ex3": {
    "title": "Exercise 3 · Predict, then run (Analysis)",
    "code": "fun main() {\n    // a) Platform type from Java: compiles as String!, crashes at runtime with NullPointerException when property is missing\n    // val a: String = System.getProperty(\"user.mood\"); println(a)\n\n    // b) Safe call on platform type: prints null safely\n    val b = System.getProperty(\"user.mood\")?.length; println(b)\n\n    // c) Safe cast with Elvis: prints none\n    val c = (42 as Any) as? String ?: \"none\"; println(c)\n\n    // d) Unsafe cast: compiles, crashes at runtime with ClassCastException (Int cannot be cast to String)\n    // val d = (42 as Any) as String; println(d)\n\n    // e) Smart cast on local var not captured or modified: prints 3\n    var e: String? = \"Run\"; if (e != null) println(e.length)\n}",
    "why": "Demonstrates platform type runtime NPE (a), safe call short-circuiting to null (b), safe cast `as?` returning null to trigger Elvis (c), `ClassCastException` on `as` (d), and local `var` smart casting when the variable isn't reassigned between check and use (e)."
  },
  "0013-ex1": {
    "title": "Exercise 1 · Session Configurator (Write from blank)",
    "code": "fun createSession(\n    activity: String,\n    targetDistanceKm: Double = 5.0,\n    allowGps: Boolean = true,\n    coachNote: String? = null\n): String {\n    val note = coachNote ?: \"None\"\n    return \"Activity: $activity, Goal: $targetDistanceKm km, GPS: $allowGps, Note: $note\"\n}\n\nfun main() {\n    println(createSession(\"Cycling\"))\n    println(createSession(\"Running\", targetDistanceKm = 10.0))\n    println(createSession(\"Yoga\", allowGps = false, coachNote = \"Focus on breath\"))\n}",
    "why": "Declares default parameters directly in the signature, eliminating constructor overloads. Callers use named arguments to specify only the optional parameters they care about."
  },
  "0013-ex2": {
    "title": "Exercise 2 · Kill the Telescoping Overloads (Refactor)",
    "code": "fun startRun(athlete: String, distanceKm: Double = 5.0, isRace: Boolean = false): String {\n    val raceTag = if (isRace) \" (Race)\" else \"\"\n    return \"Athlete $athlete: $distanceKm km$raceTag\"\n}\n\nfun main() {\n    println(startRun(\"Elena\"))\n    println(startRun(\"Marcus\", distanceKm = 10.0))\n    println(startRun(\"Sarah\", distanceKm = 21.1, isRace = true))\n}",
    "why": "Collapses 3 telescoping methods into 1 function with defaults. Named arguments at the call site document what boolean and numeric literals mean.",
    "expectedOutput": "Athlete Elena: 5.0 km\nAthlete Marcus: 10.0 km\nAthlete Sarah: 21.1 km (Race)"
  },
  "0014-ex1": {
    "title": "Exercise 1 · Metrics & Conversions (Write from blank)",
    "code": "fun milesToKm(miles: Double): Double = miles * 1.60934\n\nfun paceDescription(minPerKm: Double): String = when {\n    minPerKm < 3.5 -> \"Elite\"\n    minPerKm in 3.5..5.0 -> \"Fast\"\n    else -> \"Steady\"\n}\n\nfun calorieEstimate(weightKg: Double, km: Double): Double = weightKg * km * 1.036\n\nfun main() {\n    println(milesToKm(10.0))\n    println(paceDescription(4.2))\n    println(calorieEstimate(70.0, 10.0))\n}",
    "why": "Single-expression bodies (`=`) eliminate `{ return ... }` boilerplate. Explicit return types document public API contracts."
  },
  "0014-ex2": {
    "title": "Exercise 2 · Simplify Java-Flavoured Calculators (Refactor)",
    "code": "fun calculateBmi(weightKg: Double, heightM: Double): Double =\n    weightKg / (heightM * heightM)\n\nfun isHealthyWeight(bmi: Double): Boolean = bmi in 18.5..24.9\n\nfun formatAthleteBadge(name: String, rank: Int): String = \"#$rank: $name\"\n\nfun main() {\n    val bmi = calculateBmi(72.0, 1.78)\n    println(\"BMI: $bmi (Healthy: ${isHealthyWeight(bmi)})\")\n    println(formatAthleteBadge(\"Alex\", 1))\n}",
    "why": "Replaces verbose `if (condition) return true else return false` with direct boolean range check `bmi in 18.5..24.9`. All three functions use expression bodies.",
    "expectedOutput": "BMI: 22.724403484408533 (Healthy: true)\n#1: Alex"
  },
  "0015-ex1": {
    "title": "Exercise 1 · Clean String & Number Extensions (Write from blank)",
    "code": "val Double.asKm: String\n    get() = \"$this km\"\n\nfun String?.blankToNull(): String? =\n    if (this.isNullOrBlank()) null else this\n\nfun main() {\n    println(42.195.asKm)\n\n    val s1: String? = \"   \"\n    val s2: String? = \"Intervals\"\n    val s3: String? = null\n\n    println(s1.blankToNull())\n    println(s2.blankToNull())\n    println(s3.blankToNull())\n}",
    "why": "Extension properties use `get() = ...` without backing fields. Extensions on nullable receivers (`String?`) safely handle null receivers internally with `this.isNullOrBlank()`.",
    "expectedOutput": "42.195 km\nnull\nIntervals\nnull"
  },
  "0015-ex2": {
    "title": "Exercise 2 · Retire the Java WorkoutUtils Class (Refactor)",
    "code": "class Workout(val title: String, val minutes: Int, val heartRate: Int?)\n\nfun Workout.isIntense(): Boolean =\n    (heartRate ?: 0) > 160 || minutes > 60\n\nfun Workout.formattedSummary(): String {\n    val hr = if (heartRate != null) \"$heartRate bpm\" else \"No HR\"\n    return \"$title: $minutes min ($hr)\"\n}\n\nfun main() {\n    val w1 = Workout(\"Track Intervals\", 45, 172)\n    val w2 = Workout(\"Walk\", 30, null)\n\n    println(\"${w1.formattedSummary()} - Intense: ${w1.isIntense()}\")\n    println(\"${w2.formattedSummary()} - Intense: ${w2.isIntense()}\")\n}",
    "why": "Eliminates dummy static utility object `WorkoutUtils`. Member access inside extensions directly references receiver properties (`heartRate`, `minutes`).",
    "expectedOutput": "Track Intervals: 45 min (172 bpm) - Intense: true\nWalk: 30 min (No HR) - Intense: false"
  },
  "0016-ex1": {
    "title": "Exercise 1 · Infix Sets & Point Addition (Write from blank)",
    "code": "infix fun Int.setsOf(reps: Int): String = \"$this sets of $reps reps\"\n\nclass Point(val x: Int, val y: Int) {\n    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)\n}\n\nfun main() {\n    println(3 setsOf 10)\n\n    val p1 = Point(2, 4)\n    val p2 = Point(3, 5)\n    val sum = p1 + p2\n    println(\"Point: (${sum.x}, ${sum.y})\")\n}",
    "why": "`infix` allows dotless call `3 setsOf 10`. `operator fun plus` maps to the `+` symbol for coordinate addition.",
    "expectedOutput": "3 sets of 10 reps\nPoint: (5, 9)"
  },
  "0016-ex2": {
    "title": "Exercise 2 · Clean Up Domain Calculations (Refactor)",
    "code": "class Money(val amount: Double) {\n    operator fun plus(other: Money): Money = Money(amount + other.amount)\n    operator fun times(factor: Int): Money = Money(amount * factor)\n}\n\ninfix fun String.taggedWith(tag: String): String = \"[$tag] $this\"\n\nfun main() {\n    val m1 = Money(25.0)\n    val m2 = Money(15.0)\n    val total = (m1 + m2) * 2\n    println(\"Total: $${total.amount}\")\n\n    val tagged = \"Morning Run\" taggedWith \"Cardio\"\n    println(tagged)\n}",
    "why": "Replaces clunky chained `m1.add(m2).multiply(2)` with natural `(m1 + m2) * 2`. Infix function `taggedWith` reads like an English sentence.",
    "expectedOutput": "Total: $80.0\n[Cardio] Morning Run"
  },
  "0017-ex1": {
    "title": "Exercise 1 · Session Logger & Rate Calculator (Write from blank)",
    "code": "val Int.secondsToTime: String\n    get() {\n        val m = this / 60\n        val s = this % 60\n        val sPadded = if (s < 10) \"0$s\" else \"$s\"\n        return \"$m:$sPadded\"\n    }\n\ninfix fun String.assignedTo(coach: String?): String =\n    \"$this -> ${coach ?: \"Unassigned\"}\"\n\nfun calcSpeed(distanceMeters: Double, seconds: Int = 3600): Double =\n    distanceMeters / seconds\n\nfun main() {\n    println(125.secondsToTime)\n    println(300.secondsToTime)\n    println(\"Hill Repeats\" assignedTo \"Elena\")\n    println(\"Solo Run\" assignedTo null)\n    println(\"Speed: ${calcSpeed(5000.0, 1000)} m/s\")\n    println(\"Speed default: ${calcSpeed(5000.0)} m/s\")\n}",
    "why": "Combines an extension property with formatting, an infix function handling nullable coaches, and a single-expression function with a default parameter."
  },
  "0017-ex2": {
    "title": "Exercise 2 · The Java/AI Functions Special (Refactor)",
    "code": "class Distance(val km: Double) {\n    operator fun plus(other: Distance): Distance = Distance(km + other.km)\n}\n\nfun Distance.format(unit: String = \"kilometers\"): String = \"$km $unit\"\n\nfun calculatePace(km: Double, minutes: Double): Double = minutes / km\n\nfun main() {\n    val d1 = Distance(10.0)\n    val d2 = Distance(5.0)\n    val sum = d1 + d2\n    println(sum.format())\n    println(sum.format(\"km\"))\n    println(\"Pace: ${calculatePace(sum.km, 75.0)} min/km\")\n}",
    "why": "Retires `DistanceHelper` object. Uses `operator fun plus` for `d1 + d2`, default argument for `format()`, and single-expression `calculatePace`.",
    "expectedOutput": "15.0 kilometers\n15.0 km\nPace: 5.0 min/km"
  },
  "0018-ex1": {
    "title": "Exercise 1 · Workout Tracker Model (Write from blank)",
    "code": "class WorkoutSession(\n    val title: String,\n    val distanceKm: Double,\n    val durationMinutes: Int = 30\n) {\n    init {\n        require(distanceKm > 0.0) { \"Distance must be positive: $distanceKm\" }\n        require(durationMinutes > 0) { \"Duration must be positive: $durationMinutes\" }\n    }\n\n    val paceMinPerKm: Double\n        get() = durationMinutes / distanceKm\n\n    val isLongRun: Boolean\n        get() = distanceKm >= 15.0 || durationMinutes >= 90\n}\n\nfun main() {\n    val session1 = WorkoutSession(\"Morning 10k\", 10.0, 50)\n    println(\"${session1.title}: pace ${session1.paceMinPerKm} min/km, long run: ${session1.isLongRun}\")\n\n    val session2 = WorkoutSession(\"Half Marathon\", 21.1, 105)\n    println(\"${session2.title}: pace ${session2.paceMinPerKm} min/km, long run: ${session2.isLongRun}\")\n}",
    "why": "Properties declared with `val` directly in constructor header. Validation runs inside `init` using `require()`. Computed properties use custom getters `get()` without backing storage."
  },
  "0018-ex2": {
    "title": "Exercise 2 · Kill the Java Getter/Setter Boilerplate (Refactor)",
    "code": "class AthleteProfile(\n    val name: String,\n    weeklyKm: Double\n) {\n    var weeklyKm: Double = weeklyKm\n        set(value) {\n            require(value >= 0.0) { \"Distance cannot be negative\" }\n            field = value\n        }\n\n    val isHighVolume: Boolean\n        get() = weeklyKm >= 50.0\n}\n\nfun main() {\n    val athlete = AthleteProfile(\"Marcus\", 42.0)\n    println(\"${athlete.name}: ${athlete.weeklyKm} km, high volume: ${athlete.isHighVolume}\")\n    athlete.weeklyKm = 55.0\n    println(\"${athlete.name}: ${athlete.weeklyKm} km, high volume: ${athlete.isHighVolume}\")\n}",
    "why": "Replaces manual `getName()` and `getWeeklyKm()` methods with direct property access. Custom setter validates input and assigns to `field`. `isHighVolume` becomes a computed property.",
    "expectedOutput": "Marcus: 42.0 km, high volume: false\nMarcus: 55.0 km, high volume: true"
  },
  "0019-ex1": {
    "title": "Exercise 1 · Modular Repository & Secondary Constructor (Write from blank)",
    "code": "open class BaseLogger(val tag: String) {\n    open fun log(msg: String): String = \"[$tag] $msg\"\n}\n\ninternal class SessionLogger(tag: String, val verbose: Boolean) : BaseLogger(tag) {\n    constructor(tag: String) : this(tag, false)\n\n    override fun log(msg: String): String =\n        if (verbose) \"VERBOSE: \" + super.log(msg) else super.log(msg)\n}\n\nfun main() {\n    val logger1 = SessionLogger(\"GPS\", verbose = true)\n    println(logger1.log(\"Satellite acquired\"))\n\n    val logger2 = SessionLogger(\"HeartRate\")\n    println(logger2.log(\"Connected\"))\n}",
    "why": "Base class marked `open` along with method `log`. `SessionLogger` is scoped with `internal`. Secondary constructor delegates to primary with `this(tag, false)`."
  },
  "0019-ex2": {
    "title": "Exercise 2 · Eliminate Redundant Constructors (Refactor)",
    "code": "class NetworkConfig(\n    val baseUrl: String,\n    val timeoutMs: Int = 5000,\n    val retries: Int = 3,\n    val debugMode: Boolean = false\n)\n\nfun main() {\n    val c1 = NetworkConfig(\"https://api.run.com\")\n    println(\"${c1.baseUrl}: ${c1.timeoutMs}ms, retries: ${c1.retries}, debug: ${c1.debugMode}\")\n\n    val c2 = NetworkConfig(\"https://api.run.com\", timeoutMs = 10000)\n    println(\"${c2.baseUrl}: ${c2.timeoutMs}ms, retries: ${c2.retries}, debug: ${c2.debugMode}\")\n\n    val c3 = NetworkConfig(\"https://api.run.com\", 2000, 5, true)\n    println(\"${c3.baseUrl}: ${c3.timeoutMs}ms, retries: ${c3.retries}, debug: ${c3.debugMode}\")\n}",
    "why": "Collapses 4 secondary constructors into 1 primary constructor with default parameter values.",
    "expectedOutput": "https://api.run.com: 5000ms, retries: 3, debug: false\nhttps://api.run.com: 10000ms, retries: 3, debug: false\nhttps://api.run.com: 2000ms, retries: 5, debug: true"
  },
  "0020-ex1": {
    "title": "Exercise 1 · Immutable Workout Milestone (Write from blank)",
    "code": "data class Milestone(val name: String, val targetKm: Double, val achieved: Boolean = false)\n\nfun main() {\n    val m1 = Milestone(\"5K Qualifier\", 5.0)\n    val m2 = m1.copy(achieved = true)\n    val (name, km, done) = m2\n    println(\"Milestone: $name ($km km) - Done: $done\")\n    println(\"Are equal: ${m1 == m2}\")\n}",
    "why": "Data class auto-generates `copy()` and destructuring `componentN()` functions. Structural equality `m1 == m2` compares all constructor values."
  },
  "0020-ex2": {
    "title": "Exercise 2 · Fix the Identity Bug (Refactor)",
    "code": "data class UserSession(\n    val username: String,\n    val heartRate: Int = 0,\n    val deviceTag: String = \"Watch\"\n)\n\nfun main() {\n    val user1 = UserSession(\"Elena\", heartRate = 165, deviceTag = \"ChestStrap\")\n    val user2 = UserSession(\"Elena\", heartRate = 110, deviceTag = \"WristWatch\")\n\n    println(\"Are equal: ${user1 == user2}\")\n    println(user1)\n}",
    "why": "Properties moved from class body into primary constructor header so they participate in `equals()`, `hashCode()`, and `toString()`.",
    "expectedOutput": "Are equal: false\nUserSession(username=Elena, heartRate=165, deviceTag=ChestStrap)"
  },
  "0021-ex1": {
    "title": "Exercise 1 · Heart Rate Zone Enum (Write from blank)",
    "code": "enum class CardioZone(val minBpm: Int, val maxBpm: Int) {\n    REST(0, 99),\n    FAT_BURN(100, 139),\n    CARDIO(140, 169),\n    PEAK(170, 220);\n\n    fun contains(bpm: Int): Boolean = bpm in minBpm..maxBpm\n}\n\nfun classify(bpm: Int): CardioZone =\n    CardioZone.entries.firstOrNull { it.contains(bpm) } ?: CardioZone.REST\n\nfun main() {\n    println(classify(72))\n    println(classify(145))\n    println(classify(180))\n}",
    "why": "Uses `CardioZone.entries` for zero-allocation iteration. Semicolon separates enum constants from member functions."
  },
  "0021-ex2": {
    "title": "Exercise 2 · Replace Java Integer Constants with an Enum (Refactor)",
    "code": "enum class WorkoutType(val multiplier: Double) {\n    RUN(1.0),\n    CYCLE(0.7),\n    SWIM(1.2)\n}\n\nfun getMultiplier(type: WorkoutType): Double = type.multiplier\n\nfun main() {\n    for (t in WorkoutType.entries) {\n        println(\"Type $t multiplier: ${getMultiplier(t)}\")\n    }\n}",
    "why": "Replaces brittle integer constants with type-safe enum. `multiplier` is stored as an immutable constructor property directly on the enum entries.",
    "expectedOutput": "Type RUN multiplier: 1.0\nType CYCLE multiplier: 0.7\nType SWIM multiplier: 1.2"
  },
  "0022-ex1": {
    "title": "Exercise 1 · Android UI State Model (Write from blank)",
    "code": "sealed interface WorkoutUiState {\n    data object Loading : WorkoutUiState\n    data class Ready(val workoutTitle: String, val distanceKm: Double) : WorkoutUiState\n    data class Failure(val reason: String, val recoverable: Boolean = true) : WorkoutUiState\n}\n\nfun statusMessage(state: WorkoutUiState): String = when (state) {\n    WorkoutUiState.Loading -> \"Please wait: syncing with device...\"\n    is WorkoutUiState.Ready -> \"Workout ready: ${state.workoutTitle} (${state.distanceKm} km)\"\n    is WorkoutUiState.Failure -> \"Failed: ${state.reason} [Retry: ${state.recoverable}]\"\n}\n\nfun main() {\n    val s1: WorkoutUiState = WorkoutUiState.Loading\n    val s2: WorkoutUiState = WorkoutUiState.Ready(\"Hill Sprints\", 6.5)\n    val s3: WorkoutUiState = WorkoutUiState.Failure(\"GPS Signal Lost\", recoverable = false)\n\n    println(statusMessage(s1))\n    println(statusMessage(s2))\n    println(statusMessage(s3))\n}",
    "why": "`sealed interface` enforces compile-time exhaustiveness. In each branch, Kotlin automatically smart-casts `state` to access subtype-specific fields."
  },
  "0022-ex2": {
    "title": "Exercise 2 · Eliminate the Anemic Nullable State Antipattern (Refactor)",
    "code": "sealed interface SyncResult {\n    data class Success(val items: List<String>) : SyncResult\n    data class Error(val code: Int, val message: String) : SyncResult\n}\n\nfun handleResponse(response: SyncResult): String = when (response) {\n    is SyncResult.Success -> \"Received ${response.items.size} workouts\"\n    is SyncResult.Error -> \"Error ${response.code}: ${response.message}\"\n}\n\nfun main() {\n    val r1: SyncResult = SyncResult.Success(listOf(\"Run\", \"Swim\"))\n    val r2: SyncResult = SyncResult.Error(404, \"Server not found\")\n\n    println(handleResponse(r1))\n    println(handleResponse(r2))\n}",
    "why": "Replaces mutually exclusive nullable fields with distinct sealed subtypes. Eliminates impossible states and null checks.",
    "expectedOutput": "Received 2 workouts\nError 404: Server not found"
  },
  "0023-ex1": {
    "title": "Exercise 1 · Factory Companion & Data Object State (Write from blank)",
    "code": "sealed interface DownloadState {\n    data object Idle : DownloadState\n    data class InProgress(val percent: Int) : DownloadState\n}\n\nclass SessionToken private constructor(val raw: String) {\n    companion object {\n        const val PREFIX = \"RUN_\"\n        fun create(userId: String): SessionToken = SessionToken(\"$PREFIX$userId\")\n    }\n}\n\nfun main() {\n    val state: DownloadState = DownloadState.Idle\n    println(\"State: $state\")\n\n    val token = SessionToken.create(\"u123\")\n    println(\"Token: ${token.raw}\")\n}",
    "why": "`data object` outputs clean string representation. Private constructor forces callers to instantiate through the companion object factory."
  },
  "0023-ex2": {
    "title": "Exercise 2 · Kill the Java Static Pattern (Refactor)",
    "code": "object AppConfig {\n    val environment = \"Production\"\n    val timeoutMs = 4000\n}\n\nclass User(val id: String, val role: String) {\n    companion object {\n        fun makeAdmin(id: String): User = User(id, \"ADMIN\")\n    }\n}\n\nfun main() {\n    println(\"${AppConfig.environment}: ${AppConfig.timeoutMs}ms\")\n    val admin = User.makeAdmin(\"a01\")\n    println(\"User ${admin.id}: ${admin.role}\")\n}",
    "why": "Replaces manual double-checked locking singleton with language-level `object AppConfig`. Companion factory uses single-expression body.",
    "expectedOutput": "Production: 4000ms\nUser a01: ADMIN"
  },
  "0024-ex1": {
    "title": "Exercise 1 · Type-Safe Workout Identifiers (Write from blank)",
    "code": "@JvmInline\nvalue class UserId(val value: String)\n\n@JvmInline\nvalue class WorkoutId(val value: String)\n\nfun formatAssignment(user: UserId, workout: WorkoutId): String =\n    \"User ${user.value} -> Workout ${workout.value}\"\n\nfun main() {\n    val u = UserId(\"u_99\")\n    val w = WorkoutId(\"w_404\")\n    val out = formatAssignment(u, w)\n    println(out)\n    check(out == \"User u_99 -> Workout w_404\") { \"Unexpected output: $out\" }\n}",
    "why": "`@JvmInline value class` makes accidental argument swapping (`formatAssignment(w, u)`) a compile-time error while passing raw `String` references in direct non-null JVM signatures."
  },
  "0024-ex2": {
    "title": "Exercise 2 · Eliminate Primitive Obsession (Refactor)",
    "code": "@JvmInline\nvalue class Seconds(val value: Int) {\n    val minutes: Double get() = value / 60.0\n}\n\nfun calculatePace(duration: Seconds, distanceKm: Double): Double =\n    duration.minutes / distanceKm\n\nfun main() {\n    val pace = calculatePace(Seconds(1500), 5.0)\n    println(\"Pace: $pace min/km\")\n    check(pace == 5.0) { \"Expected 5.0, got $pace\" }\n}",
    "why": "Encapsulates unit conversion (`minutes`) on a zero-allocation `Seconds` value class with a custom getter (no backing field) so raw ints can never be confused with milliseconds or minutes.",
    "expectedOutput": "Pace: 5.0 min/km"
  },
  "0025-ex1": {
    "title": "Exercise 1 · Complete Feature Architecture (Write from blank)",
    "code": "@JvmInline\nvalue class SessionId(val raw: String)\n\nenum class ActivityType(val icon: String) {\n    RUN(\"🏃\"),\n    CYCLE(\"🚴\")\n}\n\ndata class SessionRecord(val id: SessionId, val type: ActivityType, val distanceKm: Double)\n\nsealed interface SyncState {\n    data object Pending : SyncState\n    data class Completed(val record: SessionRecord) : SyncState\n}\n\nfun describeSync(state: SyncState): String = when (state) {\n    SyncState.Pending -> \"Syncing...\"\n    is SyncState.Completed -> \"${state.record.type.icon} ${state.record.id.raw}: ${state.record.distanceKm} km\"\n}\n\nfun main() {\n    val id = SessionId(\"sess_01\")\n    val record = SessionRecord(id, ActivityType.RUN, 10.5)\n    val state: SyncState = SyncState.Completed(record)\n    println(describeSync(state))\n}",
    "why": "Synthesizes value classes, enums, data classes, and sealed interfaces into a bulletproof, type-safe architecture."
  },
  "0025-ex2": {
    "title": "Exercise 2 · The Java Architecture Special (Refactor)",
    "code": "data class AthleteProfile(val name: String, val maxHr: Int)\n\nfun main() {\n    val p1 = AthleteProfile(\"Alex\", 190)\n    val p2 = AthleteProfile(\"Alex\", 190)\n    println(\"Equal: ${p1 == p2}\")\n    println(p1)\n}",
    "why": "A single line `data class` automatically synthesizes getters, `equals()`, `hashCode()`, and `toString()`, replacing 30 lines of boilerplate.",
    "expectedOutput": "Equal: true\nAthleteProfile(name=Alex, maxHr=190)"
  },
  "0026-ex1": {
    "title": "Exercise 1 · Custom Workout Filter & Timer (Write from blank)",
    "code": "fun filterPaces(paces: List<Double>, predicate: (Double) -> Boolean): List<Double> {\n    val result = mutableListOf<Double>()\n    for (p in paces) {\n        if (predicate(p)) result.add(p)\n    }\n    return result\n}\n\nfun runBenchmark(tag: String, block: () -> Unit) {\n    println(\"Starting $tag...\")\n    block()\n    println(\"Finished $tag\")\n}\n\nfun main() {\n    val paces = listOf(3.8, 5.2, 4.1, 6.0, 3.4)\n    val fastPaces = filterPaces(paces) { it < 4.5 }\n    println(\"Fast paces: $fastPaces\")\n\n    runBenchmark(\"100m Dash\") {\n        println(\"Athlete sprinting across track...\")\n    }\n}",
    "why": "Higher-order functions declare function types `(Double) -> Boolean` and `() -> Unit`, called with trailing lambda syntax."
  },
  "0026-ex2": {
    "title": "Exercise 2 · Eliminate Java Callback Interfaces (Refactor)",
    "code": "class WorkoutSyncer {\n    fun sync(workoutId: String, onUpdate: (String) -> Unit) {\n        onUpdate(\"Connecting to $workoutId\")\n        onUpdate(\"Uploading metrics\")\n        onUpdate(\"Done\")\n    }\n}\n\nfun main() {\n    val syncer = WorkoutSyncer()\n    syncer.sync(\"w_12\") { status ->\n        println(\"Progress: $status\")\n    }\n}",
    "why": "Replaces Java SAM callback interface with a native Kotlin function type `(String) -> Unit` and trailing lambda.",
    "expectedOutput": "Progress: Connecting to w_12\nProgress: Uploading metrics\nProgress: Done"
  },
  "0027-ex1": {
    "title": "Exercise 1 · Performance Inlining & Qualified Returns (Write from blank)",
    "code": "inline fun processUntilNegative(numbers: List<Int>, action: (Int) -> Unit) {\n    numbers.forEach {\n        if (it == 0) return@forEach\n        if (it < 0) return\n        action(it)\n    }\n}\n\nfun main() {\n    val list = listOf(10, 0, 25, 0, 40, -1, 50, 60)\n    processUntilNegative(list) {\n        println(\"Processed: $it\")\n    }\n    println(\"Complete\")\n}",
    "why": "`inline` avoids function object allocation. `return@forEach` skips the current iteration, while non-local `return` exits the outer calling context immediately."
  },
  "0027-ex2": {
    "title": "Exercise 2 · Replace Lambdas with Member References (Refactor)",
    "code": "data class Athlete(val name: String, val age: Int)\n\nfun isAdult(athlete: Athlete): Boolean = athlete.age >= 18\n\nfun main() {\n    val athletes = listOf(\n        Athlete(\"Sam\", 17),\n        Athlete(\"Alex\", 24),\n        Athlete(\"Elena\", 31)\n    )\n\n    val adults = athletes.filter(::isAdult)\n    val names = adults.map(Athlete::name)\n    val formatted = names.map(String::uppercase)\n\n    println(\"Formatted: $formatted\")\n}",
    "why": "Replaces verbose forwarding lambdas with member references: `::isAdult` (top-level), `Athlete::name` (property), and `String::uppercase` (method).",
    "expectedOutput": "Formatted: [ALEX, ELENA]"
  },
  "0028-ex1": {
    "title": "Exercise 1 · Custom Workout Plan Builder DSL (Write from blank)",
    "code": "class PlanBuilder {\n    var title: String = \"\"\n    private val exercises = mutableListOf<String>()\n\n    fun exercise(name: String) {\n        exercises.add(name)\n    }\n\n    fun build(): String = \"$title:\\n\" + exercises.joinToString(\"\\n\") { \" - $it\" }\n}\n\nfun buildPlan(setup: PlanBuilder.() -> Unit): String {\n    val builder = PlanBuilder()\n    builder.setup()\n    return builder.build()\n}\n\nfun main() {\n    val plan = buildPlan {\n        title = \"Leg Day\"\n        exercise(\"Back Squats\")\n        exercise(\"Romanian Deadlifts\")\n        exercise(\"Calf Raises\")\n    }\n    println(plan)\n}",
    "why": "`PlanBuilder.() -> Unit` makes `builder` the implicit `this` inside the lambda, allowing direct method calls."
  },
  "0028-ex2": {
    "title": "Exercise 2 · Replace Java StringBuilder Boilerplate (Refactor)",
    "code": "fun formatWorkoutCard(athlete: String, km: Double, pace: String): String = buildString {\n    append(\"=== Workout Card ===\\n\")\n    append(\"Athlete: $athlete\\n\")\n    append(\"Distance: $km km\\n\")\n    append(\"Pace: $pace\")\n}\n\nfun main() {\n    println(formatWorkoutCard(\"Alex\", 10.0, \"4:30 min/km\"))\n}",
    "why": "`buildString` provides a `StringBuilder` receiver scope directly, eliminating manual allocation and `.toString()`.",
    "expectedOutput": "=== Workout Card ===\nAthlete: Alex\nDistance: 10.0 km\nPace: 4:30 min/km"
  },
  "0029-ex1": {
    "title": "Exercise 1 · Type-Safe Report Builder (Write from blank)",
    "code": "class ReportScope {\n    val lines = mutableListOf<String>()\n    fun header(title: String) { lines.add(\"=== $title ===\") }\n    fun item(text: String) { lines.add(\"* $text\") }\n}\n\ninline fun buildReport(block: ReportScope.() -> Unit): String {\n    val scope = ReportScope()\n    scope.block()\n    return scope.lines.joinToString(\"\\n\")\n}\n\nfun main() {\n    val report = buildReport {\n        header(\"Weekly Running Log\")\n        item(\"Mon: 10k Tempo\")\n        item(\"Wed: 8k Recovery\")\n        item(\"Sat: 21k Long Run\")\n    }\n    println(report)\n}",
    "why": "Combines `inline` for zero allocation overhead with `ReportScope.() -> Unit` for clean DSL syntax."
  },
  "0029-ex2": {
    "title": "Exercise 2 · The Functional Anti-Pattern Special (Refactor)",
    "code": "data class User(val name: String, val active: Boolean)\n\nfun isUserActive(u: User): Boolean = u.active\n\nfun getActiveNames(users: List<User>): List<String> =\n    users.filter(::isUserActive).map { it.name.uppercase() }\n\nfun main() {\n    val users = listOf(\n        User(\"Sam\", true),\n        User(\"Alex\", false),\n        User(\"Elena\", true)\n    )\n    println(getActiveNames(users))\n}",
    "why": "Replaces imperative loop with a declarative, composable functional pipeline using `filter` and `map`.",
    "expectedOutput": "[SAM, ELENA]"
  },
  "0030-ex1": {
    "title": "Exercise 1 · User Registration Audit Pipeline",
    "code": "fun processUserRegistration(rawEmail: String?): String? =\n    rawEmail?.trim()?.takeIf { it.isNotBlank() }?.let { email ->\n        email.lowercase()\n    }?.also { sanitized ->\n        println(\"Audit: Registering user $sanitized\")\n    }\n\nfun main() {\n    val res1 = processUserRegistration(\"  Alice@Acme.COM  \")\n    val res2 = processUserRegistration(\"   \")\n    val res3 = processUserRegistration(null)\n    println(\"Result 1: $res1\")\n    println(\"Result 2: $res2\")\n    println(\"Result 3: $res3\")\n}",
    "why": "Combines `takeIf` to discard blank input, `let` to transform to lowercase, and `also` for transparent logging without breaking the expression flow.",
    "expectedOutput": "Audit: Registering user alice@acme.com\nResult 1: alice@acme.com\nResult 2: null\nResult 3: null"
  },
  "0030-ex2": {
    "title": "Exercise 2 · Eliminating Imperative Temp Variables (Refactor)",
    "code": "data class UserProfile(val id: String, val nickname: String?)\n\nfun printWelcomeBadge(profile: UserProfile?) {\n    profile?.nickname?.let { \"[MEMBER: ${it.uppercase()}]\" }\n        ?.also { println(\"Sending analytics: rendered badge\") }\n        ?.let { println(it) }\n}\n\nfun main() {\n    printWelcomeBadge(UserProfile(\"u101\", \"dev_master\"))\n    printWelcomeBadge(UserProfile(\"u102\", null))\n    printWelcomeBadge(null)\n}",
    "why": "Replaces 2 levels of nested null-checking if statements with safe-call chain, `let` mapping, and `also` side-effect.",
    "expectedOutput": "Sending analytics: rendered badge\n[MEMBER: DEV_MASTER]"
  },
  "0031-ex1": {
    "title": "Exercise 1 · HTTP Request Builder & Execution",
    "code": "class HttpRequest {\n    var url: String = \"\"\n    var method: String = \"GET\"\n    var timeoutMs: Int = 1000\n    val headers = mutableMapOf<String, String>()\n}\n\nfun buildJsonPost(url: String): HttpRequest = HttpRequest().apply {\n    this.url = url\n    method = \"POST\"\n    timeoutMs = 5000\n    headers[\"Content-Type\"] = \"application/json\"\n}\n\nfun HttpRequest.toCurlCommand(): String = run {\n    val headerStr = headers.entries.joinToString(\" \") { \"-H '${it.key}: ${it.value}'\" }\n    \"curl -X $method '$url' $headerStr\".trim()\n}\n\nfun main() {\n    val req = buildJsonPost(\"https://api.example.com/orders\")\n    println(\"Configured URL: ${req.url}\")\n    println(\"Configured Method: ${req.method}\")\n    println(req.toCurlCommand())\n}",
    "why": "`apply` is idiomatic for object initialization returning the configured receiver; `run` transforms receiver into a computed string."
  },
  "0031-ex2": {
    "title": "Exercise 2 · Java-Style Android View Configuration (Refactor)",
    "code": "class TextView {\n    var text: String = \"\"\n    var textColorHex: String = \"#000000\"\n    var textSizeSp: Float = 14f\n    var isVisible: Boolean = false\n    fun setPadding(l: Int, t: Int, r: Int, b: Int) {}\n}\n\nfun setupHeaderView(): TextView = TextView().apply {\n    text = \"Dashboard Overview\"\n    textColorHex = \"#1E88E5\"\n    textSizeSp = 22f\n    isVisible = true\n    setPadding(16, 24, 16, 24)\n}\n\nfun main() {\n    val tv = setupHeaderView()\n    println(\"Configured text: ${tv.text}, visible: ${tv.isVisible}\")\n}",
    "why": "Eliminates redundant local variable declarations and repetitive `tv.` prefixes by configuring directly inside `apply`.",
    "expectedOutput": "Configured text: Dashboard Overview, visible: true"
  },
  "0032-ex1": {
    "title": "Exercise 1 · Sanitized Port Validator Pipeline",
    "code": "fun resolvePort(configuredPort: Int?, defaultPort: Int = 8080): Int =\n    (configuredPort?.takeIf { it in 1..65535 } ?: defaultPort).also { port ->\n        println(\"Resolved active port: $port\")\n    }\n\nfun main() {\n    println(resolvePort(443))\n    println(resolvePort(-1))\n    println(resolvePort(null))\n    println(resolvePort(70000))\n}",
    "why": "`takeIf` filters the nullable port within valid numeric range; Elvis (`?:`) supplies the fallback, and `also` logs the final resolution."
  },
  "0032-ex2": {
    "title": "Exercise 2 · Flatten the 'Arrow of Scope' Anti-Pattern (Refactor)",
    "code": "data class Company(val name: String, val ceo: Person?)\ndata class Person(val name: String, val email: String?)\n\nfun sendCeoEmail(company: Company?, message: String) {\n    val email = company?.ceo?.email?.takeIf { \"@\" in it } ?: return\n    println(\"Sending to $email: $message\")\n}\n\nfun main() {\n    val comp = Company(\"Acme Corp\", Person(\"Alice Smith\", \"alice@acme.com\"))\n    sendCeoEmail(comp, \"Welcome aboard!\")\n    sendCeoEmail(null, \"Hello\")\n}",
    "why": "Flattens 3 levels of nested `?.let` into a single safe-navigation chain with an early-return guard clause.",
    "expectedOutput": "Sending to alice@acme.com: Welcome aboard!"
  },
  "0033-ex1": {
    "title": "Exercise 1 · Transactional Order Processor",
    "code": "data class Order(val id: String, val amountCents: Int, var status: String = \"PENDING\")\n\nfun processOrder(order: Order?): Order? =\n    order?.takeIf { it.amountCents > 0 }\n        ?.apply { status = \"PROCESSED\" }\n        ?.also { println(\"AUDIT: Order ${it.id} processed for ${it.amountCents}c\") }\n\nfun main() {\n    val o1 = processOrder(Order(\"ord-1\", 4500))\n    val o2 = processOrder(Order(\"ord-2\", -500))\n    val o3 = processOrder(null)\n    println(\"O1: $o1\")\n    println(\"O2: $o2\")\n    println(\"O3: $o3\")\n}",
    "why": "Clean composition of `takeIf` (validation), `apply` (state mutation), and `also` (audit logging side-effect)."
  },
  "0033-ex2": {
    "title": "Exercise 2 · The Scope Overuse Special (Refactor)",
    "code": "data class ServerConfig(var host: String = \"localhost\", var port: Int = 8080)\n\nfun startServer(rawHost: String?, rawPort: Int?): ServerConfig {\n    val host = rawHost?.trim()?.takeIf { it.isNotBlank() } ?: \"localhost\"\n    val port = rawPort ?: 8080\n    return ServerConfig(host, port).also {\n        println(\"Server ready on ${it.host}:${it.port}\")\n    }\n}\n\nfun main() {\n    val c = startServer(\"  api.internal  \", null)\n    println(\"Final config: ${c.host}:${c.port}\")\n}",
    "why": "Replaces 6 nested scope functions with clear default parameters, standard Elvis operators, and a single `also` for startup logging.",
    "expectedOutput": "Server ready on api.internal:8080\nFinal config: api.internal:8080"
  },
  "0034-ex1": {
    "title": "Exercise 1 · Two Sum with Primitive Array",
    "code": "fun twoSum(nums: IntArray, target: Int): IntArray {\n    val seen = HashMap<Int, Int>() // value -> index\n    for (i in nums.indices) {\n        val complement = target - nums[i]\n        val matchIndex = seen[complement]\n        if (matchIndex != null) {\n            return intArrayOf(matchIndex, i)\n        }\n        seen[nums[i]] = i\n    }\n    return intArrayOf()\n}\n\nfun main() {\n    val nums = intArrayOf(2, 7, 11, 15)\n    val result = twoSum(nums, 9)\n    println(\"Indices: ${result.joinToString()}\")\n}",
    "why": "Operates directly on unboxed `IntArray` for O(N) time and optimal cache locality without boxing overhead."
  },
  "0034-ex2": {
    "title": "Exercise 2 · Java-Style Collection Construction (Refactor)",
    "code": "fun generateFilterTags(includeAdmin: Boolean, region: String?): List<String> = buildList {\n    add(\"USER\")\n    add(\"ACTIVE\")\n    if (includeAdmin) add(\"ADMIN\")\n    region?.let { add(\"REGION_${it.uppercase()}\") }\n}\n\nfun main() {\n    println(generateFilterTags(true, \"eu-west\"))\n}",
    "why": "`buildList` allows imperative conditional additions during initialization while exposing a read-only `List<String>` with zero defensive copies.",
    "expectedOutput": "[USER, ACTIVE, ADMIN, REGION_EU-WEST]"
  },
  "0035-ex1": {
    "title": "Exercise 1 · Product Catalog Lookup Indexer",
    "code": "data class Product(val sku: String, val price: Double, val active: Boolean)\n\nfun createCatalogIndex(products: List<Product>): Map<String, Double> =\n    products\n        .filter { it.active && it.price > 0.0 }\n        .associate { it.sku.uppercase() to it.price }\n\nfun main() {\n    val items = listOf(\n        Product(\"p-100\", 19.99, true),\n        Product(\"p-200\", 0.0, true),\n        Product(\"p-300\", 49.50, false),\n        Product(\"p-400\", 12.00, true)\n    )\n    val index = createCatalogIndex(items)\n    println(index)\n}",
    "why": "Combines `filter` with `associate` (or `associateBy`) for concise, declarative index generation."
  },
  "0035-ex2": {
    "title": "Exercise 2 · Eliminating Imperative Extraction Loops (Refactor)",
    "code": "data class RawUser(val id: Long, val name: String, val rawRoles: String?)\ndata class UserDto(val id: Long, val name: String, val roles: List<String>)\n\nfun transformUsers(rawList: List<RawUser>): List<UserDto> = rawList.map { raw ->\n    val roles = raw.rawRoles\n        ?.split(\",\")\n        ?.map { it.trim().uppercase() }\n        ?.filter { it.isNotEmpty() }\n        ?: emptyList()\n    UserDto(raw.id, raw.name, roles)\n}\n\nfun main() {\n    val raw = listOf(\n        RawUser(1, \"Alice\", \"admin, editor\"),\n        RawUser(2, \"Bob\", null),\n        RawUser(3, \"Carol\", \"viewer\")\n    )\n    println(transformUsers(raw))\n}",
    "why": "Replaces manual nested loops and mutable `ArrayList`s with a declarative `map` transformation.",
    "expectedOutput": "[UserDto(id=1, name=Alice, roles=[ADMIN, EDITOR]), UserDto(id=2, name=Bob, roles=[]), UserDto(id=3, name=Carol, roles=[VIEWER])]"
  },
  "0036-ex1": {
    "title": "Exercise 1 · Order Ledger Category Revenue Report",
    "code": "data class Order(val category: String, val amountCents: Long, val isCompleted: Boolean)\n\nfun calculateCategoryRevenue(orders: List<Order>): Map<String, Long> =\n    orders.filter { it.isCompleted }\n        .groupBy({ it.category }, { it.amountCents })\n        .mapValues { (_, centsList) -> centsList.sum() }\n\nfun main() {\n    val orders = listOf(\n        Order(\"Electronics\", 29900, true),\n        Order(\"Groceries\", 4500, true),\n        Order(\"Electronics\", 15000, false),\n        Order(\"Groceries\", 2500, true),\n        Order(\"Books\", 1200, true)\n    )\n    println(calculateCategoryRevenue(orders))\n}",
    "why": "Uses `groupBy` with value transform followed by `mapValues` to compute grouped aggregate revenue cleanly."
  },
  "0036-ex2": {
    "title": "Exercise 2 · Two-Pass Filter Split Anti-Pattern (Refactor)",
    "code": "data class Applicant(val name: String, val score: Int)\n\nfun splitApplicants(applicants: List<Applicant>): Pair<List<Applicant>, List<Applicant>> =\n    applicants.partition { it.score >= 70 }\n\nfun main() {\n    val list = listOf(\n        Applicant(\"Alice\", 85),\n        Applicant(\"Bob\", 62),\n        Applicant(\"Charlie\", 91)\n    )\n    val (pass, fail) = splitApplicants(list)\n    println(\"Passed: ${pass.map { it.name }}\")\n    println(\"Failed: ${fail.map { it.name }}\")\n}",
    "why": "`partition` splits the collection in a single iteration pass into matching and non-matching lists, halving traversal time.",
    "expectedOutput": "Passed: [Alice, Charlie]\nFailed: [Bob]"
  },
  "0037-ex1": {
    "title": "Exercise 1 · Max Average Subarray of Size K",
    "code": "fun findMaxAverage(nums: IntArray, k: Int): Double {\n    var currentSum = 0L\n    for (i in 0 until k) {\n        currentSum += nums[i]\n    }\n    var maxSum = currentSum\n    for (i in k until nums.size) {\n        currentSum += nums[i] - nums[i - k]\n        if (currentSum > maxSum) maxSum = currentSum\n    }\n    return maxSum.toDouble() / k\n}\n\nfun main() {\n    val arr = intArrayOf(1, 12, -5, -6, 50, 3)\n    val maxAvg = findMaxAverage(arr, 4)\n    println(\"Max Average: $maxAvg\")\n}",
    "why": "Optimal O(N) sliding window with O(1) space, avoiding re-summing sub-windows on every shift."
  },
  "0037-ex2": {
    "title": "Exercise 2 · Messy Custom Sorting Comparator (Refactor)",
    "code": "data class Player(val username: String, val level: Int, val ping: Int)\n\nfun sortLeaderboard(players: List<Player>): List<Player> = players.sortedWith(\n    compareByDescending<Player> { it.level }\n        .thenBy { it.ping }\n        .thenBy { it.username }\n)\n\nfun main() {\n    val players = listOf(\n        Player(\"Shadow\", 50, 42),\n        Player(\"Blaze\", 50, 18),\n        Player(\"Ace\", 30, 20)\n    )\n    println(sortLeaderboard(players))\n}",
    "why": "`compareByDescending` chained with `thenBy` replaces 15 lines of verbose Java comparator boilerplate with a readable 4-line expression.",
    "expectedOutput": "[Player(username=Blaze, level=50, ping=18), Player(username=Shadow, level=50, ping=42), Player(username=Ace, level=30, ping=20)]"
  },
  "0038-ex1": {
    "title": "Exercise 1 · Valid Parentheses with ArrayDeque",
    "code": "fun isValidParentheses(s: String): Boolean {\n    val stack = ArrayDeque<Char>()\n    for (ch in s) {\n        when (ch) {\n            '(', '{', '[' -> stack.addLast(ch)\n            ')' -> if (stack.removeLastOrNull() != '(') return false\n            '}' -> if (stack.removeLastOrNull() != '{') return false\n            ']' -> if (stack.removeLastOrNull() != '[') return false\n        }\n    }\n    return stack.isEmpty()\n}\n\nfun main() {\n    println(isValidParentheses(\"()[]{}\"))\n    println(isValidParentheses(\"(]\"))\n    println(isValidParentheses(\"([{}])\"))\n    println(isValidParentheses(\"]\"))\n}",
    "why": "`ArrayDeque.addLast` and `removeLastOrNull` form Kotlin's idiomatic unsynchronized, high-performance LIFO stack."
  },
  "0038-ex2": {
    "title": "Exercise 2 · Top-K Elements Optimization (Refactor)",
    "code": "import java.util.PriorityQueue\n\nfun findKthLargest(nums: IntArray, k: Int): Int {\n    val minHeap = PriorityQueue<Int>()\n    for (num in nums) {\n        minHeap.add(num)\n        if (minHeap.size > k) {\n            minHeap.poll()\n        }\n    }\n    return minHeap.peek()\n}\n\nfun main() {\n    val arr = intArrayOf(3, 2, 1, 5, 6, 4)\n    println(\"3rd largest: ${findKthLargest(arr, 3)}\")\n}",
    "why": "Min-Heap keeps at most K elements at all times, reducing time complexity from O(N log N) to O(N log K) and auxiliary memory to O(K).",
    "expectedOutput": "3rd largest: 4"
  },
  "0039-ex1": {
    "title": "Exercise 1 · Infinite Paginated API Stream",
    "code": "fun fetchItemsSequence(): Sequence<String> =\n    generateSequence(seed = 1) { it + 1 }\n        .flatMap { page -> listOf(\"Item-${page}A\", \"Item-${page}B\").asSequence() }\n\nfun main() {\n    val items = fetchItemsSequence().take(5).toList()\n    println(items)\n}",
    "why": "`generateSequence` creates an infinite on-demand pagination generator that lazily computes items only as demanded by `take(5)`."
  },
  "0039-ex2": {
    "title": "Exercise 2 · Heavy Eager Pipeline Optimization (Refactor)",
    "code": "data class LogEntry(val id: Int, val level: String, val message: String)\n\nfun findFirstErrorAlerts(logs: List<LogEntry>, limit: Int): List<String> =\n    logs.asSequence()\n        .filter { it.level == \"ERROR\" }\n        .map { \"[ALERT ${it.id}] ${it.message.uppercase()}\" }\n        .take(limit)\n        .toList()\n\nfun main() {\n    val sampleLogs = (1..100_000).map { id ->\n        LogEntry(id, if (id % 10 == 0) \"ERROR\" else \"INFO\", \"Message payload $id\")\n    }\n    val alerts = findFirstErrorAlerts(sampleLogs, 3)\n    println(alerts)\n}",
    "why": "`asSequence()` processes items vertically, terminating after exactly 3 matching errors and avoiding allocating large intermediate lists.",
    "expectedOutput": "[[ALERT 10] MESSAGE PAYLOAD 10, [ALERT 20] MESSAGE PAYLOAD 20, [ALERT 30] MESSAGE PAYLOAD 30]"
  },
  "0040-ex1": {
    "title": "Exercise 1 · Top-K Frequent Elements",
    "code": "import java.util.PriorityQueue\n\nfun topKFrequent(nums: IntArray, k: Int): IntArray {\n    val counts = HashMap<Int, Int>()\n    for (n in nums) {\n        counts[n] = (counts[n] ?: 0) + 1\n    }\n\n    // Min-Heap ordered by frequency count\n    val minHeap = PriorityQueue<Int>(compareBy { counts[it] })\n    for (num in counts.keys) {\n        minHeap.add(num)\n        if (minHeap.size > k) {\n            minHeap.poll()\n        }\n    }\n\n    val result = IntArray(k)\n    var idx = 0\n    while (minHeap.isNotEmpty()) {\n        result[idx++] = minHeap.poll()\n    }\n    return result\n}\n\nfun main() {\n    val arr = intArrayOf(1, 1, 1, 2, 2, 3, 3, 3, 3, 4)\n    val top2 = topKFrequent(arr, 2)\n    println(\"Top 2: ${top2.joinToString()}\")\n}",
    "why": "Combines frequency counting with a Min-Heap of size K, returning unboxed `IntArray` in O(N log K) time."
  },
  "0040-ex2": {
    "title": "Exercise 2 · The Algorithmic Anti-Pattern Special (Refactor)",
    "code": "fun analyzeSensorData(readings: List<Double>): Map<String, Double> {\n    val valid = readings.filter { it >= 0.0 }\n    if (valid.isEmpty()) {\n        return mapOf(\"average\" to 0.0, \"max\" to 0.0, \"min\" to 0.0)\n    }\n    return mapOf(\n        \"average\" to valid.average(),\n        \"max\" to (valid.maxOrNull() ?: 0.0),\n        \"min\" to (valid.minOrNull() ?: 0.0)\n    )\n}\n\nfun main() {\n    val data = listOf(12.5, -1.0, 24.0, 18.2, -99.0)\n    println(analyzeSensorData(data))\n}",
    "why": "Replaces 20 lines of manual loop variables with idiomatic stdlib functions: `filter`, `average`, `maxOrNull`, and `minOrNull`.",
    "expectedOutput": "{average=18.233333333333334, max=24.0, min=12.5}"
  },
  "0041-ex1": {
    "title": "Exercise 1 · Thread-Safe Heavy Report Engine",
    "code": "class ReportGenerator {\n    val heavyTemplate: String by lazy {\n        println(\"Compiling complex template...\")\n        \"=== ANNUAL REPORT TEMPLATE ===\"\n    }\n    \n    lateinit var author: String\n\n    fun generate(data: String): String {\n        if (!::author.isInitialized) {\n            throw IllegalStateException(\"Author required\")\n        }\n        return \"$heavyTemplate\\nAuthor: $author\\nData: $data\"\n    }\n}\n\nfun main() {\n    val gen = ReportGenerator()\n    gen.author = \"Dr. Jane Hopper\"\n    println(\"ReportGenerator created.\")\n    println(gen.generate(\"Q4 Revenue: $1.2M\"))\n    println(gen.generate(\"Q1 Projection: $1.4M\"))\n}",
    "why": "Combines thread-safe deferred initialization (`by lazy`) with dependency injection readiness (`lateinit var`) and safe reflection check (`::author.isInitialized`)."
  },
  "0041-ex2": {
    "title": "Exercise 2 · Java-Style Lazy Double-Checked Locking (Refactor)",
    "code": "class RemoteServiceConfig {\n    val apiKey: String by lazy {\n        println(\"Fetching remote API key from secure vault...\")\n        \"SEC_LIVE_99887766\"\n    }\n}\n\nfun main() {\n    val config = RemoteServiceConfig()\n    println(config.apiKey)\n    println(config.apiKey)\n}",
    "why": "Replaces 18 lines of volatile synchronization, double checks, and force-unwraps with a single idiomatic `by lazy` property.",
    "expectedOutput": "Fetching remote API key from secure vault...\nSEC_LIVE_99887766\nSEC_LIVE_99887766"
  },
  "0042-ex1": {
    "title": "Exercise 1 · Audited Configuration with Observable Delegate",
    "code": "import kotlin.properties.Delegates\n\nclass AppEnvironment {\n    var activeProfile: String by Delegates.observable(\"development\") { _, old, new ->\n        println(\"Profile update: switched from $old to $new\")\n    }\n\n    var maxConnections: Int by Delegates.vetoable(10) { _, _, new ->\n        new in 1..100\n    }\n}\n\nfun main() {\n    val env = AppEnvironment()\n    env.activeProfile = \"staging\"\n    env.activeProfile = \"production\"\n\n    env.maxConnections = 50\n    println(\"Connections: ${env.maxConnections}\")\n    env.maxConnections = 500\n    println(\"Connections: ${env.maxConnections}\")\n}",
    "why": "`Delegates.observable` intercepts assignments for auditing, while `Delegates.vetoable` conditionally prevents invalid assignments.",
    "expectedOutput": "Profile update: switched from development to staging\nProfile update: switched from staging to production\nConnections: 50\nConnections: 50"
  },
  "0042-ex2": {
    "title": "Exercise 2 · Manual Setter Notification Boilerplate (Refactor)",
    "code": "import kotlin.properties.Delegates\n\nclass DownloadTracker {\n    var progress: Int by Delegates.observable(0) { _, old, new ->\n        println(\"Download progress updated: $old% -> $new%\")\n    }\n}\n\nfun main() {\n    val tracker = DownloadTracker()\n    tracker.progress = 25\n    tracker.progress = 75\n    tracker.progress = 100\n}",
    "why": "Replaces manual backing field `_progress` and boilerplate getter/setter with `by Delegates.observable`.",
    "expectedOutput": "Download progress updated: 0% -> 25%\nDownload progress updated: 25% -> 75%\nDownload progress updated: 75% -> 100%"
  },
  "0043-ex1": {
    "title": "Exercise 1 · Auditing Analytics Client Decorator",
    "code": "interface AnalyticsService {\n    fun trackEvent(name: String)\n    fun identifyUser(userId: String)\n    fun flush()\n}\n\nclass ConsoleAnalytics : AnalyticsService {\n    override fun trackEvent(name: String) = println(\"Console: event $name\")\n    override fun identifyUser(userId: String) = println(\"Console: identify $userId\")\n    override fun flush() = println(\"Console: flushed\")\n}\n\nclass AuditingAnalytics(private val inner: AnalyticsService) : AnalyticsService by inner {\n    var eventCount = 0\n\n    override fun trackEvent(name: String) {\n        eventCount++\n        println(\"Audit: Event #$eventCount -> $name\")\n        inner.trackEvent(name)\n    }\n}\n\nfun main() {\n    val service: AnalyticsService = AuditingAnalytics(ConsoleAnalytics())\n    service.identifyUser(\"u-99\")\n    service.trackEvent(\"login\")\n    service.trackEvent(\"purchase\")\n    service.flush()\n}",
    "why": "Class delegation (`by inner`) forwards `identifyUser` and `flush` automatically while allowing targeted override of `trackEvent`.",
    "expectedOutput": "Console: identify u-99\nAudit: Event #1 -> login\nConsole: event login\nAudit: Event #2 -> purchase\nConsole: event purchase\nConsole: flushed"
  },
  "0043-ex2": {
    "title": "Exercise 2 · Java-Style Manual Forwarding Decorator (Refactor)",
    "code": "interface TaskQueue {\n    fun push(task: String)\n    fun pop(): String?\n    fun peek(): String?\n    val size: Int\n}\n\nclass SimpleQueue : TaskQueue {\n    private val items = ArrayDeque<String>()\n    override fun push(task: String) { items.addLast(task) }\n    override fun pop(): String? = items.removeFirstOrNull()\n    override fun peek(): String? = items.firstOrNull()\n    override val size: Int get() = items.size\n}\n\nclass MonitoredTaskQueue(private val inner: TaskQueue) : TaskQueue by inner {\n    override fun push(task: String) {\n        println(\"Task queued: $task\")\n        inner.push(task)\n    }\n}\n\nfun main() {\n    val q = MonitoredTaskQueue(SimpleQueue())\n    q.push(\"Send Email\")\n    println(\"Queue size: ${q.size}\")\n}",
    "why": "Eliminates all boilerplate forwarding methods (`pop`, `peek`, `size`) by declaring `TaskQueue by inner`.",
    "expectedOutput": "Task queued: Send Email\nQueue size: 1"
  },
  "0044-ex1": {
    "title": "Exercise 1 · Thread-Safe Metric Cache with Class Delegation",
    "code": "interface MetricStore {\n    fun record(key: String, value: Double)\n    fun read(key: String): Double?\n}\n\nclass InMemoryMetricStore : MetricStore {\n    private val data = HashMap<String, Double>()\n    override fun record(key: String, value: Double) { data[key] = value }\n    override fun read(key: String): Double? = data[key]\n}\n\nclass CachedMetricStore(private val store: MetricStore) : MetricStore by store {\n    val statusSummary: String by lazy { \"MetricStore active with cache\" }\n\n    override fun record(key: String, value: Double) {\n        println(\"LOG: Recorded $key = $value\")\n        store.record(key, value)\n    }\n}\n\nfun main() {\n    val store = CachedMetricStore(InMemoryMetricStore())\n    println(store.statusSummary)\n    store.record(\"cpu_usage\", 42.5)\n    println(\"CPU: ${store.read(\"cpu_usage\")}\")\n}",
    "why": "Seamless combination of interface delegation (`by store`), lazy computed property (`by lazy`), and method interception."
  },
  "0044-ex2": {
    "title": "Exercise 2 · Over-Inherited Base Class Anti-Pattern (Refactor)",
    "code": "interface Notifier {\n    fun sendNotification(msg: String)\n}\n\nclass StandardNotifier : Notifier {\n    override fun sendNotification(msg: String) = println(\"Notification: $msg\")\n}\n\nclass OrderService(private val notifier: Notifier = StandardNotifier()) : Notifier by notifier {\n    fun completeOrder(id: String) {\n        println(\"Order $id finished.\")\n        sendNotification(\"Your order $id is ready!\")\n    }\n}\n\nfun main() {\n    val s = OrderService()\n    s.completeOrder(\"ord-777\")\n}",
    "why": "Replaces brittle base class inheritance with interface delegation and dependency injection.",
    "expectedOutput": "Order ord-777 finished.\nNotification: Your order ord-777 is ready!"
  },
  "0045-ex1": {
    "title": "Exercise 1 · Generic Clamped Range Search",
    "code": "fun <T : Comparable<T>> clamp(value: T, min: T, max: T): T = when {\n    value < min -> min\n    value > max -> max\n    else -> value\n}\n\nfun main() {\n    println(clamp(15, 0, 10))\n    println(clamp(-5, 0, 100))\n    println(clamp(\"kotlin\", \"a\", \"m\"))\n}",
    "why": "Upper bound `T : Comparable<T>` enables comparison operators (`<`, `>`) across numbers, strings, and any custom comparable types."
  },
  "0045-ex2": {
    "title": "Exercise 2 · Unsafe Casting in Generic Pipeline (Refactor)",
    "code": "fun <T : Comparable<T>> compareAndFormat(itemA: T, itemB: T): String {\n    val largest = if (itemA > itemB) itemA else itemB\n    return \"Winner: $largest\"\n}\n\nfun main() {\n    println(compareAndFormat(42, 100))\n    println(compareAndFormat(\"zebra\", \"apple\"))\n}",
    "why": "Eliminates unchecked runtime casts to `Comparable<Any>` and enforces homogenous compile-time type safety.",
    "expectedOutput": "Winner: 100\nWinner: zebra"
  },
  "0046-ex1": {
    "title": "Exercise 1 · Event Producer and Consumer Architecture",
    "code": "open class UIEvent(val timestamp: Long)\nclass ClickEvent(timestamp: Long, val buttonId: String) : UIEvent(timestamp)\n\ninterface EventSource<out T : UIEvent> {\n    fun poll(): T?\n}\n\ninterface EventDispatcher<in T : UIEvent> {\n    fun dispatch(event: T)\n}\n\nfun main() {\n    val clickSource: EventSource<ClickEvent> = object : EventSource<ClickEvent> {\n        override fun poll(): ClickEvent = ClickEvent(System.currentTimeMillis(), \"btn_submit\")\n    }\n    val uiSource: EventSource<UIEvent> = clickSource\n    println(\"Polled event timestamp: ${uiSource.poll()?.timestamp}\")\n}",
    "why": "`out T` enables covariance (subtyping preserved), allowing `EventSource<ClickEvent>` to be safely used as `EventSource<UIEvent>`."
  },
  "0046-ex2": {
    "title": "Exercise 2 · Call-Site Variance Array Copy (Refactor)",
    "code": "fun <T> copyArray(from: Array<out T>, to: Array<in T>) {\n    for (i in from.indices) {\n        to[i] = from[i]\n    }\n}\n\nfun main() {\n    val strings: Array<String> = arrayOf(\"Kotlin\", \"Multiplatform\", \"Android\")\n    val objects: Array<Any?> = arrayOfNulls(3)\n    copyArray(strings, objects)\n    println(objects.joinToString())\n    check(objects.contentEquals(arrayOf<Any?>(\"Kotlin\", \"Multiplatform\", \"Android\")))\n}",
    "why": "`Array<out T>` restricts `from` to producing `T` values, and `Array<in T>` restricts `to` to consuming `T` values—allowing `Array<String>` to copy safely into `Array<Any?>`.",
    "expectedOutput": "Kotlin, Multiplatform, Android"
  },
  "0047-ex1": {
    "title": "Exercise 1 · Type-Safe Service Locator with Reified Generics",
    "code": "import kotlin.reflect.KClass\n\nclass ServiceRegistry {\n    val services = mutableMapOf<KClass<*>, Any>()\n\n    inline fun <reified T : Any> register(instance: T) {\n        services[T::class] = instance\n    }\n\n    inline fun <reified T : Any> resolve(): T {\n        val instance = services[T::class] ?: throw IllegalStateException(\"Service not found: ${T::class.simpleName}\")\n        return instance as T\n    }\n}\n\nclass UserRepository\nclass PaymentService\n\nfun main() {\n    val registry = ServiceRegistry()\n    registry.register(UserRepository())\n    registry.register(PaymentService())\n\n    val repo = registry.resolve<UserRepository>()\n    println(\"Resolved service: ${repo::class.simpleName}\")\n}",
    "why": "`reified T` allows accessing `T::class` at runtime without passing explicit `Class<T>` objects, preserving type safety."
  },
  "0047-ex2": {
    "title": "Exercise 2 · Java-Style Class Token Passing (Refactor)",
    "code": "class Container {\n    val items = mutableListOf<Any>()\n}\n\ninline fun <reified T> Container.findFirst(): T? {\n    for (item in items) {\n        if (item is T) {\n            return item\n        }\n    }\n    return null\n}\n\nfun main() {\n    val c = Container()\n    c.items.add(100)\n    c.items.add(\"Hello Kotlin\")\n    c.items.add(3.14)\n\n    val str = c.findFirst<String>()\n    println(\"Found string: $str\")\n}",
    "why": "`inline` with `reified T` allows idiomatic `item is T` type check and cast, eliminating Java's `Class<T>.isInstance` boilerplate.",
    "expectedOutput": "Found string: Hello Kotlin"
  },
  "0048-ex1": {
    "title": "Exercise 1 · Type-Safe In-Memory Message Bus",
    "code": "open class Message\nclass TextMessage(val text: String) : Message()\nclass AlertMessage(val level: Int) : Message()\n\nclass MessageBus {\n    val messages = mutableListOf<Message>()\n\n    fun publish(msg: Message) {\n        messages.add(msg)\n    }\n\n    inline fun <reified T : Message> filterMessages(): List<T> =\n        messages.filterIsInstance<T>()\n}\n\nfun main() {\n    val bus = MessageBus()\n    bus.publish(TextMessage(\"Hello\"))\n    bus.publish(AlertMessage(5))\n    bus.publish(TextMessage(\"World\"))\n\n    val texts = bus.filterMessages<TextMessage>()\n    println(\"Texts: ${texts.map { it.text }}\")\n}",
    "why": "Combines generic upper bound `T : Message` with `inline reified` and stdlib `filterIsInstance<T>()`."
  },
  "0048-ex2": {
    "title": "Exercise 2 · Eliminating Java Wildcard Casts (Refactor)",
    "code": "interface DataSupplier<out T> {\n    fun get(): T\n}\n\nfun printAllSuppliers(suppliers: List<DataSupplier<CharSequence>>) {\n    for (s in suppliers) {\n        println(\"Item: ${s.get()}\")\n    }\n}\n\nclass StringSupplier(val v: String) : DataSupplier<String> {\n    override fun get(): String = v\n}\n\nfun main() {\n    val list: List<DataSupplier<String>> = listOf(StringSupplier(\"Alpha\"), StringSupplier(\"Beta\"))\n    printAllSuppliers(list)\n}",
    "why": "Making `DataSupplier<out T>` declaration-site covariant eliminates repetitive call-site `<out CharSequence>` projections everywhere.",
    "expectedOutput": "Item: Alpha\nItem: Beta"
  },
  "0049-ex1": {
    "title": "Exercise 1 · Parallel User Profile Aggregator",
    "code": "import kotlinx.coroutines.*\n\ndata class UserSummary(val details: String, val preferences: String)\n\nsuspend fun fetchDetails(userId: String): String {\n    delay(50)\n    return \"Details for $userId\"\n}\n\nsuspend fun fetchPreferences(userId: String): String {\n    delay(50)\n    return \"Theme=Dark\"\n}\n\nsuspend fun loadUserProfile(userId: String): UserSummary = coroutineScope {\n    val defDetails = async { fetchDetails(userId) }\n    val defPrefs = async { fetchPreferences(userId) }\n    UserSummary(defDetails.await(), defPrefs.await())\n}\n\nfun main() = runBlocking {\n    val summary = loadUserProfile(\"user_42\")\n    println(summary)\n}",
    "why": "`coroutineScope` with concurrent `async` calls runs both suspending network requests in parallel, halving total latency."
  },
  "0049-ex2": {
    "title": "Exercise 2 · Blocking Thread Spawning Anti-Pattern (Refactor)",
    "code": "import kotlinx.coroutines.*\n\nsuspend fun runCoroutinesBatch() = coroutineScope {\n    (1..5).forEach { id ->\n        launch {\n            delay(50)\n            println(\"Coroutine task $id completed on ${Thread.currentThread().name}\")\n        }\n    }\n}\n\nfun main() = runBlocking {\n    runCoroutinesBatch()\n}",
    "why": "Replaces heavy OS `Thread` allocations and blocking `Thread.sleep` with lightweight coroutines and non-blocking `delay`.",
    "expectedOutput": "Coroutine task 1 completed on main\nCoroutine task 2 completed on main\nCoroutine task 3 completed on main\nCoroutine task 4 completed on main\nCoroutine task 5 completed on main"
  },
  "0050-ex1": {
    "title": "Exercise 1 · Clean Architecture Repository Layer",
    "code": "import kotlinx.coroutines.*\n\nclass UserRepository {\n    private fun readDiskCache(id: String): String {\n        Thread.sleep(20)\n        return \"UserCachedData_$id\"\n    }\n\n    suspend fun getUser(id: String): String = withContext(Dispatchers.IO) {\n        readDiskCache(id)\n    }\n}\n\nfun main() = runBlocking {\n    val repo = UserRepository()\n    val user = repo.getUser(\"101\")\n    println(\"Fetched user: $user\")\n}",
    "why": "`withContext(Dispatchers.IO)` offloads blocking disk operations to an elastic I/O thread pool without blocking the caller's thread.",
    "expectedOutput": "Fetched user: UserCachedData_101"
  },
  "0050-ex2": {
    "title": "Exercise 2 · Thread Pool Hardcoding Anti-Pattern (Refactor)",
    "code": "import kotlinx.coroutines.*\n\nsuspend fun calculateHash(data: String): String = withContext(Dispatchers.Default) {\n    data.reversed().uppercase()\n}\n\nfun main() = runBlocking {\n    val result = calculateHash(\"kotlin_coroutines\")\n    println(\"Calculated: $result\")\n}",
    "why": "Replaces custom `ExecutorService` and callback gymnastics with `suspend fun` and `withContext(Dispatchers.Default)`.",
    "expectedOutput": "Calculated: SENITUOROC_NILTOK"
  },
  "0051-ex1": {
    "title": "Exercise 1 · Resilient Batch Worker with supervisorScope",
    "code": "import kotlinx.coroutines.*\n\nsuspend fun executeBatchTasks(taskIds: List<String>): List<String> = supervisorScope {\n    val deferredList = taskIds.map { id ->\n        async {\n            if (id == \"fail\") throw RuntimeException(\"Task failed\")\n            \"SUCCESS-$id\"\n        }\n    }\n    deferredList.map { def ->\n        runCatching { def.await() }.getOrDefault(\"FAILED\")\n    }\n}\n\nfun main() = runBlocking {\n    val results = executeBatchTasks(listOf(\"A\", \"fail\", \"B\"))\n    println(results)\n}",
    "why": "`supervisorScope` isolates child failures so one failing task does not cancel its sibling coroutines."
  },
  "0051-ex2": {
    "title": "Exercise 2 · Uncancellable Tight Loop Anti-Pattern (Refactor)",
    "code": "import kotlinx.coroutines.*\n\nsuspend fun pollSensorsUncancellable(onFirstStep: () -> Unit) = withContext(Dispatchers.Default) {\n    var steps = 0\n    while (steps < 5) {\n        ensureActive()\n        steps++\n        println(\"Step $steps\")\n        if (steps == 1) onFirstStep()\n    }\n    println(\"Finished all $steps steps (ignored cancellation!)\")\n}\n\nfun main() = runBlocking {\n    lateinit var job: Job\n    job = launch {\n        pollSensorsUncancellable {\n            println(\"Cancelling job...\")\n            job.cancel()\n        }\n    }\n    job.join()\n    println(\"Job cancelled cleanly!\")\n}",
    "why": "`ensureActive()` makes CPU-bound loops cooperative, throwing `CancellationException` promptly when the parent Job is cancelled.",
    "expectedOutput": "Step 1\nCancelling job...\nJob cancelled cleanly!"
  },
  "0052-ex1": {
    "title": "Exercise 1 · Stock Ticker Flow Pipeline",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.flow.*\n\nfun trackStockPrices(symbol: String, initialPrice: Double): Flow<Double> = flow {\n    emit(initialPrice)\n    var price = initialPrice\n    repeat(3) {\n        delay(20)\n        price += 1.5\n        emit(price)\n    }\n}.flowOn(Dispatchers.Default)\n\nfun main() = runBlocking {\n    trackStockPrices(\"KT\", 98.0)\n        .filter { it > 100.0 }\n        .map { \"TICKER: $it\" }\n        .collect { println(it) }\n}",
    "why": "`flow { emit() }` creates a cold stream, and `.flowOn(Dispatchers.Default)` safely offloads emissions without violating flow invariants."
  },
  "0052-ex2": {
    "title": "Exercise 2 · Imperative Callback Stream to Flow (Refactor)",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.flow.*\n\nfun fetchHistoricalReadings(): Flow<Int> = flow {\n    for (i in 1..4) {\n        emit(i * 10)\n    }\n}\n\nfun main() = runBlocking {\n    fetchHistoricalReadings().collect { reading ->\n        println(\"Reading: $reading\")\n    }\n}",
    "why": "Replaces push callbacks with pull-based cold `Flow` that can be transformed with `map`, `filter`, and collected reactively.",
    "expectedOutput": "Reading: 10\nReading: 20\nReading: 30\nReading: 40"
  },
  "0053-ex1": {
    "title": "Exercise 1 · Atomic Download Queue Coordinator (Write from blank)",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.flow.*\n\ndata class QueueState(\n    val activeIds: Set<String> = emptySet(),\n    val completedCount: Int = 0\n)\n\nclass DownloadCoordinator {\n    private val _state = MutableStateFlow(QueueState())\n    val state: StateFlow<QueueState> = _state.asStateFlow()\n\n    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 4)\n    val events: SharedFlow<String> = _events.asSharedFlow()\n\n    fun startDownload(id: String) {\n        _state.update { current ->\n            current.copy(activeIds = current.activeIds + id)\n        }\n    }\n\n    fun completeDownload(id: String) {\n        var wasActive = false\n        _state.update { current ->\n            if (id in current.activeIds) {\n                wasActive = true\n                current.copy(\n                    activeIds = current.activeIds - id,\n                    completedCount = current.completedCount + 1\n                )\n            } else {\n                current\n            }\n        }\n        if (wasActive) {\n            _events.tryEmit(\"Done: $id\")\n        }\n    }\n}\n\nfun main() = runBlocking {\n    val coord = DownloadCoordinator()\n    val alerts = mutableListOf<String>()\n    val job = launch(start = CoroutineStart.UNDISPATCHED) {\n        coord.events.collect { alerts.add(it) }\n    }\n\n    coord.startDownload(\"video.mp4\")\n    coord.startDownload(\"slides.pdf\")\n    check(coord.state.value == QueueState(setOf(\"video.mp4\", \"slides.pdf\"), 0))\n\n    coord.completeDownload(\"video.mp4\")\n    yield()\n    check(coord.state.value == QueueState(setOf(\"slides.pdf\"), 1))\n    check(alerts == listOf(\"Done: video.mp4\"))\n\n    job.cancel()\n    println(\"All checks passed! Final state: ${coord.state.value}\")\n}",
    "why": "Uses backing properties (`asStateFlow()` / `asSharedFlow()`) to prevent external mutation, and `_state.update { ... }` to guarantee atomic CAS transitions under concurrent coroutines."
  },
  "0053-ex2": {
    "title": "Exercise 2 · Fix Leaked Mutable State & Non-Atomic Updates (Refactor)",
    "code": "import kotlinx.coroutines.flow.*\n\ndata class Session(val username: String = \"Guest\", val permissions: Set<String> = emptySet())\n\nclass AuthSession {\n    private val _session = MutableStateFlow(Session())\n    val session: StateFlow<Session> = _session.asStateFlow()\n\n    fun login(user: String) {\n        _session.value = Session(username = user)\n    }\n\n    fun addPermission(perm: String) {\n        _session.update { current ->\n            current.copy(permissions = current.permissions + perm)\n        }\n    }\n}\n\nfun main() {\n    val auth = AuthSession()\n    auth.login(\"Alice\")\n    auth.addPermission(\"ADMIN\")\n    check(auth.session.value == Session(\"Alice\", setOf(\"ADMIN\")))\n    println(\"Verified session: ${auth.session.value}\")\n}",
    "why": "Encapsulates `_session` privately behind `asStateFlow()` and replaces the racy `session.value = session.value.copy(...)` read-modify-write with an atomic `_session.update { ... }` CAS loop.",
    "expectedOutput": "Verified session: Session(username=Alice, permissions=[ADMIN])"
  },
  "0054-ex1": {
    "title": "Exercise 1 · Real-Time Live Search Debouncer",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.flow.*\n\nfun searchPipeline(queryFlow: Flow<String>): Flow<List<String>> =\n    queryFlow\n        .filter { it.isNotBlank() }\n        .map { query -> listOf(\"Result 1 for $query\", \"Result 2 for $query\") }\n        .flowOn(Dispatchers.IO)\n\nfun main() = runBlocking {\n    val queries = flowOf(\"kot\", \"\", \"kotlin\", \"coroutines\")\n    searchPipeline(queries).collect { results ->\n        println(\"Search results: $results\")\n    }\n}",
    "why": "Combines Flow operators (`filter`, `map`) with `flowOn(Dispatchers.IO)` for non-blocking stream processing."
  },
  "0054-ex2": {
    "title": "Exercise 2 · The Coroutine Anti-Pattern Special (Refactor)",
    "code": "import kotlinx.coroutines.*\n\nsuspend fun fetchSensorData(): String = withContext(Dispatchers.IO) {\n    delay(50)\n    \"Temperature: 24C\"\n}\n\nfun main() = runBlocking {\n    println(fetchSensorData())\n}",
    "why": "Eliminates blocking `Thread.sleep` and removes broad try-catch that swallows `CancellationException`.",
    "expectedOutput": "Temperature: 24C"
  },
  "0055-ex1": {
    "title": "Exercise 1 · Safe Config Port & Host Parser with Result (Write from blank)",
    "code": "fun parseEndpoint(raw: String): Result<Pair<String, Int>> = runCatching {\n    require(\":\" in raw) { \"Missing ':' separator in $raw\" }\n    val (host, portStr) = raw.split(\":\", limit = 2)\n    require(host.isNotBlank()) { \"Host cannot be blank\" }\n    val port = portStr.toInt()\n    require(port in 1..65535) { \"Port out of range: $port\" }\n    host to port\n}\n\nfun main() {\n    check(parseEndpoint(\"db.internal:5432\").getOrNull() == (\"db.internal\" to 5432))\n    check(parseEndpoint(\"localhost\").isFailure)\n    check(parseEndpoint(\":8080\").isFailure)\n    check(parseEndpoint(\"api:99999\").isFailure)\n    println(\"All endpoint parser checks passed!\")\n}",
    "why": "Combines `runCatching` at a parsing boundary with expressive `require` preconditions and destructuring (`val (host, portStr) = ...`)."
  },
  "0055-ex2": {
    "title": "Exercise 2 · Preconditions & Result Refactor",
    "code": "fun parseEmail(raw: String?): Result<String> = runCatching {\n    requireNotNull(raw) { \"Email cannot be null\" }\n    require(\"@\" in raw) { \"Missing @ symbol\" }\n    raw.trim().lowercase()\n}\n\nfun main() {\n    check(parseEmail(\"  User@Example.COM \").getOrNull() == \"user@example.com\")\n    check(parseEmail(null).isFailure)\n    check(parseEmail(\"invalid-email\").isFailure)\n    println(\"All email validation checks passed!\")\n}",
    "why": "`requireNotNull` validates non-nullability and smart-casts `raw` to `String` via compiler contract, eliminating custom exception boilerplate.",
    "expectedOutput": "All email validation checks passed!"
  },
  "0056-ex1": {
    "title": "Exercise 1 · Clean Builder Replacement (Write from blank)",
    "code": "data class HttpClientConfig(\n    val baseUrl: String,\n    val timeoutMs: Int = 5000,\n    val retryCount: Int = 3,\n    val followRedirects: Boolean = true\n) {\n    init {\n        require(baseUrl.startsWith(\"https://\")) { \"HTTPS required: $baseUrl\" }\n    }\n}\n\nfun main() {\n    val config = HttpClientConfig(\"https://api.example.com\", timeoutMs = 2000)\n    check(config.timeoutMs == 2000 && config.retryCount == 3 && config.followRedirects)\n    println(config)\n}",
    "why": "Default parameters + named arguments + `init` preconditions replace 40+ lines of mutable Java Builder ceremony while guaranteeing immutable, validated instances.",
    "expectedOutput": "HttpClientConfig(baseUrl=https://api.example.com, timeoutMs=2000, retryCount=3, followRedirects=true)"
  },
  "0056-ex2": {
    "title": "Exercise 2 · The Ultimate Java-in-Kotlin Smell Refactor",
    "code": "data class Employee(val name: String, val salary: Double?)\n\nfun findHighEarners(employees: List<Employee>): List<String> =\n    employees\n        .filter { (it.salary ?: 0.0) > 50000.0 }\n        .map { it.name.uppercase() }\n\nfun main() {\n    val list = listOf(Employee(\"Alice\", 75000.0), Employee(\"Bob\", 40000.0), Employee(\"Carol\", null))\n    println(findHighEarners(list))\n}",
    "why": "Replaces Java 8 streams, `Optional`, manual getters, and `Collectors.toList()` with Kotlin standard collection extensions and Elvis operator.",
    "expectedOutput": "[ALICE]"
  },
  "0057-ex1": {
    "title": "Exercise 1 · UDF Search & Bookmark ViewModel with One-Off Effects",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.channels.Channel\nimport kotlinx.coroutines.flow.*\n\ndata class ArticleFeedState(\n    val query: String = \"\",\n    val allArticles: List<String> = listOf(\"Kotlin K2\", \"Compose UDF\", \"Spring Coroutines\", \"KMP iOS\"),\n    val bookmarked: Set<String> = emptySet()\n) {\n    val visibleArticles: List<String>\n        get() = if (query.isBlank()) allArticles\n                else allArticles.filter { it.contains(query, ignoreCase = true) }\n}\n\nsealed interface ArticleEffect {\n    data class ShowSnackbar(val message: String) : ArticleEffect\n}\n\nclass ArticleViewModel {\n    private val _state = MutableStateFlow(ArticleFeedState())\n    val state: StateFlow<ArticleFeedState> = _state.asStateFlow()\n\n    private val _effects = Channel<ArticleEffect>(Channel.BUFFERED)\n    val effects: Flow<ArticleEffect> = _effects.receiveAsFlow()\n\n    fun onQueryChange(q: String) {\n        _state.update { it.copy(query = q) }\n    }\n\n    fun toggleBookmark(title: String) {\n        var added = false\n        _state.update { current ->\n            if (title in current.bookmarked) {\n                added = false\n                current.copy(bookmarked = current.bookmarked - title)\n            } else {\n                added = true\n                current.copy(bookmarked = current.bookmarked + title)\n            }\n        }\n        if (added) {\n            _effects.trySend(ArticleEffect.ShowSnackbar(\"Saved: $title\"))\n        }\n    }\n}\n\nfun main() = runBlocking {\n    val vm = ArticleViewModel()\n    vm.onQueryChange(\"compose\")\n    check(vm.state.value.visibleArticles == listOf(\"Compose UDF\"))\n\n    vm.toggleBookmark(\"Compose UDF\")\n    check(\"Compose UDF\" in vm.state.value.bookmarked)\n    val firstEffect = vm.effects.first()\n    check(firstEffect == ArticleEffect.ShowSnackbar(\"Saved: Compose UDF\"))\n\n    vm.toggleBookmark(\"Compose UDF\")\n    check(\"Compose UDF\" !in vm.state.value.bookmarked)\n    println(\"All Android UDF capstone checks passed!\")\n}",
    "why": "Models derived UI filtering on an immutable `data class`, performs atomic CAS mutations via `_state.update`, and delivers single-consumption snackbar triggers through a buffered `Channel`."
  },
  "0057-ex2": {
    "title": "Exercise 2 · Legacy Multi-Boolean State Refactor",
    "code": "sealed interface ProfileUiState {\n    data object Loading : ProfileUiState\n    data class Success(val username: String) : ProfileUiState\n    data class Error(val message: String) : ProfileUiState\n}\n\nfun renderUi(state: ProfileUiState): String = when (state) {\n    ProfileUiState.Loading -> \"Loading...\"\n    is ProfileUiState.Success -> \"Welcome ${state.username}\"\n    is ProfileUiState.Error -> \"Error: ${state.message}\"\n}\n\nfun main() {\n    check(renderUi(ProfileUiState.Loading) == \"Loading...\")\n    check(renderUi(ProfileUiState.Success(\"Sam\")) == \"Welcome Sam\")\n    println(renderUi(ProfileUiState.Success(\"Sam\")))\n}",
    "why": "Replaces 5 independent mutable flags with an exhaustive `sealed interface` where invalid state combinations cannot even compile.",
    "expectedOutput": "Welcome Sam"
  },
  "0058-ex1": {
    "title": "Exercise 1 · Spring DTOs, Preconditions & Service Mapping",
    "code": "data class CreateOrderRequest(\n    val customerId: String,\n    val amountCents: Long,\n    val notes: String? = null\n) {\n    init {\n        require(customerId.isNotBlank()) { \"Customer ID cannot be blank\" }\n        require(amountCents > 0) { \"Amount must be positive: $amountCents\" }\n    }\n}\n\ndata class OrderResponse(\n    val id: String,\n    val customerId: String,\n    val amountCents: Long,\n    val notes: String,\n    val status: String\n)\n\nfun CreateOrderRequest.toResponse(orderId: String): OrderResponse =\n    OrderResponse(\n        id = orderId,\n        customerId = customerId.trim(),\n        amountCents = amountCents,\n        notes = notes?.trim()?.takeIf { it.isNotEmpty() } ?: \"NONE\",\n        status = \"CONFIRMED\"\n    )\n\nfun createOrder(req: CreateOrderRequest): OrderResponse =\n    req.toResponse(\"ord-123\")\n\nfun main() {\n    val req = CreateOrderRequest(\"  cust_99 \", 4500)\n    val res = createOrder(req)\n    check(res == OrderResponse(\"ord-123\", \"cust_99\", 4500, \"NONE\", \"CONFIRMED\"))\n\n    val invalidFailed = runCatching { CreateOrderRequest(\"\", 100) }.isFailure\n    val negativeFailed = runCatching { CreateOrderRequest(\"cust_1\", -5) }.isFailure\n    check(invalidFailed && negativeFailed)\n    println(\"All Spring DTO & service checks passed: $res\")\n}",
    "why": "Enforces domain invariants inside the DTO `init` block, avoids anemic mapper classes via an extension function `toResponse`, and normalizes optional strings with `?.trim()?.takeIf { ... } ?: \"NONE\"`."
  },
  "0058-ex2": {
    "title": "Exercise 2 · Java-Style Spring Service Refactor",
    "code": "class UserEntity(var id: Long? = null, var email: String = \"\", var active: Boolean = true)\n\nfun updateEmail(entity: UserEntity, newEmail: String?): UserEntity {\n    require(!newEmail.isNullOrBlank()) { \"Email cannot be blank\" }\n    return entity.apply {\n        email = newEmail.trim().lowercase()\n    }\n}\n\nfun main() {\n    val user = UserEntity(1, \"old@example.com\")\n    val updated = updateEmail(user, \"  New@Example.COM \")\n    check(updated.email == \"new@example.com\")\n    check(runCatching { updateEmail(user, \"   \") }.isFailure)\n    println(\"Updated email: ${updated.email}\")\n}",
    "why": "`require(!newEmail.isNullOrBlank())` validates both null and whitespace while smart-casting `newEmail` to non-null `String`; `apply` mutates and returns the JPA entity cleanly.",
    "expectedOutput": "Updated email: new@example.com"
  },
  "0059-ex1": {
    "title": "Exercise 1 · Two Pointers: Container With Most Water",
    "code": "fun maxArea(height: IntArray): Int {\n    var left = 0\n    var right = height.lastIndex\n    var best = 0\n\n    while (left < right) {\n        val area = minOf(height[left], height[right]) * (right - left)\n        best = maxOf(best, area)\n        if (height[left] < height[right]) left++ else right--\n    }\n    return best\n}\n\nfun main() {\n    check(maxArea(intArrayOf(1, 8, 6, 2, 5, 4, 8, 3, 7)) == 49)\n    check(maxArea(intArrayOf(1, 1)) == 1)\n    check(maxArea(intArrayOf(4, 3, 2, 1, 4)) == 16)\n    println(\"All Two Pointers checks passed!\")\n}",
    "why": "Uses `height.lastIndex`, stdlib `minOf`/`maxOf`, and unboxed `IntArray` for `O(N)` time and `O(1)` heap allocations."
  },
  "0059-ex2": {
    "title": "Exercise 2 · Sliding Window Maximum: Monotonic Deque",
    "code": "fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {\n    if (nums.isEmpty()) return intArrayOf()\n    val deque = ArrayDeque<Int>() // indices in decreasing order of nums[idx]\n    val result = IntArray(nums.size - k + 1)\n\n    for (i in nums.indices) {\n        while (deque.isNotEmpty() && deque.first() <= i - k) {\n            deque.removeFirst()\n        }\n        while (deque.isNotEmpty() && nums[deque.last()] <= nums[i]) {\n            deque.removeLast()\n        }\n        deque.addLast(i)\n\n        if (i >= k - 1) {\n            result[i - k + 1] = nums[deque.first()]\n        }\n    }\n    return result\n}\n\nfun main() {\n    val arr = intArrayOf(1, 3, -1, -3, 5, 3, 6, 7)\n    val maxArr = maxSlidingWindow(arr, 3)\n    check(maxArr.contentEquals(intArrayOf(3, 3, 5, 5, 6, 7)))\n    check(maxSlidingWindow(intArrayOf(9, 8, 7), 2).contentEquals(intArrayOf(9, 8)))\n    println(\"Window Maxes: ${maxArr.joinToString()}\")\n}",
    "why": "`kotlin.collections.ArrayDeque` maintains a monotonic decreasing index queue in `O(N)` amortized time—every index enters and exits the deque at most once."
  },
  "0060-ex1": {
    "title": "Exercise 1 · Testable KMP Platform Abstraction & Fake",
    "code": "interface KeyValueStore {\n    fun put(key: String, value: String)\n    fun get(key: String): String?\n}\n\nclass InMemoryKeyValueStore : KeyValueStore {\n    private val map = mutableMapOf<String, String>()\n    override fun put(key: String, value: String) {\n        map[key] = value\n    }\n    override fun get(key: String): String? = map[key]\n}\n\nclass SessionRepository(private val store: KeyValueStore) {\n    val isLoggedIn: Boolean\n        get() = store.get(\"auth_token\") != null\n\n    fun saveToken(token: String) {\n        require(token.isNotBlank()) { \"Token cannot be blank\" }\n        store.put(\"auth_token\", token)\n    }\n}\n\nfun main() {\n    val fakeStore = InMemoryKeyValueStore()\n    val repo = SessionRepository(fakeStore)\n    check(!repo.isLoggedIn)\n\n    repo.saveToken(\"jwt_abc_123\")\n    check(repo.isLoggedIn)\n    check(fakeStore.get(\"auth_token\") == \"jwt_abc_123\")\n    check(runCatching { repo.saveToken(\"   \") }.isFailure)\n    println(\"All KMP abstraction & fake checks passed!\")\n}",
    "why": "Defining a pure Kotlin `interface` in `commonMain` decouples business logic from Android/iOS storage SDKs and makes `commonTest` unit tests effortless using fast in-memory fakes."
  },
  "0060-ex2": {
    "title": "Exercise 2 · Decouple JVM-Bound Domain Model for commonMain",
    "code": "data class DomainEvent(val id: String, val timestampMs: Long)\n\nfun DomainEvent.formatWith(formatter: (Long) -> String): String =\n    formatter(timestampMs)\n\nfun main() {\n    val event = DomainEvent(\"evt_01\", 1700000000000L)\n    val formatted = event.formatWith { ms -> \"${event.id}@$ms\" }\n    check(formatted == \"evt_01@1700000000000\")\n    println(formatted)\n}",
    "why": "Eliminates `java.text.SimpleDateFormat` and `java.util.Date` so `DomainEvent` compiles cleanly across JVM, Android, iOS Native, JS, and Wasm."
  },
  "0061-ex1": {
    "title": "Exercise 1 · Multiplatform Event Processing Pipeline",
    "code": "sealed interface AppEvent {\n    data class NetworkEvent(val code: Int) : AppEvent\n    data class ClickEvent(val tag: String) : AppEvent\n}\n\nfun AppEvent.isCritical(): Boolean = when (this) {\n    is AppEvent.NetworkEvent -> code >= 500\n    is AppEvent.ClickEvent -> tag == \"panic_button\"\n}\n\nclass EventProcessor {\n    private val queue by lazy { ArrayDeque<AppEvent>() }\n\n    fun enqueue(e: AppEvent) {\n        queue.addLast(e)\n    }\n\n    fun drainSummary(): Map<Boolean, Int> =\n        buildList {\n            while (queue.isNotEmpty()) add(queue.removeFirst())\n        }.groupingBy { it.isCritical() }.eachCount()\n}\n\nfun main() {\n    val processor = EventProcessor()\n    processor.enqueue(AppEvent.NetworkEvent(200))\n    processor.enqueue(AppEvent.NetworkEvent(503))\n    processor.enqueue(AppEvent.ClickEvent(\"save\"))\n    processor.enqueue(AppEvent.ClickEvent(\"panic_button\"))\n\n    val summary = processor.drainSummary()\n    check(summary == mapOf(false to 2, true to 2))\n    check(processor.drainSummary().isEmpty())\n    println(\"All cumulative pipeline checks passed: $summary\")\n}",
    "why": "Combines `sealed interface`, exhaustive `when`, `by lazy`, `ArrayDeque`, `buildList`, and `groupingBy { ... }.eachCount()` (which avoids intermediate list allocations per group)."
  },
  "0061-ex2": {
    "title": "Exercise 2 · The Grand Anti-Pattern Refactor",
    "code": "class LegacyOrder(val id: String?, val items: List<String>?, val total: Double?)\n\nfun List<LegacyOrder>?.highValueTotal(): Double =\n    orEmpty()\n        .filter { it.id != null }\n        .sumOf { order -> order.total?.takeIf { it >= 100.0 } ?: 0.0 }\n\nfun main() {\n    val orders = listOf(\n        LegacyOrder(\"o1\", listOf(\"Laptop\"), 1200.0),\n        LegacyOrder(\"o2\", listOf(\"Pen\"), 5.0),\n        LegacyOrder(null, listOf(\"Ghost\"), 500.0),\n        LegacyOrder(\"o3\", listOf(\"Monitor\"), 300.0)\n    )\n    val nullOrders: List<LegacyOrder>? = null\n    check(orders.highValueTotal() == 1500.0)\n    check(nullOrders.highValueTotal() == 0.0)\n    println(\"Grand refactor verified: ${orders.highValueTotal()}\")\n}",
    "why": "`orEmpty()` handles a nullable collection receiver without an early `if (orders == null)`, while `sumOf` + `?.takeIf` computes the `Double` total with zero intermediate boxed `List<Double>` allocations.",
    "expectedOutput": "Grand refactor verified: 1500.0"
  },
  "0062-ex1": {
    "title": "Exercise 1 · Testable In-Memory User Service with Fake (Write from blank)",
    "code": "data class User(val id: String, val email: String, val active: Boolean = true)\n\ninterface UserDao {\n    fun insert(user: User)\n    fun findById(id: String): User?\n    fun findByEmail(email: String): User?\n}\n\nclass FakeUserDao : UserDao {\n    private val users = mutableMapOf<String, User>()\n    override fun insert(user: User) { users[user.id] = user }\n    override fun findById(id: String): User? = users[id]\n    override fun findByEmail(email: String): User? = users.values.find { it.email == email }\n}\n\nclass UserService(private val dao: UserDao) {\n    fun register(id: String, email: String): User {\n        require(\"@\" in email) { \"Invalid email format: $email\" }\n        check(dao.findByEmail(email) == null) { \"Email already taken: $email\" }\n        val user = User(id, email)\n        dao.insert(user)\n        return user\n    }\n}\n\nfun main() {\n    val fakeDao = FakeUserDao()\n    val service = UserService(fakeDao)\n\n    val u1 = service.register(\"u1\", \"alice@example.com\")\n    check(u1.email == \"alice@example.com\")\n    check(fakeDao.findById(\"u1\") == u1)\n\n    val dupFailed = runCatching { service.register(\"u2\", \"alice@example.com\") }.isFailure\n    check(dupFailed) { \"Should reject duplicate email\" }\n\n    val invalidFailed = runCatching { service.register(\"u3\", \"invalid-email\") }.isFailure\n    check(invalidFailed) { \"Should reject invalid email format\" }\n\n    println(\"✅ All UserService fake tests passed successfully!\")\n}",
    "why": "Using an in-memory `FakeUserDao` tests real persistence behavior and state transitions without any mocking libraries, ensuring 100% compatibility across JVM and KMP `commonTest`."
  },
  "0062-ex2": {
    "title": "Exercise 2 · Replace Brittle Verification Mocks with Deterministic State Assertions (Refactor)",
    "code": "interface DiscountRule {\n    fun calculateDiscount(amountCents: Long): Long\n}\n\nclass FixedDiscountRule(private val discountCents: Long) : DiscountRule {\n    override fun calculateDiscount(amountCents: Long): Long =\n        minOf(amountCents, discountCents)\n}\n\nclass CheckoutService(private val discountRule: DiscountRule) {\n    fun calculateTotal(subtotalCents: Long): Long {\n        require(subtotalCents >= 0) { \"Subtotal cannot be negative\" }\n        val discount = discountRule.calculateDiscount(subtotalCents)\n        return subtotalCents - discount\n    }\n}\n\nfun main() {\n    val rule = FixedDiscountRule(500)\n    val checkout = CheckoutService(rule)\n    val total = checkout.calculateTotal(2000)\n    check(total == 1500L) { \"Expected 1500, got $total\" }\n    \n    check(checkout.calculateTotal(300) == 0L)\n    println(\"✅ Checkout calculation verified without mocking frameworks!\")\n}",
    "why": "Tests verify outputs directly against concrete domain rules instead of inspecting private mock interactions, allowing internal refactorings without broken test suites.",
    "expectedOutput": "✅ Checkout calculation verified without mocking frameworks!"
  },
  "0063-ex1": {
    "title": "Exercise 1 · Retry with Exponential Backoff Simulator (Write from blank)",
    "code": "class RetryPolicy(val maxRetries: Int = 3, val initialDelayMs: Long = 1000) {\n    fun calculateNextDelay(attempt: Int): Long =\n        initialDelayMs * (1L shl (attempt - 1))\n}\n\nclass FakeNetworkClient(private val failuresBeforeSuccess: Int) {\n    var attempts = 0\n        private set\n\n    fun request(): String {\n        attempts++\n        if (attempts <= failuresBeforeSuccess) {\n            throw IllegalStateException(\"Network failure on attempt $attempts\")\n        }\n        return \"SUCCESS\"\n    }\n}\n\nfun executeWithRetry(client: FakeNetworkClient, policy: RetryPolicy): Pair<String, Long> {\n    var totalDelay = 0L\n    var lastError: Exception? = null\n\n    for (attempt in 1..policy.maxRetries) {\n        try {\n            val result = client.request()\n            return result to totalDelay\n        } catch (e: Exception) {\n            lastError = e\n            if (attempt < policy.maxRetries) {\n                totalDelay += policy.calculateNextDelay(attempt)\n            }\n        }\n    }\n    throw lastError ?: IllegalStateException(\"Retries exhausted\")\n}\n\nfun main() {\n    val policy = RetryPolicy(maxRetries = 3, initialDelayMs = 1000)\n    check(policy.calculateNextDelay(1) == 1000L)\n    check(policy.calculateNextDelay(2) == 2000L)\n    check(policy.calculateNextDelay(3) == 4000L)\n\n    val client = FakeNetworkClient(failuresBeforeSuccess = 2)\n    val (result, totalDelay) = executeWithRetry(client, policy)\n    check(result == \"SUCCESS\")\n    check(totalDelay == 3000L) { \"Expected 3000ms delay, got $totalDelay\" }\n    check(client.attempts == 3)\n\n    println(\"✅ Retry policy with backoff verified in virtual time!\")\n}",
    "why": "Simulates exponential backoff timing using unboxed bitwise shift `1L shl (attempt - 1)` and accumulates virtual delay without thread sleeping."
  },
  "0063-ex2": {
    "title": "Exercise 2 · Eliminate Real-Time Sleep in Coroutine Tests (Refactor)",
    "code": "class DebouncedSearchEngine {\n    private var lastQuery: String = \"\"\n    var searchesExecuted = 0\n        private set\n\n    fun onNewInput(query: String, timestampMs: Long) {\n        lastQuery = query\n    }\n\n    fun flushIfIdle(currentTimestampMs: Long, lastInputTimestampMs: Long, debounceWindowMs: Long = 300) {\n        if (currentTimestampMs - lastInputTimestampMs >= debounceWindowMs && lastQuery.isNotBlank()) {\n            searchesExecuted++\n        }\n    }\n}\n\nfun main() {\n    val engine = DebouncedSearchEngine()\n    var clock = 1000L\n    \n    engine.onNewInput(\"k\", clock)\n    clock += 100\n    engine.onNewInput(\"ko\", clock)\n    clock += 100\n    engine.onNewInput(\"kot\", clock)\n    \n    clock += 200\n    engine.flushIfIdle(clock, lastInputTimestampMs = 1200)\n    check(engine.searchesExecuted == 0) { \"Search should not trigger before debounce window\" }\n    \n    clock += 150\n    engine.flushIfIdle(clock, lastInputTimestampMs = 1200)\n    check(engine.searchesExecuted == 1) { \"Search should trigger exactly once after idle window\" }\n    \n    println(\"✅ Debounce timing verified deterministically without Thread.sleep!\")\n}",
    "why": "Using virtual time increments replaces flaky, slow `Thread.sleep()` with 100% deterministic microsecond-fast test execution.",
    "expectedOutput": "✅ Debounce timing verified deterministically without Thread.sleep!"
  },
  "0064-ex1": {
    "title": "Exercise 1 · Testable Live Search Pipeline (Write from blank)",
    "code": "class SearchFilter(val minLength: Int = 3)\n\nfun filterSearchQueries(queries: List<String>, filter: SearchFilter): List<String> =\n    queries\n        .map(String::trim)\n        .filter { it.isNotBlank() && it.length >= filter.minLength }\n\nfun main() {\n    val filter = SearchFilter(minLength = 3)\n    val rawInputs = listOf(\"  k \", \"ko\", \"kot\", \"kotlin\", \"\", \"   \", \"android\")\n    val results = filterSearchQueries(rawInputs, filter)\n\n    check(results == listOf(\"kot\", \"kotlin\", \"android\")) { \"Unexpected results: $results\" }\n\n    val strictResults = filterSearchQueries(rawInputs, SearchFilter(minLength = 4))\n    check(strictResults == listOf(\"kotlin\", \"android\"))\n\n    println(\"✅ Search query filter pipeline verified!\")\n}",
    "why": "Combines member reference `String::trim` with functional filtering, making query pipelines easily verifiable across different search criteria."
  },
  "0064-ex2": {
    "title": "Exercise 2 · Refactor Hanging Flow Collector into Step-by-Step Assertions (Refactor)",
    "code": "sealed interface UiState {\n    data object Idle : UiState\n    data object Loading : UiState\n    data class Success(val data: String) : UiState\n}\n\nclass FlowTestQueue {\n    private val queue = ArrayDeque<UiState>()\n\n    fun emit(state: UiState) { queue.addLast(state) }\n    fun next(): UiState = queue.removeFirst()\n    fun hasMore(): Boolean = queue.isNotEmpty()\n}\n\nfun main() {\n    val tester = FlowTestQueue()\n    \n    tester.emit(UiState.Idle)\n    tester.emit(UiState.Loading)\n    tester.emit(UiState.Success(\"User profile loaded\"))\n\n    check(tester.next() == UiState.Idle)\n    check(tester.next() == UiState.Loading)\n    val success = tester.next()\n    check(success == UiState.Success(\"User profile loaded\"))\n    check(!tester.hasMore()) { \"No more events should remain\" }\n\n    println(\"✅ Sequential stream states verified step-by-step!\")\n}",
    "why": "Step-by-step stream consumption tests individual state transitions without hanging on infinite hot streams like `StateFlow`.",
    "expectedOutput": "✅ Sequential stream states verified step-by-step!"
  },
  "0065-ex1": {
    "title": "Exercise 1 · Testable Order Processing Pipeline (Write from blank)",
    "code": "data class Order(val id: String, val amountCents: Long, val status: String = \"PENDING\")\n\ninterface OrderStore {\n    fun save(order: Order)\n    fun get(id: String): Order?\n}\n\nclass FakeOrderStore : OrderStore {\n    private val orders = mutableMapOf<String, Order>()\n    override fun save(order: Order) { orders[order.id] = order }\n    override fun get(id: String): Order? = orders[id]\n}\n\nclass OrderProcessor(private val store: OrderStore) {\n    fun completeOrder(id: String): Order {\n        val order = requireNotNull(store.get(id)) { \"Order not found: $id\" }\n        check(order.status == \"PENDING\") { \"Order already completed: $id\" }\n        val updated = order.copy(status = \"COMPLETED\")\n        store.save(updated)\n        return updated\n    }\n}\n\nfun main() {\n    val store = FakeOrderStore()\n    val processor = OrderProcessor(store)\n\n    store.save(Order(\"ord-1\", 3500))\n\n    val completed = processor.completeOrder(\"ord-1\")\n    check(completed.status == \"COMPLETED\")\n    check(store.get(\"ord-1\")?.status == \"COMPLETED\")\n\n    val duplicateFailed = runCatching { processor.completeOrder(\"ord-1\") }.isFailure\n    check(duplicateFailed) { \"Should reject completing an already-completed order\" }\n\n    val missingFailed = runCatching { processor.completeOrder(\"missing\") }.isFailure\n    check(missingFailed) { \"Should fail when order does not exist\" }\n\n    println(\"✅ All order processor unit tests passed cleanly!\")\n}",
    "why": "Demonstrates the complete senior testing pattern: immutable data classes, interface contract, in-memory Fake, precondition enforcement (`requireNotNull`, `check`), and deterministic assertions."
  },
  "0065-ex2": {
    "title": "Exercise 2 · Production Bug Hunt: Fix 3 Concurrency & Testing Anti-Patterns (Refactor)",
    "code": "data class WalletState(val balanceCents: Long = 0, val transactionCount: Int = 0)\n\nclass CleanWallet {\n    private var _state = WalletState()\n    val state: WalletState get() = _state\n\n    fun deposit(amountCents: Long) {\n        require(amountCents > 0) { \"Deposit amount must be positive: $amountCents\" }\n        _state = _state.copy(\n            balanceCents = _state.balanceCents + amountCents,\n            transactionCount = _state.transactionCount + 1\n        )\n    }\n}\n\nfun main() {\n    val wallet = CleanWallet()\n    wallet.deposit(5000)\n    check(wallet.state == WalletState(5000, 1))\n\n    val invalidRejected = runCatching { wallet.deposit(-500) }.isFailure\n    check(invalidRejected) { \"Should reject negative deposits\" }\n\n    println(\"✅ Wallet bugs fixed and verified!\")\n}",
    "why": "Encapsulates private state with a read-only getter, enforces `require(amountCents > 0)`, and performs immutable state transitions via `copy()`.",
    "expectedOutput": "✅ Wallet bugs fixed and verified!"
  },
  "0066-ex1": {
    "title": "Exercise 1 · Type-Safe Route Guard DSL & Backoff Spec (Write from blank)",
    "code": "@DslMarker\nannotation class RouteDsl\n\nfun interface RoleGuard {\n    fun canAccess(role: String): Boolean\n}\n\nsealed interface Screen {\n    data object Feed : Screen\n    data class Profile(val verified: Boolean) : Screen\n    data class Checkout(val cents: Int, val hasCard: Boolean) : Screen\n}\n\n@RouteDsl\nclass PolicyBuilder {\n    private var guard: RoleGuard = RoleGuard { true }\n\n    fun checkRole(guard: RoleGuard) {\n        this.guard = guard\n    }\n\n    fun build(): (String, Screen) -> String = { role, screen ->\n        if (!guard.canAccess(role)) {\n            \"Denied\"\n        } else {\n            when (screen) {\n                is Screen.Checkout if !screen.hasCard -> \"Add Card\"\n                is Screen.Checkout -> \"Pay ${screen.cents}c\"\n                is Screen.Profile if !screen.verified -> \"Verify Email\"\n                is Screen.Profile -> \"Open Profile\"\n                Screen.Feed -> \"Open Feed\"\n            }\n        }\n    }\n}\n\nfun routePolicy(setup: PolicyBuilder.() -> Unit): (String, Screen) -> String =\n    PolicyBuilder().apply(setup).build()\n\ntailrec fun backoffMs(attempt: Int, accMs: Long = 100L): Long =\n    if (attempt <= 0) accMs else backoffMs(attempt - 1, accMs * 2)\n\nfun main() {\n    val evaluate = routePolicy {\n        checkRole { role -> role != \"guest\" }\n    }\n    check(evaluate(\"guest\", Screen.Feed) == \"Denied\")\n    check(evaluate(\"member\", Screen.Checkout(2500, hasCard = false)) == \"Add Card\")\n    check(evaluate(\"member\", Screen.Checkout(2500, hasCard = true)) == \"Pay 2500c\")\n    check(evaluate(\"member\", Screen.Profile(verified = false)) == \"Verify Email\")\n    check(backoffMs(3) == 800L)\n    println(\"All Modern Kotlin DSL & guard checks passed!\")\n}",
    "why": "Unifies `@DslMarker` receiver scoping, `fun interface` SAM conversion, Kotlin 2.2+ exhaustive `when` guard conditions, and `tailrec` loop elimination cleanly in pure Kotlin stdlib.",
    "expectedOutput": "All Modern Kotlin DSL & guard checks passed!"
  },
  "0066-ex2": {
    "title": "Exercise 2 · Flatten Nested if Chains & Naive Stack Recursion (Refactor)",
    "code": "sealed interface JobState {\n    data class Running(val progress: Int) : JobState\n    data class Error(val reason: String, val fatal: Boolean) : JobState\n    data object Idle : JobState\n}\n\nfun interface RetryRule {\n    fun shouldRetry(reason: String): Boolean\n}\n\nfun describeJob(state: JobState, rule: RetryRule): String = when (state) {\n    JobState.Idle -> \"Waiting\"\n    is JobState.Running if state.progress >= 90 -> \"Finishing\"\n    is JobState.Running -> \"Working (${state.progress}%)\"\n    is JobState.Error if !state.fatal && rule.shouldRetry(state.reason) -> \"Retrying: ${state.reason}\"\n    is JobState.Error -> \"Aborted: ${state.reason}\"\n}\n\ntailrec fun sumUpTo(n: Int, acc: Long = 0L): Long =\n    if (n <= 0) acc else sumUpTo(n - 1, acc + n)\n\nfun main() {\n    val rule = RetryRule { \"timeout\" in it }\n    check(describeJob(JobState.Idle, rule) == \"Waiting\")\n    check(describeJob(JobState.Running(95), rule) == \"Finishing\")\n    check(describeJob(JobState.Running(40), rule) == \"Working (40%)\")\n    check(describeJob(JobState.Error(\"network timeout\", fatal = false), rule) == \"Retrying: network timeout\")\n    check(describeJob(JobState.Error(\"network timeout\", fatal = true), rule) == \"Aborted: network timeout\")\n    check(sumUpTo(100_000) == 5_000_050_000L)\n    println(\"Refactored DSL, when guards, and tailrec verified!\")\n}",
    "why": "Replaces anonymous object boilerplate with `fun interface` SAM, collapses nested `if-else` trees into guarded exhaustive `when`, and turns `sumUpTo` into O(1)-stack `tailrec`.",
    "expectedOutput": "Refactored DSL, when guards, and tailrec verified!"
  },
  "0067-ex1": {
    "title": "Exercise 1 · Lifecycle-Safe Sensor Stream & Mutex Token Cache (Write from blank)",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.channels.awaitClose\nimport kotlinx.coroutines.flow.*\nimport kotlinx.coroutines.sync.Mutex\nimport kotlinx.coroutines.sync.withLock\n\nclass FakeSensor {\n    var running = false\n    fun start(onValue: (Int) -> Unit) {\n        running = true\n        onValue(101)\n        onValue(102)\n        onValue(103)\n    }\n    fun stop() { running = false }\n}\n\nfun sensorStream(sensor: FakeSensor): Flow<Int> = callbackFlow {\n    sensor.start { value -> trySend(value) }\n    awaitClose { sensor.stop() }\n}\n\nclass TokenCache(private val fetchToken: suspend () -> String) {\n    private val mutex = Mutex()\n    private var cachedToken: String? = null\n\n    suspend fun getToken(): String = mutex.withLock {\n        cachedToken ?: fetchToken().also { cachedToken = it }\n    }\n}\n\nfun main() = runBlocking {\n    val sensor = FakeSensor()\n    val readings = sensorStream(sensor).take(2).toList()\n    check(readings == listOf(101, 102))\n    check(!sensor.running) { \"awaitClose must call sensor.stop()!\" }\n\n    var networkCalls = 0\n    val cache = TokenCache {\n        delay(20)\n        networkCalls++\n        \"tok_$networkCalls\"\n    }\n    val tokens = coroutineScope {\n        List(5) { async { cache.getToken() } }.awaitAll()\n    }\n    check(networkCalls == 1 && tokens.all { it == \"tok_1\" })\n    println(\"sensorStream=$readings stopped=${!sensor.running} networkCalls=$networkCalls\")\n}",
    "why": "`callbackFlow` bridges callback APIs safely with `trySend` and deterministic cleanup in `awaitClose`; `Mutex.withLock` coordinates single-flight async fetching without blocking OS threads.",
    "expectedOutput": "sensorStream=[101, 102] stopped=true networkCalls=1"
  },
  "0067-ex2": {
    "title": "Exercise 2 · Cancel Stale Searches with flatMapLatest & Fix WhileSubscribed State (Refactor)",
    "code": "@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)\nimport kotlinx.coroutines.*\nimport kotlinx.coroutines.flow.*\n\nsuspend fun runSearchQuery(\n    q: String,\n    onStarted: (String) -> Unit,\n    onFinished: (String) -> Unit\n): String {\n    onStarted(q)\n    if (q == \"a\") delay(150) else delay(10)\n    val result = \"Hits:$q\"\n    onFinished(result)\n    return result\n}\n\nfun buildResultsFlow(\n    queries: Flow<String>,\n    onStarted: (String) -> Unit,\n    onFinished: (String) -> Unit\n): Flow<String> = queries.flatMapLatest { q ->\n    flow { emit(runSearchQuery(q, onStarted, onFinished)) }\n}\n\nfun main() = runBlocking {\n    val completed = mutableListOf<String>()\n    val aStarted = CompletableDeferred<Unit>()\n    val queryTrigger = MutableSharedFlow<String>(extraBufferCapacity = 4)\n\n    val scope = CoroutineScope(coroutineContext + SupervisorJob())\n    val uiState: StateFlow<String> = buildResultsFlow(\n        queries = queryTrigger,\n        onStarted = { if (it == \"a\") aStarted.complete(Unit) },\n        onFinished = { completed.add(it) }\n    ).stateIn(scope, SharingStarted.WhileSubscribed(5_000), \"Idle\")\n\n    check(uiState.value == \"Idle\")\n\n    val collected = mutableListOf<String>()\n    val collector = launch {\n        uiState.take(2).toList(collected)\n    }\n    queryTrigger.subscriptionCount.first { it > 0 }\n    queryTrigger.emit(\"a\")\n    aStarted.await()\n    queryTrigger.emit(\"android\")\n    collector.join()\n\n    check(collected.last() == \"Hits:android\")\n    check(\"Hits:a\" !in completed) { \"Stale query 'a' was NOT cancelled: $completed\" }\n    scope.cancel()\n    println(\"Collected=$collected completed=$completed\")\n}",
    "why": "`flatMapLatest` cancels in-flight collection of slow previous queries automatically when newer input arrives, eliminating stale out-of-order search results.",
    "expectedOutput": "Collected=[Idle, Hits:android] completed=[Hits:android]"
  },
  "0068-ex1": {
    "title": "Exercise 1 · Version Catalog Resolver & JVM-Safe Bridge (Write from blank)",
    "code": "import java.lang.reflect.Modifier\n\nfun resolveCatalogCoordinates(toml: String): Map<String, String> {\n    val versions = mutableMapOf<String, String>()\n    val coords = linkedMapOf<String, String>()\n    var section = \"\"\n    val kvRegex = Regex(\"\"\"(\\w+(?:\\.\\w+)?)\\s*=\\s*\"([^\"]+)\"\"\"\")\n\n    for (rawLine in toml.lines()) {\n        val line = rawLine.trim()\n        if (line.isEmpty() || line.startsWith(\"#\")) continue\n        if (line.startsWith(\"[\") && line.endsWith(\"]\")) {\n            section = line.removeSurrounding(\"[\", \"]\").trim()\n            continue\n        }\n        when (section) {\n            \"versions\" -> {\n                val parts = line.split(\"=\", limit = 2)\n                if (parts.size == 2) {\n                    versions[parts[0].trim()] = parts[1].trim().removeSurrounding(\"\\\"\")\n                }\n            }\n            \"libraries\" -> {\n                val parts = line.split(\"=\", limit = 2)\n                if (parts.size == 2) {\n                    val alias = parts[0].trim().replace('-', '.').replace('_', '.')\n                    val fields = kvRegex.findAll(parts[1])\n                        .associate { it.groupValues[1] to it.groupValues[2] }\n                    val group = requireNotNull(fields[\"group\"])\n                    val name = requireNotNull(fields[\"name\"])\n                    val ver = fields[\"version\"]\n                        ?: fields[\"version.ref\"]?.let { ref ->\n                            requireNotNull(versions[ref]) { \"Missing version ref: $ref\" }\n                        }\n                        ?: error(\"Missing version for $alias\")\n                    coords[\"libs.$alias\"] = \"$group:$name:$ver\"\n                }\n            }\n        }\n    }\n    return coords\n}\n\nobject BuildConfigBridge {\n    @JvmStatic\n    @JvmOverloads\n    fun formatEndpoint(baseUrl: String, version: Int = 2, strict: Boolean = true): String =\n        \"$baseUrl/v$version?strict=$strict\"\n\n    @JvmStatic\n    @Throws(IllegalArgumentException::class)\n    fun requireVersionTag(tag: String): String {\n        require(tag.startsWith(\"v\") && tag.length > 1) { \"Invalid version tag: $tag\" }\n        return tag\n    }\n}\n\n@JvmName(\"totalInts\")\nfun total(items: List<Int>): Int = items.sum()\n\n@JvmName(\"totalStrings\")\nfun total(items: List<String>): Int = items.sumOf { it.length }\n\nfun main() {\n    val sampleToml = \"\"\"\n        [versions]\n        coroutines = \"1.10.1\"\n        serialization = \"1.8.0\"\n\n        [libraries]\n        kotlinx-coroutines-core = { group = \"org.jetbrains.kotlinx\", name = \"kotlinx-coroutines-core\", version.ref = \"coroutines\" }\n        kotlinx-serialization-json = { group = \"org.jetbrains.kotlinx\", name = \"kotlinx-serialization-json\", version.ref = \"serialization\" }\n    \"\"\".trimIndent()\n\n    val coords = resolveCatalogCoordinates(sampleToml)\n    check(coords[\"libs.kotlinx.coroutines.core\"] == \"org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1\") {\n        \"Got $coords\"\n    }\n    check(coords[\"libs.kotlinx.serialization.json\"] == \"org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0\")\n\n    val bridgeMethod = BuildConfigBridge::class.java.getMethod(\"formatEndpoint\", String::class.java)\n    check(Modifier.isStatic(bridgeMethod.modifiers))\n    check(bridgeMethod.invoke(null, \"https://api.example.com\") == \"https://api.example.com/v2?strict=true\")\n\n    check(BuildConfigBridge.requireVersionTag(\"v14\") == \"v14\")\n    check(runCatching { BuildConfigBridge.requireVersionTag(\"14\") }.isFailure)\n\n    check(total(listOf(4, 6, 10)) == 20)\n    check(total(listOf(\"kt\", \"dsl\")) == 5)\n    println(\"Catalog resolver & JVM interop verified!\")\n}",
    "why": "Demonstrates how Gradle Version Catalogs normalize hyphenated TOML aliases into dotted Kotlin DSL coordinates, alongside `@JvmStatic`, `@JvmOverloads`, `@Throws`, and `@JvmName` bytecode bridges.",
    "expectedOutput": "Catalog resolver & JVM interop verified!"
  },
  "0068-ex2": {
    "title": "Exercise 2 · Harden Fragile Decoding & Collapse Redundant Overloads (Refactor)",
    "code": "import java.lang.reflect.Modifier\n\ndata class UserDto(val id: String, val displayName: String, val role: String = \"member\")\n\nclass LegacyDecoder {\n    companion object {\n        @JvmStatic\n        @JvmOverloads\n        fun decode(\n            raw: Map<String, String?>,\n            ignoreUnknown: Boolean = true,\n            coerceNullRole: Boolean = true\n        ): UserDto {\n            val allowed = setOf(\"id\", \"display_name\", \"role\")\n            if (!ignoreUnknown) {\n                val unknown = raw.keys - allowed\n                require(unknown.isEmpty()) { \"Unknown keys: $unknown\" }\n            }\n            val id = requireNotNull(raw[\"id\"]) { \"Missing id\" }\n            val name = requireNotNull(raw[\"display_name\"]) { \"Missing display_name\" }\n            val role = raw[\"role\"] ?: if (coerceNullRole) \"member\" else error(\"Role cannot be null!\")\n            return UserDto(id, name, role)\n        }\n    }\n}\n\nfun main() {\n    val rollingApiPayload = mapOf(\n        \"id\" to \"u_42\",\n        \"display_name\" to \"Ada\",\n        \"role\" to null,\n        \"new_backend_experiment\" to \"enabled\"\n    )\n\n    val oneArg = LegacyDecoder::class.java.getMethod(\"decode\", Map::class.java)\n    check(Modifier.isStatic(oneArg.modifiers))\n    val user = oneArg.invoke(null, rollingApiPayload) as UserDto\n    check(user == UserDto(\"u_42\", \"Ada\", \"member\")) { \"Got $user\" }\n\n    check(runCatching { LegacyDecoder.decode(rollingApiPayload, ignoreUnknown = false) }.isFailure)\n    println(\"Refactored tolerant decoder verified: $user\")\n}",
    "why": "Replaces three manual overloads with `@JvmStatic @JvmOverloads`, making resilient decoding (`ignoreUnknown` and null-to-default coercion) the safe default while keeping strict validation opt-in.",
    "expectedOutput": "Refactored tolerant decoder verified: UserDto(id=u_42, displayName=Ada, role=member)"
  },
  "0069-ex1": {
    "title": "Exercise 1 · Offline-First Sync Engine Capstone (Write from blank)",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.channels.awaitClose\nimport kotlinx.coroutines.flow.*\nimport kotlinx.coroutines.sync.Mutex\nimport kotlinx.coroutines.sync.withLock\n\nclass FakeBus {\n    var subscribed = false\n    fun subscribe(onEvent: (String) -> Unit) {\n        subscribed = true\n        onEvent(\"ONLINE\")\n        onEvent(\"METERED\")\n        onEvent(\"OFFLINE\")\n    }\n    fun unsubscribe() { subscribed = false }\n}\n\n@DslMarker\nannotation class SyncDsl\n\nfun interface RetryPredicate {\n    fun canRetry(attempt: Int, errorCode: Int): Boolean\n}\n\n@SyncDsl\nclass SyncPolicyBuilder {\n    private var predicate: RetryPredicate = RetryPredicate { _, _ -> false }\n\n    fun retryWhen(predicate: RetryPredicate) {\n        this.predicate = predicate\n    }\n\n    fun build(): RetryPredicate = predicate\n}\n\nfun syncPolicy(block: SyncPolicyBuilder.() -> Unit): RetryPredicate =\n    SyncPolicyBuilder().apply(block).build()\n\nsealed interface SyncOutcome {\n    data object UpToDate : SyncOutcome\n    data class Synced(val count: Int) : SyncOutcome\n    data class Failed(val code: Int, val attempt: Int) : SyncOutcome\n}\n\nfun classifyOutcome(outcome: SyncOutcome, retryRule: RetryPredicate): String = when (outcome) {\n    SyncOutcome.UpToDate -> \"Idle\"\n    is SyncOutcome.Synced if outcome.count == 0 -> \"No Updates\"\n    is SyncOutcome.Synced -> \"Synced ${outcome.count}\"\n    is SyncOutcome.Failed if retryRule.canRetry(outcome.attempt, outcome.code) -> \"Retry(${outcome.code})\"\n    is SyncOutcome.Failed -> \"Fatal(${outcome.code})\"\n}\n\nfun busEvents(bus: FakeBus): Flow<String> = callbackFlow {\n    bus.subscribe { event -> trySend(event) }\n    awaitClose { bus.unsubscribe() }\n}\n\nclass SyncTokenVault(private val fetch: suspend () -> String) {\n    private val mutex = Mutex()\n    private var cached: String? = null\n\n    suspend fun getToken(): String = mutex.withLock {\n        cached ?: fetch().also { cached = it }\n    }\n}\n\nfun main() = runBlocking {\n    val policy = syncPolicy {\n        retryWhen { attempt, code -> attempt < 3 && code >= 500 }\n    }\n    check(classifyOutcome(SyncOutcome.UpToDate, policy) == \"Idle\")\n    check(classifyOutcome(SyncOutcome.Synced(0), policy) == \"No Updates\")\n    check(classifyOutcome(SyncOutcome.Synced(12), policy) == \"Synced 12\")\n    check(classifyOutcome(SyncOutcome.Failed(503, attempt = 1), policy) == \"Retry(503)\")\n    check(classifyOutcome(SyncOutcome.Failed(503, attempt = 3), policy) == \"Fatal(503)\")\n\n    val bus = FakeBus()\n    val events = busEvents(bus).take(2).toList()\n    check(events == listOf(\"ONLINE\", \"METERED\"))\n    check(!bus.subscribed) { \"Bus listener leaked!\" }\n\n    var tokenCalls = 0\n    val vault = SyncTokenVault {\n        delay(15)\n        tokenCalls++\n        \"jwt_$tokenCalls\"\n    }\n    val tokens = coroutineScope { List(6) { async { vault.getToken() } }.awaitAll() }\n    check(tokenCalls == 1 && tokens.all { it == \"jwt_1\" }) { \"Got calls=$tokenCalls tokens=$tokens\" }\n    println(\"Capstone passed: events=$events unsubscribed=${!bus.subscribed} tokenCalls=$tokenCalls\")\n}",
    "why": "Synthesizes Section 13 end-to-end: `@DslMarker` + `fun interface`, guarded `when`, lifecycle-safe `callbackFlow`/`awaitClose`, and single-flight `Mutex.withLock` token retrieval.",
    "expectedOutput": "Capstone passed: events=[ONLINE, METERED] unsubscribed=true tokenCalls=1"
  },
  "0069-ex2": {
    "title": "Exercise 2 · Audit & Repair a Buggy Android Sync PR (Refactor)",
    "code": "import kotlinx.coroutines.*\nimport kotlinx.coroutines.channels.awaitClose\nimport kotlinx.coroutines.flow.*\nimport kotlinx.coroutines.sync.Mutex\nimport kotlinx.coroutines.sync.withLock\nimport java.lang.reflect.Modifier\n\nclass FakeTicker {\n    var attached = false\n    fun attach(onTick: (String) -> Unit) {\n        attached = true\n        onTick(\"tick-1\")\n        onTick(\"tick-2\")\n        onTick(\"tick-3\")\n    }\n    fun detach() { attached = false }\n}\n\nsealed interface SyncResult {\n    data class Synced(val items: Int) : SyncResult\n    data class Error(val code: Int) : SyncResult\n}\n\nfun tickerStream(ticker: FakeTicker): Flow<String> = callbackFlow {\n    ticker.attach { trySend(it) }\n    awaitClose { ticker.detach() }\n}\n\nclass ProfileCache(private val fetch: suspend () -> String) {\n    private val mutex = Mutex()\n    private var cached: String? = null\n\n    suspend fun getProfile(): String = mutex.withLock {\n        cached ?: fetch().also { cached = it }\n    }\n}\n\nclass EndpointHelper {\n    companion object {\n        @JvmStatic\n        @JvmOverloads\n        fun buildUrl(host: String, version: Int = 2): String = \"$host/v$version\"\n    }\n}\n\nfun formatSyncState(result: SyncResult): String = when (result) {\n    is SyncResult.Synced if result.items == 0 -> \"No Changes\"\n    is SyncResult.Synced -> \"Synced ${result.items}\"\n    is SyncResult.Error if result.code >= 500 -> \"Retryable\"\n    is SyncResult.Error -> \"Fatal\"\n}\n\nfun main() = runBlocking {\n    val ticker = FakeTicker()\n    val ticks = runCatching { tickerStream(ticker).take(2).toList() }.getOrElse { emptyList() }\n    check(ticks == listOf(\"tick-1\", \"tick-2\")) { \"Bug 1: ticks=$ticks\" }\n    check(!ticker.attached) { \"Bug 1: ticker was never detached via awaitClose!\" }\n\n    var networkHits = 0\n    val cache = ProfileCache {\n        delay(15)\n        networkHits++\n        \"profile_1\"\n    }\n    val profiles = coroutineScope { List(4) { async { cache.getProfile() } }.awaitAll() }\n    check(networkHits == 1 && profiles.all { it == \"profile_1\" }) {\n        \"Bug 2: cache stampede caused networkHits=$networkHits\"\n    }\n\n    val javaMethod = runCatching {\n        EndpointHelper::class.java.getMethod(\"buildUrl\", String::class.java)\n    }.getOrNull()\n    check(javaMethod != null && Modifier.isStatic(javaMethod.modifiers)) {\n        \"Bug 3: missing @JvmStatic @JvmOverloads on EndpointHelper.buildUrl\"\n    }\n    check(javaMethod.invoke(null, \"https://host\") == \"https://host/v2\")\n\n    check(formatSyncState(SyncResult.Synced(0)) == \"No Changes\") { \"Bug 4: 0 items should be 'No Changes'\" }\n    check(formatSyncState(SyncResult.Synced(5)) == \"Synced 5\")\n    check(formatSyncState(SyncResult.Error(503)) == \"Retryable\")\n    check(formatSyncState(SyncResult.Error(404)) == \"Fatal\")\n    println(\"All 4 PR bugs fixed cleanly!\")\n}",
    "why": "Eliminates callback leaks with `awaitClose`, prevents coroutine cache stampedes with `Mutex.withLock`, exposes Java static/default bridges via `@JvmStatic @JvmOverloads`, and enforces guarded exhaustive `when` handling.",
    "expectedOutput": "All 4 PR bugs fixed cleanly!"
  }
};
