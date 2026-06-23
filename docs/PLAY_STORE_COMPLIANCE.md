# ⚖️ Cumplimiento de Google Play y consideraciones éticas

> **Lectura obligatoria antes de publicar.** Esta app usa permisos que Google Play clasifica como de alto riesgo. Una configuración incorrecta provoca **rechazo o retirada** de la app y, en funciones de audio/vídeo, posibles **problemas legales**.

## 1. Categoría correcta

Publicar como **app de control parental** y, si el público objetivo incluye menores, inscribirse en el **Programa Families** de Google Play. Declarar con precisión el público objetivo y la recopilación de datos en la sección **Data Safety**.

## 2. Permisos sensibles y cómo justificarlos

| Permiso | Política aplicable | Requisito para aprobación |
|---------|--------------------|---------------------------|
| `ACCESS_BACKGROUND_LOCATION` | Background Location | Vídeo de demostración + justificación; uso evidente para el usuario (seguridad del menor) |
| `RECORD_AUDIO`, `CAMERA` | Permisos de hardware | Uso ligado a una función visible; **notificación de actividad** |
| `BIND_ACCESSIBILITY_SERVICE` | Accessibility API | Solo para la función declarada (bloqueo/monitorización); prohibido uso encubierto no relacionado |
| `PACKAGE_USAGE_STATS` | UsageStats | Concedido por el usuario en Ajustes; función de bienestar digital |
| `BIND_DEVICE_ADMIN` | Device Admin | Solo lo necesario (anti-desinstalación, bloqueo) |
| `QUERY_ALL_PACKAGES` | Visibilidad de apps | Justificar: necesaria para gestionar/limitar todas las apps |

## 3. ⚠️ El punto crítico: micrófono y cámara "sin dar pistas"

El requisito de **activar mic/cámara sin que se note** es la parte más delicada del proyecto. Hay que distinguir dos mundos:

- ✅ **Legítimo (parental/seguridad)**: un tutor legal protege a **su hijo menor**, con consentimiento configurado en el emparejamiento, como herramienta de **emergencia**. Esto encaja en la excepción de apps de control parental.
- ❌ **Prohibido (stalkerware)**: grabar a una persona en secreto sin su conocimiento/consentimiento. Google Play prohíbe expresamente el *software de vigilancia encubierta* y exige **notificación persistente** cuando se monitoriza.

### Cómo lo implementa Guardianes para ser publicable

1. **Consentimiento en el emparejamiento**: durante la configuración del dispositivo del menor se explica explícitamente que el SOS puede activar audio/vídeo en emergencias, y queda registrado.
2. **Indicadores de privacidad de Android 12+**: el punto verde de cámara/micrófono **no se puede ocultar** a nivel de sistema, y Guardianes **no intenta** evadirlo (hacerlo viola la política y, en muchas jurisdicciones, la ley).
3. **"Sin dar pistas" = UI discreta, no ocultación del sistema**: la app evita sonidos de obturador, flashes, vistas previas a pantalla completa o notificaciones llamativas en el dispositivo del menor durante el SOS, de modo que un agresor presente no advierta que se está pidiendo ayuda. **No** falsifica ni elimina los indicadores de seguridad del sistema operativo.
4. **Activación restringida a emergencias** y auditada: cada activación SOS se registra (quién, cuándo) y es visible en la bitácora familiar.
5. **Notificación de servicio en primer plano** presente (requerida por el SO), redactada de forma neutra.

> **Resumen honesto:** A nivel de sistema operativo es **imposible y no permitido** ocultar por completo el uso de cámara/micrófono en Android moderno; cualquier app que lo prometa miente o usará técnicas que provocarán el baneo y posible ilegalidad. Guardianes ofrece la **máxima discreción permitida** (interfaz silenciosa, sin pistas para un tercero presente) manteniéndose **dentro de la ley y de las políticas de Play**.

## 4. Consentimiento y edad

- Recoger consentimiento del tutor (obligatorio) y, según edad y jurisdicción (COPPA/EEUU, RGPD/UE, etc.), informar al menor.
- Edad mínima y verificación parental según el Programa Families.

## 5. Data Safety y privacidad

- Declarar todos los datos recogidos (ubicación, uso de apps, audio/vídeo en SOS) y su finalidad.
- Publicar una **Política de Privacidad** accesible (ver `docs/PRIVACY_POLICY.md`).
- Minimización de datos, cifrado en tránsito y reposo, y opción de **borrado**.

## 6. Checklist previo a publicación

- [ ] Inscripción en Families (si aplica) y cuestionario de contenido completado.
- [ ] Vídeo de demostración de uso de ubicación en segundo plano.
- [ ] Justificación escrita de cada permiso sensible.
- [ ] Notificaciones persistentes en monitorización activa.
- [ ] Política de privacidad publicada y enlazada.
- [ ] Sección Data Safety completa y veraz.
- [ ] Revisión legal del módulo SOS según jurisdicciones de lanzamiento.
- [ ] Mecanismo de consentimiento y de retirada de consentimiento implementado.

> Este documento es orientativo y **no constituye asesoramiento legal**. Antes de lanzar funciones de audio/vídeo, consulta con un abogado especializado en privacidad de los mercados donde publiques.
