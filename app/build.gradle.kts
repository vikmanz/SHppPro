plugins {
    id("com.android.application")           // core
    id("org.jetbrains.kotlin.android")      // core
    id("androidx.navigation.safeargs")      // safe args for navigation
    id("kotlin-kapt")                       // Hilt
    id("com.google.dagger.hilt.android")    // Hilt
}

android {
    namespace = "com.vikmanz.shpppro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vikmanz.shpppro"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // ViewBinding
    buildFeatures {
        viewBinding = true
    }

    // Hilt uses Java VERSION_1_8 but kaptGenerateStubsDebugKotlin use VERSION_18
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Hilt uses Java VERSION_1_8 but kaptGenerateStubsDebugKotlin use VERSION_18
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildToolsVersion = rootProject.extra["buildToolsVersion"] as String

}


dependencies {

    // Core
    implementation (libs.core.ktx)
    implementation (libs.lifecycle.runtime.ktx)
    implementation (libs.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // UI
    implementation (libs.ui)
    implementation (libs.ui.graphics)
    implementation (libs.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.glide)                      // Glide
    debugImplementation (libs.ui.tooling)
    androidTestImplementation (libs.ui.test.junit4)

    // Lifecycle components
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    // Kotlin coroutines components
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.hilt.navigation.fragment) // via Hilt

    // Hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.hilt.compiler)

    // Faker
    implementation(libs.javafaker)       // generator for fake info

    // Retrofit
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation (libs.converter.gson)    // Gson
    implementation (libs.gson) // Gson

    // Symbol Processing (for Moshi, Room) and Kapt (for Hilt)
    implementation(libs.symbol.processing.api)
    kapt(libs.hilt.android.compiler)

}

// Symbol Processing Kapt (for Hilt)
kapt {
    correctErrorTypes = true // Allow references to generated code
}