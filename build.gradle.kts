plugins {
    alias(libs.plugins.androidApplication) apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false

    id("com.google.firebase.crashlytics") version "3.0.3" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral() // Add this line
        mavenLocal()
        maven {
            url = uri("https://jitpack.io")
        }
        // jcenter()
    }
    dependencies {
        //noinspection UseTomlInstead
        classpath("com.android.tools.build:gradle:8.7.3")
        //noinspection UseTomlInstead
        classpath("com.google.gms:google-services:4.4.2")
        // classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")

    }
}