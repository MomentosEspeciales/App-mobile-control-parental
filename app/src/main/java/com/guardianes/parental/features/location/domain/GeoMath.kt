package com.guardianes.parental.features.location.domain

import com.guardianes.parental.core.domain.model.GeoPoint
import com.guardianes.parental.core.domain.model.SafeZone
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Utilidades geográficas puras para evaluación de zonas seguras. */
object GeoMath {

    private const val EARTH_RADIUS_M = 6_371_000.0

    /** Distancia en metros entre dos puntos (fórmula de Haversine). */
    fun distanceMeters(a: GeoPoint, b: GeoPoint): Double {
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)

        val h = sin(dLat / 2) * sin(dLat / 2) +
            sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        return 2 * EARTH_RADIUS_M * atan2(sqrt(h), sqrt(1 - h))
    }

    /** ¿El punto está dentro del radio de la zona? */
    fun isInsideZone(point: GeoPoint, zone: SafeZone): Boolean =
        distanceMeters(point, zone.center) <= zone.radiusMeters

    /** Transición detectada al pasar de una posición previa a otra nueva. */
    enum class ZoneTransition { ENTER, EXIT, NONE }

    /**
     * Determina la transición respecto a una zona considerando el margen de
     * precisión del GPS para reducir falsos positivos.
     */
    fun evaluateTransition(
        previous: GeoPoint?,
        current: GeoPoint,
        zone: SafeZone,
    ): ZoneTransition {
        // Solo cuenta como fuera si supera el radio + el error de medición.
        val effectiveRadius = zone.radiusMeters + current.accuracyMeters
        val wasInside = previous?.let { distanceMeters(it, zone.center) <= zone.radiusMeters }
        val isInside = distanceMeters(current, zone.center) <= effectiveRadius

        return when {
            wasInside == null -> ZoneTransition.NONE
            wasInside && !isInside -> ZoneTransition.EXIT
            !wasInside && isInside -> ZoneTransition.ENTER
            else -> ZoneTransition.NONE
        }
    }
}
