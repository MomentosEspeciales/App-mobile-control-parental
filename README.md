# 🛡️ Guardianes — Control Parental Gamificado

> App de control parental para Android centrada en la **seguridad del menor**, el **acompañamiento positivo** y la **transparencia**. Diseñada para publicarse en Google Play cumpliendo sus políticas de *Apps de control parental*, *Permisos sensibles* y *Datos de menores (Families)*.

[![Android](https://img.shields.io/badge/Platform-Android%208%2B-3DDC84?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4)](https://developer.android.com/jetpack/compose)

---

## ✨ ¿Qué hace Guardianes?

Guardianes es **una sola app con dos modos** que se eligen durante el onboarding:

| Modo | Instalada en | Para qué sirve |
|------|--------------|----------------|
| 👨‍👩‍👧 **Padre/Madre** | Teléfono del tutor | Ver estadísticas, recibir alertas, definir zonas seguras, aprobar recompensas, localizar bajo demanda y activar el **SOS** en emergencias. |
| 🧒 **Hijo/a** | Teléfono del menor | Ver su progreso gamificado, canjear puntos por tiempo, lanzar su propio botón de pánico y cumplir límites de uso. |

Ambos dispositivos se **emparejan** mediante un código QR/numérico seguro y se sincronizan en tiempo real.

---

## 🎯 Funcionalidades

### Seguridad y emergencias
- 📍 **Localización bajo demanda** del dispositivo del menor (con registro de quién y cuándo la solicitó).
- 🟢 **Zonas seguras (geofencing)**: define un centro y un radio; si el menor sale, el padre recibe una **alarma** inmediata.
- 🆘 **Modo Emergencia / SOS**: en caso de peligro permite transmitir audio/vídeo y ubicación al tutor. *(Ver [consideraciones legales y de privacidad](docs/PLAY_STORE_COMPLIANCE.md) — funciona con consentimiento explícito y bajo control de Play).*
- 🚨 **Alertas de exposición peligrosa**: contenido adulto, lenguaje de acoso/grooming, autolesión, apps de riesgo, intentos de desinstalación.
- 📞 **Botón de pánico del menor** que avisa a todos los tutores con ubicación.

### Bienestar digital y control de uso
- 📊 **Estadísticas de uso**: tiempo por app, por categoría, evolución semanal, primeras/últimas horas de pantalla.
- ⏱️ **Límites de tiempo** por app, por categoría y total diario; **horarios** (escuela, sueño, comidas).
- 🚫 **Bloqueo de apps** y filtrado de contenido web.
- 😴 **Modo descanso** y **modo enfoque/colegio**.

### Gamificación (refuerzo positivo)
- 🏆 **Puntos** por cumplir objetivos saludables, tareas y rutinas.
- 🎁 **Tienda de recompensas**: canjea puntos por **minutos extra de pantalla** u otras recompensas que defina la familia.
- 🔥 **Rachas, niveles, medallas y retos** semanales.

### Administración familiar
- 👨‍👩‍👧‍👦 Soporte **multi-tutor** y **multi-hijo**.
- 🔔 Centro de notificaciones y bitácora de actividad.
- 🔒 PIN/biometría para proteger ajustes en el dispositivo del menor.

---

## 🏗️ Arquitectura (resumen)

- **UI**: Jetpack Compose + Material 3 + Navigation Compose.
- **Patrón**: MVVM + Clean Architecture (capas `ui` / `domain` / `data`).
- **DI**: Hilt.
- **Local**: Room + DataStore.
- **Trabajo en segundo plano**: WorkManager + Foreground Services.
- **Backend / tiempo real**: Firebase (Auth, Firestore, Cloud Messaging, Storage, Crashlytics).
- **Sistema**: `UsageStatsManager`, Geofencing API (Play Services Location), Accessibility Service, Device Admin, CameraX, MediaRecorder.

Detalle completo en **[ARCHITECTURE.md](ARCHITECTURE.md)**.

---

## 📂 Estructura del repositorio

```
.
├── app/                      # Módulo de aplicación Android
│   └── src/main/
│       ├── java/com/guardianes/parental/
│       │   ├── core/         # data, di, util, modelos de dominio
│       │   ├── features/     # location, usage, emergency, gamification, monitoring
│       │   └── ui/           # theme, navigation, pantallas (onboarding, parent, child)
│       └── AndroidManifest.xml
├── docs/                     # Cumplimiento Play, privacidad, roadmap, features
├── gradle/                   # Version catalog (libs.versions.toml)
└── build.gradle.kts / settings.gradle.kts
```

---

## 🚀 Puesta en marcha

> **Requisitos**: Android Studio Ladybug+, JDK 17, SDK Android 34.

```bash
# 1. Clonar
git clone <repo>

# 2. Configurar Firebase
#    - Crea un proyecto en https://console.firebase.google.com
#    - Descarga google-services.json y colócalo en app/
#    - (Hay app/google-services.json.sample como referencia)

# 3. Compilar
./gradlew assembleDebug

# 4. Generar bundle para Play
./gradlew bundleRelease
```

Consulta **[docs/ROADMAP.md](docs/ROADMAP.md)** para el estado de cada funcionalidad y los siguientes pasos.

### 📲 Obtener el APK SIN instalar nada (recomendado)

No necesitas Android Studio. GitHub compila el APK por ti:

1. Entra en la pestaña **Actions** del repositorio en GitHub.
2. Abre el workflow **«Build APK»** (se ejecuta solo en cada push; también puedes lanzarlo a mano con *Run workflow*).
3. Cuando termine (✅), entra en la ejecución y descarga el artefacto **`guardianes-debug-apk`**.
4. Copia ese `.apk` a tu teléfono Android y ábrelo (activa *«Instalar apps de orígenes desconocidos»* si lo pide).

> Es un APK **de depuración** (firmado con la clave de debug), perfecto para probar la app. Para publicar en Play se genera un **AAB de release firmado** (`./gradlew bundleRelease` con tu keystore).

---

## ⚖️ Privacidad, ética y cumplimiento

Esta app maneja datos de **menores** y permisos **muy sensibles** (ubicación en segundo plano, micrófono, cámara, accesibilidad). Su publicación en Google Play exige cumplir políticas estrictas:

- El menor (según edad) y el tutor deben recibir **información clara** de qué se monitoriza.
- Funciones como SOS de audio/vídeo se diseñan como **herramienta de seguridad con consentimiento**, no como espionaje encubierto de terceros (lo que violaría la política *stalkerware*).
- Notificación persistente y aviso de monitorización activa según lo requiere Play.

Lee **obligatoriamente** [docs/PLAY_STORE_COMPLIANCE.md](docs/PLAY_STORE_COMPLIANCE.md) y [docs/PRIVACY_POLICY.md](docs/PRIVACY_POLICY.md) antes de publicar.

---

## 📝 Licencia

Pendiente de definir por la familia/empresa propietaria. Ver `LICENSE`.
