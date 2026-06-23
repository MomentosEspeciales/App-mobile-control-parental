package com.guardianes.parental.ui.child

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guardianes.parental.core.domain.model.RewardType
import com.guardianes.parental.features.gamification.domain.GamificationEngine
import com.guardianes.parental.ui.components.LabeledProgress
import com.guardianes.parental.ui.components.SectionTitle

@Composable
fun ChildHomeScreen(viewModel: ChildViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let { snackbar.showSnackbar(it); viewModel.consumeMessage() }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val child = state.child
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "¡Hola${child?.let { ", ${it.name}" } ?: ""}! 🌟",
                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
                )
            }

            // Tarjeta de nivel/puntos
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.fillMaxWidth().padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(64.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("${child?.level ?: 1}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.size(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Nivel ${child?.level ?: 1}", style = MaterialTheme.typography.titleLarge)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp))
                                    Text(" ${child?.points ?: 0} puntos",
                                        style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.size(12.dp))
                                    Icon(Icons.Default.LocalFireDepartment, null,
                                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    Text(" ${child?.streakDays ?: 0} días",
                                        style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        LabeledProgress(
                            label = "Progreso al nivel ${(child?.level ?: 1) + 1}",
                            trailing = "${(state.levelProgress * 100).toInt()}%",
                            fraction = state.levelProgress,
                        )
                    }
                }
            }

            // Misiones
            item { SectionTitle("Tus misiones") }
            items(state.quests, key = { it.id }) { quest ->
                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.size(8.dp))
                            Text(quest.title, style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f))
                            Text(if (quest.completed) "✅" else "+${quest.rewardPoints}")
                        }
                        LabeledProgress(
                            label = quest.description,
                            trailing = "${quest.progress}/${quest.goal}",
                            fraction = quest.progressFraction,
                        )
                    }
                }
            }

            // Tienda de recompensas
            item { SectionTitle("Canjea tus puntos") }
            items(state.rewards, key = { it.id }) { reward ->
                val canAfford = (state.child?.points ?: 0) >= reward.costPoints
                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(reward.title, style = MaterialTheme.typography.titleMedium)
                            val extra = if (reward.type == RewardType.EXTRA_SCREEN_TIME)
                                "+${reward.payloadMinutes} min de pantalla" else reward.description
                            Text(extra, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { viewModel.redeem(reward.id) },
                            enabled = canAfford,
                        ) { Text("${reward.costPoints} pts") }
                    }
                }
            }

            // Botón de pánico
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = viewModel::panic,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Default.Sos, null, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.size(10.dp))
                    Text("PEDIR AYUDA", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                }
                Text(
                    "Pulsa si te sientes en peligro. Avisaremos a tu familia al instante.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
