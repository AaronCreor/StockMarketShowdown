buildscript {
    repositories {
        google()
    }
    dependencies {
        // Add Safe Args plugin dependency
        classpath(libs.google.services)
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}