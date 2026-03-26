@file:Suppress("DEPRECATION")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.lifecompanion.kotlin.multiplatform.compose.plugin)
    alias(libs.plugins.lifecompanion.detekt.plugin)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    android {
        namespace = "com.mena97villalobos.designsystem"
        compileSdk {
            version = release(36) {
                minorApiLevel = 1
            }
        }
        minSdk = 36

        withHostTest {}

        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        optimization {
            consumerKeepRules.apply {
                publish = true
                file("consumer-rules.pro")
            }
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":domain"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(libs.kotlinx.datetime)
            // Coil image loading (Compose Multiplatform)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            // Coil network stack uses Ktor
            implementation(libs.ktor.core)
            implementation(libs.jetbrains.compose.ui.tooling)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)
            implementation(libs.material)
            implementation(libs.jetbrains.coroutines)
            implementation(libs.ktor.okhttp)
        }

        iosMain.dependencies {
            // Foundation-backed formatters use platform APIs.
            implementation(libs.ktor.darwin)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.androidx.espresso.core)
            }
        }
    }
}
