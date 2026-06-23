package com.guardianes.parental.ui.parent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guardianes.parental.ui.components.SectionTitle

@Composable
fun SettingsScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)); SectionTitle("Ajustes") }

        item { GroupTitle("Control y límites") }
        item { SettingRow(Icons.Default.AccessTime, "Límites de tiempo y horarios", "Define cuándo y cuánto puede usar cada app") }
        item { SettingRow(Icons.Default.Block, "Apps bloqueadas y filtros web", "Gestiona qué puede abrir el menor") }

        item { GroupTitle("Seguridad") }
        item { SettingRow(Icons.Default.Shield, "Zonas seguras", "Geocercas y alarmas") }
        item { SettingRow(Icons.Default.Security, "Protección anti-desinstalación", "Device Admin / PIN del menor") }
        item { SettingRow(Icons.Default.Fingerprint, "Bloqueo de ajustes", "PIN o biometría para proteger la configuración") }

        item { GroupTitle("Familia") }
        item { SettingRow(Icons.Default.Group, "Tutores y dispositivos", "Añade tutores o vincula otro teléfono") }

        item { GroupTitle("Privacidad") }
        item { SettingRow(Icons.Default.PrivacyTip, "Datos y privacidad", "Consentimientos, exportar y eliminar datos") }

        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Guardianes v1.0.0 · Cumple las políticas de control parental de Google Play. " +
                    "El uso de cámara/micrófono se limita a emergencias y respeta los indicadores de privacidad del sistema.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GroupTitle(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
    )
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().clickable {}) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
