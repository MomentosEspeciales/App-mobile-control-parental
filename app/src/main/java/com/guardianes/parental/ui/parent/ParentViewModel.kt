package com.guardianes.parental.ui.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianes.parental.core.domain.model.ChildProfile
import com.guardianes.parental.core.domain.model.CommandStatus
import com.guardianes.parental.core.domain.model.CommandType
import com.guardianes.parental.core.domain.model.DailyUsageSummary
import com.guardianes.parental.core.domain.model.GeoPoint
import com.guardianes.parental.core.domain.model.RemoteCommand
import com.guardianes.parental.core.domain.model.SafeZone
import com.guardianes.parental.core.domain.model.SafetyAlert
import com.guardianes.parental.core.domain.repository.AlertRepository
import com.guardianes.parental.core.domain.repository.ChildRepository
import com.guardianes.parental.core.domain.repository.CommandRepository
import com.guardianes.parental.core.domain.repository.LocationRepository
import com.guardianes.parental.core.domain.repository.SafeZoneRepository
import com.guardianes.parental.core.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ParentUiState(
    val children: List<ChildProfile> = emptyList(),
    val selectedChildId: String? = null,
    val alerts: List<SafetyAlert> = emptyList(),
    val zones: List<SafeZone> = emptyList(),
    val latestLocation: GeoPoint? = null,
    val todayUsage: DailyUsageSummary? = null,
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class ParentViewModel @Inject constructor(
    private val childRepo: ChildRepository,
    private val alertRepo: AlertRepository,
    private val zoneRepo: SafeZoneRepository,
    private val locationRepo: LocationRepository,
    private val usageRepo: UsageRepository,
    private val commandRepo: CommandRepository,
) : ViewModel() {

    private val familyId = "family_demo"
    private val selectedChild = MutableStateFlow("child_1")
    private val actionMessage = MutableStateFlow<String?>(null)

    /** Mensaje transitorio para mostrar en un Snackbar. */
    val message = actionMessage.asStateFlow()

    val state = combineState()

    private fun combineState(): kotlinx.coroutines.flow.StateFlow<ParentUiState> {
        val children = childRepo.observeChildren(familyId)
        val alerts = alertRepo.observeAlerts(familyId)
        val zones = selectedChild.flatMapLatest { zoneRepo.observeZones(it) }
        val location = selectedChild.flatMapLatest { locationRepo.observeLatestLocation(it) }
        val usage = selectedChild.flatMapLatest { usageRepo.observeDailySummary(it, "2026-06-23") }

        return kotlinx.coroutines.flow.combine(
            children, alerts, zones, location, usage,
        ) { c, a, z, loc, u ->
            ParentUiState(
                children = c,
                selectedChildId = selectedChild.value,
                alerts = a,
                zones = z,
                latestLocation = loc,
                todayUsage = u,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ParentUiState())
    }

    fun selectChild(childId: String) { selectedChild.value = childId }

    fun acknowledgeAlert(alertId: String) {
        viewModelScope.launch { alertRepo.acknowledge(alertId) }
    }

    fun locateNow() {
        val childId = selectedChild.value
        viewModelScope.launch {
            commandRepo.sendCommand(command(childId, CommandType.LOCATE_NOW))
            locationRepo.requestLocationNow(childId)
            actionMessage.value = "Solicitud de ubicación enviada"
        }
    }

    /**
     * Activa el modo emergencia. En el dispositivo del menor, el comando se ejecuta
     * de forma discreta (sin pistas para un agresor presente) PERO manteniendo los
     * indicadores de privacidad del sistema, tal como exige la política de Play.
     */
    fun triggerSos(video: Boolean) {
        val childId = selectedChild.value
        viewModelScope.launch {
            val type = if (video) CommandType.START_SOS_VIDEO else CommandType.START_SOS_AUDIO
            commandRepo.sendCommand(command(childId, type))
            actionMessage.value = "Modo emergencia (${if (video) "vídeo" else "audio"}) solicitado"
        }
    }

    fun ringDevice() {
        viewModelScope.launch {
            commandRepo.sendCommand(command(selectedChild.value, CommandType.RING_DEVICE))
            actionMessage.value = "Haciendo sonar el dispositivo"
        }
    }

    fun lockDevice() {
        viewModelScope.launch {
            commandRepo.sendCommand(command(selectedChild.value, CommandType.LOCK_DEVICE))
            actionMessage.value = "Bloqueo enviado"
        }
    }

    fun consumeMessage() { actionMessage.value = null }

    private fun command(childId: String, type: CommandType) = RemoteCommand(
        id = UUID.randomUUID().toString(),
        familyId = familyId,
        childId = childId,
        issuedByGuardianId = "guardian_self",
        type = type,
        createdAt = System.currentTimeMillis(),
        status = CommandStatus.PENDING,
    )
}
