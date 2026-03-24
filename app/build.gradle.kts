plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.androidtv.tmdb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidtv.tmdb"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "TMDB_BEARER_TOKEN",
            "\"eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4Nzc1MWQ5Y2RkMzVkNzRmMjY3NDY0MjMxNjk5YzJhZCIsIm5iZiI6MTc3MzkxNDY1Ni40NDksInN1YiI6IjY5YmJjYTIwMDkwMmMxZDhlODA1MjdkOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.gQ0H8nuWgvcMNqBXQGo5wSlfcfKZn1U0pxKi1_6p7Wo\""
        )

        buildConfigField(
            "String",
            "TMDB_IMAGE_BASE_URL",
            "\"https://image.tmdb.org/t/p/\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
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
    // AndroidX Leanback (TV UI framework)
    implementation("androidx.leanback:leanback:1.0.0")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Lifecycle + ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Networking (Retrofit + OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}
