package com.guardianes.parental.features.location

import com.guardianes.parental.core.domain.model.GeoPoint
import com.guardianes.parental.core.domain.model.SafeZone
import com.guardianes.parental.features.location.domain.GeoMath
import com.guardianes.parental.features.location.domain.GeoMath.ZoneTransition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeoMathTest {

    private val home = GeoPoint(40.4168, -3.7038) // Madrid
    private val zone = SafeZone(
        id = "z1", name = "Casa", center = home, radiusMeters = 150f,
    )

    @Test
    fun `distancia a si mismo es cero`() {
        assertEquals(0.0, GeoMath.distanceMeters(home, home), 0.5)
    }

    @Test
    fun `distancia conocida aproximada entre dos puntos`() {
        val other = GeoPoint(40.4180, -3.7038) // ~133 m al norte
        val d = GeoMath.distanceMeters(home, other)
        assertTrue("Esperado ~130m, fue $d", d in 120.0..145.0)
    }

    @Test
    fun `punto cercano esta dentro de la zona`() {
        val near = GeoPoint(40.4169, -3.7039)
        assertTrue(GeoMath.isInsideZone(near, zone))
    }

    @Test
    fun `punto lejano esta fuera de la zona`() {
        val far = GeoPoint(40.4300, -3.7038)
        assertTrue(!GeoMath.isInsideZone(far, zone))
    }

    @Test
    fun `transicion de salida se detecta`() {
        val inside = GeoPoint(40.4168, -3.7038, accuracyMeters = 5f)
        val outside = GeoPoint(40.4300, -3.7038, accuracyMeters = 5f)
        assertEquals(ZoneTransition.EXIT, GeoMath.evaluateTransition(inside, outside, zone))
    }

    @Test
    fun `transicion de entrada se detecta`() {
        val outside = GeoPoint(40.4300, -3.7038, accuracyMeters = 5f)
        val inside = GeoPoint(40.4168, -3.7038, accuracyMeters = 5f)
        assertEquals(ZoneTransition.ENTER, GeoMath.evaluateTransition(outside, inside, zone))
    }

    @Test
    fun `sin posicion previa no hay transicion`() {
        val inside = GeoPoint(40.4168, -3.7038)
        assertEquals(ZoneTransition.NONE, GeoMath.evaluateTransition(null, inside, zone))
    }
}
