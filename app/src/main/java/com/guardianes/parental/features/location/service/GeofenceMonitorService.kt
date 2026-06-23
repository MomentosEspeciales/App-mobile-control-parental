package com.guardianes.parental.features.location.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.guardianes.parental.R
import com.guardianes.parental.core.system.Notifications

/**
 * Servicio en primer plano que mantiene activa la monitorización de zonas seguras.
 *
 * Estrategia: se apoya en la API de Geofencing de Play Services (registrada vía
 * [com.guardianes.parental.features.location.geofence.GeofenceManager]) para que el
 * sistema notifique transiciones con bajo consumo, y mantiene este servicio para
 * disponer de actualizaciones de ubicación continuas cuando hay una zona crítica
 * activa o el tutor solicita seguimiento.
 */
class GeofenceMonitorService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startInForeground()
        // TODO: iniciar solicitudes de ubicación y evaluar GeoMath.evaluateTransition.
        return START_STICKY
    }

    private fun startInForeground() {
        val notification: Notification = NotificationCompat.Builder(this, Notifications.CHANNEL_FOREGROUND)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle(getString(R.string.geofence_notification_title))
            .setContentText(getString(R.string.geofence_notification_text))
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                Notifications.ID_GEOFENCE_SERVICE,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION,
            )
        } else {
            startForeground(Notifications.ID_GEOFENCE_SERVICE, notification)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, GeofenceMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, GeofenceMonitorService::class.java))
        }
    }
}
