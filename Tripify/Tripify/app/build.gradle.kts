plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.example.tripify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tripify"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.maps)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.picasso)
    implementation ("com.google.firebase:firebase-appcheck-safetynet:16.0.0-beta01")
    implementation ("com.google.firebase:firebase-firestore:23.0.3")
    implementation ("com.google.firebase:firebase-database:20.0.3")

    implementation("com.google.firebase:firebase-database")
    implementation ("com.google.android.gms:play-services-auth:21.1.0")
    implementation ("com.stripe:stripe-android:20.41.0")
    implementation ("com.google.firebase:firebase-functions:20.4.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.libraries.places:places:3.4.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")



}