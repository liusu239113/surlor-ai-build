plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ai.assistance.operit.gametool"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    // GameTool 不直接依赖 :app，避免循环依赖
    // GameTool 的 API 由 app 模块消费

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.animation)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)

    // WebView
    implementation(libs.androidx.webkit)

    // Serialization
    implementation(libs.kotlinx.serialization)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Coil for image loading
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // OkHttp
    implementation(libs.okhttp)
}
