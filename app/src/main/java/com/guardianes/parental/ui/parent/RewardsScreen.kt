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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guardianes.parental.ui.components.LabeledProgress
import com.guardianes.parental.ui.components.SectionTitle
import com.guardianes.parental.ui.gamification.GamificationViewModel

@Composable
fun RewardsScreen(viewModel: GamificationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            SectionTitle("Gamificación")
            state.child?.let {
                Text(
                    "${it.name} · Nivel ${it.level} · ${it.points} pts",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item { SectionTitle("Otorgar puntos") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10 to "Tarea", 20 to "Buen comportamiento", 30 to "Logro").forEach { (pts, reason) ->
                    FilledTonalButton(
                        onClick = { viewModel.grantPoints(pts, reason) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Add, null); Text("$pts")
                    }
                }
            }
        }

        item { SectionTitle("Catálogo de recompensas") }
        items(state.rewards, key = { it.id }) { reward ->
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardGiftcard, null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(reward.title, style = MaterialTheme.typography.titleMedium)
                        Text(reward.description, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${reward.costPoints} pts", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item { SectionTitle("Misiones activas") }
        items(state.quests, key = { it.id }) { quest ->
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.size(8.dp))
                        Text(quest.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text("+${quest.rewardPoints} pts", color = MaterialTheme.colorScheme.primary)
                    }
                    LabeledProgress(
                        label = if (quest.completed) "Completada ✅" else quest.description,
                        trailing = "${quest.progress}/${quest.goal}",
                        fraction = quest.progressFraction,
                    )
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
