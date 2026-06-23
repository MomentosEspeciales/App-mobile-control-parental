package com.guardianes.parental.ui.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianes.parental.core.domain.model.AlertSeverity
import com.guardianes.parental.core.domain.model.AlertType
import com.guardianes.parental.core.domain.model.ChildProfile
import com.guardianes.parental.core.domain.model.Quest
import com.guardianes.parental.core.domain.model.Reward
import com.guardianes.parental.core.domain.model.SafetyAlert
import com.guardianes.parental.core.domain.repository.AlertRepository
import com.guardianes.parental.core.domain.repository.ChildRepository
import com.guardianes.parental.core.domain.repository.GamificationRepository
import com.guardianes.parental.features.gamification.domain.GamificationEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChildUiState(
    val child: ChildProfile? = null,
    val rewards: List<Reward> = emptyList(),
    val quests: List<Quest> = emptyList(),
    val levelProgress: Float = 0f,
)

@HiltViewModel
class ChildViewModel @Inject constructor(
    private val childRepo: ChildRepository,
    private val gamificationRepo: GamificationRepository,
    private val alertRepo: AlertRepository,
) : ViewModel() {

    private val childId = "child_1"
    private val familyId = "family_demo"

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    val state = combine(
        childRepo.observeChild(childId),
        gamificationRepo.observeRewards(familyId),
        gamificationRepo.observeQuests(childId),
    ) { child, rewards, quests ->
        ChildUiState(
            child = child,
            rewards = rewards,
            quests = quests,
            levelProgress = child?.let { GamificationEngine.levelProgress(it.points) } ?: 0f,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChildUiState())

    fun redeem(rewardId: String) {
        viewModelScope.launch {
            runCatching { gamificationRepo.redeemReward(childId, rewardId) }
                .onSuccess { _message.value = "¡Canje realizado! Tiempo extra desbloqueado 🎉" }
                .onFailure { _message.value = "Te faltan puntos para esta recompensa" }
        }
    }

    /**
     * Botón de pánico del menor: avisa a todos los tutores con máxima prioridad.
     * En producción además inicia [EmergencyService] de forma discreta.
     */
    fun panic() {
        viewModelScope.launch {
            alertRepo.raiseAlert(
                SafetyAlert(
                    id = UUID.randomUUID().toString(),
                    childId = childId,
                    type = AlertType.PANIC_BUTTON,
                    severity = AlertSeverity.CRITICAL,
                    title = "🚨 Botón de pánico activado",
                    description = "El menor ha pedido ayuda. Contacta de inmediato.",
                    timestamp = System.currentTimeMillis(),
                )
            )
            _message.value = "Ayuda solicitada. Tus tutores han sido avisados."
        }
    }

    fun consumeMessage() { _message.value = null }
}
