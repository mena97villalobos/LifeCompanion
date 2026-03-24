import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.lifecompanion.kotlin.multiplatform.compose.plugin)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.lifecompanion.detekt.plugin)
}

room {
    schemaDirectory(layout.projectDirectory.dir("schemas"))
}

ksp {
    arg("room.schemaLocation", "${projectDir.absolutePath}/schemas")
}

kotlin {
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
        androidMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.androidx.core.ktx)
            implementation(libs.bundles.room)
            implementation(libs.bundles.koin)
            implementation(libs.jetbrains.compose.runtime)
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
    ksp(libs.room.compiler)
}
