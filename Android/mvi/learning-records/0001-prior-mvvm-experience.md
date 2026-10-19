# Prior knowledge: shipped MVVM apps, comfortable with Kotlin

Tim has already built and shipped Android apps using MVVM (ViewModel,
LiveData/StateFlow, repository pattern) and is comfortable with Kotlin
generally. This means lessons should skip Kotlin syntax basics and
`ViewModel`/`StateFlow`/coroutine fundamentals entirely, and teach MVI by
contrast with the MVVM patterns he already uses — not by introducing Android
architecture from scratch.

**Implications**: Future lessons can assume fluency with `StateFlow`,
`viewModelScope`, sealed classes, and Compose basics. Zone of proximal
development starts at "what MVI changes relative to MVVM," not "what is a
ViewModel."
