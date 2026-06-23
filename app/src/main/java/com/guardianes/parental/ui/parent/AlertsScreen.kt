package com.guardianes.parental.ui.parent

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guardianes.parental.core.domain.model.AlertSeverity
import com.guardianes.parental.core.domain.model.SafetyAlert
import com.guardianes.parental.core.util.Format
import com.guardianes.parental.ui.components.SectionTitle
import com.guardianes.parental.ui.components.StatusPill

@Composable
fun AlertsScreen(state: ParentUiState, viewModel: ParentViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)); SectionTitle("Centro de alertas") }
        if (state.alerts.isEmpty()) {
            item { Text("No hay alertas. Todo en orden ✅", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(state.alerts, key = { it.id }) { alert ->
            AlertCard(alert) { viewModel.acknowledgeAlert(alert.id) }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun AlertCard(alert: SafetyAlert, onAck: () -> Unit) {
    val color = severityColor(alert.severity)
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.acknowledged) MaterialTheme.colorScheme.surface
            else color.copy(alpha = 0.08f),
        ),
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WarningAmber, null, tint = color)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(alert.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    StatusPill(severityLabel(alert.severity), color)
                }
                Text(
                    alert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(Format.relative(alert.timestamp), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!alert.acknowledged) {
                IconButton(onClick = onAck) { Icon(Icons.Default.Check, "Marcar como visto") }
            }
        }
    }
}

private fun severityColor(s: AlertSeverity): Color = when (s) {
    AlertSeverity.INFO -> Color(0xFF3D5AFE)
    AlertSeverity.WARNING -> Color(0xFFFF8A00)
    AlertSeverity.CRITICAL -> Color(0xFFE5484D)
}

private fun severityLabel(s: AlertSeverity): String = when (s) {
    AlertSeverity.INFO -> "Info"
    AlertSeverity.WARNING -> "Aviso"
    AlertSeverity.CRITICAL -> "Crítico"
}
