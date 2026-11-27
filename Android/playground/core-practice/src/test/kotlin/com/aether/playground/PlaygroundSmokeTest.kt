package com.aether.playground

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaygroundSmokeTest {

    @Test
    fun `lesson 0001 reducer preserves immutability and records audit log`() {
        val logs = mutableListOf<String>()
        val initial = UserSession(userId = "usr_2027")

        val missing = Lesson0001Sandbox.validateAndReduce(initial, emptyMap(), logs::add)
        assertEquals("MISSING_TOKEN", missing.error)
        assertTrue(logs.isEmpty())

        val upgraded = Lesson0001Sandbox.validateAndReduce(
            missing,
            mapOf("token" to "jwt_live_token"),
            logs::add
        )
        assertTrue(upgraded.isAuthenticated)
        assertNull(upgraded.error)
        assertEquals(listOf("AUTH_OK:usr_2027"), logs)
    }

    @Test
    fun `lesson 0002 & 0008 UsdMicros avoids floating point drift with Turbine`() = runTest {
        val store = ReactivePortfolioStore(UsdMicros(100_000L)) // $0.10 in micros

        store.balance.test {
            assertEquals(UsdMicros(100_000L), awaitItem())
            store.deposit(UsdMicros(200_000L)) // + $0.20 -> exact 300_000L ($0.30)!
            assertEquals(UsdMicros(300_000L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
