# Reglas ProGuard/R8 para Guardianes

# Modelos de dominio usados con Firestore (reflexión)
-keepclassmembers class com.guardianes.parental.core.domain.model.** {
    <init>(...);
    <fields>;
}
-keepclassmembers class com.guardianes.parental.core.data.remote.dto.** {
    <init>(...);
    <fields>;
}

# Firebase
-keepattributes Signature
-keepattributes *Annotation*

# Hilt genera código; no requiere reglas adicionales con AGP reciente.
