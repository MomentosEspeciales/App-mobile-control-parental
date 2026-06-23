package com.guardianes.parental.core.di

import com.guardianes.parental.core.data.demo.DemoAlertRepository
import com.guardianes.parental.core.data.demo.DemoChildRepository
import com.guardianes.parental.core.data.demo.DemoCommandRepository
import com.guardianes.parental.core.data.demo.DemoGamificationRepository
import com.guardianes.parental.core.data.demo.DemoLocationRepository
import com.guardianes.parental.core.data.demo.DemoSafeZoneRepository
import com.guardianes.parental.core.data.demo.DemoUsageRepository
import com.guardianes.parental.core.data.session.DataStoreSessionRepository
import com.guardianes.parental.core.domain.repository.AlertRepository
import com.guardianes.parental.core.domain.repository.ChildRepository
import com.guardianes.parental.core.domain.repository.CommandRepository
import com.guardianes.parental.core.domain.repository.GamificationRepository
import com.guardianes.parental.core.domain.repository.LocationRepository
import com.guardianes.parental.core.domain.repository.SafeZoneRepository
import com.guardianes.parental.core.domain.repository.SessionRepository
import com.guardianes.parental.core.domain.repository.UsageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Enlaza las interfaces de repositorio con su implementación.
 *
 * Actualmente usa las implementaciones DEMO (en memoria) para que la app sea
 * ejecutable de inmediato. Para producción, crea un `FirebaseRepositoryModule`
 * con las implementaciones reales y reemplaza estos bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindSession(impl: DataStoreSessionRepository): SessionRepository

    @Binds @Singleton
    abstract fun bindChild(impl: DemoChildRepository): ChildRepository

    @Binds @Singleton
    abstract fun bindLocation(impl: DemoLocationRepository): LocationRepository

    @Binds @Singleton
    abstract fun bindSafeZone(impl: DemoSafeZoneRepository): SafeZoneRepository

    @Binds @Singleton
    abstract fun bindUsage(impl: DemoUsageRepository): UsageRepository

    @Binds @Singleton
    abstract fun bindAlert(impl: DemoAlertRepository): AlertRepository

    @Binds @Singleton
    abstract fun bindGamification(impl: DemoGamificationRepository): GamificationRepository

    @Binds @Singleton
    abstract fun bindCommand(impl: DemoCommandRepository): CommandRepository
}
