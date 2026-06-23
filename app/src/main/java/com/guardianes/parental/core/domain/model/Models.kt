package com.guardianes.parental.core.domain.model

/**
 * Modelos de dominio puros de Guardianes. Sin dependencias de Android para
 * mantener la capa de dominio testeable y reutilizable.
 */

/** Rol con el que se configura el dispositivo. */
enum class UserRole { PARENT, CHILD, UNSET }

/** Tutor (madre/padre/cuidador). */
data class Guardian(
    val id: String,
    val name: String,
    val email: String,
    val familyId: String,
    val fcmToken: String? = null,
)

/** Perfil del menor supervisado. */
data class ChildProfile(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val birthDate: Long? = null,
    val familyId: String,
    val deviceOnline: Boolean = false,
    val batteryLevel: Int? = null,
    val lastSeen: Long? = null,
    // Estado gamificado
    val points: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 0,
)

/** Posición geográfica con metadatos. */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 0f,
    val timestamp: Long = 0L,
)

/** Zona segura: un círculo donde el menor puede moverse libremente. */
data class SafeZone(
    val id: String,
    val name: String,
    val center: GeoPoint,
    val radiusMeters: Float,
    val active: Boolean = true,
    val alertOnExit: Boolean = true,
    val alertOnEnter: Boolean = false,
    val schedule: TimeWindow? = null,
)

/** Ventana horaria reutilizable (límites, zonas, modos). */
data class TimeWindow(
    val startMinuteOfDay: Int,   // 0..1439
    val endMinuteOfDay: Int,     // 0..1439
    val daysOfWeek: Set<Int> = (1..7).toSet(), // 1=Lun .. 7=Dom
)

/** Estadística de uso de una app en un día. */
data class AppUsage(
    val packageName: String,
    val appLabel: String,
    val category: AppCategory,
    val foregroundMillis: Long,
    val launches: Int,
)

enum class AppCategory {
    SOCIAL, GAMES, VIDEO, EDUCATION, COMMUNICATION, BROWSER, PRODUCTIVITY, OTHER
}

/** Agregado diario de uso para mostrar estadísticas. */
data class DailyUsageSummary(
    val date: String,                 // ISO yyyy-MM-dd
    val totalScreenMillis: Long,
    val perApp: List<AppUsage>,
    val firstUnlock: Long? = null,
    val lastUnlock: Long? = null,
)

/** Regla de límite/horario aplicada al menor. */
data class UsageRule(
    val id: String,
    val type: RuleType,
    val targetPackage: String? = null,    // null = aplica a categoría o total
    val targetCategory: AppCategory? = null,
    val dailyLimitMillis: Long? = null,
    val blockedWindow: TimeWindow? = null,
    val active: Boolean = true,
)

enum class RuleType { APP_TIME_LIMIT, CATEGORY_TIME_LIMIT, TOTAL_TIME_LIMIT, BLOCK_WINDOW, APP_BLOCK }

/** Alerta de seguridad mostrada al tutor. */
data class SafetyAlert(
    val id: String,
    val childId: String,
    val type: AlertType,
    val severity: AlertSeverity,
    val title: String,
    val description: String,
    val timestamp: Long,
    val acknowledged: Boolean = false,
    val location: GeoPoint? = null,
)

enum class AlertType {
    LEFT_SAFE_ZONE, ENTERED_SAFE_ZONE, PANIC_BUTTON, ADULT_CONTENT, BULLYING_LANGUAGE,
    SELF_HARM_SIGNAL, GROOMING_SIGNAL, RISKY_APP_INSTALLED, UNINSTALL_ATTEMPT,
    LOW_BATTERY, DEVICE_OFFLINE, TIME_LIMIT_REACHED, SOS_STARTED
}

enum class AlertSeverity { INFO, WARNING, CRITICAL }

/** Recompensa canjeable por puntos. */
data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val costPoints: Int,
    val type: RewardType,
    val payloadMinutes: Int? = null,    // para EXTRA_SCREEN_TIME
    val enabled: Boolean = true,
)

enum class RewardType { EXTRA_SCREEN_TIME, CUSTOM_FAMILY_REWARD }

/** Movimiento del libro de puntos. */
data class PointsEntry(
    val id: String,
    val childId: String,
    val delta: Int,
    val reason: String,
    val timestamp: Long,
)

/** Reto/misión gamificada. */
data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val progress: Int = 0,
    val goal: Int = 1,
    val completed: Boolean = false,
) {
    val progressFraction: Float
        get() = if (goal <= 0) 0f else (progress.toFloat() / goal).coerceIn(0f, 1f)
}

/** Comando padre→hijo ejecutado en el dispositivo del menor. */
data class RemoteCommand(
    val id: String,
    val familyId: String,
    val childId: String,
    val issuedByGuardianId: String,
    val type: CommandType,
    val createdAt: Long,
    val status: CommandStatus = CommandStatus.PENDING,
)

enum class CommandType { LOCATE_NOW, START_SOS_AUDIO, START_SOS_VIDEO, STOP_SOS, LOCK_DEVICE, RING_DEVICE, REFRESH_USAGE }
enum class CommandStatus { PENDING, DELIVERED, EXECUTED, FAILED }
