package com.guardianes.parental.core.data.demo

import com.guardianes.parental.core.domain.model.AppCategory
import com.guardianes.parental.core.domain.model.AppUsage
import com.guardianes.parental.core.domain.model.AlertSeverity
import com.guardianes.parental.core.domain.model.AlertType
import com.guardianes.parental.core.domain.model.ChildProfile
import com.guardianes.parental.core.domain.model.CommandStatus
import com.guardianes.parental.core.domain.model.DailyUsageSummary
import com.guardianes.parental.core.domain.model.GeoPoint
import com.guardianes.parental.core.domain.model.PointsEntry
import com.guardianes.parental.core.domain.model.Quest
import com.guardianes.parental.core.domain.model.RemoteCommand
import com.guardianes.parental.core.domain.model.Reward
import com.guardianes.parental.core.domain.model.RewardType
import com.guardianes.parental.core.domain.model.SafetyAlert
import com.guardianes.parental.core.domain.model.SafeZone
import com.guardianes.parental.core.domain.model.UsageRule
import com.guardianes.parental.core.domain.repository.AlertRepository
import com.guardianes.parental.core.domain.repository.ChildRepository
import com.guardianes.parental.core.domain.repository.CommandRepository
import com.guardianes.parental.core.domain.repository.GamificationRepository
import com.guardianes.parental.core.domain.repository.LocationRepository
import com.guardianes.parental.core.domain.repository.SafeZoneRepository
import com.guardianes.parental.core.domain.repository.UsageRepository
import com.guardianes.parental.features.gamification.domain.GamificationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Almacén compartido en memoria con datos de ejemplo. Permite ejecutar y
 * demostrar toda la UI sin un backend real. En producción se sustituye por
 * la implementación Firebase (ver core/data/remote).
 */
@Singleton
class DemoStore @Inject constructor() {
    val children = MutableStateFlow(
        listOf(
            ChildProfile(
                id = "child_1", name = "Lucía", familyId = "family_demo",
                deviceOnline = true, batteryLevel = 72, lastSeen = System.currentTimeMillis(),
                points = 340, level = GamificationEngine.levelForPoints(340), streakDays = 5,
            ),
            ChildProfile(
                id = "child_2", name = "Mateo", familyId = "family_demo",
                deviceOnline = false, batteryLevel = 18, lastSeen = System.currentTimeMillis() - 1.hours.inWholeMilliseconds,
                points = 120, level = GamificationEngine.levelForPoints(120), streakDays = 2,
            ),
        )
    )

    val latestLocation = MutableStateFlow(
        GeoPoint(40.4168, -3.7038, accuracyMeters = 12f, timestamp = System.currentTimeMillis())
    )

    val zones = MutableStateFlow(
        listOf(
            SafeZone("z_home", "Casa", GeoPoint(40.4168, -3.7038), 150f),
            SafeZone("z_school", "Colegio", GeoPoint(40.4250, -3.6900), 200f),
        )
    )

    val alerts = MutableStateFlow(
        listOf(
            SafetyAlert(
                "a1", "child_1", AlertType.TIME_LIMIT_REACHED, AlertSeverity.INFO,
                "Límite de Instagram alcanzado", "Lucía llegó a 60 min en redes sociales.",
                System.currentTimeMillis() - 30.minutes.inWholeMilliseconds,
            ),
            SafetyAlert(
                "a2", "child_2", AlertType.LOW_BATTERY, AlertSeverity.WARNING,
                "Batería baja", "El dispositivo de Mateo está al 18%.",
                System.currentTimeMillis() - 1.hours.inWholeMilliseconds,
            ),
            SafetyAlert(
                "a3", "child_1", AlertType.LEFT_SAFE_ZONE, AlertSeverity.CRITICAL,
                "Salió de la zona segura «Colegio»", "Detectado a 320 m del centro de la zona.",
                System.currentTimeMillis() - 2.hours.inWholeMilliseconds,
                location = GeoPoint(40.4280, -3.6850),
            ),
        )
    )

