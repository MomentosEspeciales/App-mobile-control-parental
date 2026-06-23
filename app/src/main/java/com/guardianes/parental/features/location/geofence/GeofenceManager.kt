package com.guardianes.parental.features.location.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.guardianes.parental.core.domain.model.SafeZone

/**
 * Registra y elimina geocercas en Play Services a partir de las [SafeZone] de la familia.
 * Requiere permisos de ubicación (incl. segundo plano) concedidos por el usuario.
 */
class GeofenceManager(private val context: Context) {

    private val client: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
    }

    @SuppressLint("MissingPermission")
    fun register(zones: List<SafeZone>) {
        val geofences = zones.filter { it.active }.map { zone ->
            var transitions = 0
            if (zone.alertOnEnter) transitions = transitions or Geofence.GEOFENCE_TRANSITION_ENTER
            if (zone.alertOnExit) transitions = transitions or Geofence.GEOFENCE_TRANSITION_EXIT
            if (transitions == 0) transitions = Geofence.GEOFENCE_TRANSITION_EXIT

            Geofence.Builder()
                .setRequestId(zone.id)
                .setCircularRegion(zone.center.latitude, zone.center.longitude, zone.radiusMeters)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(transitions)
                .build()
        }
        if (geofences.isEmpty()) return

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        client.addGeofences(request, pendingIntent)
    }

    fun clear() {
        client.removeGeofences(pendingIntent)
    }
}
