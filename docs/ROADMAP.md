# 🗺️ Roadmap y estado de funcionalidades

Estado: ✅ implementado · 🟡 andamiaje/parcial · ⬜ pendiente

## Fundamentos
- ✅ Estructura de proyecto Gradle (Kotlin DSL + version catalog)
- ✅ Manifiesto con permisos y servicios declarados
- ✅ Tema Material 3, navegación, DI (Hilt)
- ✅ Modelos de dominio y contratos de repositorio
- 🟡 Capa de datos Firebase (interfaces + impl. de referencia)
- ⬜ Reglas de seguridad de Firestore

## Onboarding y emparejamiento
- ✅ Selección de rol (Padre/Hijo)
- 🟡 Emparejamiento por código/QR (flujo y UI; backend real pendiente)
- ⬜ Verificación parental (Families)

## Modo Padre
- ✅ Dashboard con tarjetas de estado y estadísticas (UI + VM)
- 🟡 Mapa y localización bajo demanda
- 🟡 Gestión de zonas seguras (geofencing)
- 🟡 Centro de alertas
- 🟡 Editor de límites y horarios
- 🟡 Aprobación de recompensas
- ✅ Botón de activación SOS (envío de comando)

## Modo Hijo
- ✅ Home gamificado (puntos, nivel, racha)
- 🟡 Tienda de recompensas / canje por tiempo
- ✅ Botón de pánico
- 🟡 Aplicación de límites y bloqueo

## Servicios de sistema
- 🟡 `GeofenceMonitorService` + receiver
- 🟡 `AppBlockerAccessibilityService`
- 🟡 `UsageTrackingWorker`
- 🟡 `EmergencyService` (SOS audio/vídeo)
- 🟡 `GuardianesMessagingService` (FCM)

## Gamificación
- ✅ Motor de puntos/niveles (dominio + casos de uso)
- ✅ Cálculo de canje puntos→minutos
- 🟡 Persistencia y sincronización

## Próximos pasos prioritarios
1. Conectar Firebase real (Auth + Firestore + FCM) y reglas de seguridad.
2. Completar geofencing end-to-end con pruebas en dispositivo.
3. Clasificador de contenido para alertas (on-device).
4. Suite de tests (dominio primero).
5. Revisión legal del módulo SOS por jurisdicción.
6. Preparar ficha de Play (Data Safety, vídeo de permisos, política de privacidad).