    val rewards = MutableStateFlow(
        listOf(
            Reward("rw1", "30 min extra", "Media hora más de pantalla", 50, RewardType.EXTRA_SCREEN_TIME, 30),
            Reward("rw2", "1 hora extra", "Una hora más de pantalla", 90, RewardType.EXTRA_SCREEN_TIME, 60),
            Reward("rw3", "Elegir película", "Eliges la peli del viernes", 120, RewardType.CUSTOM_FAMILY_REWARD),
        )
    )

    val quests = MutableStateFlow(
        listOf(
            Quest("q1", "Madrugador digital", "No usar el móvil antes de las 8:00", 20, 3, 5),
            Quest("q2", "Tareas primero", "Completar deberes antes de jugar", 30, 1, 1, completed = true),
            Quest("q3", "Desconexión nocturna", "Sin pantalla después de las 21:00", 40, 4, 7),
        )
    )

    val pointsLedger = MutableStateFlow(
        listOf(
            PointsEntry("p1", "child_1", +30, "Tareas completadas", System.currentTimeMillis() - 3.hours.inWholeMilliseconds),
            PointsEntry("p2", "child_1", -50, "Canje: 30 min extra", System.currentTimeMillis() - 2.hours.inWholeMilliseconds),
            PointsEntry("p3", "child_1", +20, "Madrugador digital", System.currentTimeMillis() - 1.hours.inWholeMilliseconds),
        )
    )

    val rules = MutableStateFlow<List<UsageRule>>(
        listOf(
            UsageRule("r1", com.guardianes.parental.core.domain.model.RuleType.CATEGORY_TIME_LIMIT,
                targetCategory = AppCategory.SOCIAL, dailyLimitMillis = 1.hours.inWholeMilliseconds),
            UsageRule("r2", com.guardianes.parental.core.domain.model.RuleType.TOTAL_TIME_LIMIT,
                dailyLimitMillis = 3.hours.inWholeMilliseconds),
        )
    )

    val commands = MutableStateFlow<List<RemoteCommand>>(emptyList())

    fun todaySummary(): DailyUsageSummary = DailyUsageSummary(
        date = "2026-06-23",
        totalScreenMillis = (2.hours + 35.minutes).inWholeMilliseconds,
        perApp = listOf(
            AppUsage("com.instagram.android", "Instagram", AppCategory.SOCIAL, 55.minutes.inWholeMilliseconds, 14),
            AppUsage("com.google.android.youtube", "YouTube", AppCategory.VIDEO, 48.minutes.inWholeMilliseconds, 9),
            AppUsage("com.roblox.client", "Roblox", AppCategory.GAMES, 32.minutes.inWholeMilliseconds, 5),
            AppUsage("com.duolingo", "Duolingo", AppCategory.EDUCATION, 20.minutes.inWholeMilliseconds, 3),
        ),
    )
}

@Singleton
class DemoChildRepository @Inject constructor(private val store: DemoStore) : ChildRepository {
    override fun observeChildren(familyId: String) = store.children.asStateFlow()
    override fun observeChild(childId: String): Flow<ChildProfile?> =
        store.children.map { list -> list.firstOrNull { it.id == childId } }
    override suspend fun upsertChild(child: ChildProfile) {
        store.children.value = store.children.value.map { if (it.id == child.id) child else it }
    }
}

@Singleton
class DemoLocationRepository @Inject constructor(private val store: DemoStore) : LocationRepository {
    override fun observeLatestLocation(childId: String) = store.latestLocation.asStateFlow()
    override suspend fun requestLocationNow(childId: String) {
        // En demo, simplemente "refresca" el timestamp.
        store.latestLocation.value = store.latestLocation.value.copy(timestamp = System.currentTimeMillis())
    }
    override suspend fun pushLocation(childId: String, point: GeoPoint) { store.latestLocation.value = point }
    override fun observeHistory(childId: String, limit: Int): Flow<List<GeoPoint>> =
        store.latestLocation.map { listOf(it) }
}

