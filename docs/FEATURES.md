# 📋 Catálogo de funcionalidades

Mapeo entre lo que pidió la familia/cliente y dónde vive en el código.

| # | Funcionalidad solicitada | Estado | Dónde está |
|---|--------------------------|--------|------------|
| 1 | Estadísticas de uso | 🟡 | `features/usage/UsageStatsCollector.kt`, `ui/parent/DashboardScreen.kt` |
| 2 | Alertas ante exposiciones peligrosas | 🟡 | `features/monitoring/ContentClassifier.kt`, `ui/parent/AlertsScreen.kt` |
| 3 | Rastreo bajo demanda del móvil | 🟡 | `features/location/*`, `ui/parent/MapScreen.kt`, comando `LOCATE_NOW` |
| 4 | Restringir uso | 🟡 | `features/usage/service/AppBlockerAccessibilityService.kt`, modelo `UsageRule` |
| 5 | Ganar puntos canjeables por tiempo | ✅ | `features/gamification/domain/GamificationEngine.kt`, pantallas de recompensas |
| 6 | Activar micro/cámara discretamente en peligro | 🟡 | `features/emergency/service/EmergencyService.kt` (+ compliance) |
| 7 | Radio/zona segura con alarma al salir | 🟡 | `features/location/domain/GeoMath.kt`, `geofence/*` |

## Funcionalidades adicionales incluidas (propias de una buena app de control parental)

- 👨‍👩‍👧‍👦 **Multi-tutor y multi-hijo** (modelo de datos por `familyId`).
- 🔔 **Centro de alertas** con severidades (info/aviso/crítico) y acuse de recibo.
- 🆘 **Botón de pánico del menor** (no solo SOS iniciado por el padre).
- 📞 **Comandos remotos**: localizar, sonar, bloquear, refrescar uso.
- 🔋 **Alertas de batería baja y de dispositivo sin conexión**.
- 😴 **Modos**: descanso/sueño, colegio/enfoque (horarios).
- 🏆 **Niveles, rachas y misiones** (refuerzo positivo, no solo castigo).
- 🔒 **Anti-desinstalación** (Device Admin) y bloqueo de ajustes con PIN/biometría.
- 🔁 **Reactivación tras reinicio** (`BootReceiver`).
- 🛡️ **Privacidad por diseño**: análisis on-device, minimización de datos, indicadores del sistema respetados, bitácora de auditoría.

## Funcionalidades recomendadas para siguientes versiones

- Filtrado web/DNS seguro y SafeSearch forzado.
- Resumen semanal por email al tutor + informe de bienestar digital.
- Detección de instalación de apps de riesgo y de cambios de SIM.
- "Modo coche"/no molestar al conducir.
- Geocercas con horario (p. ej. solo en horario escolar) — ya modelado en `SafeZone.schedule`.
- Soporte iOS (la lógica de dominio en Kotlin es portable; UI nativa aparte).
