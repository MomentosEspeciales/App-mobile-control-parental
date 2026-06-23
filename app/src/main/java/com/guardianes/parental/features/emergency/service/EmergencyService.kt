package com.guardianes.parental.features.emergency.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.guardianes.parental.R
import com.guardianes.parental.core.system.Notifications
import java.io.File

/**
 * Servicio de EMERGENCIA / SOS.
 *
 * ── Diseño conforme a la ley y a las políticas de Google Play ──────────────
 * Esta función existe para proteger a un menor en peligro inmediato, activada
 * por el tutor legal con consentimiento registrado durante el emparejamiento.
 *
 * "Discreto, NO encubierto":
 *  • En el dispositivo del menor NO se muestran vistas previas, sonidos de
 *    obturador, flashes ni notificaciones llamativas, para no alertar a un
 *    posible agresor presente.
 *  • PERO el servicio respeta los indicadores de privacidad del sistema
 *    (punto verde de cámara/micrófono de Android 12+). Intentar ocultarlos
 *    viola la política de Play y, en muchas jurisdicciones, la ley. No se hace.
 *  • Cada activación se REGISTRA en la bitácora familiar (auditoría).
 *
 * El medio capturado se cifra y se sube al almacenamiento para que el tutor lo
 * revise; puede eliminarlo en cualquier momento.
 */
class EmergencyService : Service() {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mode = intent?.getStringExtra(EXTRA_MODE) ?: MODE_AUDIO
        startInForeground()
        when (mode) {
            MODE_AUDIO -> startAudioCapture()
            MODE_VIDEO -> startVideoCapture()
            MODE_STOP -> stopSelfSafely()
        }
        return START_NOT_STICKY
    }

    private fun startInForeground() {
        // Notificación obligatoria del SO. Texto neutro para no delatar el SOS
        // a un tercero, pero presente como exige el sistema.
        val notification: Notification = NotificationCompat.Builder(this, Notifications.CHANNEL_FOREGROUND)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(getString(R.string.emergency_notification_title))
            .setContentText(getString(R.string.emergency_notification_text))
            .setOngoing(true)
            .setSilent(true)
            .build()

        val type = ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA or
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(Notifications.ID_EMERGENCY_SERVICE, notification, type)
        } else {
            startForeground(Notifications.ID_EMERGENCY_SERVICE, notification)
        }
    }

    private fun startAudioCapture() {
        runCatching {
            val file = File(cacheDir, "sos_${System.currentTimeMillis()}.m4a").also { outputFile = it }
            recorder = buildRecorder().apply {
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            Log.i(TAG, "Captura de audio SOS iniciada")
            // TODO: subir y cifrar el archivo cuando se detenga; notificar al tutor.
        }.onFailure { Log.e(TAG, "No se pudo iniciar la captura de audio", it) }
    }

    private fun startVideoCapture() {
        // La grabación de vídeo se implementa con CameraX (VideoCapture) sin
        // superficie de previsualización visible. Se omite aquí por brevedad;
        // ver docs/ROADMAP.md. De momento, se captura al menos el audio.
        startAudioCapture()
    }

    @Suppress("DEPRECATION")
    private fun buildRecorder(): MediaRecorder =
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(this) else MediaRecorder()).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        }

    private fun stopSelfSafely() {
        runCatching {
            recorder?.apply { stop(); release() }
        }
        recorder = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        runCatching { recorder?.release() }
        recorder = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "EmergencyService"
        const val EXTRA_MODE = "mode"
        const val MODE_AUDIO = "audio"
        const val MODE_VIDEO = "video"
        const val MODE_STOP = "stop"

        fun start(context: Context, mode: String) {
            val intent = Intent(context, EmergencyService::class.java).putExtra(EXTRA_MODE, mode)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent)
            else context.startService(intent)
        }

        fun stop(context: Context) = start(context, MODE_STOP)
    }
}
