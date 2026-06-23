package com.guardianes.parental.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guardianes.parental.core.domain.model.AppCategory
import com.guardianes.parental.core.util.Format
import com.guardianes.parental.ui.components.LabeledProgress
import com.guardianes.parental.ui.components.SectionTitle
import com.guardianes.parental.ui.components.StatCard
import com.guardianes.parental.ui.components.StatusPill

@Composable
fun DashboardScreen(state: ParentUiState, viewModel: ParentViewModel) {
    var showSosDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Hola 👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Resumen de tu familia",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Selector de hijo
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.children.forEach { child ->
                    FilterChip(
                        selected = child.id == state.selectedChildId,
                        onClick = { viewModel.selectChild(child.id) },
                        label = { Text(child.name) },
                    )
                }
            }
        }

        val child = state.children.firstOrNull { it.id == state.selectedChildId }
        if (child != null) {
            item {
                Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(48.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center,
                            ) { Text(child.name.take(1), style = MaterialTheme.typography.titleLarge) }
                            Spacer(Modifier.size(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(child.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Nivel ${child.level} · ${child.points} pts · 🔥 ${child.streakDays} días",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            StatusPill(
                                text = if (child.deviceOnline) "En línea" else "Sin conexión",
                                color = if (child.deviceOnline) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            // Tarjetas de estado
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        icon = Icons.Default.Timer,
                        value = Format.duration(state.todayUsage?.totalScreenMillis ?: 0),
                        label = "Pantalla hoy",
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        icon = Icons.Default.BatteryFull,
                        value = "${child.batteryLevel ?: 0}%",
                        label = "Batería",
                        modifier = Modifier.weight(1f),
                        accent = if ((child.batteryLevel ?: 100) < 20) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        icon = Icons.Default.NotificationsActive,
                        value = "${state.alerts.count { !it.acknowledged }}",
                        label = "Alertas activas",
                        modifier = Modifier.weight(1f),
                        accent = MaterialTheme.colorScheme.error,
                    )
                    StatCard(
                        icon = Icons.Default.Schedule,
                        value = child.lastSeen?.let { Format.relative(it) } ?: "—",
                        label = "Visto",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Acciones rápidas
        item { SectionTitle("Acciones rápidas") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = viewModel::locateNow, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.MyLocation, null); Spacer(Modifier.size(6.dp)); Text("Localizar")
                }
                OutlinedButton(onClick = viewModel::ringDevice, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.VolumeUp, null); Spacer(Modifier.size(6.dp)); Text("Sonar")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = viewModel::lockDevice, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Lock, null); Spacer(Modifier.size(6.dp)); Text("Bloquear")
                }
                Button(
                    onClick = { showSosDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Default.Sos, null); Spacer(Modifier.size(6.dp)); Text("Emergencia")
                }
            }
        }

        // Uso por app
        state.todayUsage?.let { usage ->
            item { SectionTitle("Uso de hoy por app") }
            items(usage.perApp, usage.totalScreenMillis)
        }

        item { Spacer(Modifier.height(24.dp)) }
    }

    if (showSosDialog) {
        SosDialog(
            onDismiss = { showSosDialog = false },
            onAudio = { viewModel.triggerSos(video = false); showSosDialog = false },
            onVideo = { viewModel.triggerSos(video = true); showSosDialog = false },
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.items(
    apps: List<com.guardianes.parental.core.domain.model.AppUsage>,
    total: Long,
) {
    apps.forEach { app ->
        item(key = app.packageName) {
            LabeledProgress(
                label = "${app.appLabel}  ·  ${categoryLabel(app.category)}",
                trailing = Format.duration(app.foregroundMillis),
                fraction = if (total > 0) app.foregroundMillis.toFloat() / total else 0f,
                color = categoryColor(app.category),
            )
        }
    }
}

@Composable
private fun SosDialog(onDismiss: () -> Unit, onAudio: () -> Unit, onVideo: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Sos, null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Modo emergencia") },
        text = {
            Column {
                Text(
                    "Activa la captura discreta para proteger al menor en caso de peligro. " +
                        "El dispositivo no mostrará pistas a un tercero presente, pero el sistema " +
                        "Android mantendrá su indicador de privacidad (punto verde), como exige la ley.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(12.dp))
                AssistChip(onClick = {}, label = { Text("Quedará registrado en la bitácora familiar") })
            }
        },
        confirmButton = {
            Button(
                onClick = onVideo,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) { Icon(Icons.Default.Videocam, null); Spacer(Modifier.size(6.dp)); Text("Audio + Vídeo") }
        },
        dismissButton = {
            TextButton(onClick = onAudio) { Text("Solo audio") }
        },
    )
}

private fun categoryLabel(c: AppCategory): String = when (c) {
    AppCategory.SOCIAL -> "Redes"
    AppCategory.GAMES -> "Juegos"
    AppCategory.VIDEO -> "Vídeo"
    AppCategory.EDUCATION -> "Educación"
    AppCategory.COMMUNICATION -> "Comunicación"
    AppCategory.BROWSER -> "Navegador"
    AppCategory.PRODUCTIVITY -> "Productividad"
    AppCategory.OTHER -> "Otros"
}

private fun categoryColor(c: AppCategory): Color = when (c) {
    AppCategory.SOCIAL -> Color(0xFFE5484D)
    AppCategory.GAMES -> Color(0xFF8E4DFF)
    AppCategory.VIDEO -> Color(0xFFFF8A00)
    AppCategory.EDUCATION -> Color(0xFF1B873F)
    else -> Color(0xFF3D5AFE)
}
