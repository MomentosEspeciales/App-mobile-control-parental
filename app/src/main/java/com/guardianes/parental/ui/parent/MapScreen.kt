package com.guardianes.parental.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guardianes.parental.core.util.Format
import com.guardianes.parental.ui.components.SectionTitle

/**
 * Pantalla de ubicación y zonas seguras.
 *
 * Nota: el mapa interactivo (Google Maps Compose) requiere una API key válida.
 * Para mantener la app ejecutable sin clave, aquí se muestra un panel de
 * ubicación; sustituir [LocationPanel] por un `GoogleMap { ... }` cuando la
 * clave esté configurada (ver MAPS_API_KEY en build.gradle.kts).
 */
@Composable
fun MapScreen(state: ParentUiState, viewModel: ParentViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)); SectionTitle("Ubicación") }
        item { LocationPanel(state) }
        item {
            Button(onClick = viewModel::locateNow, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.MyLocation, null); Spacer(Modifier.size(8.dp))
                Text("Localizar ahora")
            }
        }
        item { SectionTitle("Zonas seguras") }
        items(state.zones, key = { it.id }) { zone ->
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Home, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(zone.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Radio ${zone.radiusMeters.toInt()} m · Alarma al salir: ${if (zone.alertOnExit) "sí" else "no"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = zone.active, onCheckedChange = {})
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun LocationPanel(state: ParentUiState) {
    val loc = state.latestLocation
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.GpsFixed, null, modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                if (loc != null) {
                    Text(
                        "%.5f, %.5f".format(loc.latitude, loc.longitude),
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Precisión ±${loc.accuracyMeters.toInt()} m · ${Format.relative(loc.timestamp)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text("Sin ubicación reciente", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
