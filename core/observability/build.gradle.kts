import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.lifecompanion.kotlin.multiplatform.library.plugin)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.lifecompanion.detekt.plugin)
}

// Optional project properties (gradle.properties / local.properties / CI -P flags).
// SENTRY_DSN is intentionally blank by default so local/dev builds run without crash reporting.
// The app version is NOT a build-time constant here — it is read at runtime from the platform
// (Android BuildConfig.VERSION_NAME, iOS CFBundleShortVersionString) so it can never drift.
val sentryDsn = (findProperty("sentry.dsn") as? String).orEmpty()
val sentryEnvironment = (findProperty("sentry.environment") as? String) ?: "development"

buildkonfig {
    packageName = "com.mena97villalobos.observability"
    // Expose the generated object so the app/composeApp entry points can read the DSN.
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(STRING, "SENTRY_DSN", sentryDsn)
        buildConfigField(STRING, "SENTRY_ENVIRONMENT", sentryEnvironment)
    }
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    android {
        namespace = "com.mena97villalobos.observability"
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
            implementation(libs.kermit)
            implementation(libs.sentry.kotlin.multiplatform)
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.bundles.koin)
        }

        commonTest.dependencies {
            // Keep commonTest platform-agnostic: kotlin("test") maps to the right framework per
            // target (JUnit on Android/JVM, the native test runner on iOS). JVM-only JUnit must not
            // leak into commonTest or it breaks the iOS test compilation.
            implementation(kotlin("test"))
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
}
