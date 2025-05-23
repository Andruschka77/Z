plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-android")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.z"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.z"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.play.services.location)
    implementation(libs.androidx.activity.compose.v170)
    implementation(libs.ui)
    implementation(libs.androidx.lifecycle.runtime.ktx.v287)
    implementation(libs.maps.mobile)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.location.v2101)

//    implementation(libs.ktor.client.core)
////    implementation(libs.ktor.client.android)
////    implementation(libs.ktor.client.serialization)
////    implementation(libs.ktor.client.content.negotiation)
////    implementation(libs.ktor.client.logging)
////    implementation(libs.xlogback.classic)
////    implementation(libs.kotlinx.serialization.json)
////    implementation(libs.androidx.security.crypto)
////    implementation(libs.ktor.client.android.v237)
////    implementation(libs.ktor.serialization.kotlinx.json)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("io.ktor:ktor-client-core:3.1.1")
    implementation("io.ktor:ktor-client-android:3.1.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("io.ktor:ktor-server-call-logging-jvm:3.1.1")
    implementation("io.ktor:ktor-client-logging:3.1.1")
}