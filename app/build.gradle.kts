plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin") version "2.7.7"
}

android {
    namespace = "com.example.nutrition_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nutritionapp"
        minSdk = 21
        targetSdk = 35
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    android.buildFeatures.apply {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("com.google.android.material:material:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Тесты
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("androidx.cardview:cardview:1.0.0")
    // Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
}
