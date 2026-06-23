# 🔐 Firma de la app (keystore) y generación del AAB

## Qué es y por qué importa
Google Play exige que cada versión vaya **firmada**. Guardianes usa un *keystore*
(la clave de subida). Se recomienda activar **Play App Signing**: Google custodia
la clave de firma final y tú solo gestionas esta clave de subida (recuperable por
soporte si la pierdes).

> ⚠️ **Nunca** subas el `.jks`, sus contraseñas ni `keystore.properties` al
> repositorio. Ya están en `.gitignore`. Haz una copia de seguridad del keystore
> en un lugar seguro: sin él (y sin Play App Signing) no podrías actualizar la app.

## Datos del keystore generado
- **Archivo**: `guardianes-upload.jks`
- **Alias**: `guardianes`
- **Algoritmo**: RSA 2048, válido hasta **2053** (cumple el requisito de Play).
- **Contraseña**: se entregó por chat al generarlo (guárdala en tu gestor de contraseñas).

## Opción A — Generar el AAB en GitHub (recomendado, sin instalar nada)

1. En GitHub: **Settings → Secrets and variables → Actions → New repository secret**
   y crea estos 4 secrets:
   | Secret | Valor |
   |--------|-------|
   | `KEYSTORE_BASE64` | el contenido de `guardianes-upload.jks.base64.txt` |
   | `KEYSTORE_PASSWORD` | la contraseña del keystore |
   | `KEY_PASSWORD` | la misma contraseña |
   | `KEY_ALIAS` | `guardianes` |
2. Ve a **Actions → «Release AAB» → Run workflow**.
3. Al terminar, descarga el artefacto **`guardianes-release-aab`**: dentro está
   `app-release.aab`, listo para subir a Play Console.

## Opción B — Firmar en local

1. Copia `guardianes-upload.jks` a la raíz del proyecto.
2. Crea `keystore.properties` (copia de `keystore.properties.sample`) con tus datos.
3. Ejecuta:
   ```bash
   ./gradlew bundleRelease     # genera app/build/outputs/bundle/release/app-release.aab
   ./gradlew assembleRelease   # (opcional) APK de release firmado
   ```

## Subida a Google Play
1. Crea la app en **Play Console** y activa **Play App Signing**.
2. Sube el `app-release.aab` a un *track* (interno / cerrado / producción).
3. Completa la ficha: Data Safety, política de privacidad, cuestionario de
   contenido y, si aplica, el **Programa Families** (ver `docs/PLAY_STORE_COMPLIANCE.md`).
