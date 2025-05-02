plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.pixelcodex"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pixelcodex"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "API_KEY", "\"${project.findProperty("AIzaSyBLCNJ6LiSOTy8s1FFqxz3YhwEf7qhkrek")}\"")

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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    // Dependencies for the app
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation (libs.play.services.auth) // Add this line for Google Sign In
    implementation (libs.firebase.auth.v2300)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // For HTTP requests
    implementation (libs.json)
    implementation(libs.generativeai)
    implementation(libs.guava)



    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)


    // Lottie dependency for animation support
    implementation(libs.lottie)
    implementation(libs.material)
}