@Singleton
class DemoSafeZoneRepository @Inject constructor(private val store: DemoStore) : SafeZoneRepository {
    override fun observeZones(childId: String) = store.zones.asStateFlow()
    override suspend fun saveZone(childId: String, zone: SafeZone) {
        val existing = store.zones.value.any { it.id == zone.id }
        store.zones.value = if (existing) store.zones.value.map { if (it.id == zone.id) zone else it }
        else store.zones.value + zone
    }
    override suspend fun deleteZone(childId: String, zoneId: String) {
        store.zones.value = store.zones.value.filterNot { it.id == zoneId }
    }
}

@Singleton
class DemoUsageRepository @Inject constructor(private val store: DemoStore) : UsageRepository {
    override fun observeDailySummary(childId: String, date: String): Flow<DailyUsageSummary?> =
        MutableStateFlow(store.todaySummary())
    override fun observeWeekly(childId: String): Flow<List<DailyUsageSummary>> =
        MutableStateFlow(List(7) { store.todaySummary() })
    override suspend fun reportUsage(childId: String, summary: DailyUsageSummary) {}
    override fun observeRules(childId: String) = store.rules.asStateFlow()
    override suspend fun saveRule(childId: String, rule: UsageRule) {
        val existing = store.rules.value.any { it.id == rule.id }
        store.rules.value = if (existing) store.rules.value.map { if (it.id == rule.id) rule else it }
        else store.rules.value + rule
    }
    override suspend fun deleteRule(childId: String, ruleId: String) {
        store.rules.value = store.rules.value.filterNot { it.id == ruleId }
    }
    override suspend fun currentlyBlockedPackages(childId: String): Set<String> = emptySet()
}

@Singleton
class DemoAlertRepository @Inject constructor(private val store: DemoStore) : AlertRepository {
    override fun observeAlerts(familyId: String) = store.alerts.asStateFlow()
    override suspend fun raiseAlert(alert: SafetyAlert) { store.alerts.value = listOf(alert) + store.alerts.value }
    override suspend fun acknowledge(alertId: String) {
        store.alerts.value = store.alerts.value.map { if (it.id == alertId) it.copy(acknowledged = true) else it }
    }
}

@Singleton
class DemoGamificationRepository @Inject constructor(private val store: DemoStore) : GamificationRepository {
    override fun observeRewards(familyId: String) = store.rewards.asStateFlow()
    override fun observeQuests(childId: String) = store.quests.asStateFlow()
    override fun observePointsLedger(childId: String) = store.pointsLedger.asStateFlow()
    override suspend fun addPoints(childId: String, delta: Int, reason: String) {
        store.children.value = store.children.value.map {
            if (it.id == childId) {
                val newPoints = (it.points + delta).coerceAtLeast(0)
                it.copy(points = newPoints, level = GamificationEngine.levelForPoints(newPoints))
            } else it
        }
        store.pointsLedger.value = listOf(
            PointsEntry(System.nanoTime().toString(), childId, delta, reason, System.currentTimeMillis())
        ) + store.pointsLedger.value
    }
    override suspend fun redeemReward(childId: String, rewardId: String) {
        val reward = store.rewards.value.firstOrNull { it.id == rewardId } ?: return
        val child = store.children.value.firstOrNull { it.id == childId } ?: return
        when (val r = GamificationEngine.redeem(child.points, reward)) {
            is GamificationEngine.RedeemResult.Success ->
                addPoints(childId, -reward.costPoints, "Canje: ${reward.title}")
            else -> throw IllegalStateException("No se pudo canjear: $r")
        }
    }
    override suspend fun saveReward(familyId: String, reward: Reward) {
        store.rewards.value = store.rewards.value + reward
    }
}

@Singleton
class DemoCommandRepository @Inject constructor(private val store: DemoStore) : CommandRepository {
    override suspend fun sendCommand(command: RemoteCommand) {
        store.commands.value = store.commands.value + command
    }
    override fun observeCommands(childId: String) = store.commands.asStateFlow()
    override suspend fun updateStatus(commandId: String, status: CommandStatus) {
        store.commands.value = store.commands.value.map {
            if (it.id == commandId) it.copy(status = status) else it
        }
    }
}
