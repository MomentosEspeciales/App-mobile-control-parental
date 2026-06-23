package com.guardianes.parental.features.usage.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Device Admin de Guardianes.
 *
 * Se usa con el ALCANCE MÍNIMO necesario para una función de control parental:
 *  • Dificultar la desinstalación no autorizada de la app en el teléfono del menor.
 *  • Permitir bloquear el dispositivo a distancia (force-lock).
 *
 * No se solicitan políticas más allá de lo declarado en res/xml/device_admin_policies.xml.
 */
class GuardianesDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        Log.i(TAG, "Device Admin activado: protección anti-desinstalación habilitada")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        Log.w(TAG, "Device Admin desactivado")
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence =
        "Si desactivas la administración, se reducirá la protección del control parental " +
            "y se avisará a los tutores."

    companion object { private const val TAG = "GuardianesDeviceAdmin" }
}
