package com.guardianes.parental.ui.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianes.parental.core.domain.model.ChildProfile
import com.guardianes.parental.core.domain.model.PointsEntry
import com.guardianes.parental.core.domain.model.Quest
import com.guardianes.parental.core.domain.model.Reward
import com.guardianes.parental.core.domain.repository.ChildRepository
import com.guardianes.parental.core.domain.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamificationUiState(
    val child: ChildProfile? = null,
    val rewards: List<Reward> = emptyList(),
    val quests: List<Quest> = emptyList(),
    val ledger: List<PointsEntry> = emptyList(),
)

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val gamificationRepo: GamificationRepository,
    childRepo: ChildRepository,
) : ViewModel() {

    private val familyId = "family_demo"
    private val childId = "child_1"
    private val error = MutableStateFlow<String?>(null)
    val errorMessage = error.asStateFlow()

    val state = combine(
        childRepo.observeChild(childId),
        gamificationRepo.observeRewards(familyId),
        gamificationRepo.observeQuests(childId),
        gamificationRepo.observePointsLedger(childId),
    ) { child, rewards, quests, ledger ->
        GamificationUiState(child, rewards, quests, ledger)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GamificationUiState())

    fun redeem(rewardId: String) {
        viewModelScope.launch {
            runCatching { gamificationRepo.redeemReward(childId, rewardId) }
                .onFailure { error.value = "No tienes puntos suficientes" }
        }
    }

    fun grantPoints(delta: Int, reason: String) {
        viewModelScope.launch { gamificationRepo.addPoints(childId, delta, reason) }
    }

    fun consumeError() { error.value = null }
}
