package com.guardianes.parental.core.domain.repository

import com.guardianes.parental.core.domain.model.AppUsage
import com.guardianes.parental.core.domain.model.ChildProfile
import com.guardianes.parental.core.domain.model.DailyUsageSummary
import com.guardianes.parental.core.domain.model.GeoPoint
import com.guardianes.parental.core.domain.model.PointsEntry
import com.guardianes.parental.core.domain.model.Quest
import com.guardianes.parental.core.domain.model.RemoteCommand
import com.guardianes.parental.core.domain.model.Reward
import com.guardianes.parental.core.domain.model.SafetyAlert
import com.guardianes.parental.core.domain.model.SafeZone
import com.guardianes.parental.core.domain.model.UsageRule
import kotlinx.coroutines.flow.Flow

/** Acceso a perfiles de menores de la familia. */
interface ChildRepository {
    fun observeChildren(familyId: String): Flow<List<ChildProfile>>
    fun observeChild(childId: String): Flow<ChildProfile?>
    suspend fun upsertChild(child: ChildProfile)
}

/** Ubicación bajo demanda e histórico. */
interface LocationRepository {
    fun observeLatestLocation(childId: String): Flow<GeoPoint?>
    suspend fun requestLocationNow(childId: String)
    suspend fun pushLocation(childId: String, point: GeoPoint)
    fun observeHistory(childId: String, limit: Int = 100): Flow<List<GeoPoint>>
}

/** Zonas seguras (geocercas). */
interface SafeZoneRepository {
    fun observeZones(childId: String): Flow<List<SafeZone>>
    suspend fun saveZone(childId: String, zone: SafeZone)
    suspend fun deleteZone(childId: String, zoneId: String)
}

/** Estadísticas y reglas de uso. */
interface UsageRepository {
    fun observeDailySummary(childId: String, date: String): Flow<DailyUsageSummary?>
    fun observeWeekly(childId: String): Flow<List<DailyUsageSummary>>
    suspend fun reportUsage(childId: String, summary: DailyUsageSummary)
    fun observeRules(childId: String): Flow<List<UsageRule>>
    suspend fun saveRule(childId: String, rule: UsageRule)
    suspend fun deleteRule(childId: String, ruleId: String)
    /** Devuelve los paquetes actualmente bloqueados según reglas + uso. */
    suspend fun currentlyBlockedPackages(childId: String): Set<String>
}

/** Alertas de seguridad. */
interface AlertRepository {
    fun observeAlerts(familyId: String): Flow<List<SafetyAlert>>
    suspend fun raiseAlert(alert: SafetyAlert)
    suspend fun acknowledge(alertId: String)
}

/** Gamificación: puntos, recompensas y misiones. */
interface GamificationRepository {
    fun observeRewards(familyId: String): Flow<List<Reward>>
    fun observeQuests(childId: String): Flow<List<Quest>>
    fun observePointsLedger(childId: String): Flow<List<PointsEntry>>
    suspend fun addPoints(childId: String, delta: Int, reason: String)
    /** Canjea una recompensa; lanza excepción si no procede. */
    suspend fun redeemReward(childId: String, rewardId: String)
    suspend fun saveReward(familyId: String, reward: Reward)
}

/** Comandos remotos padre→hijo. */
interface CommandRepository {
    suspend fun sendCommand(command: RemoteCommand)
    fun observeCommands(childId: String): Flow<List<RemoteCommand>>
    suspend fun updateStatus(commandId: String, status: com.guardianes.parental.core.domain.model.CommandStatus)
}

/** Sesión y rol del dispositivo. */
interface SessionRepository {
    val role: Flow<com.guardianes.parental.core.domain.model.UserRole>
    val familyId: Flow<String?>
    val activeChildId: Flow<String?>
    suspend fun setRole(role: com.guardianes.parental.core.domain.model.UserRole)
    suspend fun setFamily(familyId: String)
    suspend fun setActiveChild(childId: String)
    suspend fun isOnboardingComplete(): Boolean
}
