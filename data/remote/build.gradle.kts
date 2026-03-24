import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.lifecompanion.kotlin.multiplatform.compose.plugin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.lifecompanion.detekt.plugin)
}

val exchangeBaseUrl: String by project
val exchangeApiKey: String by project
val minioEndpoint: String by project
val minioEndpointAccessKey: String by project
val minioEndpointSecretKey: String by project
val minioBucketName: String by project

val httpLoggingEnabled = findProperty("remote.http.logging")?.toString() == "true"

buildkonfig {
    packageName = "com.mena97villalobos.remote"
    defaultConfigs {
        buildConfigField(STRING, "EXCHANGE_API_ENDPOINT", exchangeBaseUrl)
        buildConfigField(STRING, "EXCHANGE_API_KEY", exchangeApiKey)
        buildConfigField(STRING, "MINIO_ENDPOINT", minioEndpoint)
        buildConfigField(STRING, "MINIO_ENDPOINT_ACCESS_KEY", minioEndpointAccessKey)
        buildConfigField(STRING, "MINIO_ENDPOINT_SECRET_KEY", minioEndpointSecretKey)
        buildConfigField(STRING, "MINIO_BUCKET_NAME", minioBucketName)
        buildConfigField(BOOLEAN, "HTTP_LOGGING", httpLoggingEnabled.toString())
    }
}

kotlin {
    android {
        namespace = "com.mena97villalobos.remote"
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

            implementation(libs.firebase.firestore)
            implementation(libs.androidx.core.ktx)
            implementation(libs.minio)

            implementation(libs.bundles.koin)
            implementation(libs.bundles.koin.ktor)

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
    add("androidMainImplementation", platform(libs.firebase.bom))
    add("androidMainImplementation", platform(libs.koin.bom))
}
