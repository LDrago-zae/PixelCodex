// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    repositories {
        google()  // Required for Android Gradle Plugin
        mavenCentral()
    }
    dependencies {
        // Define the Android Gradle Plugin classpath here
        classpath(libs.gradle.v870)  // Update to AGP 8.7.0
        // If you're using Kotlin, add the Kotlin Gradle Plugin // Optional for Kotlin
    }
}


