package com.guardianes.parental.core.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Reactiva el monitoreo (geocercas, servicios) tras un reinicio del dispositivo,
 * de modo que la protección persista sin intervención del usuario.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Dispositivo reiniciado: reactivando protección Guardianes")
            // TODO: reprogramar geocercas y servicios según el rol persistido.
            // GeofenceMonitorService.start(context)
        }
    }
}
