package com.guardianes.parental.features.location.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

/**
 * Recibe las transiciones de geocerca y dispara la alerta correspondiente.
 *
 * En el dispositivo del menor, al detectar una SALIDA de una zona segura:
 *  1. Captura la ubicación actual.
 *  2. Crea una [com.guardianes.parental.core.domain.model.SafetyAlert] CRÍTICA.
 *  3. La sincroniza para que el teléfono del tutor reciba la alarma vía FCM.
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            Log.e(TAG, "Error de geocerca: ${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        val zoneIds = event.triggeringGeofences?.map { it.requestId }.orEmpty()

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_EXIT ->
                Log.w(TAG, "El menor SALIÓ de la(s) zona(s): $zoneIds → generar alerta CRÍTICA")
            Geofence.GEOFENCE_TRANSITION_ENTER ->
                Log.i(TAG, "El menor ENTRÓ en la(s) zona(s): $zoneIds")
        }
        // TODO: inyectar AlertRepository (vía EntryPoint) y publicar la alerta + notificación.
    }

    companion object { private const val TAG = "GeofenceReceiver" }
}
