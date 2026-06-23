package com.guardianes.parental.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianes.parental.core.domain.model.UserRole
import com.guardianes.parental.core.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val session: SessionRepository,
) : ViewModel() {

    val role = session.role.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserRole.UNSET,
    )

    fun chooseRole(role: UserRole) {
        viewModelScope.launch {
            session.setRole(role)
            // En un flujo real, aquí se crea/empareja la familia.
            session.setFamily("family_demo")
            if (role == UserRole.PARENT) session.setActiveChild("child_1")
        }
    }
}
