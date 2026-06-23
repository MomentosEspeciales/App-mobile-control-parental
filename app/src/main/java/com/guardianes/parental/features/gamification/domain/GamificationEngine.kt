package com.guardianes.parental.features.gamification.domain

import com.guardianes.parental.core.domain.model.Reward
import com.guardianes.parental.core.domain.model.RewardType
import kotlin.math.sqrt

/**
 * Motor de gamificación: nivelado, canje de puntos por tiempo y validaciones.
 * Lógica pura y determinista para poder testearla sin Android.
 */
object GamificationEngine {

    /** Puntos base necesarios para subir de un nivel al siguiente. */
    private const val BASE_LEVEL_POINTS = 100.0

    /** Minutos de pantalla por punto al canjear (configurable por la familia). */
    const val DEFAULT_MINUTES_PER_POINT = 1

    /**
     * Calcula el nivel a partir de los puntos totales acumulados.
     * Progresión cuadrática: niveles cada vez más caros.
     */
    fun levelForPoints(totalPoints: Int): Int {
        if (totalPoints <= 0) return 1
        return (sqrt(totalPoints / BASE_LEVEL_POINTS) + 1).toInt()
    }

    /** Puntos acumulados necesarios para alcanzar [level]. */
    fun pointsRequiredForLevel(level: Int): Int {
        if (level <= 1) return 0
        val n = (level - 1).toDouble()
        return (n * n * BASE_LEVEL_POINTS).toInt()
    }

    /** Progreso (0f..1f) dentro del nivel actual. */
    fun levelProgress(totalPoints: Int): Float {
        val current = levelForPoints(totalPoints)
        val floor = pointsRequiredForLevel(current)
        val ceil = pointsRequiredForLevel(current + 1)
        if (ceil <= floor) return 0f
        return ((totalPoints - floor).toFloat() / (ceil - floor)).coerceIn(0f, 1f)
    }

    /**
     * Resultado de intentar canjear una recompensa.
     */
    sealed interface RedeemResult {
        data class Success(
            val remainingPoints: Int,
            val grantedMinutes: Int,
        ) : RedeemResult

        data object InsufficientPoints : RedeemResult
        data object RewardDisabled : RedeemResult
    }

    /**
     * Intenta canjear [reward] con el saldo [currentPoints].
     * No muta estado: devuelve el resultado para que la capa de datos persista.
     */
    fun redeem(currentPoints: Int, reward: Reward): RedeemResult {
        if (!reward.enabled) return RedeemResult.RewardDisabled
        if (currentPoints < reward.costPoints) return RedeemResult.InsufficientPoints

        val minutes = when (reward.type) {
            RewardType.EXTRA_SCREEN_TIME -> reward.payloadMinutes ?: 0
            RewardType.CUSTOM_FAMILY_REWARD -> 0
        }
        return RedeemResult.Success(
            remainingPoints = currentPoints - reward.costPoints,
            grantedMinutes = minutes,
        )
    }

    /**
     * Convierte una cantidad de puntos en minutos de pantalla extra,
     * según el ratio configurado por la familia.
     */
    fun pointsToMinutes(points: Int, minutesPerPoint: Int = DEFAULT_MINUTES_PER_POINT): Int =
        (points.coerceAtLeast(0)) * minutesPerPoint.coerceAtLeast(0)
}
