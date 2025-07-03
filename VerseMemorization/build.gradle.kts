plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.yveltius.versememorization"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.core.ktx)
    implementation(libs.kotlinx.serialization.json)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)

    // Koin Test features
    testImplementation(libs.koin.test)
    // Koin for JUnit 4
    testImplementation(libs.koin.test.junit4)
    // Koin for JUnit 5
    testImplementation(libs.insert.koin.koin.test.junit5)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}