# Zero-Java to Senior Java 25 LTS Syntax Bridge & Final Pre-Study Audit

## Context
Before beginning active study from Lesson `0002`, the learner confirmed their baseline: a **Junior Android Developer who can read some Kotlin, but is currently unable to read or write Java**, aiming for 2027 Senior Engineering mastery (Java 25 LTS & Spring Boot 4.0 on Linux production servers).

## Key Improvements Applied Across Master Plan & Lessons
1. **Upgraded Lesson `0074` & Reference `0074` (Ground-Zero Kotlin-to-Java Grammar & Syntax Decoder):**
   - Added a full side-by-side Kotlin-to-Java 25 grammar decoder (`fun main()` vs `public static void main(String[] args)`, `val`/`var` type-after-colon vs `final` type-on-the-left, mandatory semicolons, `new` keyword, return-type-first methods, `for (i in 0 until n)` vs `for (int i = 0; i < n; i++)`, `for (x in list)` vs `for (var x : list)`, `.length` vs `.length()` vs `.size()`, `if/else` & Elvis `?:` vs ternary `? :`, and `record` component methods `order.id()`).
   - Simplified `0074` Exercise 2 (`KotlinHabitBugFix.java`) so a complete Java beginner can solve the null-guard with a foundational Java ternary (`label == null ? "NONE" : label.trim()`) without needing `Optional.ofNullable(...).map(String::trim)` (which is taught 20 lessons later in `0022`).
   - Expanded `reference/0074-kotlin-to-java-bridge.html` with the full 11-row grammar table and a Senior Production Pitfalls Checklist.

2. **Added 25 Dedicated `Android / Kotlin -> Java 25 Syntax & Mental-Model Bridge` Callouts Across the Main Track:**
   - Embedded step-by-step Kotlin/Android-to-Java syntax translation boxes directly inside core lessons `0002`, `0003`, `0004`, `0005`, `0007`, `0009`, `0010`, `0011`, `0015`, `0016`, `0017`, `0018`, `0019`, `0020`, `0021`, `0023`, `0024`, `0025`, `0026`, `0027`, `0038`, `0039`, `0057`, `0060`, and `0062` (expanding explicit Kotlin/Android bridge coverage to **39 lessons** across the main track).
   - Connected Android concepts directly to Backend Java 25 & Linux server equivalents (ART/DEX -> HotSpot JVM on Linux, lowercase primitives vs capitalized wrappers, `when` -> `switch`/`yield`, `int[]` `.length` vs `String.length()` vs `List.size()`, no `$var` String interpolation in Java 25, no `BigDecimal` operator overloading, no `[]` bracket indexing on Java `List`/`Map`, `out`/`in` -> `? extends`/`? super`, eager Kotlin collection functions vs lazy `.stream()`, MockK -> Mockito, Room -> JPA/Hibernate 7, Hilt/Dagger -> Spring IoC proxies, Retrofit -> Spring `@RestController` & `RestClient`).

3. **Full 90-Lesson & 90-Reference Verification:**
   - Compiled all standalone exercise solutions against local OpenJDK `25.0.4.1` (`javac --release 25`).
   - Converted 7 raw markdown strings in exercise instructions (`0075`, `0076`, `0077`, `0078`, `0085`, `0086`) into clean `<code>` and `<strong>` tags.
   - Updated 13 residual `Java 21` / `se21` JLS links and labels (`0003`, `0004`, `0006`, `0007`, `0009`, `0010`, `0014`, `0017`, `0018`, `0029`, `0036`, `0038`, `0059`) to Java SE 25 (`se25`) and Spring Boot 4.0.
