plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.lifecompanion.detekt.plugin)
}

val exchangeBaseUrl: String by project
val exchangeApiKey: String by project
val minioEndpoint: String by project
val minioEndpointAccessKey: String by project
val minioEndpointSecretKey: String by project
val minioBucketName: String by project

android {
    namespace = "com.mena97villalobos.remote"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "EXCHANGE_API_ENDPOINT", "\"$exchangeBaseUrl\"")
            buildConfigField("String", "EXCHANGE_API_KEY", "\"$exchangeApiKey\"")
            buildConfigField("String", "MINIO_ENDPOINT", "\"$minioEndpoint\"")
            buildConfigField("String", "MINIO_ENDPOINT_ACCESS_KEY", "\"$minioEndpointAccessKey\"")
            buildConfigField("String", "MINIO_ENDPOINT_SECRET_KEY", "\"$minioEndpointSecretKey\"")
            buildConfigField("String", "MINIO_BUCKET_NAME", "\"$minioBucketName\"")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "EXCHANGE_API_ENDPOINT", "\"$exchangeBaseUrl\"")
            buildConfigField("String", "EXCHANGE_API_KEY", "\"$exchangeApiKey\"")
            buildConfigField("String", "MINIO_ENDPOINT", "\"$minioEndpoint\"")
            buildConfigField("String", "MINIO_ENDPOINT_ACCESS_KEY", "\"$minioEndpointAccessKey\"")
            buildConfigField("String", "MINIO_ENDPOINT_SECRET_KEY", "\"$minioEndpointSecretKey\"")
            buildConfigField("String", "MINIO_BUCKET_NAME", "\"$minioBucketName\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.minio)

    // Koin + Ktor
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    implementation(libs.bundles.koin.ktor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}