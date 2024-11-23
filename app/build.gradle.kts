plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.smokeapplicationapppro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smokeapplicationapppro"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //gif
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    //firebase
    implementation ("com.google.firebase:firebase-auth:21.1.0")
    implementation ("com.google.firebase:firebase-database:20.0.3")
    implementation ("com.google.firebase:firebase-core:20.1.0")

    //gestures
    implementation ("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.6.0")


}
