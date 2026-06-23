package com.guardianes.parental.features.gamification

import com.guardianes.parental.core.domain.model.Reward
import com.guardianes.parental.core.domain.model.RewardType
import com.guardianes.parental.features.gamification.domain.GamificationEngine
import com.guardianes.parental.features.gamification.domain.GamificationEngine.RedeemResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun `nivel inicial es 1 sin puntos`() {
        assertEquals(1, GamificationEngine.levelForPoints(0))
        assertEquals(1, GamificationEngine.levelForPoints(-50))
    }

    @Test
    fun `el nivel crece con los puntos`() {
        // 100 pts -> nivel 2, 400 -> nivel 3, 900 -> nivel 4
        assertEquals(2, GamificationEngine.levelForPoints(100))
        assertEquals(3, GamificationEngine.levelForPoints(400))
        assertEquals(4, GamificationEngine.levelForPoints(900))
    }

    @Test
    fun `puntos requeridos por nivel son coherentes con el nivelado`() {
        val required = GamificationEngine.pointsRequiredForLevel(3)
        assertEquals(3, GamificationEngine.levelForPoints(required))
    }

    @Test
    fun `progreso de nivel esta entre 0 y 1`() {
        val p = GamificationEngine.levelProgress(250)
        assertTrue(p in 0f..1f)
    }

    @Test
    fun `canje exitoso descuenta puntos y concede minutos`() {
        val reward = Reward(
            id = "r1", title = "30 min extra", description = "",
            costPoints = 50, type = RewardType.EXTRA_SCREEN_TIME, payloadMinutes = 30,
        )
        val result = GamificationEngine.redeem(currentPoints = 120, reward = reward)
        assertTrue(result is RedeemResult.Success)
        result as RedeemResult.Success
        assertEquals(70, result.remainingPoints)
        assertEquals(30, result.grantedMinutes)
    }

    @Test
    fun `canje sin saldo suficiente falla`() {
        val reward = Reward(
            id = "r1", title = "", description = "",
            costPoints = 200, type = RewardType.EXTRA_SCREEN_TIME, payloadMinutes = 30,
        )
        assertEquals(RedeemResult.InsufficientPoints, GamificationEngine.redeem(50, reward))
    }

    @Test
    fun `recompensa deshabilitada no se puede canjear`() {
        val reward = Reward(
            id = "r1", title = "", description = "",
            costPoints = 10, type = RewardType.EXTRA_SCREEN_TIME, payloadMinutes = 5, enabled = false,
        )
        assertEquals(RedeemResult.RewardDisabled, GamificationEngine.redeem(1000, reward))
    }

    @Test
    fun `conversion puntos a minutos respeta el ratio`() {
        assertEquals(20, GamificationEngine.pointsToMinutes(10, minutesPerPoint = 2))
        assertEquals(0, GamificationEngine.pointsToMinutes(-5))
    }
}
