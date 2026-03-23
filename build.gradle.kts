// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.lifecompanion.detekt.plugin) apply false
}

// Apply only on the root project so mergeDetektReports can wire subproject SARIF inputs.
// Do not use allprojects: Detekt must run after Android/Kotlin on each module or AGP fails
// (KotlinSourceSetContainer is registered by the Kotlin plugin).
apply(plugin = "com.mena97villalobos.lifecompanion.detekt.plugin")