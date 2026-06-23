package com.guardianes.parental.core.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.guardianes.parental.core.domain.model.UserRole
import com.guardianes.parental.core.domain.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "guardianes_session")

/**
 * Persiste el rol y el contexto familiar del dispositivo en DataStore.
 */
@Singleton
class DataStoreSessionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : SessionRepository {

    private object Keys {
        val ROLE = stringPreferencesKey("role")
        val FAMILY_ID = stringPreferencesKey("family_id")
        val ACTIVE_CHILD = stringPreferencesKey("active_child")
    }

    override val role: Flow<UserRole> = context.dataStore.data.map { prefs ->
        prefs[Keys.ROLE]?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: UserRole.UNSET
    }

    override val familyId: Flow<String?> = context.dataStore.data.map { it[Keys.FAMILY_ID] }

    override val activeChildId: Flow<String?> = context.dataStore.data.map { it[Keys.ACTIVE_CHILD] }

    override suspend fun setRole(role: UserRole) {
        context.dataStore.edit { it[Keys.ROLE] = role.name }
    }

    override suspend fun setFamily(familyId: String) {
        context.dataStore.edit { it[Keys.FAMILY_ID] = familyId }
    }

    override suspend fun setActiveChild(childId: String) {
        context.dataStore.edit { it[Keys.ACTIVE_CHILD] = childId }
    }

    override suspend fun isOnboardingComplete(): Boolean {
        val prefs = context.dataStore.data.first()
        val role = prefs[Keys.ROLE]
        return role != null && role != UserRole.UNSET.name && prefs[Keys.FAMILY_ID] != null
    }
}
