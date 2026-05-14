plugins {

    id("com.android.application")

    id("org.jetbrains.kotlin.android")

    id("com.google.devtools.ksp")
}

android {

    namespace = "com.example.mahilashaktiunnativ2"

    compileSdk = 36

    buildToolsVersion = "36.0.0"

    defaultConfig {

        applicationId =
            "com.example.mahilashaktiunnativ2.demo"

        minSdk = 24

        targetSdk = 36

        versionCode = 2

        versionName = "1.1-demo"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(

                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),

                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_17

        targetCompatibility =
            JavaVersion.VERSION_17
    }

    kotlinOptions {

        jvmTarget = "17"
    }

    buildFeatures {

        compose = true
    }

    composeOptions {

        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4"
    )

    implementation(
        "io.coil-kt:coil-compose:2.6.0"
    )

    implementation(
        "androidx.room:room-runtime:2.6.1"
    )

    implementation(
        "androidx.room:room-ktx:2.6.1"
    )

    add(
        "ksp",
        "androidx.room:room-compiler:2.6.1"
    )

    implementation(
        "com.github.yalantis:ucrop:2.2.8"
    )

    implementation(
        "androidx.core:core-ktx:1.13.1"
    )

    implementation(
        "androidx.activity:activity-compose:1.9.1"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.4"
    )

    implementation(libs.androidx.core.ktx)

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    implementation(
        libs.androidx.activity.compose
    )

    implementation(
        platform(libs.androidx.compose.bom)
    )

    implementation(libs.androidx.compose.ui)

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    implementation(
        "androidx.appcompat:appcompat:1.7.0"
    )

    testImplementation(libs.junit)

    androidTestImplementation(
        libs.androidx.junit
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )
}
