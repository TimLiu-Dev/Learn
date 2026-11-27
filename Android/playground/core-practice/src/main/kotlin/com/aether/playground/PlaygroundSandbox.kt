package com.aether.playground

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

/**
 * Live Kotlin 2.x (K2) Companion Sandbox for Lessons 0001 - 0076.
 *
 * Paste or extend any lesson's coding exercise here and run:
 *   JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew test
 */
@JvmInline
value class UsdMicros(val micros: Long) {
    init {
        require(micros >= 0L) { "UsdMicros must be non-negative: $micros" }
    }
    operator fun plus(other: UsdMicros): UsdMicros = UsdMicros(micros + other.micros)
}

@Serializable
data class UserSession(
    val userId: String,
    val authToken: String? = null,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

/**
 * Starter exercise mirror for Lesson 0001: Modern Kotlin 2.x Essentials.
 */
object Lesson0001Sandbox {
    fun validateAndReduce(
        current: UserSession,
        rawEvent: Map<String, Any?>,
        onAudit: (String) -> Unit
    ): UserSession {
        val hasValidToken = rawEvent["token"] is String && (rawEvent["token"] as String).isNotBlank()
        if (!hasValidToken) {
            return current.copy(error = "MISSING_TOKEN")
        }
        val token = rawEvent["token"] as String
        return current.copy(
            authToken = token,
            isAuthenticated = true,
            error = null
        ).also { updated ->
            onAudit("AUTH_OK:${updated.userId}")
        }
    }
}

class ReactivePortfolioStore(initialBalance: UsdMicros = UsdMicros(0L)) {
    private val _balance = MutableStateFlow(initialBalance)
    val balance: StateFlow<UsdMicros> = _balance.asStateFlow()

    fun deposit(delta: UsdMicros) {
        _balance.update { it + delta }
    }
}
