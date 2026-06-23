package com.guardianes.parental.features.monitoring

import com.guardianes.parental.core.domain.model.AlertSeverity
import com.guardianes.parental.core.domain.model.AlertType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentClassifierTest {

    @Test
    fun `texto inofensivo no genera senales`() {
        assertTrue(ContentClassifier.classify("Hola, ¿jugamos al fútbol esta tarde?").isEmpty())
    }

    @Test
    fun `detecta senal de autolesion como critica`() {
        val signals = ContentClassifier.classify("a veces quiero morir")
        assertTrue(signals.any { it.type == AlertType.SELF_HARM_SIGNAL })
        assertTrue(ContentClassifier.hasCritical("a veces quiero morir"))
    }

    @Test
    fun `detecta grooming como critico`() {
        val signals = ContentClassifier.classify("es nuestro secreto, no se lo digas a tus padres")
        assertTrue(signals.any { it.type == AlertType.GROOMING_SIGNAL })
        assertEquals(AlertSeverity.CRITICAL, signals.first().severity)
    }

    @Test
    fun `detecta acoso como aviso`() {
        val signals = ContentClassifier.classify("eres un inútil")
        assertTrue(signals.any { it.type == AlertType.BULLYING_LANGUAGE })
    }

    @Test
    fun `no es sensible a mayusculas`() {
        assertFalse(ContentClassifier.classify("PORNO").isEmpty())
    }

    @Test
    fun `las senales se ordenan por gravedad descendente`() {
        // contiene acoso (warning) y autolesión (critical)
        val signals = ContentClassifier.classify("eres un inútil y quiero morir")
        assertEquals(AlertSeverity.CRITICAL, signals.first().severity)
    }
}
