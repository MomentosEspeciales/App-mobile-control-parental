package com.guardianes.parental.features.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.guardianes.parental.core.domain.model.CommandType
import com.guardianes.parental.features.emergency.service.EmergencyService

/**
 * Recibe mensajes FCM. Dos usos principales:
 *
 *  1. En el teléfono del MENOR: comandos del tutor (LOCATE_NOW, START_SOS_*, LOCK_DEVICE…)
 *     que se ejecutan en segundo plano y devuelven el resultado a Firestore.
 *  2. En el teléfono del TUTOR: notificaciones push de alertas críticas
 *     (salida de zona segura, botón de pánico, exposición peligrosa…).
 */
class GuardianesMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.i(TAG, "Nuevo token FCM")
        // TODO: persistir el token en el perfil (Guardian/Child) para enrutar comandos.
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["commandType"] ?: return
        when (runCatching { CommandType.valueOf(type) }.getOrNull()) {
            CommandType.START_SOS_AUDIO -> EmergencyService.start(this, EmergencyService.MODE_AUDIO)
            CommandType.START_SOS_VIDEO -> EmergencyService.start(this, EmergencyService.MODE_VIDEO)
            CommandType.STOP_SOS -> EmergencyService.stop(this)
            CommandType.LOCATE_NOW -> Log.i(TAG, "Comando: localizar ahora (capturar y subir ubicación)")
            CommandType.LOCK_DEVICE -> Log.i(TAG, "Comando: bloquear dispositivo")
            CommandType.RING_DEVICE -> Log.i(TAG, "Comando: hacer sonar el dispositivo")
            CommandType.REFRESH_USAGE -> Log.i(TAG, "Comando: refrescar estadísticas de uso")
            null -> Log.w(TAG, "Comando desconocido: $type")
        }
    }

    companion object { private const val TAG = "GuardianesFCM" }
}
