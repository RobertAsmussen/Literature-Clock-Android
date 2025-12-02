plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.literatureclock"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.literatureclock"
        // FORCE API 10 HERE
        minSdk = 10
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
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
    // Standard Android APIs only
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.11.1")
}