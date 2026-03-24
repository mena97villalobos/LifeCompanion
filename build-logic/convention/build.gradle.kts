import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.mena97villalobos.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    // Required at runtime: Gradle decorates the plugin class and loads DetektExtension/Detekt when applying.
    implementation(libs.detekt.gradle.plugin)
}

gradlePlugin {
    plugins {
        create("lifecompanionDetekt") {
            id = "com.mena97villalobos.lifecompanion.detekt.plugin"
            implementationClass = "com.mena97villalobos.convention.DetektConventionPlugin"
        }
        create("lifecompanionKotlinMultiplatformCompose") {
            id = "com.mena97villalobos.lifecompanion.kotlin.multiplatform.compose.plugin"
            implementationClass =
                "com.mena97villalobos.convention.KotlinMultiplatformComposeConventionPlugin"
        }
    }
}