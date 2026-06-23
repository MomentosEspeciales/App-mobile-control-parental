package com.guardianes.parental.features.usage.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

/**
 * Servicio de accesibilidad usado EXCLUSIVAMENTE para la función declarada de
 * control parental: detectar qué app pasa a primer plano y, si está bloqueada por
 * una regla activa (límite de tiempo alcanzado, horario de descanso/colegio, app
 * vetada), devolver al menor a una pantalla de bloqueo.
 *
 * No se recopila ni transmite contenido de pantalla ajeno a esta finalidad,
 * conforme a la política de Accessibility de Google Play.
 */
class AppBlockerAccessibilityService : AccessibilityService() {

    /** Conjunto de paquetes bloqueados ahora mismo (lo actualiza el motor de reglas). */
    @Volatile
    private var blockedPackages: Set<String> = emptySet()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        if (pkg in blockedPackages && pkg != packageName) {
            // Devuelve al menor al inicio y muestra una pantalla de bloqueo amable.
            performGlobalAction(GLOBAL_ACTION_HOME)
            // TODO: lanzar BlockScreenActivity con el motivo y los puntos disponibles.
        }
    }

    override fun onInterrupt() { /* sin estado que limpiar */ }

    /** Actualiza la lista de bloqueos (invocado por el motor de reglas / WorkManager). */
    fun updateBlockedPackages(packages: Set<String>) {
        blockedPackages = packages
    }

    companion object {
        /** Intent helper para abrir los ajustes de accesibilidad y que el tutor active el servicio. */
        fun accessibilitySettingsIntent(): Intent =
            Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }
}
