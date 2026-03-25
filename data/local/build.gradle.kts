import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.lifecompanion.kotlin.multiplatform.library.plugin)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.lifecompanion.detekt.plugin)
}

room {
    schemaDirectory(layout.projectDirectory.dir("schemas"))
}

ksp {
    arg("room.schemaLocation", layout.projectDirectory.dir("schemas").asFile.absolutePath)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    android {
        namespace = "com.mena97villalobos.local"
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
            implementation(project(":domain"))
            implementation(libs.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.bundles.koin)
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

dependencies {
    add("androidMainImplementation", platform(libs.koin.bom))
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
}
