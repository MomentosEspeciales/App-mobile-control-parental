package com.guardianes.parental.features.usage

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import com.guardianes.parental.core.domain.model.AppCategory
import com.guardianes.parental.core.domain.model.AppUsage
import com.guardianes.parental.core.domain.model.DailyUsageSummary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Recolector real de estadísticas de uso a partir de [UsageStatsManager].
 * Requiere el permiso especial PACKAGE_USAGE_STATS, que el usuario concede en
 * Ajustes (ver [usageAccessSettingsIntent] / [hasUsageAccess]).
 */
class UsageStatsCollector(private val context: Context) {

    /** ¿Tiene la app concedido el acceso a estadísticas de uso? */
    fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /** Construye el resumen de uso del día de hoy. */
    fun collectToday(): DailyUsageSummary {
        val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val (start, end) = todayRange()

        val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            .orEmpty()
            .filter { it.totalTimeInForeground > 0 }

        val pm = context.packageManager
        val perApp = stats.map { s ->
            val label = runCatching {
                pm.getApplicationLabel(pm.getApplicationInfo(s.packageName, 0)).toString()
            }.getOrDefault(s.packageName)
            AppUsage(
                packageName = s.packageName,
                appLabel = label,
                category = categorize(s.packageName),
                foregroundMillis = s.totalTimeInForeground,
                launches = 0,
            )
        }.sortedByDescending { it.foregroundMillis }

        return DailyUsageSummary(
            date = isoDate(start),
            totalScreenMillis = perApp.sumOf { it.foregroundMillis },
            perApp = perApp,
        )
    }

    private fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val end = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis to end
    }

    private fun isoDate(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(millis)

    /** Heurística simple de categorización; en producción usar la categoría del sistema. */
    private fun categorize(pkg: String): AppCategory = when {
        listOf("instagram", "tiktok", "snapchat", "facebook", "twitter").any { pkg.contains(it) } -> AppCategory.SOCIAL
        listOf("youtube", "netflix", "disney", "primevideo").any { pkg.contains(it) } -> AppCategory.VIDEO
        listOf("game", "roblox", "minecraft", "clash").any { pkg.contains(it) } -> AppCategory.GAMES
        listOf("duolingo", "khan", "classroom", "edu").any { pkg.contains(it) } -> AppCategory.EDUCATION
        listOf("whatsapp", "telegram", "messenger", "dialer").any { pkg.contains(it) } -> AppCategory.COMMUNICATION
        listOf("chrome", "browser", "firefox").any { pkg.contains(it) } -> AppCategory.BROWSER
        else -> AppCategory.OTHER
    }

    companion object {
        fun usageAccessSettingsIntent(): Intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }
}
