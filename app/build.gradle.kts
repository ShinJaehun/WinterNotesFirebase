plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.shinjaehun.winternotesfirebase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shinjaehun.winternotesfirebase"
        minSdk = 26
        targetSdk = 34
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // for sdp, ssp
    runtimeOnly("com.intuit.sdp:sdp-android:1.1.0")
    runtimeOnly("com.intuit.ssp:ssp-android:1.1.0")

    implementation("com.makeramen:roundedimageview:2.3.0")

    //coil
    implementation(libs.coil3.coil)
    implementation(libs.coil3.coil.network.okhttp)

    implementation(libs.firebase.bom)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
}

