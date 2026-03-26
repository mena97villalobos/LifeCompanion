package com.mena97villalobos.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/** Applies KMP and the Android KMP library plugin only (no Compose). */
class KotlinMultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
        }
    }
}
