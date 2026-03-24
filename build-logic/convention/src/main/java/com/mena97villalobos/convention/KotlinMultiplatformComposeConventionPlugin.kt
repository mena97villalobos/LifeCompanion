package com.mena97villalobos.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/** Applies KMP, Android-KMP library, Compose Multiplatform, and the Kotlin Compose compiler plugins. */
class KotlinMultiplatformComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
            apply("org.jetbrains.compose")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
    }
}
