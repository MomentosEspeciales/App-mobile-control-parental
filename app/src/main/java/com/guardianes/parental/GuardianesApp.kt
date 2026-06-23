package com.guardianes.parental

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.guardianes.parental.core.system.Notifications
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la app. Inicializa Hilt y los canales de notificación.
 */
@HiltAndroidApp
class GuardianesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.createNotificationChannel(
            NotificationChannel(
                Notifications.CHANNEL_ALERTS,
                "Alertas de seguridad",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = "Avisos urgentes sobre la seguridad del menor" }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                Notifications.CHANNEL_FOREGROUND,
                "Servicios en segundo plano",
                NotificationManager.IMPORTANCE_LOW,
            ).apply { description = "Indica cuándo Guardianes está protegiendo el dispositivo" }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                Notifications.CHANNEL_GAMIFICATION,
                "Logros y recompensas",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "Puntos, niveles y misiones completadas" }
        )
    }
}
