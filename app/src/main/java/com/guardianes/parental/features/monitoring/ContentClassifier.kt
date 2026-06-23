package com.guardianes.parental.features.monitoring

import com.guardianes.parental.core.domain.model.AlertSeverity
import com.guardianes.parental.core.domain.model.AlertType
import java.util.Locale

/**
 * Clasificador de contenido ON-DEVICE de primer nivel para detectar señales de
 * exposición peligrosa (acoso, contenido adulto, autolesión, grooming).
 *
 * Privacidad por diseño: el análisis ocurre en el dispositivo; sólo se envía al
 * tutor una ALERTA (tipo + severidad + extracto mínimo), nunca el flujo completo
 * de lo que el menor lee o escribe. En producción este motor por palabras clave
 * se complementa con un modelo on-device (TFLite/ML Kit) para reducir falsos
 * positivos y soportar más idiomas.
 */
object ContentClassifier {

    data class Signal(val type: AlertType, val severity: AlertSeverity)

    // Listas semilla (ampliables / localizables). Se comparan en minúsculas.
    private val selfHarm = setOf(
        "quiero morir", "no quiero vivir", "hacerme daño", "suicidarme", "cortarme",
    )
    private val grooming = setOf(
        "no se lo digas a tus padres", "es nuestro secreto", "mándame una foto tuya",
        "cuántos años tienes", "quedamos a solas",
    )
    private val bullying = setOf(
        "te voy a pegar", "eres un inútil", "nadie te quiere", "mátate", "idiota de mierda",
    )
    private val adult = setOf(
        "porno", "xxx", "contenido +18", "nudes",
    )

    /**
     * Analiza un texto y devuelve las señales detectadas, ordenadas por gravedad.
     */
    fun classify(text: String): List<Signal> {
        val normalized = text.lowercase(Locale.getDefault())
        val signals = mutableListOf<Signal>()

        if (selfHarm.any { normalized.contains(it) }) {
            signals += Signal(AlertType.SELF_HARM_SIGNAL, AlertSeverity.CRITICAL)
        }
        if (grooming.any { normalized.contains(it) }) {
            signals += Signal(AlertType.GROOMING_SIGNAL, AlertSeverity.CRITICAL)
        }
        if (bullying.any { normalized.contains(it) }) {
            signals += Signal(AlertType.BULLYING_LANGUAGE, AlertSeverity.WARNING)
        }
        if (adult.any { normalized.contains(it) }) {
            signals += Signal(AlertType.ADULT_CONTENT, AlertSeverity.WARNING)
        }

        return signals.sortedByDescending { it.severity.ordinal }
    }

    /** Conveniencia: ¿hay alguna señal crítica? */
    fun hasCritical(text: String): Boolean =
        classify(text).any { it.severity == AlertSeverity.CRITICAL }
}
