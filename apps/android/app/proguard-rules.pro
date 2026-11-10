# Chaquopy & Python runtime JNI preservation
-keep class com.chaquo.python.** { *; }
-dontwarn com.chaquo.python.**

# Preserve Hermes IPC models and GSON serialization
-keep class org.nousresearch.hermes.evolution.engine.HermesProtocol** { *; }
-keepclassmembers class org.nousresearch.hermes.evolution.engine.HermesProtocol** { <fields>; }
-keep class org.nousresearch.hermes.evolution.data.model.** { *; }
-keepclassmembers class org.nousresearch.hermes.evolution.data.model.** { <fields>; }

# AndroidX Security Crypto & Keystore
-keep class androidx.security.crypto.** { *; }

# Coroutines and WorkManager
-keepattributes *Annotation*
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keepclassmembers class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
