# 🏗️ Arquitectura — Guardianes

## 1. Visión general

Guardianes es una app Android nativa (Kotlin) construida con **Clean Architecture + MVVM**. Una sola base de código sirve a **dos roles** (Padre y Hijo) que se determinan en el onboarding y se persisten en `DataStore`. El estado se sincroniza entre dispositivos a través de **Firebase**.

```
┌──────────────────────────────────────────────────────────────┐
│                          UI (Compose)                         │
│   onboarding · auth · parent/* · child/*  (Screens + VM)      │
└───────────────▲───────────────────────────────▲──────────────┘
                │ StateFlow / UiState            │ eventos
┌───────────────┴───────────────────────────────┴──────────────┐
│                         DOMAIN                                 │
│   Modelos · UseCases · Repository (interfaces)                │
└───────────────▲───────────────────────────────▲──────────────┘
                │                                │
┌───────────────┴────────────────┐ ┌────────────┴──────────────┐
│            DATA local           │ │        DATA remota         │
│  Room · DataStore · Sistema     │ │  Firebase (Firestore/FCM/  │
│  (UsageStats, Geofence, Camera) │ │  Auth/Storage)             │
└─────────────────────────────────┘ └───────────────────────────┘
```

### Capas
- **ui**: Composables sin lógica de negocio; observan `UiState` expuesto por los `ViewModel`.
- **domain**: modelos puros de Kotlin + interfaces de repositorio + casos de uso. Sin dependencias de Android salvo lo imprescindible.
- **data**: implementaciones de repositorio que combinan fuentes locales (Room/DataStore/APIs de sistema) y remotas (Firebase).

## 2. Módulos funcionales (`features/`)

| Paquete | Responsabilidad | APIs/Tecnología clave |
|---------|-----------------|-----------------------|
| `location` | Tracking bajo demanda + geofencing de zonas seguras | `FusedLocationProviderClient`, `GeofencingClient`, `BroadcastReceiver` |
| `usage` | Estadísticas de uso, límites y bloqueo de apps | `UsageStatsManager`, `AccessibilityService`, `DevicePolicyManager` |
| `emergency` | Modo SOS: audio/vídeo + ubicación en peligro | `CameraX`, `MediaRecorder`, `ForegroundService`, Storage |
| `gamification` | Puntos, niveles, recompensas, canje por tiempo | Room + reglas de dominio |
| `monitoring` | Detección de exposición peligrosa y alertas | Accessibility + clasificador de contenido + FCM |

## 3. Modelo de datos (Firestore)

```
families/{familyId}
  ├─ guardians/{userId}        # tutores
  ├─ children/{childId}        # perfiles de menores
  │    ├─ usageDaily/{date}    # agregados de uso
  │    ├─ locations/{ts}       # histórico de ubicación
  │    ├─ rewards/{rewardId}   # catálogo y canjes
  │    └─ pointsLedger/{ts}    # movimientos de puntos
  ├─ safeZones/{zoneId}        # geocercas
  ├─ alerts/{alertId}          # alertas de seguridad
  ├─ rules/{ruleId}            # límites y horarios
  └─ commands/{commandId}      # órdenes padre→hijo (localizar, SOS, bloquear)
```

El patrón **command** (`commands/`) permite que el padre solicite acciones (p. ej. "localizar ahora", "iniciar SOS") que el dispositivo del menor recibe por **FCM** y ejecuta, devolviendo el resultado a Firestore.

## 4. Comunicación entre dispositivos

```
Padre  ──escribe command──►  Firestore  ──FCM push──►  Hijo
Hijo   ──ejecuta y sube resultado──►  Firestore  ──realtime listener──►  Padre
```

- **FCM data messages** despiertan al dispositivo del menor para ejecutar comandos.
- **Firestore listeners** dan actualización en tiempo real (ubicación, alertas, puntos).
- Todo comando queda **auditado** (quién, cuándo, resultado) por transparencia y cumplimiento.

## 5. Servicios en segundo plano

| Servicio | Tipo | Función |
|----------|------|---------|
| `GeofenceMonitorService` | Foreground (`location`) | Mantiene activas las geocercas y notifica salidas |
| `UsageTrackingWorker` | WorkManager periódico | Recoge y agrega estadísticas de uso |
| `AppBlockerAccessibilityService` | AccessibilityService | Detecta app en primer plano y aplica bloqueos |
| `EmergencyService` | Foreground (`microphone`/`camera`) | Captura SOS; **muestra notificación según política** |
| `GuardianesMessagingService` | FirebaseMessagingService | Recibe comandos y alertas push |

> ⚠️ **Foreground services con tipo declarado** son obligatorios desde Android 14 (API 34). Cada servicio declara su `foregroundServiceType` en el manifiesto.

## 6. Permisos y degradación

La app está diseñada para **degradar con elegancia**: si un permiso sensible no se concede, la función asociada se desactiva y se informa al usuario, sin bloquear el resto de la app. Ver `core/util/PermissionManager`.

## 7. Seguridad

- Reglas de seguridad de Firestore por `familyId` y rol (ver `docs/firestore.rules` futuro).
- Datos sensibles locales cifrados con `EncryptedSharedPreferences` / `Jetpack Security`.
- Emparejamiento con código de un solo uso y expiración.
- Protección de ajustes en el dispositivo del menor con PIN/biometría y Device Admin para evitar desinstalación no autorizada.

## 8. Testing (objetivo)

- **Unit**: casos de uso de dominio (gamificación, reglas de límites, evaluación de geocercas).
- **Instrumented**: DAOs de Room, navegación.
- **UI**: Compose tests de pantallas críticas.

Ver el estado real en [docs/ROADMAP.md](docs/ROADMAP.md).
