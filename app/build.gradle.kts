import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.project.travelcompanionapp"
    compileSdk = 35


    val localProperties =  Properties()
    val localPropertiesFile = File(rootDir,"secret.properties")
    if(localPropertiesFile.exists() && localPropertiesFile.isFile){

        localPropertiesFile.inputStream().use {

            localProperties.load(it)
        }
    }
    defaultConfig {
        applicationId = "com.project.travelcompanionapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","WEATHER_API",localProperties.getProperty("WEATHER_API"))


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
        buildConfig = true
    }
}

dependencies {


    implementation (libs.retrofit)
    implementation (libs.converter.gson.v290)
    implementation (libs.okhttp)
    implementation(libs.androidx.cardview)
    implementation(libs.firebase.analytics)
    implementation(libs.kotlin.stdlib)
    implementation (libs.github.glide)
    implementation (libs.androidx.viewpager2)
    implementation (libs.ismaeldivita.chip.navigation.bar)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.constraintlayout)
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}